package de.digitalcollections.iiif.model.jackson.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.Motivation;
import de.digitalcollections.iiif.model.OtherContent;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.openannotation.ContentAsText;
import de.digitalcollections.iiif.model.openannotation.CssStyle;
import de.digitalcollections.iiif.model.openannotation.SpecificResource;
import de.digitalcollections.iiif.model.sharedcanvas.AnnotationList;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.model.sharedcanvas.Layer;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Range;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;
import java.io.IOException;

/** Custom deserializer for Resource types.
 *
 * Needed since we need to preserve the @type for OtherContent, which would get lost
 * if we left the type resolving to Jackson.
 */
public class ResourceDeserializer extends JsonDeserializer<Resource> {

  private static final ImmutableMap<String, Class<? extends Resource>> MAPPING = new ImmutableMap.Builder<String, Class<? extends Resource>>()
      .put(Annotation.TYPE, Annotation.class)
      .put(AnnotationList.TYPE, AnnotationList.class)
      .put(Canvas.TYPE, Canvas.class)
      .put(Collection.TYPE, Collection.class)
      .put(Layer.TYPE, Layer.class)
      .put(Manifest.TYPE, Manifest.class)
      .put(Range.TYPE, Range.class)
      .put(Sequence.TYPE, Sequence.class)
      .put(ImageContent.TYPE, ImageContent.class)
      .put(SpecificResource.TYPE, SpecificResource.class)
      .put(ContentAsText.TYPE, ContentAsText.class)
      .put(CssStyle.TYPE, CssStyle.class)
      .build();

  public Resource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    String containingField = getContainingField(p);
    if (p.getCurrentToken() == JsonToken.START_OBJECT) {
      ObjectNode obj = mapper.readTree(p);
      String typeName = getTypeName(containingField, ctxt, obj);
      if (typeName.equals("oa:Choice")) {
        return parseChoice(containingField, mapper, obj, ctxt);
      } else {
        return mapper.treeToValue(obj, MAPPING.getOrDefault(typeName, OtherContent.class));
      }
    } else if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
      String stringValue = p.getValueAsString();
      String typeName = getMissingType(ctxt, containingField);
      return resourceFromString(MAPPING.getOrDefault(typeName, OtherContent.class),
                                stringValue);
    } else if (p.getCurrentToken() == JsonToken.START_ARRAY) {
      // TODO
      throw new RuntimeException("Could not deserialize Resource.");
    } else {
      throw new RuntimeException("Could not deserialize Resource.");
    }
  }

  private String getTypeName(String containingField, DeserializationContext ctxt, ObjectNode obj) {
    JsonNode typeNode = obj.get("@type");
    if (typeNode.isMissingNode()) {
      return getMissingType(ctxt, containingField);
    } else {
      return typeNode.textValue();
    }
  }

  private Resource parseChoice(String containingField, ObjectMapper mapper, ObjectNode tree,
                               DeserializationContext ctxt) throws JsonProcessingException {
    ObjectNode defaultTree = (ObjectNode) tree.get("default");
    Class<? extends Resource> resourceType = MAPPING.getOrDefault(
        getTypeName(containingField, ctxt, defaultTree), OtherContent.class);
    Resource defaultResource = mapper.treeToValue(defaultTree, resourceType);
    ArrayNode alternativesArray = (ArrayNode) tree.get("item");
    for (JsonNode subNode : alternativesArray) {
      defaultResource.addAlternative(mapper.treeToValue(subNode, resourceType));
    }
    return defaultResource;
  }

  private String getMissingType(DeserializationContext ctxt, String containingField) {
    switch (containingField) {
      case "on":
        return getOnType(ctxt);
      case "within":
        return getWithinType(ctxt);
      case "canvases":
        return "sc:Canvas";
      case "thumbnail":
      case "logo":
        return "dctypes:Image";
      default:
        return null;
    }
  }

  private String getWithinType(DeserializationContext ctxt) {
    Resource obj = (Resource) ctxt.getParser().getCurrentValue();
    if (obj instanceof Manifest || obj instanceof Collection) {
      return "sc:Collection";
    } else if (obj instanceof AnnotationList) {
      return "sc:Layer";
    } else {
      return null;
    }
  }

  /** Get type for  "on" values that are plain URIs by deducing the type from their parent. */
  private String getOnType(DeserializationContext ctxt) {
    // Easiest way: The parser has already constructed an annotation object with a motivation.
    // This is highly dependendant on the order of keys in the JSON, i.e. if "on" is the first key in the annotation
    // object, this won't work.
    Object curVal = ctxt.getParser().getCurrentValue();
    boolean isPaintingAnno = (curVal != null && curVal instanceof Annotation
                              && ((Annotation) curVal).getMotivation() != null
                              && ((Annotation) curVal).getMotivation().equals(Motivation.PAINTING));
    if (isPaintingAnno) {
      return "sc:Canvas";
    }
    // More reliable way: Walk up the parsing context until we hit a IIIF resource that we can deduce the type from
    // Usually this shouldn't be more than two levels up
    JsonStreamContext parent = ctxt.getParser().getParsingContext().getParent();
    while (parent != null && (parent.getCurrentValue() == null || !(parent.getCurrentValue() instanceof Resource))) {
      parent = parent.getParent();
    }
    if (parent != null) {
      Resource parentObj = (Resource) parent.getCurrentValue();
      return parentObj.getType();
    }
    return null;
  }

  private Resource resourceFromString(Class<? extends Resource> clz, String resourceString) {
    try {
      return clz.getConstructor(String.class).newInstance(resourceString);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format(
          "Could not construct %s from '%s'",
          clz.getName(), resourceString));
    }
  }

  private static String getContainingField(JsonParser parser) {
    final JsonStreamContext ctx = parser.getParsingContext();
    if (ctx.inArray()) {
      return ctx.getParent().getCurrentName();
    } else {
      return ctx.getCurrentName();
    }
  }
}

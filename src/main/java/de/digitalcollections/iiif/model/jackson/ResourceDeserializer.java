package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.OtherContent;
import de.digitalcollections.iiif.model.api.Motivation;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.openannotation.Choice;
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
  private final static ImmutableMap<String, Class<? extends Resource>> MAPPING =
      new ImmutableMap.Builder<String, Class<? extends Resource>>()
        .put(Annotation.TYPE, Annotation.class)
        .put(AnnotationList.TYPE, AnnotationList.class)
        .put(Canvas.TYPE, Canvas.class)
        .put(Collection.TYPE, Collection.class)
        .put(Layer.TYPE, Layer.class)
        .put(Manifest.TYPE, Manifest.class)
        .put(Range.TYPE, Range.class)
        .put(Sequence.TYPE, Sequence.class)
        .put(Choice.TYPE, Choice.class)
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
      JsonNode typeNode = obj.get("@type");
      String typeName;
      if (typeNode.isMissingNode()) {
        typeName = getMissingType(ctxt, containingField);
      } else {
        typeName = typeNode.textValue();
      }
      return mapper.treeToValue(obj, MAPPING.getOrDefault(typeName, OtherContent.class));
    } else if (p.getCurrentToken() == JsonToken.VALUE_STRING){
      String stringValue = p.getValueAsString();
      String typeName = getMissingType(ctxt, containingField);
      return resourceFromString(MAPPING.getOrDefault(typeName, OtherContent.class),
                                stringValue);
    } else {
      throw new RuntimeException("Could not deserialize Resource.");
    }
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
    Annotation anno = (Annotation) ctxt.getParser().getCurrentValue();
    if (anno.getMotivation().equals(Motivation.PAINTING)) {
      return "sc:Canvas";
    }
    JsonStreamContext parent = ctxt.getParser().getParsingContext().getParent();
    /* Parent resource is two levels up: on-resource -> annotation -> resource */
    if (parent != null) {
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

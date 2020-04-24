package de.digitalcollections.iiif.model.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.ModelUtilities;
import de.digitalcollections.iiif.model.ModelUtilities.Completeness;
import de.digitalcollections.iiif.model.Motivation;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ResourceSerializer extends JsonSerializer<Resource> {

  private final JsonSerializer<Object> defaultSerializer;

  public ResourceSerializer(JsonSerializer<Object> defaultSerializer) {
    this.defaultSerializer = defaultSerializer;
  }

  @Override
  public void serialize(Resource value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    // Add @context to top-level object
    if (gen.getOutputContext().getParent() == null) {
      value._context = Resource.CONTEXT;
    }

    if (value.getAlternatives() != null && !value.getAlternatives().isEmpty()) {
      gen.writeStartObject();
      gen.writeStringField("@type", "oa:Choice");
      gen.writeFieldName("default");
      List<Resource> alternatives = value.getAlternatives();
      value.setAlternatives(null);
      defaultSerializer.serialize(value, gen, serializers);
      gen.writeArrayFieldStart("item");
      for (Resource alt : alternatives) {
        defaultSerializer.serialize(alt, gen, serializers);
      }
      gen.writeEndArray();
      gen.writeEndObject();
      return;
    }

    // Remove @type from ImageContent if necessary
    String containingField = getContainingField(gen);
    String typeBackup = null;
    if (ImmutableSet.of("thumbnail", "logo").contains(containingField)) {
      ImageContent imgContent = (ImageContent) value;
      typeBackup = imgContent._type;
      imgContent._type = null;
    }

    String parentType = null;
    if (gen.getCurrentValue() != null) {
      Object parent = gen.getCurrentValue();
      if (parent instanceof Resource) {
        parentType = ((Resource) parent).getType();
      }
    } else if (gen.getOutputContext() != null && gen.getOutputContext().getParent() != null) {
      Object parent = gen.getOutputContext().getParent().getCurrentValue();
      if (parent instanceof Resource) {
        parentType = ((Resource) parent).getType();
      }
    }

    Completeness completeness = ModelUtilities.getCompleteness(value, value.getClass());
    if (Objects.equals(containingField, "canvases") && completeness == ModelUtilities.Completeness.ID_AND_TYPE) {
      // It's redundant to specify the @type here, since it's clear we have canvases from the field name
      completeness = ModelUtilities.Completeness.ID_ONLY;
    }
    if (Objects.equals(containingField, "within") && completeness == ModelUtilities.Completeness.ID_AND_TYPE) {
      // It's also redundant in these cases, since the specification prescribes a convention
      String withinType = value.getType();
      boolean skipType = (("sc:Manifest".equals(parentType) && "sc:Collection".equals(withinType))
          || ("sc:AnnotationList".equals(parentType) && "sc:Layer".equals(withinType))
          || ("sc:Collection".equals(parentType) && "sc:Collection".equals(withinType)));
      if (skipType) {
        completeness = ModelUtilities.Completeness.ID_ONLY;
      }
    } else if (Objects.equals(containingField, "on") && completeness == ModelUtilities.Completeness.ID_AND_TYPE) {
      boolean skipType = (value instanceof Canvas
          && gen.getCurrentValue() instanceof Annotation
          && Objects.equals(((Annotation) gen.getCurrentValue()).getMotivation(), Motivation.PAINTING));
      if (skipType) {
        completeness = ModelUtilities.Completeness.ID_ONLY;
      }
    } else {
      ImmutableSet<String> skipParents = ImmutableSet.of("contentLayer", "ranges", "annotations");
      boolean shouldSkip = (Arrays.asList("prev", "next", "first", "last").contains(containingField)
                            || (completeness == Completeness.ID_AND_TYPE && skipParents.contains(containingField))
                            || ("otherContent".equals(containingField) && "sc:Layer".equals(parentType)));
      if (shouldSkip) {
        completeness = Completeness.ID_ONLY;
      }
    }
    switch (completeness) {
      case EMPTY:
        // Empty IIIF Resources should be null
        gen.writeNull();
        break;
      case ID_ONLY:
        // Resources with only an identifier should be a string
        gen.writeString(value.getIdentifier().toString());
        break;
      default:
        // Otherwise delegate to default serializer
        defaultSerializer.serialize(value, gen, serializers);
    }

    // Set the type back on the value if it was removed
    if (typeBackup != null) {
      ((ImageContent) value)._type = typeBackup;
    }
  }

  private static String getContainingField(JsonGenerator gen) {
    JsonStreamContext ctx = gen.getOutputContext();
    if (ctx.inArray()) {
      return ctx.getParent().getCurrentName();
    } else {
      return ctx.getCurrentName();
    }
  }
}

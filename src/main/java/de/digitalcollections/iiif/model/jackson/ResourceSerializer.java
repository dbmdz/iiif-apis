package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.ModelUtilities;
import de.digitalcollections.iiif.model.ModelUtilities.Completeness;
import de.digitalcollections.iiif.model.api.Motivation;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

class ResourceSerializer extends JsonSerializer<Resource> {

  private final static String IIIF_CONTEXT =  "http://iiif.io/api/presentation/2/context.json";
  private final JsonSerializer<Object> defaultSerializer;

  public ResourceSerializer(JsonSerializer<Object> defaultSerializer) {
    this.defaultSerializer = defaultSerializer;
  }

  @Override
  public void serialize(Resource value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    // Add @context to top-level object
    if (gen.getOutputContext().getParent() == null) {
      value._context = IIIF_CONTEXT;
    }

    // Remove @type from ImageContent if neccessary
    String containingField = getContainingField(gen);
    String typeBackup = null;
    if (ImmutableSet.of("thumbnail", "logo").contains(containingField)) {
      ImageContent imgContent = (ImageContent) value;
      typeBackup = imgContent._type;
      imgContent._type = null;
    }

    Completeness completeness = ModelUtilities.getCompleteness(value, value.getClass());
    if (Objects.equals(containingField, "canvases") && completeness == ModelUtilities.Completeness.ID_AND_TYPE) {
      // It's redundant to specify the @type here, since it's clear we have canvases from the field name
      completeness = ModelUtilities.Completeness.ID_ONLY;
    }
    if (Objects.equals(containingField, "within") && completeness == ModelUtilities.Completeness.ID_AND_TYPE) {
      // It's also redundant in these cases, since the specification prescribes a convention
      String parentType = ((Resource) gen.getCurrentValue()).getType();
      String withinType = value.getType();
      boolean skipType = (
              (parentType.equals("sc:Manifest") && withinType.equals("sc:Collection")) ||
              (parentType.equals("sc:AnnotationList") && withinType.equals("sc:Layer")) ||
              (parentType.equals("sc:Collection") && withinType.equals("sc:Collection")));
      if (skipType) {
        completeness = ModelUtilities.Completeness.ID_ONLY;
      }
    } else if (Objects.equals(containingField, "on") && completeness == ModelUtilities.Completeness.ID_AND_TYPE) {
      boolean skipType = (
            value instanceof Canvas &&
            gen.getCurrentValue() instanceof Annotation &&
            ((Annotation) gen.getCurrentValue()).getMotivation().equals(Motivation.PAINTING));
      if (skipType) {
        completeness = ModelUtilities.Completeness.ID_ONLY;
      }
    } else {
      ImmutableSet<String> skipParents = ImmutableSet.of("otherContent", "contentLayer", "ranges");
      boolean shouldSkip = (
            Arrays.asList("prev", "next", "first", "last").contains(containingField) ||
            (completeness == Completeness.ID_AND_TYPE && skipParents.contains(containingField)));
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
        gen.writeObject(value.getIdentifier().toString());
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

  @Override
  public void serializeWithType(Resource value, JsonGenerator gen, SerializerProvider serializers,
      TypeSerializer typeSer) throws IOException {
    JsonStreamContext ctx = gen.getOutputContext();
    if (ctx.getParent() == null) {
      value._context = IIIF_CONTEXT;
    }

    // Remove @type from ImageContent if neccessary
    if (ImmutableSet.of("thumbnail", "logo").contains(getContainingField(gen))) {
      ImageContent imgContent = (ImageContent) value;
      imgContent._type = null;
    }

    Completeness completeness = ModelUtilities.getCompleteness(value, value.getClass());
    if (value instanceof Canvas) {
      // Get name of the field
      String currentName = ctx.getCurrentName();

      /* Special case for "on"-fields on Image annotations on canvases: If they have only `@id` and `@type` set, we
       * skip the type to reduce the verbosity of the JSON. */
      if (Objects.equals(currentName, "on")) {
        // Is the annotation on a canvas?
        String onType = value.getType();

        // Go up two levels: on-resource -> annotation -> resource
        Resource onResource = (Resource) ctx.getParent().getParent().getCurrentValue();
        if (onResource != null && completeness == ModelUtilities.Completeness.ID_AND_TYPE && onType.equals(onResource.getType())) {
          // Only skip @type if there is no additional information besides @type and @id
          completeness = ModelUtilities.Completeness.ID_ONLY;
        }
      }
    }
    switch (completeness) {
      case EMPTY:
        // Empty IIIF Resources should be null
        gen.writeNull();
        break;
      case ID_ONLY:
      case ID_AND_TYPE:
        // Resources with only an identifier should be a string
        gen.writeObject(value.getIdentifier().toString());
        break;
      default:
        // Otherwise delegate to default serializer
        defaultSerializer.serializeWithType(value, gen, serializers, typeSer);
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

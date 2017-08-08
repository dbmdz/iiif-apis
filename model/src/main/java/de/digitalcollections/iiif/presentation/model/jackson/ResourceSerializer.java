package de.digitalcollections.iiif.presentation.model.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.presentation.model.ImageContent;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.AnnotationList;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Content;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Resource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reflections.ReflectionUtils;

class ResourceSerializer extends JsonSerializer<Resource> {
  private enum Completeness { EMPTY, ID_ONLY, ID_AND_TYPE, COMPLEX }
  private final static String IIIF_CONTEXT =  "http://iiif.io/api/presentation/2/context.json";
  private final static ImmutableSet<String> ALWAYS_SKIP_TYPE_FOR = ImmutableSet.of(
      "thumbnail", "logo");
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
    if (ALWAYS_SKIP_TYPE_FOR.contains(containingField)) {
      ImageContent imgContent = (ImageContent) value;
      typeBackup = imgContent._type;
      imgContent._type = null;
    }

    Completeness completeness = getCompleteness(value);
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
    if (ALWAYS_SKIP_TYPE_FOR.contains(getContainingField(gen))) {
      ImageContent imgContent = (ImageContent) value;
      imgContent._type = null;
    }

    Completeness completeness = getCompleteness(value);
    /* Special case for "on"-fields on Image annotations on canvases: If they have only `@id` and `@type` set, we
     * skip the type to reduce the verbosity of the JSON. */
    if (value instanceof Canvas) {
      // Get name of the field
      String currentName = ctx.getCurrentName();
      if (currentName != null && currentName.equals("on")) {
        // Is the annotation on a canvas?
        String onType = value.getType();

        // Go up two levels: on-resource -> annotation -> resource
        Resource onResource = (Resource) ctx.getParent().getParent().getCurrentValue();
        if (onResource != null && completeness == Completeness.ID_AND_TYPE && onType.equals(onResource.getType())) {
          // Only skip @type if there is no additional information besides @type and @id
          completeness = Completeness.ID_ONLY;
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

  private Completeness getCompleteness(Resource res) {
    if (res instanceof Content && !(res instanceof AnnotationList)) {
      return Completeness.COMPLEX;
    }
    Set<String> gettersWithValues = ReflectionUtils.getAllMethods(
        Resource.class,
        ReflectionUtils.withModifier(Modifier.PUBLIC),
        ReflectionUtils.withPrefix("get")).stream()
      .filter(g -> g.getAnnotation(JsonIgnore.class) == null)  // Only JSON-serializable fields
      .filter(g -> this.returnsValue(g, res))
      .map(Method::getName)
      .collect(Collectors.toSet());

    boolean hasOnlyTypeAndId = (
        gettersWithValues.size() == 2 &&
        Stream.of("getType", "getIdentifier").allMatch(gettersWithValues::contains));
    if (gettersWithValues.isEmpty()) {
      return Completeness.EMPTY;
    } else if (hasOnlyTypeAndId) {
      return Completeness.ID_AND_TYPE;
    } else if (gettersWithValues.size() == 1 && res.getIdentifier() != null) {
      return Completeness.ID_ONLY;
    } else {
      return Completeness.COMPLEX;
    }
  }

  private boolean returnsValue(Method method, Object obj) {
    try {
      return method.invoke(obj) != null;
    } catch (Exception e) {
      return false;
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

package de.digitalcollections.iiif.presentation.model.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.presentation.model.ImageContent;
import de.digitalcollections.iiif.presentation.model.service.GenericService;
import de.digitalcollections.iiif.presentation.model.service.Service;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.AnnotationList;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Layer;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Resource;
import java.io.IOException;
import java.lang.reflect.Constructor;

public class ProblemHandler extends DeserializationProblemHandler {
  private final static ImmutableSet<String> ALWAYS_IMAGE_FIELDS = ImmutableSet.of("thumbnail", "logo");

  @Override
  public JavaType handleMissingTypeId(DeserializationContext ctxt, JavaType baseType, TypeIdResolver idResolver,
      String failureMsg) throws IOException {

    String fieldName = getContainingField(ctxt.getParser());
    if (fieldName.equals("on")) {
      /* Handle "on" values that are plain URIs by deducing the type from their parent.
         Parent resource is two levels up: on-resource -> annotation -> resource */
      JsonStreamContext parent = ctxt.getParser().getParsingContext().getParent();
      if (parent != null) {
        parent = parent.getParent();
      }
      if (parent != null && parent.getCurrentValue() instanceof Resource) {
        String parentType = ((Resource) parent.getCurrentValue()).getType();
        return idResolver.typeFromId(ctxt, parentType);
      }
    } else if (ALWAYS_IMAGE_FIELDS.contains(fieldName)) {
      /* Handle fields that always contain images like thumbnail and logo */
      return ctxt.getTypeFactory().constructType(ImageContent.class);
    } else if (fieldName.equals("within")) {
      /* Handle untyped "within" resources */
      Resource obj = (Resource) ctxt.getParser().getCurrentValue();
      if (obj instanceof Manifest) {
        return ctxt.getTypeFactory().constructType(Collection.class);
      } else if (obj instanceof AnnotationList) {
        return ctxt.getTypeFactory().constructType(Layer.class);
      } else if (obj instanceof Collection) {
        return ctxt.getTypeFactory().constructType(Collection.class);
      }
    }
    return super.handleMissingTypeId(ctxt, baseType, idResolver, failureMsg);
  }

  @Override
  public JavaType handleUnknownTypeId(DeserializationContext ctxt, JavaType baseType, String subTypeId,
      TypeIdResolver idResolver, String failureMsg) throws IOException {
    if (baseType.isTypeOrSubTypeOf(Service.class)) {
      /* Handle unknown services. We do this here instead of setting a `defaultImpl` since we want
         to preserve the @context value, which would get lost.
       */
      return ctxt.getTypeFactory().constructType(GenericService.class);
    }
    return super.handleUnknownTypeId(ctxt, baseType, subTypeId, idResolver, failureMsg);
  }

  @Override
  public Object handleMissingInstantiator(DeserializationContext ctxt, Class<?> instClass, ValueInstantiator valueInsta,
      JsonParser p, String msg) throws IOException {
    /* FIXME: For some reason Jackson can't find the {@link Canvas(String)} constructor, so we do it ourselves:
     * 1. Check if the deserializer bails out on a JSON string
     * 2. Find a String-constructor on the target class
     * 3. Build the object */
    if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
      try {
        Constructor<?> constructor = instClass.getConstructor(String.class);
        return constructor.newInstance(p.getValueAsString());
      } catch (Exception e) {
        // Fall through
      }
    }
    return super.handleMissingInstantiator(ctxt, instClass, valueInsta, p, msg);
  }

  public String getContainingField(JsonParser parser) {
    final JsonStreamContext ctx = parser.getParsingContext();
    if (ctx.inArray()) {
      return ctx.getParent().getCurrentName();
    } else {
      return ctx.getCurrentName();
    }
  }
}

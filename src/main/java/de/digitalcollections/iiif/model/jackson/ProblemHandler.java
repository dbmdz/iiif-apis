package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.digitalcollections.iiif.model.OtherContent;
import de.digitalcollections.iiif.model.api.Selector;
import de.digitalcollections.iiif.model.openannotation.ContentAsText;
import de.digitalcollections.iiif.model.service.GenericService;
import de.digitalcollections.iiif.model.service.Service;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.stream.StreamSupport;

public class ProblemHandler extends DeserializationProblemHandler {
  @Override
  public JavaType handleUnknownTypeId(DeserializationContext ctxt, JavaType baseType, String subTypeId,
      TypeIdResolver idResolver, String failureMsg) throws IOException {
    if (baseType.isTypeOrSubTypeOf(Service.class)) {
      // Handle unknown services. We do this here instead of setting a `defaultImpl` since we want
      // to preserve the @context value, which would get lost.
      return ctxt.getTypeFactory().constructType(GenericService.class);
    } else if (baseType.isTypeOrSubTypeOf(Resource.class)) {
      // Handle other unknown resources
      return ctxt.getTypeFactory().constructType(OtherContent.class);
    } else if (baseType.isTypeOrSubTypeOf(Selector.class)) {
      if (ctxt.getParser().getCurrentToken() == JsonToken.START_ARRAY) {
        String type = ctxt.getParser().nextTextValue();
        if (type.equals(ContentAsText.TYPE)) {
          ctxt.getParser().finishToken();
          type = ctxt.getParser().nextTextValue();
        }
        return idResolver.typeFromId(ctxt, type);
      }
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

  @Override
  public Object handleUnexpectedToken(DeserializationContext ctxt, Class<?> targetType, JsonToken t, JsonParser p,
      String failureMsg) throws IOException {
    if (p.getCurrentName().equals("@type") && t == JsonToken.START_ARRAY) {
      // Handle multi-valued @types, only current known cases are oa:SvgSelector and oa:CssStyle
      // in combination with cnt:ContentAsText
      ObjectMapper mapper = (ObjectMapper) p.getCodec();
      String typeName = StreamSupport.stream(((ArrayNode) mapper.readTree(p)).spliterator(), false)
          .map(JsonNode::textValue)
          .filter(v -> !v.equals(ContentAsText.TYPE))
          .findFirst().get();
      if (typeName != null) {
        return typeName;
      }
    }
    return super.handleUnexpectedToken(ctxt, targetType, t, p, failureMsg);
  }
}

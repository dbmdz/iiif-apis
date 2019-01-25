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
import de.digitalcollections.iiif.model.openannotation.ContentAsText;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.geojson.Feature;

public class ProblemHandler extends DeserializationProblemHandler {

  @Override
  public Object handleMissingInstantiator(DeserializationContext ctxt, Class<?> instClass, ValueInstantiator valueInsta,
                                          JsonParser p, String msg) throws IOException {
    /* FIXME: For some reason Jackson can't find the {@link Canvas(String)} constructor, so we do it ourselves:
     * 1. Check if the deserializer bails out on a JSON string
     * 2. Find a String-constructor on the target class
     * 3. Build the object */
    try {
      // Special case for empty strings in collection fields.
      if (p.getValueAsString().isEmpty() && Collection.class.isAssignableFrom(instClass)) {
        return instClass.getConstructor().newInstance();
      } else {
        Constructor<?> constructor = instClass.getConstructor(String.class);
        return constructor.newInstance(p.getValueAsString());
      }
    } catch (Exception e) {
      // Fall through
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
          .findFirst().orElse(null);
      if (typeName != null) {
        return typeName;
      }
    }
    return super.handleUnexpectedToken(ctxt, targetType, t, p, failureMsg);
  }

  @Override
  public JavaType handleMissingTypeId(DeserializationContext ctxt, JavaType baseType, TypeIdResolver idResolver,
                                      String failureMsg) throws IOException {
    if (baseType.getRawClass() == Feature.class) {
      return idResolver.typeFromId(ctxt, "Feature");
    }
    return super.handleMissingTypeId(ctxt, baseType, idResolver, failureMsg);
  }

  @Override
  public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
    if (targetType.isEnum()) {
      String lowerCased = valueToConvert.toLowerCase();
      Optional<?> match = Arrays.stream(targetType.getEnumConstants())
          .filter(v -> v.toString().toLowerCase().equals(valueToConvert.toLowerCase()))
          .findFirst();
      if (match.isPresent()) {
        return match.get();
      }
    }
    return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg);
  }
}

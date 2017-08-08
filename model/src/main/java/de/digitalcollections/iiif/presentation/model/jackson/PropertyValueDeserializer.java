package de.digitalcollections.iiif.presentation.model.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import de.digitalcollections.iiif.presentation.model.PropertyValue;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.StreamSupport;

public class PropertyValueDeserializer extends JsonDeserializer<PropertyValue> {
  @Override
  public PropertyValue deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
    ObjectMapper mapper = (ObjectMapper) jp.getCodec();
    TreeNode node = mapper.readTree(jp);

    if (TextNode.class.isAssignableFrom(node.getClass())) {
      // Simple string
      return new PropertyValue(((TextNode) node).textValue());
    } else if (ObjectNode.class.isAssignableFrom(node.getClass())) {
      // Complex object
      String language = ((TextNode) node.get("@language")).textValue();
      String value = ((TextNode) node.get("@value")).textValue();
      return new PropertyValue(Locale.forLanguageTag(language), value);
    } else if (ArrayNode.class.isAssignableFrom(node.getClass())) {
      // Array of multiple values
      ArrayNode arr = (ArrayNode) node;
      ObjectNode curObj;
      PropertyValue propVal = new PropertyValue();
      for (int i = 0; i < arr.size(); i++) {
        if (ObjectNode.class.isAssignableFrom(arr.get(i).getClass())) {
          // Complex object
          curObj = (ObjectNode) arr.get(i);
          Locale lang = Locale.forLanguageTag(curObj.get("@language").textValue());
          TreeNode valueNode = curObj.get("@value");
          if (TextNode.class.isAssignableFrom(valueNode.getClass())) {
            // Single value
            propVal.addValues(lang, curObj.get("@value").textValue());
          } else if (valueNode instanceof ArrayNode) {
            // Multiple values
            String[] vals =  StreamSupport.stream(((ArrayNode) valueNode).spliterator(), false)
                .map(JsonNode::textValue)
                .toArray(String[]::new);
            propVal.addValues(lang, vals);
          }
        } else if (TextNode.class.isAssignableFrom(arr.get(i).getClass())) {
          // Simple string
          propVal.addValues(arr.get(i).asText());
        }
      }
      return propVal;
    } else {
      throw new IllegalArgumentException("Property values must be strings, objects or arrays");
    }
  }

}

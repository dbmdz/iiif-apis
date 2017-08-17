package de.digitalcollections.iiif.model.jackson.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableMap;
import de.digitalcollections.iiif.model.interfaces.Selector;
import de.digitalcollections.iiif.model.image.ImageApiSelector;
import de.digitalcollections.iiif.model.openannotation.SvgSelector;
import java.io.IOException;
import java.util.Map;
import java.util.stream.StreamSupport;

public class SelectorDeserializer extends JsonDeserializer<Selector> {
  private static final Map<String, Class<? extends Selector>> MAPPING =
      new ImmutableMap.Builder<String, Class<? extends Selector>>()
        .put(ImageApiSelector.TYPE, ImageApiSelector.class)
        .put(SvgSelector.TYPE, SvgSelector.class)
        .build();

  private final JsonDeserializer<Object> defaultDeserializer;

  public SelectorDeserializer(
      JsonDeserializer<Object> defaultDeserializer) {
    this.defaultDeserializer = defaultDeserializer;
  }

  public Selector deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    ObjectNode obj = mapper.readTree(p);
    String typeName;
    if (obj.get("@type").isArray()) {
      // Find the actual selector type
      typeName = StreamSupport.stream(obj.get("@type").spliterator(), false)
          .filter(v -> !v.textValue().equals("cnt:ContentAsText"))
          .findFirst().get().textValue();
      // Make @type a text value so that Jackson doesn't bail out further down the line
      obj.set("@type", new TextNode(typeName));
    } else {
      typeName = obj.get("@type").textValue();
    }
    if (MAPPING.containsKey(typeName)) {
      return mapper.treeToValue(obj, MAPPING.get(typeName));
    } else {
      throw new IllegalArgumentException("Cannot deserialize Selector.");
    }
  }
}

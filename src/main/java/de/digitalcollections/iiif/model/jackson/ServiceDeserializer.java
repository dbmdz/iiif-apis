package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.digitalcollections.iiif.model.service.AutocompleteService;
import de.digitalcollections.iiif.model.service.ContentSearchService;
import de.digitalcollections.iiif.model.service.GenericService;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.service.Service;
import java.io.IOException;
import java.util.Objects;

/** Custom deserializer for services.
 *
 * Neccessary since the type dispatching is not uniform for services,
 * sometimes we can decide by looking at @context, but other times we need to look
 * at the profile or both.
 */
public class ServiceDeserializer extends JsonDeserializer<Service> {
  @Override
  public Service deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    ObjectNode obj = mapper.readTree(p);
    String context = obj.get("@context").asText();
    String profile = obj.get("profile").asText();
    if (Objects.equals(context, ImageService.CONTEXT)) {
      return mapper.treeToValue(obj, ImageService.class);
    } else if (Objects.equals(context, ContentSearchService.CONTEXT)) {
      if (Objects.equals(profile, AutocompleteService.PROFILE)) {
        return mapper.treeToValue(obj, AutocompleteService.class);
      } else {
        return mapper.treeToValue(obj, ContentSearchService.class);
      }
    } else {
      return mapper.treeToValue(obj, GenericService.class);
    }
  }
}

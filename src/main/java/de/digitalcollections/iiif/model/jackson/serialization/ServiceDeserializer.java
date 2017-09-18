package de.digitalcollections.iiif.model.jackson.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.model.GenericService;
import de.digitalcollections.iiif.model.Service;
import de.digitalcollections.iiif.model.annex.GeoService;
import de.digitalcollections.iiif.model.annex.PhysicalDimensionsService;
import de.digitalcollections.iiif.model.auth.AccessCookieService;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.search.AutocompleteService;
import de.digitalcollections.iiif.model.search.ContentSearchService;
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
    if (isImageService(obj)) {
      return mapper.treeToValue(obj, ImageService.class);
    }

    String context = obj.get("@context").asText();
    JsonNode profileNode = obj.get("profile");
    String profile = null;
    if (profileNode != null) {
      profile = profileNode.asText();
    }
    if (Objects.equals(context, ContentSearchService.CONTEXT)) {
      if (Objects.equals(profile, AutocompleteService.PROFILE)) {
        return mapper.treeToValue(obj, AutocompleteService.class);
      } else {
        return mapper.treeToValue(obj, ContentSearchService.class);
      }
    } else if (Objects.equals(context, AccessCookieService.CONTEXT)) {
      return mapper.treeToValue(obj, AccessCookieService.class);
    } else if (Objects.equals(context, GeoService.CONTEXT)) {
      return mapper.treeToValue(obj, GeoService.class);
    } else if (Objects.equals(context, PhysicalDimensionsService.CONTEXT)) {
      return mapper.treeToValue(obj, PhysicalDimensionsService.class);
    } else {
      return mapper.treeToValue(obj, GenericService.class);
    }
  }

  public boolean isImageService(ObjectNode node) {
    JsonNode ctxNode = node.get("@context");
    JsonNode profileNode = node.get("profile");
    if (ctxNode != null && ImageService.CONTEXT.equals(ctxNode.textValue())) {
      return true;
    } else if (profileNode != null) {
      return ImmutableSet.of(ImageApiProfile.LEVEL_ONE.getIdentifier().toString(),
                             ImageApiProfile.LEVEL_TWO.getIdentifier().toString(),
                             ImageApiProfile.LEVEL_ZERO.getIdentifier().toString())
            .contains(profileNode.asText());
    } else {
      return false;
    }
  }
}

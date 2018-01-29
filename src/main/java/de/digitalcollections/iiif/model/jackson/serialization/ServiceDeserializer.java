package de.digitalcollections.iiif.model.jackson.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
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
import de.digitalcollections.iiif.model.image.Size;
import de.digitalcollections.iiif.model.image.TileInfo;
import de.digitalcollections.iiif.model.search.AutocompleteService;
import de.digitalcollections.iiif.model.search.ContentSearchService;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/** Custom deserializer for services.
 *
 * Necessary since the type dispatching is not uniform for services,
 * sometimes we can decide by looking at @context, but other times we need to look
 * at the profile or both.
 */
public class ServiceDeserializer extends JsonDeserializer<Service> {

  @Override
  public Service deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
      return new GenericService(null, p.getValueAsString());
    }
    ObjectNode obj = mapper.readTree(p);
    if (isV1ImageService(obj)) {
      return parseV1Service(obj);
    } else if (isImageService(obj)) {
      return mapper.treeToValue(obj, ImageService.class);
    }

    String context = null;
    if (obj.has("@context")) {
      context = obj.get("@context").asText();
    }
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

  private ImageService parseV1Service(ObjectNode obj) {
    ImageService service = new ImageService(obj.get("@id").asText());
    if (obj.has("@context")) {
      service.setContext(URI.create(obj.get("@context").asText()));
    }
    if (obj.has("profile")) {
      service.addProfile(ImageApiProfile.fromUrl(obj.get("profile").asText()));
    } else if (obj.has("dcterms:conformsTo")) {
      service.addProfile(ImageApiProfile.fromUrl(obj.get("dcterms:conformsTo").asText()));
    }
    if (obj.has("width")) {
      service.setWidth(obj.get("width").asInt());
    }
    if (obj.has("height")) {
      service.setHeight(obj.get("height").asInt());
    }
    if (obj.has("scale_factors") && (service.getWidth() != null && service.getHeight() != null)) {
      obj.withArray("scale_factors").forEach(
              fnode -> service.addSize(new Size(service.getWidth() / fnode.asInt(),
                                                service.getHeight() / fnode.asInt()))
      );
    }
    if (obj.has("tile_width") && obj.has("scale_factors")) {
      TileInfo tinfo = new TileInfo(obj.get("tile_width").asInt());
      obj.withArray("scale_factors").forEach(
              fnode -> tinfo.addScaleFactor(fnode.asInt()));
      if (obj.has("tile_height")) {
        tinfo.setHeight(obj.get("tile_height").intValue());
      }
      service.addTile(tinfo);
    }
    if (obj.has("formats") || obj.has("qualities")) {
      ImageApiProfile profile = new ImageApiProfile();
      if (obj.has("formats")) {
        obj.withArray("formats").forEach(
                f -> profile.addFormat(ImageApiProfile.Format.valueOf(f.asText().toUpperCase())));
      }
      if (obj.has("qualities")) {
        List<String> qualities = StreamSupport.stream(
                obj.withArray("qualities").spliterator(), false)
                .map(q -> q.asText().equals("native") ? "default" : q.asText())
                .map(q -> q.equals("grey") ? "gray" : q)
                .collect(Collectors.toList());
        qualities.forEach(q -> profile.addQuality(ImageApiProfile.Quality.valueOf(q.toUpperCase())));
      }
      service.addProfile(profile);
    }
    return service;
  }

  private boolean isV1ImageService(ObjectNode node) {
    String profile;
    if (node.has("profile")) {
      profile = node.get("profile").asText();
    } else if (node.has("dcterms:conformsTo")) {
      profile = node.get("dcterms:conformsTo").asText();
      profile = profile.replace("conformance", "compliance");
    } else {
      return false;
    }
    return ImageApiProfile.V1_PROFILES.contains(profile);
  }

  public boolean isImageService(ObjectNode node) {
    JsonNode ctxNode = node.get("@context");
    JsonNode profileNode = node.get("profile");
    if (ctxNode != null && ImageService.CONTEXT.equals(ctxNode.textValue())) {
      return true;
    } else if (profileNode != null) {
      return ImageApiProfile.V1_PROFILES.contains(profileNode.asText());
    } else {
      return false;
    }
  }
}

package de.digitalcollections.iiif.model.jackson.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.digitalcollections.iiif.model.Profile;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import java.io.IOException;
import java.net.URI;
import java.util.stream.Stream;

public class ProfileDeserializer extends JsonDeserializer<Profile> {

  private final JsonDeserializer<Object> defaultDeserializer;

  public ProfileDeserializer(JsonDeserializer<Object> defaultDeserializer) {
    this.defaultDeserializer = defaultDeserializer;
  }

  private boolean isImageApiProfile(final String profile) {
    return Stream.of(ImageApiProfile.LEVEL_ZERO, ImageApiProfile.LEVEL_ONE, ImageApiProfile.LEVEL_TWO)
      .map(p -> p.getIdentifier().toString())
      .anyMatch(profile::equals) || ImageApiProfile.V1_PROFILES.contains(profile);
  }

  @Override
  public Profile deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
      if (isImageApiProfile(p.getValueAsString())) {
        return ImageApiProfile.fromUrl(p.getValueAsString());
      } else {
        return new Profile(URI.create(p.getValueAsString()));
      }
    } else if (p.getCurrentToken() == JsonToken.START_OBJECT) {
      return p.getCodec().readValue(p, ImageApiProfile.class);
    } else {
      return (Profile) defaultDeserializer.deserialize(p, ctxt);
    }
  }
}

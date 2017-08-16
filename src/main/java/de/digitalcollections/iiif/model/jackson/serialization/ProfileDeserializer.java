package de.digitalcollections.iiif.model.jackson.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.digitalcollections.iiif.model.Profile;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import java.io.IOException;
import java.net.URI;

public class ProfileDeserializer extends JsonDeserializer<Profile> {
  private final JsonDeserializer<Object> defaultDeserializer;

  public ProfileDeserializer(JsonDeserializer<Object> defaultDeserializer) {
    this.defaultDeserializer = defaultDeserializer;
  }

  @Override
  public Profile deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
      if (p.getValueAsString().startsWith("http://iiif.io/api/image")) {
        return new ImageApiProfile(p.getValueAsString());
      } else {
        return new Profile(URI.create(p.getValueAsString()));
      }
    } else if (p.getCurrentToken() == JsonToken.START_OBJECT){
      return p.getCodec().readValue(p, ImageApiProfile.class);
    } else {
      return (Profile) defaultDeserializer.deserialize(p, ctxt);
    }
  }
}

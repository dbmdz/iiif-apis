package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.digitalcollections.iiif.model.ModelUtilities;
import de.digitalcollections.iiif.model.ModelUtilities.Completeness;
import de.digitalcollections.iiif.model.Profile;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import java.io.IOException;

public class ProfileSerializer extends JsonSerializer<Profile> {
  private final JsonSerializer<Object> defaultSerialier;

  public ProfileSerializer(JsonSerializer<Object> defaultSerialier) {
    this.defaultSerialier = defaultSerialier;
  }

  @Override
  public void serialize(Profile value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    Completeness completeness = ModelUtilities.getCompleteness(value, Profile.class);
    if (completeness == Completeness.ID_ONLY || (value instanceof ImageApiProfile && completeness == Completeness.ID_AND_TYPE)) {
      gen.writeString(value.getIdentifier().toString());
    } else {
      defaultSerialier.serialize(value, gen, serializers);
    }
  }
}

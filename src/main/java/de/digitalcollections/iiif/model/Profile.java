package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import java.net.URI;

/**
 * A profile for a {@link OtherContent} or {@link Service}.
 *
 * For image services, please use the more specific {@link ImageApiProfile} that has useful pre-defined constants
 * for the available Image API profiles.
 */
public class Profile {

  @JsonProperty("@id")
  private final URI identifier;

  public Profile(URI identifier) {
    this.identifier = identifier;
  }

  public URI getIdentifier() {
    return identifier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (this.getClass() != o.getClass()) {
      return false;
    }
    Profile profile = (Profile) o;
    return identifier.equals(profile.identifier);
  }

  @Override
  public int hashCode() {
    return identifier.hashCode();
  }
}

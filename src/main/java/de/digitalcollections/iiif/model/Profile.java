package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import java.net.URI;

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
    if (o instanceof ImageApiProfile && this instanceof ImageApiProfile) {
      return ((ImageApiProfile) this).equals((ImageApiProfile) o);
    } else if (this.getClass() != o.getClass()) {
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

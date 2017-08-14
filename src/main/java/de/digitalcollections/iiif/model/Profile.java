package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}

package de.digitalcollections.iiif.presentation.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URI;

public enum ImageAPIProfile {
  LEVEL_ZERO("http://iiif.io/api/image/2/level0.json"),
  LEVEL_ONE("http://iiif.io/api/image/2/level1.json"),
  LEVEL_TWO("http://iiif.io/api/image/2/level2.json");

  private final URI uri;

  ImageAPIProfile(String url) {
    this.uri = URI.create(url);
  }


  @Override
  @JsonValue
  public String toString() {
    return uri.toString();
  }

  public URI getUri() {
    return uri;
  }
}

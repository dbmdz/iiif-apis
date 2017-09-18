package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URI;

public enum AuthPattern {
  LOGIN("http://iiif.io/api/auth/1/login"),
  CLICKTHROUGH("http://iiif.io/api/auth/1/clickthrough"),
  KIOSK("http://iiif.io/api/auth/1/kiosk"),
  EXTERNAL("http://iiif.io/api/auth/1/external");

  private URI uri;

  @JsonCreator
  AuthPattern(String uri) {
    this.uri = URI.create(uri);
  }

  @JsonValue
  public URI getUri() {
    return uri;
  }
}

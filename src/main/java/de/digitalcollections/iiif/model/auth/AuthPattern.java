package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URI;

/**
 * There are four interaction patterns by which the client can obtain an access cookie, each identified by a profile URI.
 *
 * See http://iiif.io/api/auth/1.0/#service-description
 */
public enum AuthPattern {
  /**
   * The user will be required to log in using a separate window with a UI provided by an external authentication system.
   *
   * See http://iiif.io/api/auth/1.0/#login-interaction-pattern
   */
  LOGIN("http://iiif.io/api/auth/1/login"),
  /**
   * The user will be required to click a button within the client using content provided in the service description.
   *
   * See http://iiif.io/api/auth/1.0/#clickthrough-interaction-pattern
   */
  CLICKTHROUGH("http://iiif.io/api/auth/1/clickthrough"),
  /**
   * The user will not be required to interact with an authentication system, the client is expected to use the access
   * cookie service automatically.
   *
   * See http://iiif.io/api/auth/1.0/#kiosk-interaction-pattern
   */
  KIOSK("http://iiif.io/api/auth/1/kiosk"),
  /**
   * The user is expected to have already acquired the appropriate cookie, and the access cookie service will not be
   * used at all.
   *
   * See http://iiif.io/api/auth/1.0/#external-interaction-pattern
   */
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

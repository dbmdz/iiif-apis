package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.iiif.model.Service;
import java.net.URI;

/**
 * The client uses this service to obtain an access token which it then uses when requesting Description Resources.
 *
 * A request to the access token service must include any cookies for the content domain acquired from the user’s
 * interaction with the corresponding access cookie service, so that the server can issue the access token.
 *
 * See http://iiif.io/api/auth/1.0/#access-token-service
 */
public class AccessTokenService extends Service implements AuthService {
  public static final String PROFILE = "http://iiif.io/api/auth/1/token";

  @JsonCreator
  public AccessTokenService(@JsonProperty("@id") URI identifier) {
    super(null);
    this.setIdentifier(identifier);
  }

  public AccessTokenService(String identifier) {
    this(URI.create(identifier));
  }

  public String getProfile() {
    return PROFILE;
  }
}

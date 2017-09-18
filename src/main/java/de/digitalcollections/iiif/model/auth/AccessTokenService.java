package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.iiif.model.Service;
import java.net.URI;

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

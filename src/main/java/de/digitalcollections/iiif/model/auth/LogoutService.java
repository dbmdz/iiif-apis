package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.digitalcollections.iiif.model.Service;
import java.net.URI;

@JsonTypeName(LogoutService.PROFILE)
public class LogoutService extends Service implements AuthService {
  public static final String PROFILE = "http://iiif.io/api/auth/1/logout";

  @JsonCreator
  public LogoutService(@JsonProperty("@id") URI identifier) {
    super(null);
    this.setIdentifier(identifier);
  }

  public LogoutService(String identifier) {
    this(URI.create(identifier));
  }

  public String getProfile() {
    return PROFILE;
  }
}

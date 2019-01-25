package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.digitalcollections.iiif.model.Service;
import java.net.URI;

/**
 * In the case of the Login interaction pattern, the client will need to know if and where the user can go to log out.
 *
 * For example, the user may wish to close their session on a public terminal, or to log in again with a different account.
 *
 * See http://iiif.io/api/auth/1.0/#logout-service
 */
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

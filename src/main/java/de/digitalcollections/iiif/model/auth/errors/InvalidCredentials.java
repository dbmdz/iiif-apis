package de.digitalcollections.iiif.model.auth.errors;

public class InvalidCredentials extends AccessTokenError {
  public static final String TYPE = "invalidCredentials";

  public InvalidCredentials(String description) {
    setDescription(description);
  }

  @Override
  public String getType() {
    return TYPE;
  }
}

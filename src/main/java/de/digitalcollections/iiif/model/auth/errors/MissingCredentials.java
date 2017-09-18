package de.digitalcollections.iiif.model.auth.errors;

public class MissingCredentials extends AccessTokenError {
  public static final String TYPE = "missingCredentials";

  public MissingCredentials(String description) {
    setDescription(description);
  }

  @Override
  public String getType() {
    return TYPE;
  }
}

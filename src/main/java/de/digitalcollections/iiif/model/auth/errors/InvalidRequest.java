package de.digitalcollections.iiif.model.auth.errors;

public class InvalidRequest extends AccessTokenError {
  public static final String TYPE = "invalidRequest";

  public InvalidRequest(String description) {
    setDescription(description);
  }

  @Override
  public String getType() {
    return TYPE;
  }
}

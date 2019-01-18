package de.digitalcollections.iiif.model.auth.errors;

public class InvalidOrigin extends AccessTokenError {

  public static final String TYPE = "invalidOrigin";

  public InvalidOrigin(String description) {
    setDescription(description);
  }

  @Override
  public String getType() {
    return TYPE;
  }
}

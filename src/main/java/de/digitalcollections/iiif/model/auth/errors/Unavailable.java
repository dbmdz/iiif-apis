package de.digitalcollections.iiif.model.auth.errors;

public class Unavailable extends AccessTokenError {
  public static final String TYPE = "unavailable";

  public Unavailable(String description) {
    setDescription(description);
  }

  @Override
  public String getType() {
    return TYPE;
  }
}

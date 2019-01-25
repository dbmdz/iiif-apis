package de.digitalcollections.iiif.model.auth.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(
    use = Id.NAME,
    include = As.EXISTING_PROPERTY,
    property = "error")
@JsonSubTypes({
    @Type(name = InvalidCredentials.TYPE, value = InvalidCredentials.class),
    @Type(name = InvalidOrigin.TYPE, value = InvalidOrigin.class),
    @Type(name = InvalidRequest.TYPE, value = InvalidRequest.class),
    @Type(name = MissingCredentials.TYPE, value = MissingCredentials.class),
    @Type(name = Unavailable.TYPE, value = Unavailable.class)})
@JsonIgnoreProperties({"suppressed", "stackTrace"})
public abstract class AccessTokenError extends Exception {

  private String description;

  @JsonProperty("error")
  public abstract String getType();

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}

package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;

public class AccessToken {
  @JsonProperty("accessToken")
  private String token;

  private Duration expiresIn;

  @JsonCreator
  public AccessToken(@JsonProperty("accessToken") String token, @JsonProperty("expiresIn") Duration expiresIn) {
    this.token = token;
    this.expiresIn = expiresIn;
  }

  public String getToken() {
    return token;
  }

  public Integer getExpiresIn() {
    return Math.toIntExact(expiresIn.toMillis() / 1000);
  }

  @JsonIgnore
  public Duration getExpiresInDuration() {
    return expiresIn;
  }
}

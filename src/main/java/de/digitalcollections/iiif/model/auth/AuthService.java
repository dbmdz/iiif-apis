package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "profile")
@JsonSubTypes({
    @Type(name = AccessTokenService.PROFILE, value = AccessTokenService.class),
    @Type(name = LogoutService.PROFILE, value = LogoutService.class)})
public interface AuthService {
}

package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.Service;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The client uses this service to obtain a cookie that will be used when interacting with content
 * such as images, and with the access token service.
 *
 * <p>There are several different interaction patterns in which the client will use this service,
 * based on the user interface that must be rendered for the user, indicated by a profile URI. The
 * client obtains the link to the access cookie service from a service block in a description of the
 * protected resource.
 *
 * <p>See http://iiif.io/api/auth/1.0/#access-cookie-service
 */
@JsonPropertyOrder({
  "@context",
  "@id",
  "profile",
  "label",
  "header",
  "description",
  "confirmLabel",
  "failureHeader",
  "failureDescription",
  "service"
})
public class AccessCookieService extends Service {

  public static final String CONTEXT = "http://iiif.io/api/auth/1/context.json";

  @JsonProperty("profile")
  private AuthPattern authPattern;

  private PropertyValue confirmLabel;

  private PropertyValue header;

  private PropertyValue description;

  private PropertyValue failureHeader;

  private PropertyValue failureDescription;

  @JsonProperty("service")
  private List<AuthService> services;

  @JsonCreator
  public AccessCookieService(
      @JsonProperty("@id") String identifier, @JsonProperty("profile") AuthPattern pattern) {
    this(identifier == null ? null : URI.create(identifier), pattern);
  }

  /**
   * Create a new access cookie service from an (optional) identifier and an authentication pattern.
   *
   * @param identifier (optional) identifier
   * @param pattern authentication pattern
   * @throws IllegalArgumentException If the auth pattern is not "external" and no identifier is
   *     provided.
   */
  public AccessCookieService(URI identifier, AuthPattern pattern) throws IllegalArgumentException {
    super(URI.create(CONTEXT));
    if (identifier == null && pattern != AuthPattern.EXTERNAL) {
      throw new IllegalArgumentException("Identifier must be present!");
    }
    this.setIdentifier(identifier);
    this.authPattern = pattern;
  }

  public AuthPattern getAuthPattern() {
    return authPattern;
  }

  public PropertyValue getConfirmLabel() {
    return confirmLabel;
  }

  @JsonIgnore
  public String getConfirmLabelString() {
    return confirmLabel.getFirstValue();
  }

  public void setConfirmLabel(PropertyValue confirmLabel) {
    this.confirmLabel = confirmLabel;
  }

  public void setConfirmLabel(String label) {
    setConfirmLabel(new PropertyValue(label));
  }

  public PropertyValue getHeader() {
    return header;
  }

  @JsonIgnore
  public String getHeaderString() {
    return header.getFirstValue();
  }

  public void setHeader(PropertyValue header) {
    this.header = header;
  }

  public void setHeader(String header) {
    setHeader(new PropertyValue(header));
  }

  public PropertyValue getDescription() {
    return description;
  }

  @JsonIgnore
  public String getDescriptionString() {
    return description.getFirstValue();
  }

  public void setDescription(PropertyValue description) {
    this.description = description;
  }

  public void setDescription(String description) {
    setDescription(new PropertyValue(description));
  }

  public PropertyValue getFailureHeader() {
    return failureHeader;
  }

  @JsonIgnore
  public String getFailureHeaderString() {
    return failureHeader.getFirstValue();
  }

  public void setFailureHeader(PropertyValue failureHeader) {
    this.failureHeader = failureHeader;
  }

  public void setFailureHeader(String failureHeader) {
    setFailureHeader(new PropertyValue(failureHeader));
  }

  public PropertyValue getFailureDescription() {
    return failureDescription;
  }

  @JsonIgnore
  public String getFailureDescriptionString() {
    return failureDescription.getFirstValue();
  }

  public void setFailureDescription(PropertyValue failureDescription) {
    this.failureDescription = failureDescription;
  }

  public void setFailureDescription(String failureDescription) {
    setFailureDescription(new PropertyValue(failureDescription));
  }

  public List<AuthService> getServices() {
    return services;
  }

  public void setServices(List<AuthService> services) {
    this.services = services;
  }

  public void addService(AuthService first, AuthService... rest) {
    if (this.services == null) {
      this.services = new ArrayList<>();
    }
    this.services.addAll(Lists.asList(first, rest));
  }
}

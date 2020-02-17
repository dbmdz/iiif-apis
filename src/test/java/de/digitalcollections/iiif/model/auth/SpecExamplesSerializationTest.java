package de.digitalcollections.iiif.model.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.auth.errors.InvalidCredentials;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Feature;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.image.Size;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Collections;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class SpecExamplesSerializationTest {

  private ObjectMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  private String readFromResources(String filename) throws IOException {
    return Resources.toString(
        Resources.getResource("spec/auth/" + filename), Charset.defaultCharset());
  }

  private void assertSerializationEqualsSpec(Object obj, String specFilename)
      throws IOException, JSONException {
    String specJson = readFromResources(specFilename);
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    JSONAssert.assertEquals(specJson, json, true);
  }

  @Test
  public void testClickthroughPattern() throws IOException, JSONException {
    AccessCookieService service =
        new AccessCookieService(
            "https://authentication.example.org/clickthrough", AuthPattern.CLICKTHROUGH);
    service.setLabel("Terms of Use for Example Institution");
    service.setHeader("Restricted Material with Terms of Use");
    service.setDescription("<span>... terms of use ... </span>");
    service.setConfirmLabel("I Agree");
    service.setFailureHeader("Terms of Use Not Accepted");
    service.setFailureDescription("You must accept the terms of use to see the content.");
    service.setServices(Collections.emptyList());
    assertSerializationEqualsSpec(service, "cookieClickthrough.json");
  }

  @Test
  public void testExternalPattern() throws IOException, JSONException {
    AccessCookieService service = new AccessCookieService((URI) null, AuthPattern.EXTERNAL);
    service.setLabel("External Authentication Required");
    service.setFailureHeader("Restricted Material");
    service.setFailureDescription("This material is not viewable without prior agreement");
    service.setServices(Collections.emptyList());
    assertSerializationEqualsSpec(service, "cookieExternal.json");
  }

  @Test
  public void testKioskPattern() throws IOException, JSONException {
    AccessCookieService service =
        new AccessCookieService(
            "https://authentication.example.org/cookiebaker", AuthPattern.KIOSK);
    service.setLabel("Internal cookie granting service");
    service.setFailureHeader("Ooops!");
    service.setFailureDescription("Call Bob at ext. 1234 to reboot the cookie server");
    service.setServices(Collections.emptyList());
    assertSerializationEqualsSpec(service, "cookieKiosk.json");
  }

  @Test
  public void testLoginPattern() throws IOException, JSONException {
    AccessCookieService service =
        new AccessCookieService("https://authentication.example.org/login", AuthPattern.LOGIN);
    service.setLabel("Login to Example Institution");
    service.setHeader("Please Log In");
    service.setDescription(
        "Example Institution requires that you log in with your example account to view this content.");
    service.setConfirmLabel("Login");
    service.setFailureHeader("Authentication Failed");
    service.setFailureDescription("<a href=\"http://example.org/policy\">Access Policy</a>");
    service.addService(new AccessTokenService("https://authentication.example.org/token"));
    assertSerializationEqualsSpec(service, "cookieLogin.json");
  }

  @Test
  public void testErrorCondition() throws IOException, JSONException {
    InvalidCredentials err =
        new InvalidCredentials("The request had credentials that are not valid for the service.");
    assertSerializationEqualsSpec(err, "errorCondition.json");
  }

  @Test
  public void testImageInfoWithAuth() throws IOException, JSONException {
    ImageService service =
        new ImageService("https://www.example.org/images/image1", ImageApiProfile.LEVEL_TWO);
    service.setWidth(600);
    service.setHeight(400);
    service.addSize(new Size(150, 100), new Size(600, 400));
    ImageApiProfile profile = new ImageApiProfile();
    profile.addFormat(Format.GIF, Format.PDF);
    profile.addQuality(Quality.COLOR, Quality.GRAY);
    profile.addFeature(Feature.CANONICAL_LINK_HEADER, Feature.ROTATION_ARBITRARY);
    service.addProfile(profile);

    AccessCookieService authService =
        new AccessCookieService("https://authentication.example.org/login", AuthPattern.LOGIN);
    authService.setLabel("Login to Example Institution");
    authService.addService(
        new AccessTokenService("https://authentication.example.org/token"),
        new LogoutService("https://authentication.example.org/logout"));
    ((LogoutService) authService.getServices().get(1)).setLabel("Logout from Example Institution");
    service.addService(authService);

    assertSerializationEqualsSpec(service, "imageInfoWithAuth.json");
  }

  @Test
  public void testLoginWithLogout() throws IOException, JSONException {
    AccessCookieService authService =
        new AccessCookieService("https://authentication.example.org/login", AuthPattern.LOGIN);
    authService.setLabel("Login to Example Institution");
    authService.addService(
        new AccessTokenService("https://authentication.example.org/token"),
        new LogoutService("https://authentication.example.org/logout"));
    ((LogoutService) authService.getServices().get(1)).setLabel("Logout from Example Institution");
    assertSerializationEqualsSpec(authService, "loginWithLogout.json");
  }

  @Test
  public void testTokenResponse() throws IOException, JSONException {
    AccessToken token = new AccessToken("TOKEN_HERE", Duration.ofSeconds(3600));
    assertSerializationEqualsSpec(token, "tokenResponse.json");
  }
}

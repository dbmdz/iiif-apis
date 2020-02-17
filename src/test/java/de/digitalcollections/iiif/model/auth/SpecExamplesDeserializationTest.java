package de.digitalcollections.iiif.model.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.auth.errors.AccessTokenError;
import de.digitalcollections.iiif.model.auth.errors.InvalidCredentials;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpecExamplesDeserializationTest {

  private ObjectMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  private <T> T readFromResources(String filename, Class<T> clz) throws IOException {
    return mapper.readValue(Resources.getResource("spec/auth/" + filename), clz);
  }

  @Test
  public void testCookieServiceWithLoginPattern() throws IOException {
    AccessCookieService service = readFromResources("cookieLogin.json", AccessCookieService.class);
    assertThat(service.getAuthPattern()).isEqualTo(AuthPattern.LOGIN);
    assertThat(service.getLabelString()).isEqualTo("Login to Example Institution");
    assertThat(service.getHeaderString()).isEqualTo("Please Log In");
    assertThat(service.getFailureDescriptionString())
        .isEqualTo("<a href=\"http://example.org/policy\">Access Policy</a>");
    assertThat(service.getServices()).hasSize(1);
    assertThat(service.getServices().get(0)).isInstanceOf(AccessTokenService.class);
    assertThat(((AccessTokenService) service.getServices().get(0)).getIdentifier().toString())
        .isEqualTo("https://authentication.example.org/token");
  }

  @Test
  public void testCookieServiceWithKioskPattern() throws IOException {
    AccessCookieService service = readFromResources("cookieKiosk.json", AccessCookieService.class);
    assertThat(service.getAuthPattern()).isEqualTo(AuthPattern.KIOSK);
  }

  @Test
  public void testCookieServiceWithClickthroughPattern() throws IOException {
    AccessCookieService service =
        readFromResources("cookieClickthrough.json", AccessCookieService.class);
    assertThat(service.getAuthPattern()).isEqualTo(AuthPattern.CLICKTHROUGH);
  }

  @Test
  public void testCookieServiceWithExternalPattern() throws IOException {
    AccessCookieService service =
        readFromResources("cookieExternal.json", AccessCookieService.class);
    assertThat(service.getAuthPattern()).isEqualTo(AuthPattern.EXTERNAL);
  }

  @Test
  public void testErrorCondition() throws IOException {
    AccessTokenError err = readFromResources("errorCondition.json", AccessTokenError.class);
    assertThat(err).isInstanceOf(InvalidCredentials.class);
    assertThat(err.getDescription())
        .isEqualTo("The request had credentials that are not valid for the service.");
  }

  @Test
  public void testImageInfoWithAuth() throws IOException {
    ImageService service = readFromResources("imageInfoWithAuth.json", ImageService.class);
    assertThat(service.getServices()).hasSize(1);
    assertThat(service.getServices().get(0)).isInstanceOf(AccessCookieService.class);
    AccessCookieService authService = (AccessCookieService) service.getServices().get(0);
    assertThat(authService.getAuthPattern()).isEqualTo(AuthPattern.LOGIN);
    assertThat(authService.getServices()).hasSize(2);
    assertThat(authService.getServices().get(0)).isInstanceOf(AccessTokenService.class);
    assertThat(authService.getServices().get(1)).isInstanceOf(LogoutService.class);
  }

  @Test
  public void testLoginWithLogout() throws IOException {
    AccessCookieService authService =
        readFromResources("loginWithLogout.json", AccessCookieService.class);
    assertThat(authService.getServices()).hasSize(2);
    assertThat(authService.getServices().get(0)).isInstanceOf(AccessTokenService.class);
    assertThat(authService.getServices().get(1)).isInstanceOf(LogoutService.class);
  }

  @Test
  public void testTokenResponse() throws IOException {
    AccessToken token = readFromResources("tokenResponse.json", AccessToken.class);
    assertThat(token.getToken()).isEqualTo("TOKEN_HERE");
    assertThat(token.getExpiresInDuration().toMillis()).isEqualTo(3600 * 1000);
  }
}

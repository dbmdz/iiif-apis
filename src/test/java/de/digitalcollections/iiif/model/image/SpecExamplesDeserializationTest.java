package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.annex.GeoService;
import de.digitalcollections.iiif.model.annex.PhysicalDimensionsService;
import de.digitalcollections.iiif.model.annex.PhysicalDimensionsService.Unit;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Feature;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecExamplesDeserializationTest {

  private ObjectMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  private <T> T readFromResources(String filename, Class<T> clz) throws IOException {
    return mapper.readValue(
      Resources.getResource("spec/image/" + filename), clz);
  }

  @Test
  public void testFullResponse() throws Exception {
    ImageService imageService = readFromResources("fullResponse.json", ImageService.class);
    assertThat(imageService)
      .hasFieldOrPropertyWithValue("width", 6000)
      .hasFieldOrPropertyWithValue("height", 4000);
    assertThat(imageService.getIdentifier().toString())
      .isEqualTo("http://www.example.org/image-service/abcd1234/1E34750D-38DB-4825-A38A-B60A345E591C");
    assertThat(imageService.getSizes()).containsExactly(
      new Size(150, 100),
      new Size(600, 400),
      new Size(3000, 2000));
    assertThat(imageService.getTiles().get(0).getWidth()).isEqualTo(512);
    assertThat(imageService.getTiles().get(0).getScaleFactors()).containsExactly(1, 2, 4);
    assertThat(imageService.getTiles().get(1))
      .hasFieldOrPropertyWithValue("width", 1024)
      .hasFieldOrPropertyWithValue("height", 2048);
    assertThat(imageService.getTiles().get(1).getScaleFactors()).containsExactly(8, 16);
    assertThat(imageService.getAttribution().getLocalizations())
      .containsExactly(Locale.ENGLISH, Locale.forLanguageTag("cy"));
    assertThat(imageService.getAttribution().getValues(Locale.ENGLISH))
      .containsExactly("<span>Provided by Example Organization</span>");
    assertThat(imageService.getAttribution().getValues(Locale.forLanguageTag("cy")))
      .containsExactly("<span>Darparwyd gan Enghraifft Sefydliad</span>");
    assertThat(imageService.getLogos()).hasSize(1);
    assertThat(imageService.getLogos().get(0).getServices().get(0).getProfiles())
      .containsExactly(ImageApiProfile.LEVEL_TWO);
    assertThat(imageService.getLicenses().stream().map(URI::toString))
      .containsExactly("http://example.org/rights/license1.html",
        "http://rightsstatements.org/vocab/InC-EDU/1.0/");
    assertThat(imageService.getProfiles().get(0)).isEqualTo(ImageApiProfile.LEVEL_TWO);
    ImageApiProfile complexProfile = (ImageApiProfile) imageService.getProfiles().get(1);
    assertThat(complexProfile.getFormats())
      .containsExactlyInAnyOrder(Format.GIF, Format.PDF);
    assertThat(complexProfile.getQualities())
      .containsExactlyInAnyOrder(Quality.COLOR, Quality.GRAY);
    assertThat(complexProfile.getFeatures()).containsExactlyInAnyOrder(
      Feature.CANONICAL_LINK_HEADER, Feature.ROTATION_ARBITRARY, Feature.PROFILE_LINK_HEADER,
      new Feature("http://example.com/feature/"));

    assertThat(imageService.getServices().get(0)).isInstanceOf(PhysicalDimensionsService.class);
    PhysicalDimensionsService physService = (PhysicalDimensionsService) imageService.getServices().get(0);
    assertThat(physService)
      .hasFieldOrPropertyWithValue("physicalScale", 0.0025)
      .hasFieldOrPropertyWithValue("physicalUnits", Unit.INCHES);

    assertThat(imageService.getServices().get(1)).isInstanceOf(GeoService.class);
    GeoService geoService = (GeoService) imageService.getServices().get(1);
    assertThat(geoService.getIdentifier().toString()).isEqualTo("http://www.example.org/geojson/paris.json");
  }
}

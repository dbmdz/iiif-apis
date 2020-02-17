package de.digitalcollections.iiif.model.annex;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.GenericService;
import de.digitalcollections.iiif.model.Profile;
import de.digitalcollections.iiif.model.annex.PhysicalDimensionsService.Unit;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Feature;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.image.Size;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.geojson.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpecExamplesDeserializationTest {

  private ObjectMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  private <T> T readFromResources(String filename, Class<T> clz) throws IOException {
    return mapper.readValue(Resources.getResource("spec/annex/" + filename), clz);
  }

  @Test
  public void testAdditionalInfo() throws Exception {
    ImageService service = readFromResources("additionalInfo.json", ImageService.class);
    assertThat(service.getIdentifier().toString())
        .isEqualTo("http://www.example.org/image-service/abcd1234");
    assertThat(service)
        .hasFieldOrPropertyWithValue("width", 6000)
        .hasFieldOrPropertyWithValue("height", 4000);
    assertThat(service.getSizes())
        .containsExactly(new Size(150, 100), new Size(600, 400), new Size(3000, 2000));
    assertThat(service.getTiles().get(0))
        .hasFieldOrPropertyWithValue("width", 512)
        .hasFieldOrPropertyWithValue("scaleFactors", Arrays.asList(1, 2, 4, 8, 16));

    assertThat(service.getProfiles()).hasSize(2);
    assertThat(service.getProfiles().get(0)).isEqualTo(ImageApiProfile.LEVEL_TWO);
    assertThat(service.getProfiles().get(1)).isInstanceOf(ImageApiProfile.class);
    ImageApiProfile complexProfile = (ImageApiProfile) service.getProfiles().get(1);
    assertThat(complexProfile.getFormats()).containsExactlyInAnyOrder(Format.GIF, Format.PDF);
    assertThat(complexProfile.getQualities())
        .containsExactlyInAnyOrder(Quality.COLOR, Quality.GRAY);
    assertThat(complexProfile.getFeatures())
        .containsExactlyInAnyOrder(
            Feature.CANONICAL_LINK_HEADER,
            Feature.ROTATION_ARBITRARY,
            new Feature("http://example.com/feature"));
  }

  @Test
  public void testEmbeddedService() throws Exception {
    ImageService service = readFromResources("embeddedService.json", ImageService.class);
    assertThat(service.getAttributionString()).isEqualTo("Provided by Example Organization");
    assertThat(service.getLogos()).hasSize(1);
    assertThat(service.getLogos().get(0).getIdentifier().toString())
        .isEqualTo("http://example.org/image-service/logo/full/full/0/default.png");
    assertThat(service.getLogos().get(0).getServices().get(0)).isInstanceOf(ImageService.class);
    ImageService imageService = (ImageService) service.getLogos().get(0).getServices().get(0);
    assertThat(imageService.getIdentifier().toString())
        .isEqualTo("http://example.org/image-service/logo");
    assertThat(imageService.getProfiles()).containsExactly(ImageApiProfile.LEVEL_TWO);
  }

  @Test
  public void testGenericService() throws Exception {
    GenericService service = readFromResources("genericService.json", GenericService.class);
    assertThat(service.getContext().toString())
        .isEqualTo("http://example.org/ns/jsonld/context.json");
    assertThat(service.getIdentifier().toString())
        .isEqualTo("http://example.org/service/example.json");
    assertThat(service.getProfiles())
        .containsExactly(new Profile(URI.create("http://example.org/docs/example-service.html")));
    assertThat(service.getLabelString()).isEqualTo("Example Service");
  }

  @Test
  public void testGeoJsonEmbedded() throws Exception {
    GeoService service = readFromResources("geoJsonEmbedded.json", GeoService.class);
    assertThat(service.getFeature().getProperties()).containsEntry("name", "Paris");
    assertThat(service.getFeature().getGeometry()).isInstanceOf(Point.class);
    assertThat(((Point) service.getFeature().getGeometry()).getCoordinates())
        .hasFieldOrPropertyWithValue("longitude", 48.8567)
        .hasFieldOrPropertyWithValue("latitude", 2.3508);
  }

  @Test
  public void testGeoJsonExternal() throws Exception {
    GeoService service = readFromResources("geoJsonExternal.json", GeoService.class);
    assertThat(service.getIdentifier().toString())
        .isEqualTo("http://www.example.org/geojson/paris.json");
  }

  @Test
  public void testPhysicalDimensionsService() throws Exception {
    PhysicalDimensionsService service =
        readFromResources("physicalDimensions.json", PhysicalDimensionsService.class);
    assertThat(service)
        .hasFieldOrPropertyWithValue("physicalScale", 0.0025)
        .hasFieldOrPropertyWithValue("physicalUnits", Unit.INCHES);
  }
}

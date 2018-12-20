package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.annex.GeoService;
import de.digitalcollections.iiif.model.annex.PhysicalDimensionsService;
import de.digitalcollections.iiif.model.annex.PhysicalDimensionsService.Unit;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Feature;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
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
            Resources.getResource("spec/image/" + filename), Charset.defaultCharset());
  }

  private void assertSerializationEqualsSpec(Object obj, String specFilename) throws IOException, JSONException {
    String specJson = readFromResources(specFilename);
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    JSONAssert.assertEquals(specJson, json, true);
  }

  @Test
  public void testFullResponse() throws Exception {
    ImageService service = new ImageService("http://www.example.org/image-service/abcd1234/1E34750D-38DB-4825-A38A-B60A345E591C", ImageApiProfile.LEVEL_TWO);
    service.setWidth(6000);
    service.setHeight(4000);
    service.addSize(
            new Size(150, 100),
            new Size(600, 400),
            new Size(3000, 2000));

    TileInfo ti1 = new TileInfo(512);
    ti1.addScaleFactor(1, 2, 4);
    TileInfo ti2 = new TileInfo(1024);
    ti2.setHeight(2048);
    ti2.addScaleFactor(8, 16);
    service.addTile(ti1, ti2);

    PropertyValue attribution = new PropertyValue();
    attribution.addValue(Locale.ENGLISH, "<span>Provided by Example Organization</span>");
    attribution.addValue(Locale.forLanguageTag("cy"), "<span>Darparwyd gan Enghraifft Sefydliad</span>");
    service.setAttribution(attribution);

    ImageContent logo = new ImageContent("http://example.org/image-service/logo/full/200,/0/default.png");
    logo.addService(new ImageService("http://example.org/image-service/logo", ImageApiProfile.LEVEL_TWO));
    logo.setFormat((MimeType) null);
    service.addLogo(logo);

    service.addLicense("http://example.org/rights/license1.html",
            "http://rightsstatements.org/vocab/InC-EDU/1.0/");

    ImageApiProfile profile = new ImageApiProfile();
    profile.addFormat(Format.GIF, Format.PDF);
    profile.addQuality(Quality.COLOR, Quality.GRAY);
    profile.addFeature(Feature.CANONICAL_LINK_HEADER, Feature.ROTATION_ARBITRARY, Feature.PROFILE_LINK_HEADER,
            new Feature("http://example.com/feature/"));
    service.addProfile(profile);

    service.addService(
            new PhysicalDimensionsService(0.0025, Unit.INCHES),
            new GeoService("http://www.example.org/geojson/paris.json"));

    assertSerializationEqualsSpec(service, "fullResponse.json");
  }
}

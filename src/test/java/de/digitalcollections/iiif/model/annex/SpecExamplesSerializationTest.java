package de.digitalcollections.iiif.model.annex;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.GenericService;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.annex.PhysicalDimensionsService.Unit;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Feature;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.image.Size;
import de.digitalcollections.iiif.model.image.TileInfo;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import org.geojson.Point;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class SpecExamplesSerializationTest {

  private ObjectMapper mapper;

  @Before
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  private String readFromResources(String filename) throws IOException {
    return Resources.toString(
            Resources.getResource("spec/annex/" + filename), Charset.defaultCharset());
  }

  private void assertSerializationEqualsSpec(Object obj, String specFilename) throws IOException, JSONException {
    String specJson = readFromResources(specFilename);
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    JSONAssert.assertEquals(specJson, json, true);
  }

  @Test
  public void testAdditionalInfo() throws Exception {
    ImageService service = new ImageService("http://www.example.org/image-service/abcd1234",
            ImageApiProfile.LEVEL_TWO);

    ImageApiProfile profile = new ImageApiProfile();
    profile.addFormat(Format.GIF, Format.PDF);
    profile.addQuality(Quality.COLOR, Quality.GRAY);
    profile.addFeature(Feature.CANONICAL_LINK_HEADER, Feature.ROTATION_ARBITRARY,
            new Feature("http://example.com/feature"));
    service.addProfile(profile);

    service.setWidth(6000);
    service.setHeight(4000);
    service.addSize(new Size(150, 100),
            new Size(600, 400),
            new Size(3000, 2000));

    TileInfo tileInfo = new TileInfo(512);
    tileInfo.addScaleFactor(1, 2, 4, 8, 16);
    service.addTile(tileInfo);

    assertSerializationEqualsSpec(service, "additionalInfo.json");
  }

  @Test
  public void testEmbeddedService() throws Exception {
    // FIXME: We had to modify the spec example by adding a @context to the logo service
    //        We should instead find a way to avoid duplicate @contexts in the tree
    ImageService service = new ImageService("http://www.example.org/image-service/baseImage", ImageApiProfile.LEVEL_TWO);
    service.addAttribution("Provided by Example Organization");
    ImageContent logo = new ImageContent("http://example.org/image-service/logo/full/full/0/default.png");
    logo.addService(new ImageService("http://example.org/image-service/logo", ImageApiProfile.LEVEL_TWO));
    logo.setFormat((MimeType) null);
    service.addLogo(logo);
    assertSerializationEqualsSpec(service, "embeddedService.json");
  }

  @Test
  public void testGenericService() throws Exception {
    GenericService service = new GenericService("http://example.org/ns/jsonld/context.json",
            "http://example.org/service/example.json",
            "http://example.org/docs/example-service.html");
    service.setLabel(new PropertyValue("Example Service"));
    assertSerializationEqualsSpec(service, "genericService.json");
  }

  @Test
  public void testGeoJsonEmbedded() throws Exception {
    org.geojson.Feature feat = new org.geojson.Feature();
    feat.setProperty("name", "Paris");
    feat.setGeometry(new Point(48.8567, 2.3508));
    GeoService service = new GeoService("http://www.example.org/geojson/paris.json", feat);
    assertSerializationEqualsSpec(service, "geoJsonEmbedded.json");
  }

  @Test
  public void testGeoJsonExternal() throws Exception {
    GeoService service = new GeoService();
    service.setIdentifier(URI.create("http://www.example.org/geojson/paris.json"));
    assertSerializationEqualsSpec(service, "geoJsonExternal.json");
  }

  @Test
  public void testPhysicalDimensionsService() throws Exception {
    PhysicalDimensionsService service = new PhysicalDimensionsService(0.0025, Unit.INCHES);
    assertSerializationEqualsSpec(service, "physicalDimensions.json");
  }
}

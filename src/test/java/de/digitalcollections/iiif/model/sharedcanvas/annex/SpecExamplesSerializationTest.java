package de.digitalcollections.iiif.model.sharedcanvas.annex;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Feature;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.image.Size;
import de.digitalcollections.iiif.model.image.TileInfo;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import java.io.IOException;
import java.nio.charset.Charset;
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
}

package de.digitalcollections.iiif.model.image;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.net.URI;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectorTest {
  @Test
  public void testParseRequestFromUri() {
    URI uri = URI.create("http://www.example.org/image-service/abcd1234/full/full/0/default.jpg");
    ImageApiSelector selector = ImageApiSelector.fromImageApiUri(uri);
    assertThat(selector.getRegion().getRegion()).isNull();
    assertThat(selector.getSize().getWidth()).isNull();
    assertThat(selector.getSize().getHeight()).isNull();
    assertThat(selector.getRotation().getRotation()).isEqualTo(0);
    assertThat(selector.getQuality()).isEqualTo(ImageApiProfile.Quality.DEFAULT);
    assertThat(selector.getFormat()).isEqualTo(ImageApiProfile.Format.JPG);
    assertThat(selector.toString()).isEqualTo("abcd1234/full/full/0/default.jpg");

    // With prefix
    assertThat(ImageApiSelector.fromImageApiUri(
        URI.create("https://example.com/some-prefix/another-prefix/id/full/full/0/default.jpg")).toString())
        .isEqualTo("id/full/full/0/default.jpg");
  }

  @Test
  public void testRegion() {
    Dimension imageDims = new Dimension(3844, 7387);
    RegionRequest req = RegionRequest.fromString("full");
    assertThat(req).hasFieldOrPropertyWithValue("region", null);
    assertThat(req.toString()).isEqualTo("full");
    assertThat(req.resolve(imageDims)).isEqualTo(new Rectangle(0, 0, imageDims.width, imageDims.height));

    req = RegionRequest.fromString("square");
    assertThat(req).hasFieldOrPropertyWithValue("square", true);
    assertThat(req.toString()).isEqualTo("square");
    assertThat(req.resolve(imageDims)).isEqualTo(new Rectangle(0, 1771, imageDims.width, imageDims.width));

    req = RegionRequest.fromString("125,15,120,140");
    assertThat(req).hasFieldOrPropertyWithValue("region", new Rectangle(125, 15, 120, 140));
    assertThat(req.toString()).isEqualTo("125,15,120,140");
    assertThat(req.resolve(imageDims)).isEqualTo(new Rectangle(125, 15, 120, 140));

    req = RegionRequest.fromString("pct:41.6,7.5,40,70");
    assertThat(req).hasFieldOrPropertyWithValue("region", new Rectangle2D.Double(41.6, 7.5, 40, 70));
    assertThat(req.toString()).isEqualTo("pct:41.6,7.5,40,70");
    assertThat(req.resolve(imageDims)).isEqualTo(new Rectangle(1599, 554, 1538, 5171));

    req = RegionRequest.fromString("125,15,200,200");
    assertThat(req.resolve(new Dimension(300, 200)))
        .isEqualTo(new Rectangle(125, 15, 175, 185));

    req = RegionRequest.fromString("pct:41.6,7.5,66.6,100");
    assertThat(req.resolve(new Dimension(300, 200)))
        .isEqualTo(new Rectangle(125, 15, 175, 185));
  }

  @Test
  public void testSize() {
    Dimension imageDim = new Dimension(300, 200);
    ImageApiProfile profile = new ImageApiProfile();
    profile.setMaxWidth(200);

    SizeRequest req = SizeRequest.fromString("full");
    assertThat(req.toString()).isEqualTo("full");
    assertThat(req.resolve(imageDim, profile)).isEqualTo(imageDim);

    req = SizeRequest.fromString("max");
    assertThat(req.toString()).isEqualTo("max");
    assertThat(req.resolve(imageDim, profile)).isEqualTo(new Dimension(200, 133));

    req = SizeRequest.fromString("150,");
    assertThat(req.toString()).isEqualTo("150,");
    assertThat(req.resolve(imageDim, profile)).isEqualTo(new Dimension(150, 100));

    req = SizeRequest.fromString(",150");
    assertThat(req.toString()).isEqualTo(",150");
    assertThat(req.resolve(imageDim, profile)).isEqualTo(new Dimension(225, 150));

    req = SizeRequest.fromString("pct:50");
    assertThat(req.toString()).isEqualTo("pct:50");
    assertThat(req.resolve(imageDim, profile)).isEqualTo(new Dimension(150, 100));

    req = SizeRequest.fromString("225,100");
    assertThat(req.toString()).isEqualTo("225,100");
    assertThat(req.resolve(imageDim, profile)).isEqualTo(new Dimension(225, 100));

    req = SizeRequest.fromString("!225,100");
    assertThat(req.toString()).isEqualTo("!225,100");
    assertThat(req.isBestFit()).isTrue();
    assertThat(req.resolve(imageDim, profile)).isEqualTo(new Dimension(150, 100));
    assertThat(req.fromString("!100,100").resolve(imageDim, profile)).isEqualTo(new Dimension(100, 66));

    imageDim = new Dimension(200, 400);
    req = SizeRequest.fromString("!100,500");
    assertThat(req.toString()).isEqualTo("!100,500");
    assertThat(req.isBestFit()).isTrue();
    assertThat(req.resolve(imageDim, profile)).isEqualTo(new Dimension(100, 200));
    assertThat(req.fromString("!100,100").resolve(imageDim, profile)).isEqualTo(new Dimension(50, 100));
  }

  @Test
  public void testRotation() {
    RotationRequest req = RotationRequest.fromString("180");
    assertThat(req.toString()).isEqualTo("180");
    assertThat(req).hasFieldOrPropertyWithValue("rotation", 180.0);
    assertThat(req.isMirror()).isFalse();

    req = RotationRequest.fromString("!0");
    assertThat(req.toString()).isEqualTo("!0");
    assertThat(req).hasFieldOrPropertyWithValue("rotation", 0.0);
    assertThat(req.isMirror()).isTrue();

    req = RotationRequest.fromString("22.5");
    assertThat(req.toString()).isEqualTo("22.5");
    assertThat(req).hasFieldOrPropertyWithValue("rotation", 22.5);
    assertThat(req.isMirror()).isFalse();
  }

  @Test
  public void testUrlDecode() throws Exception {
    assertThat(ImageApiSelector.fromString("id1/full/full/0/default.png"))
        .hasFieldOrPropertyWithValue("identifier", "id1")
        .hasFieldOrPropertyWithValue("region", RegionRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("size", SizeRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("rotation", RotationRequest.fromString("0"))
        .hasFieldOrPropertyWithValue("quality", ImageApiProfile.Quality.DEFAULT)
        .hasFieldOrPropertyWithValue("format", ImageApiProfile.Format.PNG)
        .hasToString("id1/full/full/0/default.png");

    assertThat(ImageApiSelector.fromString("id1/0,10,100,200/pct:50/90/default.png"))
        .hasFieldOrPropertyWithValue("identifier", "id1")
        .hasFieldOrPropertyWithValue("region", RegionRequest.fromString("0,10,100,200"))
        .hasFieldOrPropertyWithValue("size", SizeRequest.fromString("pct:50"))
        .hasFieldOrPropertyWithValue("rotation", RotationRequest.fromString("90"))
        .hasFieldOrPropertyWithValue("quality", ImageApiProfile.Quality.DEFAULT)
        .hasFieldOrPropertyWithValue("format", ImageApiProfile.Format.PNG)
        .hasToString("id1/0,10,100,200/pct:50/90/default.png");

    assertThat(ImageApiSelector.fromString("id1/pct:10,10,80,80/50,/22.5/color.jpg"))
        .hasFieldOrPropertyWithValue("identifier", "id1")
        .hasFieldOrPropertyWithValue("region", RegionRequest.fromString("pct:10,10,80,80"))
        .hasFieldOrPropertyWithValue("size", SizeRequest.fromString("50,"))
        .hasFieldOrPropertyWithValue("rotation", RotationRequest.fromString("22.5"))
        .hasFieldOrPropertyWithValue("quality", ImageApiProfile.Quality.COLOR)
        .hasFieldOrPropertyWithValue("format", ImageApiProfile.Format.JPG)
        .hasToString("id1/pct:10,10,80,80/50,/22.5/color.jpg");

    assertThat(ImageApiSelector.fromString("bb157hs6068/full/full/270/gray.jpg"))
        .hasFieldOrPropertyWithValue("identifier", "bb157hs6068")
        .hasFieldOrPropertyWithValue("region", RegionRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("size", SizeRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("rotation", RotationRequest.fromString("270"))
        .hasFieldOrPropertyWithValue("quality", ImageApiProfile.Quality.GRAY)
        .hasFieldOrPropertyWithValue("format", ImageApiProfile.Format.JPG)
        .hasToString("bb157hs6068/full/full/270/gray.jpg");

    assertThat(ImageApiSelector.fromString("ark:%2F12025%2F654xz321/full/full/0/default.jpg"))
        .hasFieldOrPropertyWithValue("identifier", "ark:/12025/654xz321")
        .hasFieldOrPropertyWithValue("region", RegionRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("size", SizeRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("rotation", RotationRequest.fromString("0"))
        .hasFieldOrPropertyWithValue("quality", ImageApiProfile.Quality.DEFAULT)
        .hasFieldOrPropertyWithValue("format", ImageApiProfile.Format.JPG)
        .hasToString("ark:%2F12025%2F654xz321/full/full/0/default.jpg");

    assertThat(ImageApiSelector.fromString("urn:foo:a123,456/full/full/0/default.jpg"))
        .hasFieldOrPropertyWithValue("identifier", "urn:foo:a123,456")
        .hasFieldOrPropertyWithValue("region", RegionRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("size", SizeRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("rotation", RotationRequest.fromString("0"))
        .hasFieldOrPropertyWithValue("quality", ImageApiProfile.Quality.DEFAULT)
        .hasFieldOrPropertyWithValue("format", ImageApiProfile.Format.JPG)
        .hasToString("urn:foo:a123,456/full/full/0/default.jpg");

    assertThat(ImageApiSelector.fromString("urn:sici:1046-8188(199501)13:1%253C69:FTTHBI%253E2.0.TX;2-4/full/full/0/default.jpg"))
        .hasFieldOrPropertyWithValue("identifier", "urn:sici:1046-8188(199501)13:1%3C69:FTTHBI%3E2.0.TX;2-4")
        .hasFieldOrPropertyWithValue("region", RegionRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("size", SizeRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("rotation", RotationRequest.fromString("0"))
        .hasFieldOrPropertyWithValue("quality", ImageApiProfile.Quality.DEFAULT)
        .hasFieldOrPropertyWithValue("format", ImageApiProfile.Format.JPG)
        .hasToString("urn:sici:1046-8188(199501)13:1%253C69:FTTHBI%253E2.0.TX;2-4/full/full/0/default.jpg");

    assertThat(ImageApiSelector.fromString("http:%2F%2Fexample.com%2F%3F54%23a/full/full/0/default.jpg"))
        .hasFieldOrPropertyWithValue("identifier", "http://example.com/?54#a")
        .hasFieldOrPropertyWithValue("region", RegionRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("size", SizeRequest.fromString("full"))
        .hasFieldOrPropertyWithValue("rotation", RotationRequest.fromString("0"))
        .hasFieldOrPropertyWithValue("quality", ImageApiProfile.Quality.DEFAULT)
        .hasFieldOrPropertyWithValue("format", ImageApiProfile.Format.JPG)
        .hasToString("http:%2F%2Fexample.com%2F%3F54%23a/full/full/0/default.jpg");
  }

}

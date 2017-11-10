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
    assertThat(selector.toString()).isEqualTo("/full/full/0/default.jpg");
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
    assertThat(req.resolve(imageDim, profile)).isEqualTo(new Dimension(225, 100));
  }

  @Test
  public void testRotation() {
    RotationRequest req = RotationRequest.fromString("180");
    assertThat(req.toString()).isEqualTo("180");
    assertThat(req).hasFieldOrPropertyWithValue("rotation", 180);
    assertThat(req.isMirror()).isFalse();

    req = RotationRequest.fromString("!0");
    assertThat(req.toString()).isEqualTo("!0");
    assertThat(req).hasFieldOrPropertyWithValue("rotation", 0);
    assertThat(req.isMirror()).isTrue();
  }

}

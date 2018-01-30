package de.digitalcollections.iiif.model.image;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileTest {
  @Test
  public void testMerge() {
    List<ImageApiProfile> profiles = new ArrayList<>();
    profiles.add(ImageApiProfile.LEVEL_ONE);
    ImageApiProfile extraProfile = new ImageApiProfile();
    extraProfile.addFeature(ImageApiProfile.Feature.REGION_BY_PCT,
                            ImageApiProfile.Feature.SIZE_BY_CONFINED_WH,
                            ImageApiProfile.Feature.SIZE_ABOVE_FULL);
    extraProfile.addFormat(ImageApiProfile.Format.JP2);
    extraProfile.setMaxWidth(2048);
    extraProfile.setMaxArea((long) 500000);
    ImageApiProfile limitProfile = new ImageApiProfile();
    limitProfile.setMaxWidth(1024);
    limitProfile.setMaxArea((long) 1000000);
    profiles.add(extraProfile);
    profiles.add(limitProfile);
    ImageApiProfile merged = ImageApiProfile.merge(profiles);
    assertThat(merged.getFeatures()).containsExactlyInAnyOrder(
        ImageApiProfile.Feature.REGION_BY_PX,
        ImageApiProfile.Feature.SIZE_BY_W,
        ImageApiProfile.Feature.SIZE_BY_H,
        ImageApiProfile.Feature.SIZE_BY_PCT,
        ImageApiProfile.Feature.BASE_URI_REDIRECT,
        ImageApiProfile.Feature.CORS,
        ImageApiProfile.Feature.JSONLD_MEDIA_TYPE,
        ImageApiProfile.Feature.REGION_BY_PCT,
        ImageApiProfile.Feature.SIZE_BY_CONFINED_WH,
        ImageApiProfile.Feature.SIZE_ABOVE_FULL);
    assertThat(merged.getFormats()).containsExactlyInAnyOrder(
        ImageApiProfile.Format.JPG, ImageApiProfile.Format.JP2);
    assertThat(merged.getMaxWidth()).isEqualTo(1024);
    assertThat(merged.getMaxArea()).isEqualTo(500000);
  }
}

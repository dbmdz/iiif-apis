package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.digitalcollections.iiif.model.Profile;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ProfileTest {
  @Test
  public void testMerge() {
    List<Profile> profiles = new ArrayList<>();
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

  @Test
  public void testCustomProfileSerialization() throws JsonProcessingException {
    ImageApiProfile profile = new ImageApiProfile();
    profile.addFeature(
        ImageApiProfile.Feature.PROFILE_LINK_HEADER,
        ImageApiProfile.Feature.CANONICAL_LINK_HEADER,
        ImageApiProfile.Feature.REGION_SQUARE,
        ImageApiProfile.Feature.ROTATION_BY_90S,
        ImageApiProfile.Feature.MIRRORING,
        ImageApiProfile.Feature.SIZE_ABOVE_FULL);
    profile.addFormat(ImageApiProfile.Format.GIF);

    // Indicate to the client if we cannot deliver full resolution versions of the image
    profile.setMaxWidth(2048);
    profile.setMaxHeight(4096);

    IiifObjectMapper mapper = new IiifObjectMapper();
    String json = mapper.writeValueAsString(profile);
    assertThatExceptionOfType(PathNotFoundException.class).isThrownBy(
        () -> JsonPath.parse(json).read("$.qualities"));
  }
}

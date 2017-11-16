package de.digitalcollections.iiif.model;

import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.image.ImageApiSelector;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.image.Size;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.openannotation.Choice;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import java.io.IOException;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for parsing IIIF JSON.
 *
 * Covers some things that are not well tested in the examples from the specifications.
 */
public class ParsingTest {
  private IiifObjectMapper mapper;

  @Before
  public void setup() {
    this.mapper = new IiifObjectMapper();
  }

  private <T> T readFromResources(String filename, Class<T> clz) throws IOException {
    return mapper.readValue(
        Resources.getResource(filename), clz);
  }

  @Test
  public void testFullImageApiSelector() throws Exception {
    ImageApiSelector sel = readFromResources("fullImageSelector.json", ImageApiSelector.class);
    assertThat(sel.getRegion().toString()).isEqualTo("50,50,1250,1850");
    assertThat(sel.getSize().toString()).isEqualTo("800,600");
    assertThat(sel.getRotation().toString()).isEqualTo("!90");
    assertThat(sel.getQuality()).isEqualTo(Quality.DEFAULT);
    assertThat(sel.getFormat()).isEqualTo(Format.PNG);
  }

  @Test
  public void testAnnotationWithNilChoice() throws Exception {
    Annotation anno = readFromResources("annoWithNilChoice.json", Annotation.class);
    assertThat(anno.getResource()).isInstanceOf(Choice.class);
    Choice choice = (Choice) anno.getResource();
    assertThat(choice.getAlternatives().get(0)).isNull();
  }

  @Test
  public void testManifestWithStringLogo() throws Exception {
    Manifest manifest = readFromResources("stringImage.json", Manifest.class);
    assertThat(manifest.getLogoUri().toString()).isEqualTo("http://example.com/logo.png");
  }

  @Test
  public void testV10Manifest() throws Exception {
    Manifest manifest = readFromResources("presiV10Manifest.json", Manifest.class);
    assertThat(manifest.getLabelString()).isEqualTo("Book 1");
    assertThat(manifest.getMetadata().stream()
        .filter(e -> e.getLabel().getFirstValue().equals("Published"))
        .findFirst()
        .map(e -> e.getValue().getLocalizations())
        .get()).containsExactlyInAnyOrder(Locale.ENGLISH, Locale.FRENCH);
    assertThat(manifest.getDefaultSequence().getCanvases()).hasSize(3);
    assertThat(manifest.getRanges()).hasSize(1);
    assertThat(manifest.getRanges().get(0).getCanvases()).hasSize(3);
    Canvas canvas = manifest.getDefaultSequence().getCanvases().get(0);
    assertThat(canvas.getImages().get(0).getResource()).isInstanceOf(ImageContent.class);
    ImageContent imgRes = (ImageContent) canvas.getImages().get(0).getResource();
    assertThat(imgRes.getServices().get(0)).isInstanceOf(ImageService.class);
    ImageService service = (ImageService) manifest.getDefaultSequence().getCanvases().get(1).getImages()
        .get(0).getResource().getServices().get(0);
    assertThat(service.getWidth()).isEqualTo(6000);
    assertThat(service.getHeight()).isEqualTo(8000);
    assertThat(service.getSizes()).containsExactly(
        new Size(6000, 8000),
        new Size(3000, 4000),
        new Size(1500, 2000));
    assertThat(service.getTiles()).hasSize(1);
    assertThat(service.getTiles().get(0).getScaleFactors()).containsExactly(1, 2, 4);
    assertThat(service.getTiles().get(0).getWidth()).isEqualTo(1024);
    assertThat(service.getTiles().get(0).getHeight()).isEqualTo(1024);
  }

  @Test
  public void testV10ImageInfo() throws Exception {
    // FIXME: It's kind of ugly that we have to deserialize into the generic typefirst
    //        and then cast it, but if we use @JsonDeserialize on ImageService, we run into
    //        an infinite recursion...
    Service service = readFromResources("v1Info.json", Service.class);
    assertThat(service).isInstanceOf(ImageService.class);
    ImageService info = (ImageService) service;
    assertThat(info.getWidth()).isEqualTo(6000);
    assertThat(info.getHeight()).isEqualTo(4000);
    assertThat(info.getProfiles().get(0)).isEqualTo(ImageApiProfile.V1_LEVEL_ZERO);
    assertThat(info.getSizes()).containsExactly(
        new Size(6000, 4000),
        new Size(3000, 2000),
        new Size(1500, 1000));
    assertThat(info.getTiles()).hasSize(1);
    assertThat(info.getTiles().get(0).getScaleFactors()).containsExactly(1, 2, 4);
    assertThat(info.getTiles().get(0))
        .hasFieldOrPropertyWithValue("width", 1024)
        .hasFieldOrPropertyWithValue("height", 1024);
    assertThat(info.getProfiles().get(1)).isInstanceOf(ImageApiProfile.class);
    ImageApiProfile profile = (ImageApiProfile) info.getProfiles().get(1);
    assertThat(profile.getFormats()).containsExactlyInAnyOrder(Format.JPG, Format.PNG);
    assertThat(profile.getQualities()).containsExactlyInAnyOrder(Quality.GRAY, Quality.DEFAULT);
  }
}

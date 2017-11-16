package de.digitalcollections.iiif.model;

import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.image.ImageApiSelector;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.openannotation.Choice;
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
  }
}

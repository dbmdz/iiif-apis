package de.digitalcollections.iiif.model;

import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.image.ImageApiSelector;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.openannotation.Choice;
import java.io.IOException;
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
    assertThat(sel.getRegion()).isEqualTo("50,50,1250,1850");
    assertThat(sel.getSize()).isEqualTo("800,600");
    assertThat(sel.getRotation()).isEqualTo("!90");
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

}

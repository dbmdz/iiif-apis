package de.digitalcollections.iiif.model.sharedcanvas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExternalTest {
  private ObjectMapper mapper;

  @Before
  public void setup() {
    this.mapper = new IiifObjectMapper();
  }

  private <T> T readFromResources(String filename, Class<T> clz) throws IOException {
    return mapper.readValue(
        Resources.getResource("external/" + filename), clz);
  }

  @Test
  public void testBiblissima() throws Exception {
    Manifest manifest = readFromResources("biblissima_ark:12148_btv1b9007608v.json", Manifest.class);
    assertThat(manifest).isNotNull();
  }

  @Test
  public void testBiblissimaReconstructed() throws Exception {
    Manifest manifest = readFromResources("biblissima_reconstructed.json", Manifest.class);
    // Two images per canvas, one page the other miniature
    assertThat(manifest.getDefaultSequence().getCanvases())
        .allMatch(c -> c.getImages().size() == 2);
  }

}

package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.presentation.model.jackson.IiifObjectMapper;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecExamplesTest {
  private ObjectMapper mapper;

  @Before
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  @Test
  public void testFullResponse() throws IOException {
    Manifest manifest = mapper.readValue(Resources.getResource("fromspec/full_response.json"),
                                         Manifest.class);
    assertThat(manifest).isNotNull();
  }
}

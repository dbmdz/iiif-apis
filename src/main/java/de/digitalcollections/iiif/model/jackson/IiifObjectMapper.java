package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class IiifObjectMapper extends ObjectMapper {

  private void checkJacksonVersion() {
    int neededMajor = 2;
    int neededMinor = 9;
    int currentMajor = this.version().getMajorVersion();
    int currentMinor = this.version().getMinorVersion();
    if (currentMajor < neededMajor || (currentMajor == neededMajor && currentMinor < neededMinor)) {
      throw new RuntimeException(
              String.format("iiif-apis requires Jackson >= 2.9.0. The version on your claspath is %s", this.version().toString()));
    }
  }

  public IiifObjectMapper() {
    this.checkJacksonVersion();

    // Don't include null properties
    this.setSerializationInclusion(Include.NON_NULL);

    // Both are needed to add `@context` to the top-level object
    this.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
    this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    // Some array fields are unwrapped during serialization if they have only one value
    this.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    // Register the problem handler
    this.addHandler(new ProblemHandler());

    // Disable writing dates as timestamps
    this.registerModule(new JavaTimeModule());
    this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Enable automatic detection of parameter names in @JsonCreators
    this.registerModule(new ParameterNamesModule());

    // Register the module
    this.registerModule(new IiifModule());
  }
}

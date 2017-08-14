package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class IiifObjectMapper extends ObjectMapper {
  public IiifObjectMapper() {
    // Don't include null properties
    this.setSerializationInclusion(Include.NON_NULL);

    // Both are needed to add `@context` to the top-level object
    this.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
    this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    // Unwrap single-value arrays when serializing
    this.enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
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

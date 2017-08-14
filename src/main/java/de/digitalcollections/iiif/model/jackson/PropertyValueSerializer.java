package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.digitalcollections.iiif.model.PropertyValue;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PropertyValueSerializer extends StdSerializer<PropertyValue> {
  public PropertyValueSerializer() {
    this(null);
  }

  public PropertyValueSerializer(Class<PropertyValue> t) {
    super(t);
  }

  private void writeSingleLocalization(JsonGenerator jgen, Locale language, List<String> values) throws IOException {
    jgen.writeStartObject();
    jgen.writeStringField("@language", language.toLanguageTag());
    if (values.size() == 1) {
      jgen.writeStringField("@value", values.get(0));
    } else {
      jgen.writeArrayFieldStart("@value");
      for (String val : values) {
        jgen.writeString(val);
      }
      jgen.writeEndArray();
    }
    jgen.writeEndObject();
  }

  @Override
  public void serialize(PropertyValue value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
    if (value.getLocalizations().size() == 1 && value.getLocalizations().contains(Locale.ROOT)) {
      // Simple property value
      if (value.getValues().size() == 1) {
        jgen.writeString(value.getValues().get(0));
      } else {
        jgen.writeStartArray();
        for (String val : value.getValues()) {
          jgen.writeString(val);
        }
        jgen.writeEndArray();
      }
    } else {
      // Localized property value
      Set<Locale> localizations = value.getLocalizations();
      if (localizations.size() == 1) {
        Locale lang = localizations.iterator().next();
        writeSingleLocalization(jgen, lang, value.getValues(lang));
      } else if (localizations.size() > 1) {
        jgen.writeStartArray();
        for (Locale language : localizations) {
          writeSingleLocalization(jgen, language, value.getValues(language));
        }
        jgen.writeEndArray();
      }
    }
  }
}

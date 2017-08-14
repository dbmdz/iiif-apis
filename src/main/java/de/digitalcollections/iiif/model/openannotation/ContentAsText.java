package de.digitalcollections.iiif.model.openannotation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.core.model.api.MimeType;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContentAsText extends Resource {
  public static final String TYPE = "cnt:ContentAsText";
  String chars;
  MimeType format;
  Locale language;
  String type;

  @JsonProperty("@type")
  public List<String> getTypes() {
    List<String> types = new ArrayList<>();
    if (this.type != null) {
      types.add(this.type);
    }
    types.add(TYPE);
    return types;
  }

  @JsonCreator
  public ContentAsText(@JsonProperty("chars") String chars) {
    this.chars = chars;
  }

  public String getChars() {
    return chars;
  }

  public void setChars(String chars) {
    this.chars = chars;
  }

  public MimeType getFormat() {
    return format;
  }

  public void setFormat(MimeType format) {
    this.format = format;
  }

  public Locale getLanguage() {
    return language;
  }

  public void setLanguage(Locale language) {
    this.language = language;
  }

  public void setType(String type) {
    this.type = type;
  }
}

package de.digitalcollections.iiif.model.openannotation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** A resource that contains embedded textual content. */
public class ContentAsText extends Resource<ContentAsText> {

  public static final String TYPE = "cnt:ContentAsText";

  private String chars;
  private MimeType format;
  private Locale language;
  private String type;

  @JsonProperty("@type")
  public List<String> getTypes() {
    Set<String> types = new HashSet<>();
    if (this.type != null) {
      types.add(this.type);
    }
    types.add(TYPE);
    return new ArrayList(types);
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

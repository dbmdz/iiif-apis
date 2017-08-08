package de.digitalcollections.iiif.presentation.builders;

import de.digitalcollections.iiif.presentation.model.MetadataEntry;
import de.digitalcollections.iiif.presentation.model.PropertyValue;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Manifest;
import java.net.URI;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

public class ManifestBuilder {
  /** Pattern for the recommended manifest URI:
   * {scheme}://{host}/{prefix}/{identifier}/manifest **/
  private final static Pattern ID_PAT = Pattern.compile(
      "^(?<scheme>.+)://(?<host>[^/]+)/(?<prefix>.+)/(?<identifier>.+)/manifest");


  private URI baseUri;
  private int thumbnailWidth = 250;
  private Manifest manifest;

  public ManifestBuilder(URI identifier) {
    Matcher matcher = ID_PAT.matcher(identifier.toString());
    checkArgument(matcher.matches(), "Identifier must correspond to the recommended URI pattern for Manifests.");

    this.baseUri = URI.create(String.format(
        "%s://%s/%s/%s", matcher.group("scheme"), matcher.group("host"),
        matcher.group("prefix"), matcher.group("identifier")));
    this.manifest = new Manifest(identifier.toString(), "");
  }

  public ManifestBuilder addLabel(String label) {
    return addLabel(Locale.ROOT, label);
  }

  public ManifestBuilder addLabel(Locale language, String label) {
    this.manifest.addLabels(new PropertyValue(language, label));
    return this;
  }

  public ManifestBuilder addMetadata(String key, String value) {
    this.manifest.addMetadata(new MetadataEntry(key, value));
    return this;
  }
}

package de.digitalcollections.iiif.model;

import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MimeType {

  private static final Map<String, MimeType> knownTypes;
  private static final Map<String, String> extensionMapping;

  /** Regular Expression used for decoding a MIME type * */
  private static final Pattern MIME_PATTERN =
      Pattern.compile(
          "^(?<primaryType>[-a-z]+?)/(?<subType>[-\\\\.a-z0-9*]+?)(?:\\+(?<suffix>\\w+))?$");

  private static String getMimeTypeColumn(String line) {
    return Arrays.stream(line.split("\t"))
        .map(String::trim)
        .filter(l -> !l.isEmpty())
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }

  static {
    // Load list of known MIME types and their extensions from the IANA list in the
    // package resources (obtained from
    // https://svn.apache.org/repos/asf/httpd/httpd/trunk/docs/conf/mime.types)
    InputStream mimeStream = MimeType.class.getClassLoader().getResourceAsStream("mime.types");
    BufferedReader mimeReader = new BufferedReader(new InputStreamReader(mimeStream));

    Function<String[], String[]> substituteMissingExtensions =
        columns -> {
          if (columns.length > 1) {
            return columns;
          }
          return new String[] {columns[0], ""};
        };

    knownTypes =
        mimeReader
            .lines()
            .map(l -> l.replaceAll("^# ", ""))
            .filter(l -> !l.isEmpty())
            .map(line -> line.split("\t+"))
            .filter(columns -> columns.length > 0)
            .filter(columns -> MIME_PATTERN.matcher(columns[0]).matches())
            .map(substituteMissingExtensions)
            .collect(
                Collectors.toMap(
                    columns -> columns[0],
                    columns ->
                        new MimeType(
                            columns[0],
                            "".equals(columns[1])
                                ? Collections.<String>emptyList()
                                : Arrays.asList(columns[1].split(" ")))));

    // Some custom overrides to influence the order of file extensions
    // Since these are added to the end of the list, they take precedence over the
    // types from the `mime.types` file
    knownTypes.get("image/jpeg").setExtensions(Arrays.asList("jpg", "jpeg", "jpe"));
    knownTypes.get("image/tiff").setExtensions(Arrays.asList("tif", "tiff"));

    List<String> xmlExtensions = new ArrayList<>(knownTypes.get("application/xml").getExtensions());
    xmlExtensions.add("ent");
    knownTypes.get("application/xml").setExtensions(xmlExtensions);

    knownTypes.put("image/*", new MimeType("image/*", Collections.emptyList()));

    extensionMapping = new HashMap<>();
    for (Map.Entry<String, MimeType> entry : knownTypes.entrySet()) {
      String typeName = entry.getKey();
      for (String ext : entry.getValue().getExtensions()) {
        extensionMapping.put(ext, typeName);
      }
    }
  }

  /** Convenience definitions for commonly used MIME types */
  public static final MimeType MIME_WILDCARD = new MimeType("*", Collections.emptyList());

  public static final MimeType MIME_IMAGE = knownTypes.get("image/*");;
  public static final MimeType MIME_APPLICATION_JSON = knownTypes.get("application/json");
  public static final MimeType MIME_APPLICATION_OCTET_STREAM =
      knownTypes.get("application/octet-stream");
  public static final MimeType MIME_APPLICATION_XML = knownTypes.get("application/xml");
  public static final MimeType MIME_IMAGE_JPEG = knownTypes.get("image/jpeg");
  public static final MimeType MIME_IMAGE_TIF = knownTypes.get("image/tiff");
  public static final MimeType MIME_IMAGE_PNG = knownTypes.get("image/png");
  public static final MimeType MIME_TYPE_MARKDOWN = knownTypes.get("text/markdown");

  private final String primaryType;
  private final String subType;
  private final String suffix;
  private List<String> extensions;

  /**
   * Determine MIME type for the given file extension
   *
   * @param ext file extension
   * @return corresponding MimeType
   */
  public static MimeType fromExtension(String ext) {
    final String extension;
    if (ext.startsWith(".")) {
      extension = ext.substring(1).toLowerCase();
    } else {
      extension = ext.toLowerCase();
    }
    String typeName = extensionMapping.get(extension);
    if (typeName != null) {
      return knownTypes.get(typeName);
    } else {
      return null;
    }
  }

  /**
   * Determine MIME type from filename string. Returns null if no matching MIME type was found.
   *
   * @param filename filename including extension
   * @return corresponding MimeType
   */
  @SuppressWarnings("UnstableApiUsage")
  public static MimeType fromFilename(String filename) {
    return fromExtension(Files.getFileExtension(filename));
  }

  /**
   * Determine MIME type from URI.
   *
   * @param uri uri including filename with extension
   * @return corresponding MimeType
   */
  @SuppressWarnings("UnstableApiUsage")
  public static MimeType fromURI(URI uri) {
    try {
      return fromFilename(Paths.get(uri).toString());
    } catch (FileSystemNotFoundException e) {
      // For non-file URIs, try to guess the MIME type from the URL path, if possible
      return fromExtension(Files.getFileExtension(uri.toString()));
    }
  }

  /**
   * Given an existing MIME type name, look up the corresponding instance. An exception is made for
   * vendor-specific types or non-standard types.
   *
   * @param typeName mimetype name as String, e.g. "image/jpeg"
   * @return corresponding MimeType
   */
  public static MimeType fromTypename(String typeName) {
    MimeType knownType = knownTypes.get(typeName);
    if (knownType != null) {
      return knownType;
    }
    MimeType unknownType = new MimeType(typeName);
    if (!unknownType.getPrimaryType().startsWith("x-")
        || !unknownType.getSubType().startsWith("vnd.")
        || !unknownType.getSubType().startsWith("prs.")) {
      return null;
    } else {
      return unknownType;
    }
  }

  // NOTE: Constructors are private, since we want users to rely on the pre-defined MIME types
  private MimeType(String typeName) {
    this(typeName, Collections.emptyList());
  }

  private MimeType(String typeName, List<String> extensions) {
    if (typeName.equals("*")) {
      this.primaryType = "*";
      this.subType = "*";
      this.suffix = "";
    } else {
      Matcher matcher = MIME_PATTERN.matcher(typeName);
      if (!matcher.matches()) {
        throw new IllegalArgumentException(String.format("%s is not a valid MIME type!", typeName));
      }
      this.primaryType = matcher.group("primaryType");
      this.subType = matcher.group("subType");
      this.suffix = matcher.group("suffix");
      this.extensions = extensions;
    }
  }

  /**
   * Get the MIME type's name (e.g."application/json")
   *
   * @return the MimeType's type name as String
   */
  public String getTypeName() {
    StringBuilder sb = new StringBuilder(primaryType).append("/").append(subType);
    if (suffix != null) {
      sb.append("+").append(suffix);
    }
    return sb.toString();
  }

  /**
   * Get the known file extensions for the MIME type
   *
   * @return List of known file extensions for given MiemType
   */
  public List<String> getExtensions() {
    return extensions;
  }

  private void setExtensions(List<String> extensions) {
    this.extensions = extensions;
  }

  public String getPrimaryType() {
    return primaryType;
  }

  public String getSubType() {
    return subType;
  }

  public String getSuffix() {
    return suffix;
  }

  /**
   * Check if the MIME type "matches" another MIME type.
   *
   * @param other Other MIME type to compare against
   * @return Whether the other type matches this type
   */
  public boolean matches(Object other) {
    if (other instanceof MimeType) {
      MimeType mime = (MimeType) other;
      if (mime == MIME_WILDCARD || this == MIME_WILDCARD) {
        return true;
      } else if (((mime.getSubType().equals("*") || this.getSubType().equals("*")))
          && this.getPrimaryType().equals(mime.getPrimaryType())) {
        return true;
      } else {
        return super.equals(other);
      }
    } else {
      return false;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj.getClass().isAssignableFrom(MimeType.class))) {
      return false;
    }
    return this.hashCode() == obj.hashCode();
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 37 * hash + Objects.hashCode(getTypeName());
    return hash;
  }

  @Override
  public String toString() {
    return getTypeName();
  }
}

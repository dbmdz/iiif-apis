package de.digitalcollections.iiif.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.digitalcollections.iiif.model.enums.ViewingHint;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.search.ContentSearchService;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for writing out IIIF JSON. */
public class JsonMappingTest {

  private ObjectMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  @Test
  public void testCopyConstructor() {
    IiifObjectMapper testMapper = new IiifObjectMapper();

    // in newer jackson versions the `copy()` method needs to be public
    testMapper.copy();
  }

  @Test
  public void testCanvasRoundtrip() throws IOException {
    Canvas canvas = new Canvas("http://some.uri");
    canvas.addLabel("A label");
    canvas.addDescription("This is a slightly longer text about this canvas.");
    canvas.setWidth(800);
    canvas.setHeight(600);

    // Image
    canvas.addIIIFImage("http://some.uri/iiif/foo", ImageApiProfile.LEVEL_ONE);

    // Thumbnail
    ImageContent thumbnail = new ImageContent("http://some.uri/iiif/foo/full/250,/0/default.jpg");
    thumbnail.addService(new ImageService("http://some.uri/iiif/foo", ImageApiProfile.LEVEL_ONE));
    canvas.addThumbnail(thumbnail);

    // Other Content
    canvas.addSeeAlso(new OtherContent("http://some.uri/ocr/foo.hocr", "text/html"));

    // Search Service
    ContentSearchService searchService = new ContentSearchService("http://some.uri/search/foo");
    searchService.setAutocompleteServiceFromId("http://some.uri/autocomplete/foo");
    canvas.addService(searchService);

    // Metadata
    canvas.addMetadata("Author", "Ignatius Jacques Reilly");
    canvas.addMetadata("Location", "New Orleans");
    PropertyValue key = new PropertyValue();
    key.addValue(Locale.ENGLISH, "Key");
    key.addValue(Locale.GERMAN, "Schlüssel");
    key.addValue(Locale.CHINESE, "钥");
    PropertyValue value = new PropertyValue();
    value.addValue(Locale.ENGLISH, "A value", "Another value");
    value.addValue(Locale.GERMAN, "Ein Wert", "Noch ein Wert");
    value.addValue(Locale.CHINESE, "值", "另值");
    canvas.addMetadata(new MetadataEntry(key, value));

    // Other stuff
    assertThatThrownBy(() -> canvas.addViewingHint(ViewingHint.INDIVIDUALS))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Resources of type '%s' do not support the '%s' viewing hint.",
            "sc:Canvas", "individuals");
    canvas.addViewingHint(ViewingHint.NON_PAGED);

    // Licensing/Attribution
    canvas.addLicense("http://rightsstatements.org/vocab/NoC-NC/1.0/");
    canvas.addAttribution("Some fictional institution");
    canvas.addLogo("http://some.uri/logo.jpg");
    canvas.addLogo(
        new ImageContent(new ImageService("http://some.uri/iiif/logo", ImageApiProfile.LEVEL_ONE)));

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(canvas);
    DocumentContext ctx = JsonPath.parse(json);
    JsonPathAssert.assertThat(ctx)
        .jsonPathAsString("['@context']")
        .isEqualTo("http://iiif.io/api/presentation/2/context.json");
    JsonPathAssert.assertThat(ctx).jsonPathAsString("['@id']").isEqualTo("http://some.uri");
    JsonPathAssert.assertThat(ctx).jsonPathAsString("['@type']").isEqualTo("sc:Canvas");
    JsonPathAssert.assertThat(ctx).jsonPathAsString("label").isEqualTo("A label");

    // "on" on the image annotation should be a plain string, not a complex object
    JsonPathAssert.assertThat(ctx).jsonPathAsString("images[0].on").isEqualTo("http://some.uri");

    // Only the top-level object should have a IIIF Presentation API context
    assertThat(
            ((JSONArray) ctx.read("..['@context']"))
                .stream().filter(c -> c.equals("http://iiif.io/api/presentation/2/context.json")))
        .hasSize(1);

    Canvas parsedCanvas = mapper.readValue(json, Canvas.class);
    assertThat(parsedCanvas).usingRecursiveComparison().isEqualTo(canvas);
  }

  @Test
  public void testNavDate() throws IOException {
    OffsetDateTime navDate = OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    Manifest manifest = new Manifest("http://some.uri", "A label for the Manifest");
    manifest.setNavDate(navDate);
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(manifest);
    DocumentContext ctx = JsonPath.parse(json);
    JsonPathAssert.assertThat(ctx).jsonPathAsString("navDate").isEqualTo("1970-01-01T00:00:00Z");
    assertThat(mapper.readValue(json, Manifest.class).getNavDate())
        .isEqualTo(manifest.getNavDate());

    Collection coll = new Collection("http://some.uri", "A label for the Collection");
    coll.setNavDate(navDate);
    json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(coll);
    ctx = JsonPath.parse(json);
    JsonPathAssert.assertThat(ctx).jsonPathAsString("navDate").isEqualTo("1970-01-01T00:00:00Z");
    assertThat(mapper.readValue(json, Collection.class).getNavDate()).isEqualTo(navDate);
  }

  @Test
  public void testMetadataEntryWithNoValue() throws Exception {
    PropertyValue label = new PropertyValue("Key");
    PropertyValue value = new PropertyValue();
    MetadataEntry meta = new MetadataEntry(label, value);
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(meta);
    MetadataEntry parsed = mapper.readValue(json, MetadataEntry.class);
    assertThat(parsed.getLabel().getValues()).containsExactly("Key");
    assertThat(parsed.getValue()).isNull();
  }
}

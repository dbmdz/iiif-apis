package de.digitalcollections.iiif.model.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.GenericService;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.MetadataEntry;
import de.digitalcollections.iiif.model.MimeType;
import de.digitalcollections.iiif.model.Motivation;
import de.digitalcollections.iiif.model.OtherContent;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.enums.ViewingDirection;
import de.digitalcollections.iiif.model.enums.ViewingHint;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageApiSelector;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.image.TileInfo;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.openannotation.ContentAsText;
import de.digitalcollections.iiif.model.openannotation.CssStyle;
import de.digitalcollections.iiif.model.openannotation.SpecificResource;
import de.digitalcollections.iiif.model.openannotation.SvgSelector;
import de.digitalcollections.iiif.model.sharedcanvas.AnnotationList;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.model.sharedcanvas.Layer;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Range;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class SpecExamplesSerializationTest {

  private ObjectMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  private String readFromResources(String filename) throws IOException {
    return Resources.toString(
        Resources.getResource("spec/presentation/" + filename), Charset.defaultCharset());
  }

  private void assertSerializationEqualsSpec(Object obj, String specFilename)
      throws IOException, JSONException {
    String specJson = readFromResources(specFilename);
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    JSONAssert.assertEquals(specJson, json, true);
  }

  @Test
  public void testFullResponse() throws IOException, JSONException {
    Manifest manifest = new Manifest("http://example.org/iiif/book1/manifest", "Book 1");
    manifest.addMetadata("Author", "Anne Author");
    PropertyValue multiValue = new PropertyValue();
    multiValue.addValue(Locale.ENGLISH, "Paris, circa 1400");
    multiValue.addValue(Locale.FRENCH, "Paris, environ 14eme siecle");
    manifest.addMetadata(new MetadataEntry(new PropertyValue("Published"), multiValue));
    manifest.addDescription(
        "A longer description of this example book. It should give some real information.");
    manifest.setNavDate(OffsetDateTime.of(1856, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    manifest.addLicense("https://creativecommons.org/publicdomain/zero/1.0/");
    manifest.addAttribution("Provided by Example Organization");

    manifest.addService(
        new GenericService(
            "http://example.org/ns/jsonld/context.json",
            "http://example.org/service/example",
            "http://example.org/docs/example-service.html"));
    manifest.addSeeAlso(
        new OtherContent(
            "http://example.org/library/catalog/book1.marc",
            "application/marc",
            "http://example.org/profiles/marc21"));
    OtherContent rendering = new OtherContent("http://example.org/iiif/book1.pdf");
    rendering.addLabel("Download as PDF");
    manifest.addRendering(rendering);
    manifest.addWithin(new Collection("http://example.org/collections/books/"));

    Sequence seq = new Sequence("http://example.org/iiif/book1/sequence/normal");
    seq.addLabel("Current Page Order");
    seq.setViewingDirection(ViewingDirection.LEFT_TO_RIGHT);
    seq.addViewingHint(ViewingHint.PAGED);

    Canvas canvas = new Canvas("http://example.org/iiif/book1/canvas/p1", "p. 1");
    ImageContent img = new ImageContent("http://example.org/iiif/book1/res/page1.jpg");
    img.addService(
        new ImageService("http://example.org/images/book1-page1", ImageApiProfile.LEVEL_ONE));
    img.setWidth(1500);
    img.setHeight(2000);
    canvas.addImage(img);
    AnnotationList otherContent = new AnnotationList("http://example.org/iiif/book1/list/p1");
    otherContent.addWithin(new Layer("http://example.org/iiif/book1/layer/l1", "Example Layer"));
    canvas.addOtherContent(otherContent);
    canvas.setWidth(750);
    canvas.setHeight(1000);
    seq.addCanvas(canvas);

    canvas = new Canvas("http://example.org/iiif/book1/canvas/p2", "p. 2");
    canvas.setWidth(750);
    canvas.setHeight(1000);
    img = new ImageContent("http://example.org/images/book1-page2/full/1500,2000/0/default.jpg");
    ImageService imgService =
        new ImageService("http://example.org/images/book1-page2", ImageApiProfile.LEVEL_ONE);
    imgService.setWidth(6000);
    imgService.setHeight(8000);
    TileInfo tileInfo = new TileInfo(512);
    tileInfo.addScaleFactor(1, 2, 4, 8, 16);
    imgService.addTile(tileInfo);
    img.addService(imgService);
    img.setWidth(1500);
    img.setHeight(2000);
    canvas.addImage(img);
    otherContent = new AnnotationList("http://example.org/iiif/book1/list/p2");
    otherContent.addWithin(new Layer("http://example.org/iiif/book1/layer/l1"));
    canvas.addOtherContent(otherContent);
    seq.addCanvas(canvas);

    canvas = new Canvas("http://example.org/iiif/book1/canvas/p3", "p. 3");
    canvas.setWidth(750);
    canvas.setHeight(1000);
    img = new ImageContent("http://example.org/iiif/book1/res/page3.jpg");
    img.addService(
        new ImageService("http://example.org/images/book1-page3", ImageApiProfile.LEVEL_ONE));
    img.setWidth(1500);
    img.setHeight(2000);
    canvas.addImage(img);
    otherContent = new AnnotationList("http://example.org/iiif/book1/list/p3");
    otherContent.addWithin(new Layer("http://example.org/iiif/book1/layer/l1"));
    canvas.addOtherContent(otherContent);
    seq.addCanvas(canvas);
    manifest.addSequence(seq);

    Range introRange = new Range("http://example.org/iiif/book1/range/r1", "Introduction");
    introRange.addCanvas(
        seq.getCanvases().get(0).getIdentifier().toString(),
        seq.getCanvases().get(1).getIdentifier().toString(),
        seq.getCanvases().get(2).getIdentifier().toString() + "#xywh=0,0,750,300");
    manifest.addRange(introRange);

    assertSerializationEqualsSpec(manifest, "full_response.json");
  }

  @Test
  public void testAnnotationList() throws Exception {
    AnnotationList list = new AnnotationList("http://example.org/iiif/book1/list/p1");

    Annotation first = new Annotation(Motivation.PAINTING);
    first.setOn(new Canvas("http://example.org/iiif/book1/canvas/p1"));
    OtherContent resource = new OtherContent("http://example.org/iiif/book1/res/music.mp3");
    resource.setType("dctypes:Sound");
    first.setResource(resource);

    Annotation second = new Annotation(Motivation.PAINTING);
    second.setOn(new Canvas("http://example.org/iiif/book1/canvas/p1"));
    OtherContent secondResource =
        new OtherContent(
            "http://example.org/iiif/book1/res/tei-text-p1.xml", "application/tei+xml");
    secondResource.setType("dctypes:Text");
    second.setResource(secondResource);

    list.addResource(first, second);
    assertSerializationEqualsSpec(list, "annotationList.json");
  }

  @Test
  public void testAnnotationListWithTranscription() throws Exception {
    AnnotationList annotationList = new AnnotationList("http://example.org/iiif/book1/list/p1");

    Annotation annotation =
        new Annotation("http://example.org/iiif/book1/annotation/anno1", Motivation.PAINTING);
    annotation.setOn(new Canvas("http://example.org/iiif/book1/canvas/p1#xywh=100,100,300,300"));

    SpecificResource specificResource = new SpecificResource();
    ContentAsText full = new ContentAsText("Here starts book one...");
    full.setFormat(MimeType.fromTypename("text/plain"));
    full.setLanguage(Locale.ENGLISH);
    SvgSelector selector = new SvgSelector("<svg xmlns=\"...\"><path d=\"...\"/></svg>");
    specificResource.setFull(full);
    specificResource.setSelector(selector);

    annotation.setResource(specificResource);

    annotationList.addResource(annotation);

    assertSerializationEqualsSpec(annotationList, "annotationListWithTranscription.json");
  }

  @Test
  public void testAnnotationListPage() throws Exception {
    AnnotationList list = new AnnotationList("http://example.org/iiif/book1/list/l1");
    list.setStartIndex(0);
    list.addWithin(new Layer("http://example.org/iiif/book1/layer/transcription"));
    list.setNext(new AnnotationList("http://example.org/iiif/book1/list/l2"));
    list.setResources(new ArrayList<>());
    assertSerializationEqualsSpec(list, "annotationListPage.json");
  }

  @Test
  public void testAnnotationListWithinLayer() throws Exception {
    AnnotationList list = new AnnotationList("http://example.org/iiif/book1/list/l1");
    Layer within = new Layer("http://example.org/iiif/book1/layer/transcription");
    within.addLabel("Diplomatic Transcription");
    list.addWithin(within);
    assertSerializationEqualsSpec(list, "annotationListWithinLayer.json");
  }

  @Test
  public void testAnnotationWithChoice() throws Exception {
    Annotation anno =
        new Annotation("http://example.org/iiif/book1/annotation/anno1", Motivation.PAINTING);
    anno.setOn(new Canvas("http://example.org/iiif/book1/canvas/p1"));
    ImageContent color = new ImageContent("http://example.org/iiif/book1/res/page1.jpg");
    color.setFormat((MimeType) null); // Spec example doesn't have format, so we skip it
    color.addLabel("Color");
    ImageContent blackWhite =
        new ImageContent("http://example.org/iiif/book1/res/page1-blackandwhite.jpg");
    blackWhite.setFormat((MimeType) null); // Spec example doesn't have format, so we skip it
    blackWhite.addLabel("Black and White");
    color.addAlternative(blackWhite);
    anno.setResource(color);
    assertSerializationEqualsSpec(anno, "annotationWithChoice.json");
  }

  @Test
  public void testCanvas() throws Exception {
    // NOTE: This test uses a modified canvas from the specification, namely we skip the @type on
    // `thumbnail`, since the field will always have dctype:Image as its contents.
    Canvas canvas = new Canvas("http://example.org/iiif/book1/canvas/p1", "p. 1");
    canvas.setWidth(750);
    canvas.setHeight(1000);
    ImageContent thumb = new ImageContent("http://example.org/iiif/book1/canvas/p1/thumb.jpg");
    thumb.setFormat((MimeType) null); // Spec example doesn't have format, so we skip it
    thumb.setWidth(150);
    thumb.setHeight(200);
    canvas.addThumbnail(thumb);
    canvas.addOtherContent(new AnnotationList("http://example.org/iiif/book1/list/p1"));
    canvas.setImages(new ArrayList<>());
    assertSerializationEqualsSpec(canvas, "canvas.json");
  }

  @Test
  public void testCollection() throws Exception {
    Collection coll =
        new Collection(
            "http://example.org/iiif/collection/top",
            "Top Level Collection for Example Organization");
    coll.addViewingHint(ViewingHint.TOP);
    coll.addDescription("Description of Collection");
    coll.addAttribution("Provided by Example Organization");

    Collection sub1 = new Collection("http://example.org/iiif/collection/sub1", "Sub-Collection 1");
    Collection subsub1 =
        new Collection("http://example.org/iiif/collection/part1", "My Multi-volume Set");
    subsub1.addViewingHint(ViewingHint.MULTI_PART);
    Collection subsub3 =
        new Collection("http://example.org/iiif/collection/part2", "My Sub Collection");
    subsub3.addViewingHint(ViewingHint.INDIVIDUALS);
    sub1.addMember(
        subsub1, new Manifest("http://example.org/iiif/book1/manifest1", "My Book"), subsub3);
    coll.addCollection(
        sub1, new Collection("http://example.org/iiif/collection/part2", "Sub Collection 2"));
    coll.addManifest(new Manifest("http://example.org/iiif/book1/manifest", "Book 1"));
    assertSerializationEqualsSpec(coll, "collection.json");
  }

  @Test
  public void testCollectionPage() throws Exception {
    Collection coll = new Collection("http://example.org/iiif/collection/c1");
    coll.addWithin(new Collection("http://example.org/iiif/collection/top"));
    coll.setStartIndex(0);
    coll.setNext(new Collection("http://example.org/iiif/collection/c2"));
    coll.setManifests(Collections.emptyList());
    assertSerializationEqualsSpec(coll, "collectionPage.json");
  }

  @Test
  public void testEmbeddedContent() throws Exception {
    Annotation anno =
        new Annotation("http://example.org/iiif/book1/annotation/p1", Motivation.PAINTING);
    anno.setOn(new Canvas("http://example.org/iiif/book1/canvas/p1#xywh=100,150,500,25"));
    ContentAsText res = new ContentAsText("Here starts book one...");
    res.setFormat(MimeType.fromTypename("text/plain"));
    res.setLanguage(Locale.ENGLISH);
    anno.setResource(res);
    assertSerializationEqualsSpec(anno, "embeddedContent.json");
  }

  @Test
  public void testImageResource() throws Exception {
    Annotation anno =
        new Annotation("http://example.org/iiif/book1/annotation/p0001-image", Motivation.PAINTING);
    anno.setOn(new Canvas("http://example.org/iiif/book1/canvas/p1"));
    ImageContent img = new ImageContent("http://example.org/iiif/book1/res/page1.jpg");
    img.setWidth(1500);
    img.setHeight(2000);
    img.addService(
        new ImageService("http://example.org/images/book1-page1", ImageApiProfile.LEVEL_TWO));
    anno.setResource(img);
    assertSerializationEqualsSpec(anno, "imageResource.json");
  }

  @Test
  public void testImageSelector() throws Exception {
    ImageApiSelector selector = new ImageApiSelector();
    selector.setRegion("50,50,1250,1850");
    assertSerializationEqualsSpec(selector, "imageSelector.json");
  }

  @Test
  public void testLayer() throws Exception {
    Layer layer =
        new Layer("http://example.org/iiif/book1/layer/transcription", "Diplomatic Transcription");
    layer.addOtherContent(
        "http://example.org/iiif/book1/list/l1",
        "http://example.org/iiif/book1/list/l2",
        "http://example.org/iiif/book1/list/l3",
        "http://example.org/iiif/book1/list/l4");
    assertSerializationEqualsSpec(layer, "layer.json");
  }

  @Test
  public void testManifest() throws Exception {
    Manifest manifest = new Manifest("http://example.org/iiif/book1/manifest", "Book 1");
    manifest.addDescription(
        "A longer description of this example book. It should give some real information.");

    manifest.addMetadata("Author", "Anne Author");
    PropertyValue multiLangValue = new PropertyValue();
    multiLangValue.addValue(Locale.ENGLISH, "Paris, circa 1400");
    multiLangValue.addValue(Locale.FRENCH, "Paris, environ 1400");
    manifest.addMetadata(new MetadataEntry(new PropertyValue("Published"), multiLangValue));
    PropertyValue multiValue = new PropertyValue();
    multiValue.addValue("Text of note 1", "Text of note 2");
    manifest.addMetadata(new MetadataEntry(new PropertyValue("Notes"), multiValue));
    manifest.addMetadata(
        "Source",
        "<span>From: <a href=\"http://example.org/db/1.html\">Some Collection</a></span>");

    ImageContent thumb =
        new ImageContent("http://example.org/images/book1-page1/full/80,100/0/default.jpg");
    thumb.addService(
        new ImageService("http://example.org/images/book1-page1", ImageApiProfile.LEVEL_ONE));
    thumb.setFormat((MimeType) null);
    manifest.addThumbnail(thumb);

    manifest.setViewingDirection(ViewingDirection.RIGHT_TO_LEFT);
    manifest.addViewingHint(ViewingHint.PAGED);
    manifest.setNavDate(OffsetDateTime.of(1856, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));

    manifest.addLicense("http://rightsstatements.org/vocab/NoC-NC/1.0/");
    manifest.addAttribution("Provided by Example Organization");

    ImageContent logo = new ImageContent("http://example.org/logos/institution1.jpg");
    logo.addService(
        new ImageService("http://example.org/service/inst1", ImageApiProfile.LEVEL_TWO));
    logo.setFormat((MimeType) null);
    manifest.addLogo(logo);

    manifest.addRelated(new OtherContent("http://example.org/videos/video-book1.mpg"));
    manifest.addService(
        new GenericService(
            "http://example.org/ns/jsonld/context.json",
            "http://example.org/service/example",
            "http://example.org/docs/example-service.html"));
    manifest.addSeeAlso(
        new OtherContent(
            "http://example.org/library/catalog/book1.xml",
            "text/xml",
            "http://example.org/profiles/bibliographic"));
    // FIXME: "text/xml" does not seem to be supported by our Mime class, so we set it manually

    OtherContent pdfRendering = new OtherContent("http://example.org/iiif/book1.pdf");
    pdfRendering.addLabel("Download as PDF");
    manifest.addRendering(pdfRendering);

    manifest.addSequence(
        new Sequence("http://example.org/iiif/book1/sequence/normal", "Current Page Order"));
    manifest.addWithin(new Collection("http://example.org/collections/books/"));

    assertSerializationEqualsSpec(manifest, "manifest.json");
  }

  @Test
  public void testManifestWithRanges() throws Exception {
    Manifest manifest = new Manifest("http://example.org/iiif/book1/manifest");
    Range tocRange = new Range("http://example.org/iiif/book1/range/r0", "Table of Contents");
    tocRange.addViewingHint(ViewingHint.TOP);
    Range memberRange = new Range("http://example.org/iiif/book1/range/r1", "Introduction");
    memberRange.setContentLayer("http://example.org/iiif/book1/layer/introTexts");
    tocRange.addMember(
        new Canvas("http://example.org/iiif/book1/canvas/cover", "Front Cover"),
        memberRange,
        new Canvas("http://example.org/iiif/book1/canvas/backCover", "Back Cover"));

    Range canvasRange = new Range("http://example.org/iiif/book1/range/r1", "Introduction");
    canvasRange.addRange("http://example.org/iiif/book1/range/r1-1");
    canvasRange.addCanvas(
        "http://example.org/iiif/book1/canvas/p1",
        "http://example.org/iiif/book1/canvas/p2",
        "http://example.org/iiif/book1/canvas/p3#xywh=0,0,750,300");

    Range lastRange = new Range("http://example.org/iiif/book1/range/r1-1", "Objectives and Scope");
    lastRange.addCanvas("http://example.org/iiif/book1/canvas/p2#xywh=0,0,500,500");

    manifest.addRange(tocRange, canvasRange, lastRange);
    manifest.setSequences(Collections.emptyList());

    assertSerializationEqualsSpec(manifest, "manifestWithRanges.json");
  }

  @Test
  public void testPagedCollection() throws Exception {
    Collection coll =
        new Collection("http://example.org/iiif/collection/top", "Example Big Collection");
    coll.setTotal(9316290);
    coll.setFirst(new Collection("http://example.org/iiif/collection/c1"));
    assertSerializationEqualsSpec(coll, "pagedCollection.json");
  }

  @Test
  public void testPagedLayer() throws Exception {
    Layer layer =
        new Layer(
            "http://example.org/iiif/book1/layer/transcription", "Example Long Transcription");
    layer.setTotal(496923);
    layer.setFirst(new AnnotationList("http://example.org/iiif/book1/list/l1"));
    assertSerializationEqualsSpec(layer, "pagedLayer.json");
  }

  @Test
  public void testSequence() throws Exception {
    Sequence seq =
        new Sequence("http://example.org/iiif/book1/sequence/normal", "Current Page Order");
    seq.setViewingDirection(ViewingDirection.LEFT_TO_RIGHT);
    seq.addViewingHint(ViewingHint.PAGED);
    seq.setStartCanvas(URI.create("http://example.org/iiif/book1/canvas/p2"));

    seq.addCanvas(
        new Canvas("http://example.org/iiif/book1/canvas/p1", "p. 1"),
        new Canvas("http://example.org/iiif/book1/canvas/p2", "p. 2"),
        new Canvas("http://example.org/iiif/book1/canvas/p3", "p. 3"));
    assertSerializationEqualsSpec(seq, "sequence.json");
  }

  @Test
  public void testSpecificResourceAnnotation() throws Exception {
    Annotation anno =
        new Annotation("http://example.org/iiif/book1/annotation/anno1", Motivation.PAINTING);
    anno.setOn(new Canvas("http://www.example.org/iiif/book1/canvas/p1#xywh=0,0,600,900"));
    SpecificResource res =
        new SpecificResource(
            "http://www.example.org/iiif/book1-page1/50,50,1250,1850/full/0/default.jpg");
    ImageContent full =
        new ImageContent("http://example.org/iiif/book1-page1/full/full/0/default.jpg");
    full.addService(
        new ImageService("http://example.org/iiif/book1-page1", ImageApiProfile.LEVEL_TWO));
    full.setFormat((MimeType) null);
    res.setFull(full);
    ImageApiSelector selector = new ImageApiSelector();
    selector.setRegion("50,50,1250,1850");
    res.setSelector(selector);
    anno.setResource(res);
    assertSerializationEqualsSpec(anno, "specificResourceAnnotation.json");
  }

  @Test
  public void testStylesheet() throws Exception {
    Annotation anno =
        new Annotation("http://example.org/iiif/book1/annotation/anno1", Motivation.PAINTING);
    anno.setOn(new Canvas("http://example.org/iiif/book1/canvas/p1#xywh=100,150,500,30"));
    anno.setStylesheet(new CssStyle(".red {color: red;}"));
    SpecificResource res = new SpecificResource();
    res.setStyle("red");
    res.setFull(new ContentAsText("Rubrics are Red, ..."));
    anno.setResource(res);
    assertSerializationEqualsSpec(anno, "stylesheet.json");
  }

  @Test
  public void testSvgSelector() throws Exception {
    Annotation anno =
        new Annotation("http://example.org/iiif/book1/annotation/anno1", Motivation.PAINTING);
    anno.setOn(new Canvas("http://example.org/iiif/book1/canvas/p1#xywh=100,100,300,300"));
    SpecificResource res = new SpecificResource();
    res.setFull(new ImageContent("http://example.org/iiif/book1/res/page1.jpg"));
    ((ImageContent) res.getFull()).setFormat((MimeType) null);
    res.setSelector(new SvgSelector("<svg xmlns=\"...\"><path d=\"...\"/></svg>"));
    anno.setResource(res);
    assertSerializationEqualsSpec(anno, "svgSelector.json");
  }
}

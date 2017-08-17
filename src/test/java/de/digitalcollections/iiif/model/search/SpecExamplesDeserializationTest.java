package de.digitalcollections.iiif.model.sharedcanvas.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.openannotation.ContentAsText;
import de.digitalcollections.iiif.model.search.AutocompleteService;
import de.digitalcollections.iiif.model.search.ContentSearchService;
import de.digitalcollections.iiif.model.search.SearchHit;
import de.digitalcollections.iiif.model.search.SearchLayer;
import de.digitalcollections.iiif.model.search.SearchResult;
import de.digitalcollections.iiif.model.search.Term;
import de.digitalcollections.iiif.model.search.TermList;
import de.digitalcollections.iiif.model.sharedcanvas.AnnotationList;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecExamplesDeserializationTest {
  private ObjectMapper mapper;

  @Before
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  private <T> T readFromResources(String filename, Class<T> clz) throws IOException {
    return mapper.readValue(
        Resources.getResource("spec/search/" + filename), clz);
  }

  @Test
  public void testAutocomplete() throws Exception {
    ContentSearchService service = readFromResources("autocomplete.json", ContentSearchService.class);
    assertThat(service.getIdentifier().toString())
        .isEqualTo("http://example.org/services/identifier/search");
    assertThat(service.getAutocompleteService())
        .isInstanceOf(AutocompleteService.class);
    assertThat(service.getAutocompleteService().getIdentifier().toString())
        .isEqualTo("http://example.org/services/identifier/autocomplete");
  }

  @Test
  public void testBasicSearch() throws Exception {
    SearchResult result = readFromResources("basicSearch.json", SearchResult.class);
    assertThat(result.getContext())
        .containsExactly(Resource.CONTEXT, SearchResult.CONTEXT);
    assertThat(result.getResources()).hasSize(1);
    assertThat(result.getHits()).hasSize(1);
    SearchHit hit = result.getHits().get(0);
    assertThat(hit.getAnnotations()).hasSize(1);
    assertThat(hit.getAnnotations().get(0).getIdentifier().toString())
        .isEqualTo("http://example.org/identifier/annotation/anno1");
  }

  @Test
  public void testFullResponse() throws Exception {
    TermList terms = readFromResources("fullResponse.json", TermList.class);
    assertThat(terms.getIgnored()).containsExactly("user");
    assertThat(terms.getTerms()).hasSize(4);
    assertThat(terms.getTerms().get(0))
        .hasFieldOrPropertyWithValue("match", "bird")
        .hasFieldOrPropertyWithValue("url", URI.create("http://example.org/service/identifier/search?motivation=painting&q=bird"))
        .hasFieldOrPropertyWithValue("count", 15);
  }

  @Test
  public void testFullResponseWithLabels() throws Exception {
    TermList terms = readFromResources("fullResponseWithLabels.json", TermList.class);
    assertThat(terms.getTerms().stream().map(Term::getLabelString))
        .containsExactly("bird", "biro");
  }

  @Test
  public void testHighlighting() throws Exception {
    SearchResult result = readFromResources("highlighting.json", SearchResult.class);
    assertThat(result.getResources()).hasSize(1);
    Annotation anno = result.getResources().get(0);
    assertThat(anno.getResource())
        .isInstanceOf(ContentAsText.class);
    assertThat(((ContentAsText) anno.getResource()).getChars())
        .isEqualTo("There are two birds in the bush.");
    assertThat(result.getHits()).hasSize(1);
    SearchHit hit = result.getHits().get(0);
    assertThat(hit.getAnnotations()).hasSize(1);
    assertThat(hit.getAnnotations().get(0).getIdentifier())
        .isEqualTo(anno.getIdentifier());

    assertThat(hit.getSelectors()).hasSize(2);
    assertThat(hit.getSelectors().get(0))
        .hasFieldOrPropertyWithValue("exact", "birds")
        .hasFieldOrPropertyWithValue("prefix", "There are two ")
        .hasFieldOrPropertyWithValue("suffix", " in the bush");
    assertThat(hit.getSelectors().get(1))
        .hasFieldOrPropertyWithValue("exact", "bush")
        .hasFieldOrPropertyWithValue("prefix", "two birds in the ")
        .hasFieldOrPropertyWithValue("suffix", ".");
  }

  @Test
  public void testMultiAnnotationHits() throws Exception {
    SearchResult result = readFromResources("multiAnnotationHits.json", SearchResult.class);
    assertThat(result.getResources()).hasSize(2);
    assertThat(result.getResources().get(0).getOn())
        .isInstanceOf(Canvas.class);
    assertThat(result.getResources().get(1).getResource())
        .isInstanceOf(ContentAsText.class);
    assertThat(((ContentAsText) result.getResources().get(1).getResource()).getChars())
        .isEqualTo("is worth two in the bush");
    assertThat(result.getHits()).hasSize(1);
    SearchHit hit = result.getHits().get(0);
    assertThat(hit.getAnnotations().stream().map(Resource::getIdentifier))
        .containsExactlyElementsOf(result.getResources().stream().map(Resource::getIdentifier).collect(Collectors.toList()));
  }

  @Test
  public void testSnippets() throws Exception {
    SearchResult result = readFromResources("snippets.json", SearchResult.class);
    assertThat(result.getResources()).hasSize(1);
    assertThat(result.getResources().get(0).getResource())
        .isInstanceOf(ContentAsText.class);
    assertThat(((ContentAsText) result.getResources().get(0).getResource()).getChars())
        .isEqualTo("birds");

    assertThat(result.getHits()).hasSize(1);
    assertThat(result.getHits().get(0).getAnnotations()).hasSize(1);
    assertThat(result.getHits().get(0))
        .hasFieldOrPropertyWithValue("before", "There are two ")
        .hasFieldOrPropertyWithValue("after", " in the bush");
  }

  @Test
  public void testTargetResource() throws Exception {
    AnnotationList list = readFromResources("targetResource.json", AnnotationList.class);
    assertThat(list.getResources()).hasSize(1);
    Annotation anno = list.getResources().get(0);
    assertThat(((ContentAsText) anno.getResource()).getChars())
        .isEqualTo("A bird in the hand is worth two in the bush");
    assertThat(anno.getOn()).isInstanceOf(Canvas.class);
    assertThat(anno.getOn().getWithin().get(0)).isInstanceOf(Manifest.class);
    assertThat(anno.getOn().getWithin().get(0).getLabelString())
        .isEqualTo("Example Manifest");
  }

  @Test
  public void testWithIgnored() throws Exception {
    SearchResult result = readFromResources("withIgnored.json", SearchResult.class);
    assertThat(result.getWithin().get(0)).isInstanceOf(SearchLayer.class);
    SearchLayer layer = (SearchLayer) result.getWithin().get(0);
    assertThat(layer.getTotal()).isEqualTo(125);
    assertThat(layer.getIgnored()).containsExactly("user");
    assertThat(result.getNext()).isInstanceOf(SearchResult.class);
    assertThat(result.getStartIndex()).isEqualTo(0);
  }
}

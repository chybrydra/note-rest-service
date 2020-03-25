package pl.lukaszgrymulski.noteservice.it;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

@IfProfileValue(name="profile", value="test")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class NoteIntegrationTestBase {

    @LocalServerPort
    protected int port;

    protected TestRestTemplate restTemplate = new TestRestTemplate();

    protected HttpHeaders headers = new HttpHeaders();

    protected String apiErrorJsonString = "{message: \".+\", errorPath: \".+\", debugMessage: \".+\", status: \".+\"}";

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    protected CustomComparator getApiErrorJsonComparator() {
        RegularExpressionValueMatcher<Object> comparator1 = new RegularExpressionValueMatcher<>();
        Customization customization = new Customization("***", comparator1);
        return new CustomComparator(JSONCompareMode.LENIENT, customization);
    }

    protected ResponseEntity<String> sendPost(NotePersistDTO notePersistDTO) {
        String postUrl = createURLWithPort("/api/notes");
        return restTemplate.exchange(postUrl,
                HttpMethod.POST,
                new HttpEntity<>(notePersistDTO, headers),
                String.class);
    }

    protected ResponseEntity<String> sendPut(NotePersistDTO notePersistDTO, int noteId) {
        String putUrl = createURLWithPort("/api/notes/"+noteId);
        return restTemplate.exchange(putUrl,
                HttpMethod.PUT,
                new HttpEntity<>(notePersistDTO, headers),
                String.class);
    }

    protected ResponseEntity<String> sendGetAllRecentNoteVersions() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        return restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.GET, entity, String.class);
    }

    protected ResponseEntity<String> sendGetHistoryForNote(int noteId) {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        return restTemplate.exchange(createURLWithPort("/api/notes/"+noteId+"/history"),
                HttpMethod.GET, entity, String.class);
    }

    protected ResponseEntity<String> sendGetNote(int noteId) {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        return restTemplate.exchange(createURLWithPort("/api/notes/"+noteId),
                HttpMethod.GET, entity, String.class);
    }

    protected ResponseEntity<String> sendDeleteNote(int noteId) {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        return restTemplate.exchange(createURLWithPort("/api/notes/"+noteId),
                HttpMethod.DELETE, entity, String.class);
    }
}

package pl.lukaszgrymulski.noteservice.it;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

import static org.junit.Assert.assertEquals;

@IfProfileValue(name="profile", value="test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoteIntegrationTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Test
    public void test01liquibaseInitializedDatabaseReturns3Notes() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.GET, entity, String.class);
        String expected = "[{},{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void test02addingNoteShouldIncrementNoteAmountFrom3To4() throws JSONException {
        String urlWithPort = createURLWithPort("/api/notes");

        NotePersistDTO notePersistDTO = new NotePersistDTO(null, "title-test","content-test");
        HttpEntity<NotePersistDTO> postEntity = new HttpEntity<NotePersistDTO>(notePersistDTO, headers);
        restTemplate.exchange(urlWithPort, HttpMethod.POST, postEntity, String.class);

        HttpEntity<String> getEntity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                urlWithPort, HttpMethod.GET, getEntity, String.class);
        String expected = "[{},{},{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        String expectedWithId = "[{id:1},{id:2},{id:3},{id:4}]";
        JSONAssert.assertEquals(expectedWithId, response.getBody(), false);
        String notExpected = "[{},{},{}]";
        JSONAssert.assertNotEquals(notExpected, response.getBody(), false);
    }

    @Test
    public void test03addingNoteShouldReturnThisNote() throws JSONException {
        NotePersistDTO notePersistDTO = new NotePersistDTO(null, "title-test", "content-test");
        HttpEntity<NotePersistDTO> entity = new HttpEntity<NotePersistDTO>(notePersistDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.POST, entity, String.class);

        String expected = "{title:title-test,content:content-test}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        String responseContentType = response.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0);
        assertEquals(MediaType.APPLICATION_JSON_VALUE, responseContentType.split(";")[0]);
    }

    @Test
    public void test04noteWithId1ShouldHave3Versions() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes/1/history"),
                HttpMethod.GET, entity, String.class);
        String expected = "[{},{},{}]";
        String expectedExact = "[{id:1,version:1},{id:1,version:2},{id:1,version:3}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        JSONAssert.assertEquals(expectedExact, response.getBody(), false);
    }

    @Test
    public void test05onlyRecentNoteVersionShouldBeReturned() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes/1"),
                HttpMethod.GET, entity, String.class);
        String expected = "{id:1,version:3}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}

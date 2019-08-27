package pl.lukaszgrymulski.noteservice.it;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.*;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoteRandomFlowIntegrationTest extends NoteIntegrationTest {

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

    @Test
    public void test06deleteNoteShouldReturnNewEmptyNoteVersion() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes/1"),
                HttpMethod.DELETE, entity, String.class);
        String expected = "{id:1,version:4}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void test07note1ShouldReturnApiError() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes/1"),
                HttpMethod.GET, entity, String.class);
        String expected = apiErrorJsonString;
        JSONAssert.assertEquals(expected, response.getBody(), getApiErrorJsonComparator());
    }

    @Test
    public void test08afterFirstNoteDeletionThereShouldBe4NotesLeft() throws JSONException {
        String urlWithPort = createURLWithPort("/api/notes");
        HttpEntity<String> getEntity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                urlWithPort, HttpMethod.GET, getEntity, String.class);
        String expected = "[{},{},{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        String expectedWithId = "[{id:2},{id:3},{id:4},{id:5}]";
        JSONAssert.assertEquals(expectedWithId, response.getBody(), false);
        String notExpected = "[{},{},{}]";
        JSONAssert.assertNotEquals(notExpected, response.getBody(), false);
    }

    @Test
    public void test09firstNoteHistoryShouldStillBeAccessibleWith4Versions() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes/1/history"),
                HttpMethod.GET, entity, String.class);
        String expected = "[{},{},{},{}]";
        String expectedExact = "[{id:1,version:1},{id:1,version:2},{id:1,version:3},{id:1,version:4}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        JSONAssert.assertEquals(expectedExact, response.getBody(), false);
    }

    @Test
    public void test10secondNoteShouldBeModified() throws JSONException {
        String newTitle = "EDITEDTITLE";
        String newContent = "EDITEDCONTENT";
        NotePersistDTO notePersistDTO = new NotePersistDTO(null, newTitle, newContent);
        HttpEntity<NotePersistDTO> entity = new HttpEntity<NotePersistDTO>(notePersistDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes/2"),
                HttpMethod.PUT, entity, String.class);
        String expected = String.format("{id:2,version:3,title:%s,content:%s}",newTitle,newContent);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void test11thereShouldBeStill4Notes() throws JSONException {
        String urlWithPort = createURLWithPort("/api/notes");
        HttpEntity<String> getEntity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                urlWithPort, HttpMethod.GET, getEntity, String.class);
        String expected = "[{},{},{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        String expectedWithId = "[{id:2},{id:3},{id:4},{id:5}]";
        JSONAssert.assertEquals(expectedWithId, response.getBody(), false);
        String notExpected = "[{},{},{}]";
        JSONAssert.assertNotEquals(notExpected, response.getBody(), false);
    }

    @Test
    public void test12note2ShouldReturnOnlyRecentVersion() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes/2"),
                HttpMethod.GET, entity, String.class);
        String expected = "{id:2,version:3}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
}
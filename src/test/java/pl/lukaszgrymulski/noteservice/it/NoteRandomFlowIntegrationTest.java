package pl.lukaszgrymulski.noteservice.it;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoteRandomFlowIntegrationTest extends NoteIntegrationTestBase {

    @Test
    public void test00populateAndVerifyIf3NotesArePresent() throws JSONException {
        NotePersistDTO notePersistDTO11 = new NotePersistDTO(null, "t1v1","c1v1");
        NotePersistDTO notePersistDTO12 = new NotePersistDTO(null, "t1v2","c1v2");
        NotePersistDTO notePersistDTO13 = new NotePersistDTO(null, "t1v3","c1v3");
        NotePersistDTO notePersistDTO21 = new NotePersistDTO(null, "t2v1","c2v1");
        NotePersistDTO notePersistDTO22 = new NotePersistDTO(null, "t2v2","c2v2");
        NotePersistDTO notePersistDTO31 = new NotePersistDTO(null, "t3v1","c3v1");
        String postUrl = createURLWithPort("/api/notes");
        restTemplate.exchange(postUrl,
                HttpMethod.POST,
                new HttpEntity<>(notePersistDTO11, headers),
                String.class);
        restTemplate.exchange(postUrl,
                HttpMethod.POST,
                new HttpEntity<>(notePersistDTO21, headers),
                String.class);
        restTemplate.exchange(postUrl,
                HttpMethod.POST,
                new HttpEntity<>(notePersistDTO31, headers),
                String.class);
        restTemplate.exchange(postUrl + "/1",
                HttpMethod.PUT,
                new HttpEntity<>(notePersistDTO12, headers),
                String.class);
        restTemplate.exchange(postUrl + "/1",
                HttpMethod.PUT,
                new HttpEntity<>(notePersistDTO13, headers),
                String.class);
        restTemplate.exchange(postUrl + "/2",
                HttpMethod.PUT,
                new HttpEntity<>(notePersistDTO22, headers),
                String.class);
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.GET, entity, String.class);
        String expected = "[{},{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void test01verifyIfNotesHaveExpectedAmountOfVersions() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response1 = restTemplate.exchange(createURLWithPort("/api/notes/1/history"),
                HttpMethod.GET, entity, String.class);
        ResponseEntity<String> response2 = restTemplate.exchange(createURLWithPort("/api/notes/2/history"),
                HttpMethod.GET, entity, String.class);
        ResponseEntity<String> response3 = restTemplate.exchange(createURLWithPort("/api/notes/3/history"),
                HttpMethod.GET, entity, String.class);
        String expected1 = "[{id:1,version:1},{id:1,version:2},{id:1,version:3}]";
        String expected2 = "[{id:2,version:1},{id:2,version:2}]";
        String expected3 = "[{id:3,version:1}]";
        JSONAssert.assertEquals(expected1, response1.getBody(), false);
        JSONAssert.assertEquals(expected2, response2.getBody(), false);
        JSONAssert.assertEquals(expected3, response3.getBody(), false);
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
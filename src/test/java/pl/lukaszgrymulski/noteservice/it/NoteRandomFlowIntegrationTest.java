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
        sendPost(notePersistDTO11);
        sendPost(notePersistDTO21);
        sendPost(notePersistDTO31);
        sendPut(notePersistDTO12,1);
        sendPut(notePersistDTO13,1);
        sendPut(notePersistDTO22,2);
        ResponseEntity<String> response = sendGetAllRecentNoteVersions();
        String expected = "[{},{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void test01verifyIfNotesHaveExpectedAmountOfVersions() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response1 = sendGetHistoryForNote(1);
        ResponseEntity<String> response2 = sendGetHistoryForNote(2);
        ResponseEntity<String> response3 = sendGetHistoryForNote(3);
        String expected1 = "[{id:1,version:1},{id:1,version:2},{id:1,version:3}]";
        String expected2 = "[{id:2,version:1},{id:2,version:2}]";
        String expected3 = "[{id:3,version:1}]";
        JSONAssert.assertEquals(expected1, response1.getBody(), false);
        JSONAssert.assertEquals(expected2, response2.getBody(), false);
        JSONAssert.assertEquals(expected3, response3.getBody(), false);
    }

    @Test
    public void test02addingNoteShouldIncrementNoteAmountFrom3To4() throws JSONException {
        NotePersistDTO notePersistDTO = new NotePersistDTO(null, "title-test","content-test");
        sendPost(notePersistDTO);
        ResponseEntity<String> response = sendGetAllRecentNoteVersions();
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
        ResponseEntity<String> response = sendPost(notePersistDTO);
        String expected = "{title:title-test,content:content-test}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        String responseContentType = response.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0);
        assertEquals(MediaType.APPLICATION_JSON_VALUE, responseContentType.split(";")[0]);
    }

    @Test
    public void test04noteWithId1ShouldHave3Versions() throws JSONException {
        ResponseEntity<String> response = sendGetHistoryForNote(1);
        String expected = "[{},{},{}]";
        String expectedExact = "[{id:1,version:1},{id:1,version:2},{id:1,version:3}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        JSONAssert.assertEquals(expectedExact, response.getBody(), false);
    }

    @Test
    public void test05onlyRecentNoteVersionShouldBeReturned() throws JSONException {
        ResponseEntity<String> response = sendGetNote(1);
        String expected = "{id:1,version:3}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void test06deleteNoteShouldReturnNewEmptyNoteVersion() throws JSONException {
        ResponseEntity<String> response = sendDeleteNote(1);
        String expected = "{id:1,version:4}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void test07note1ShouldReturnApiError() throws JSONException {
        ResponseEntity<String> response = sendGetNote(1);
        String expected = apiErrorJsonString;
        JSONAssert.assertEquals(expected, response.getBody(), getApiErrorJsonComparator());
    }

    @Test
    public void test08afterFirstNoteDeletionThereShouldBe4NotesLeft() throws JSONException {
        ResponseEntity<String> response = sendGetAllRecentNoteVersions();
        String expected = "[{},{},{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        String expectedWithId = "[{id:2},{id:3},{id:4},{id:5}]";
        JSONAssert.assertEquals(expectedWithId, response.getBody(), false);
        String notExpected = "[{},{},{}]";
        JSONAssert.assertNotEquals(notExpected, response.getBody(), false);
    }

    @Test
    public void test09firstNoteHistoryShouldStillBeAccessibleWith4Versions() throws JSONException {
        ResponseEntity<String> response = sendGetHistoryForNote(1);
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
        ResponseEntity<String> response = sendPut(notePersistDTO, 2);
        String expected = String.format("{id:2,version:3,title:%s,content:%s}",newTitle,newContent);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void test11thereShouldBeStill4Notes() throws JSONException {
        ResponseEntity<String> response = sendGetAllRecentNoteVersions();
        String expected = "[{},{},{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        String expectedWithId = "[{id:2},{id:3},{id:4},{id:5}]";
        JSONAssert.assertEquals(expectedWithId, response.getBody(), false);
        String notExpected = "[{},{},{}]";
        JSONAssert.assertNotEquals(notExpected, response.getBody(), false);
    }

    @Test
    public void test12note2ShouldReturnOnlyRecentVersion() throws JSONException {
        ResponseEntity<String> response = sendGetNote(2);
        String expected = "{id:2,version:3}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
}
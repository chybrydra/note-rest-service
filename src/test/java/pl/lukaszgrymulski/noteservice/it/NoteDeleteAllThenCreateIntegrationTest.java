package pl.lukaszgrymulski.noteservice.it;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoteDeleteAllThenCreateIntegrationTest extends NoteIntegrationTestBase {

    @Test

    public void test00populateAndVerifyIf2NotesArePresent() throws JSONException {
        NotePersistDTO notePersistDTO11 = new NotePersistDTO(null, "t1v1","c1v1");
        NotePersistDTO notePersistDTO21 = new NotePersistDTO(null, "t2v1","c2v1");
        sendPost(notePersistDTO11);
        sendPost(notePersistDTO21);
        ResponseEntity<String> response = sendGetAllRecentNoteVersions();
        String expected = "[{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    @Test
    public void test01havingDeletedAllNotesApiErrorShouldBeReturned() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        sendDeleteNote(1);
        sendDeleteNote(2);
        ResponseEntity<String> response = sendGetAllRecentNoteVersions();
        String expected = apiErrorJsonString;
        JSONAssert.assertEquals(expected, response.getBody(), getApiErrorJsonComparator());
    }

    @Test
    public void test02createNewNoteShouldAddNoteWithId3() throws JSONException {
        NotePersistDTO notePersistDTO = new NotePersistDTO(null, "kek","kekson");
        ResponseEntity<String> response = sendPost(notePersistDTO);
        String expected = "{id:3,version:1}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

}

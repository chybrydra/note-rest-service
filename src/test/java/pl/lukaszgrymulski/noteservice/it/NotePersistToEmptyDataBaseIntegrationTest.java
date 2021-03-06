package pl.lukaszgrymulski.noteservice.it;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotePersistToEmptyDataBaseIntegrationTest extends NoteIntegrationTestBase {

    @Test
    public void test01createNewNoteShouldAddNoteWithId1() throws JSONException {
        NotePersistDTO notePersistDTO = new NotePersistDTO(1L, "kek","kekson");
        ResponseEntity<String> response = sendPost(notePersistDTO);
        String expected = "{id:1,version:1}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
}

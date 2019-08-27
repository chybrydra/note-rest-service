package pl.lukaszgrymulski.noteservice.it;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

@TestExecutionListeners({DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NotePersistToEmptyDataBaseIntegrationTest extends NoteIntegrationTest {

    @Test
    public void test01createNewNoteShouldAddNoteWithId1() throws JSONException {
        NotePersistDTO notePersistDTO = new NotePersistDTO(null, "kek","kekson");
        HttpEntity<NotePersistDTO> entity = new HttpEntity<NotePersistDTO>(notePersistDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.POST, entity, String.class);
        String expected = "{id:1,version:1}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
}

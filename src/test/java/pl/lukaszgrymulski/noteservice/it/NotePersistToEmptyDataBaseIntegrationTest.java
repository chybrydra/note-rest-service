package pl.lukaszgrymulski.noteservice.it;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotePersistToEmptyDataBaseIntegrationTest extends NoteIntegrationTestBase {

    @Test
    public void test01createNewNoteShouldAddNoteWithId1() throws JSONException {
        NotePersistDTO notePersistDTO = new NotePersistDTO(1L, "kek","kekson");
        HttpEntity<NotePersistDTO> entity = new HttpEntity<NotePersistDTO>(notePersistDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.POST, entity, String.class);
        String expected = "{id:1,version:1}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
}

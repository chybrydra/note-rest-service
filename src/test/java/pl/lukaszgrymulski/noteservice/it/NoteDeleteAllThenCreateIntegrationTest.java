package pl.lukaszgrymulski.noteservice.it;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NoteDeleteAllThenCreateIntegrationTest extends NoteIntegrationTest {

    @Test
    public void test01havingDeletedAllNotesApiErrorShouldBeReturned() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        restTemplate.exchange(createURLWithPort("/api/notes/1"),
                HttpMethod.DELETE, entity, String.class);
        restTemplate.exchange(createURLWithPort("/api/notes/2"),
                HttpMethod.DELETE, entity, String.class);
        restTemplate.exchange(createURLWithPort("/api/notes/3"),
                HttpMethod.DELETE, entity, String.class);

        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.GET, entity, String.class);
        String expected = apiErrorJsonString;
        JSONAssert.assertEquals(expected, response.getBody(), getApiErrorJsonComparator());
    }

    @Test
    public void test02createNewNoteShouldAddNoteWithId4() throws JSONException {
        NotePersistDTO notePersistDTO = new NotePersistDTO(null, "kek","kekson");
        HttpEntity<NotePersistDTO> entity = new HttpEntity<NotePersistDTO>(notePersistDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.POST, entity, String.class);
        String expected = "{id:4,version:1}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

}

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
public class NoteDeleteAllThenCreateIntegrationTest extends NoteIntegrationTestBase {

    @Test
    public void test00populateAndVerifyIf2NotesArePresent() throws JSONException {
        NotePersistDTO notePersistDTO11 = new NotePersistDTO(null, "t1v1","c1v1");
        NotePersistDTO notePersistDTO21 = new NotePersistDTO(null, "t2v1","c2v1");

        String postUrl = createURLWithPort("/api/notes");
        restTemplate.exchange(postUrl,
                HttpMethod.POST,
                new HttpEntity<>(notePersistDTO11, headers),
                String.class);
        restTemplate.exchange(postUrl,
                HttpMethod.POST,
                new HttpEntity<>(notePersistDTO21, headers),
                String.class);

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.GET, entity, String.class);
        String expected = "[{},{}]";
        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    @Test
    public void test01havingDeletedAllNotesApiErrorShouldBeReturned() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        restTemplate.exchange(createURLWithPort("/api/notes/1"),
                HttpMethod.DELETE, entity, String.class);
        restTemplate.exchange(createURLWithPort("/api/notes/2"),
                HttpMethod.DELETE, entity, String.class);

        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.GET, entity, String.class);
        String expected = apiErrorJsonString;
        JSONAssert.assertEquals(expected, response.getBody(), getApiErrorJsonComparator());
    }

    @Test
    public void test02createNewNoteShouldAddNoteWithId3() throws JSONException {
        NotePersistDTO notePersistDTO = new NotePersistDTO(null, "kek","kekson");
        HttpEntity<NotePersistDTO> entity = new HttpEntity<NotePersistDTO>(notePersistDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/notes"),
                HttpMethod.POST, entity, String.class);
        String expected = "{id:3,version:1}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

}

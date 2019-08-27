package pl.lukaszgrymulski.noteservice.controller;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.lukaszgrymulski.noteservice.NoteServiceApplication;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NoteServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NoteControllerIT {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Test
    public void testRetrieveStudentCourse() throws JSONException {
        NotePersistDTO notePersistDTO = new NotePersistDTO(null, "title-test","content-test");
        HttpEntity<NotePersistDTO> entity = new HttpEntity<NotePersistDTO>(notePersistDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/notes"),
                HttpMethod.POST,
                entity,
                String.class);

        String expected = "{title:title-test,content:content-test}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}

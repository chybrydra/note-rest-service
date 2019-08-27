package pl.lukaszgrymulski.noteservice.it;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 Spring "test" profile at start uses liquibase to initialize database and populate it with data
 from file java/resources/db_changelog/testdata/note.csv
 */

//@IfProfileValue(name="profile", value="test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NoteIntegrationTest {

    @LocalServerPort
    protected int port;

    protected TestRestTemplate restTemplate = new TestRestTemplate();

    protected HttpHeaders headers = new HttpHeaders();

    protected String apiErrorJsonString = "{message: \".+\", errorPath: \".+\", debugMessage: \".+\", status: \".+\"}";

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    protected CustomComparator getApiErrorJsonComparator() {
        RegularExpressionValueMatcher<Object> comparator1 = new RegularExpressionValueMatcher<>();
        Customization customization = new Customization("***", comparator1);
        return new CustomComparator(JSONCompareMode.LENIENT, customization);
    }
}

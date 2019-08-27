package pl.lukaszgrymulski.noteservice.it;

import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;

@IfProfileValue(name="profile", value="test")
public class NoteIntegrationTestBase {

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

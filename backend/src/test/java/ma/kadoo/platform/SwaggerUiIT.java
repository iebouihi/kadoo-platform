package ma.kadoo.platform;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import ma.kadoo.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/** Swagger UI activé (comme en profil local) : consultable sans session. */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "springdoc.swagger-ui.enabled=true")
@Import(TestcontainersConfiguration.class)
class SwaggerUiIT {

    private final HttpClient http = HttpClient.newHttpClient();

    @Value("${local.server.port}")
    int port;

    @Test
    void swagger_ui_est_accessible_sans_session() throws Exception {
        assertThat(get("/swagger-ui/index.html").statusCode()).isEqualTo(200);
    }

    @Test
    void swagger_ui_html_redirige_vers_l_interface_sans_demander_de_session() throws Exception {
        HttpResponse<String> response = get("/swagger-ui.html");

        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.headers().firstValue("Location"))
                .hasValueSatisfying(location -> assertThat(location).contains("/swagger-ui/index.html"));
    }

    @Test
    void les_api_protegees_exigent_toujours_une_session() throws Exception {
        assertThat(get("/api/v1/auth/me").statusCode()).isEqualTo(401);
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }
}

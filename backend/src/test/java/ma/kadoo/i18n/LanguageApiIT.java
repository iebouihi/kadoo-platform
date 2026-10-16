package ma.kadoo.i18n;

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

/** US-039 — critères d'acceptation, de bout en bout sur HTTP et une vraie base PostgreSQL. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class LanguageApiIT {

    @Value("${local.server.port}")
    int port;

    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> get(String path) throws Exception {
        return http.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void liste_fr_ar_en_avec_le_francais_par_defaut_en_premier() throws Exception {
        var response = get("/api/v1/public/languages");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .startsWith("[{\"code\":\"fr-MA\"")
                .contains("\"code\":\"ar-MA\"", "\"direction\":\"RTL\"", "\"code\":\"en\"")
                .contains("\"isDefault\":true");
    }

    @Test
    void les_routes_non_publiques_exigent_une_authentification() throws Exception {
        assertThat(get("/api/v1/admin/partners").statusCode()).isIn(401, 403);
    }

    @Test
    void la_sonde_de_sante_est_publique() throws Exception {
        assertThat(get("/actuator/health/readiness").statusCode()).isEqualTo(200);
    }
}

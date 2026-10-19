package ma.kadoo.identity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Navigateur minimal pour les tests back-office : conserve les cookies (session, jeton CSRF) et renvoie le jeton
 * CSRF dans l'en-tête X-XSRF-TOKEN, comme le fera le frontend. Les cookies sont gérés à la main car le cookie de
 * session est Secure et le serveur de test est en HTTP.
 */
final class BackOfficeHttp {

    private final HttpClient http = HttpClient.newHttpClient();
    private final String base;
    private final Map<String, String> cookies = new LinkedHashMap<>();

    BackOfficeHttp(int port) {
        this.base = "http://localhost:" + port;
    }

    HttpResponse<String> get(String path) throws Exception {
        return send(request(path).GET());
    }

    HttpResponse<String> post(String path, String json) throws Exception {
        if (!cookies.containsKey("XSRF-TOKEN")) {
            get("/api/v1/auth/csrf");
        }
        return postWithoutCsrf(path, json, cookies.get("XSRF-TOKEN"));
    }

    HttpResponse<String> postWithoutCsrf(String path, String json, String csrfHeader) throws Exception {
        HttpRequest.Builder builder = request(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (csrfHeader != null) {
            builder.header("X-XSRF-TOKEN", csrfHeader);
        }
        return send(builder);
    }

    HttpResponse<String> login(String email, String password) throws Exception {
        return post("/api/v1/auth/login", "{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password));
    }

    boolean hasSession() {
        return cookies.containsKey("SESSION");
    }

    private HttpRequest.Builder request(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(base + path));
        if (!cookies.isEmpty()) {
            builder.header(
                    "Cookie",
                    cookies.entrySet().stream()
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .collect(Collectors.joining("; ")));
        }
        return builder;
    }

    private HttpResponse<String> send(HttpRequest.Builder builder) throws Exception {
        HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        for (String header : response.headers().allValues("Set-Cookie")) {
            String[] nameValue = header.split(";", 2)[0].split("=", 2);
            String value = nameValue.length > 1 ? nameValue[1] : "";
            if (value.isEmpty() || header.contains("Max-Age=0")) {
                cookies.remove(nameValue[0]);
            } else {
                cookies.put(nameValue[0], value);
            }
        }
        return response;
    }
}

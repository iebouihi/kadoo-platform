package ma.kadoo.platform;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Ressource absente, ou appartenant à un autre partenaire (NFR-003) : les deux cas donnent la même réponse 404,
 * pour ne rien révéler de l'existence d'une ressource de B à un gestionnaire de A.
 */
public class NotFoundException extends ErrorResponseException {

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND, problem(), null);
    }

    private static ProblemDetail problem() {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setProperty("code", "RESOURCE_NOT_FOUND");
        return problem;
    }
}

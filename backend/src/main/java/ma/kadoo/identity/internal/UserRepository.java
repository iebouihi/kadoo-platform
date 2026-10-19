package ma.kadoo.identity.internal;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import ma.kadoo.identity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Compte à connecter, verrouillé en écriture : deux tentatives simultanées sur le même compte ne peuvent pas
     * perdre un échec (RG4, NFR-011). L'email attendu est déjà normalisé.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where lower(u.email) = :email")
    Optional<User> findForLogin(String email);

    boolean existsByRole(Role role);
}

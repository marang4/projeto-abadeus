package br.com.abadeus.domain.repository;

import br.com.abadeus.domain.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    boolean existsByEmail(String email);
    Optional<Usuarios> findByEmail(String email);
    Optional<Usuarios> findByTokenSenha(String tokenSenha);
    Optional<Usuarios> findByEmailIgnoreCase(String email);

}

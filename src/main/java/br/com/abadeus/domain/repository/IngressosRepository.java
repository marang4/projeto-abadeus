package br.com.abadeus.domain.repository;

import br.com.abadeus.domain.entity.Ingressos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngressosRepository extends JpaRepository<Ingressos, Long> {

    Optional<Ingressos> findByCodIngresso(String codIngresso);
}

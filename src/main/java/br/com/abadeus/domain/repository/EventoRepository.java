package br.com.abadeus.domain.repository;

import br.com.abadeus.domain.entity.Eventos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Eventos, Long> {

}

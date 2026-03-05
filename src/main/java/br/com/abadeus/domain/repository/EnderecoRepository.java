package br.com.abadeus.domain.repository;

import br.com.abadeus.domain.entity.Enderecos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Enderecos, Long> {

}

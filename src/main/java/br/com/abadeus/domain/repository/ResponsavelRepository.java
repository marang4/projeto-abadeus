package br.com.abadeus.domain.repository;

import br.com.abadeus.domain.entity.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    List<Responsavel> findAllByAtivoTrue();

    Optional<Responsavel> findByIdAndAtivoTrue(Long id);

    boolean existsByCpfAndAtivoTrue(String cpf);
}
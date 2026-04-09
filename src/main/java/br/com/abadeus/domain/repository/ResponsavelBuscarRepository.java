package br.com.abadeus.domain.repository;

import br.com.abadeus.domain.entity.ResponsavelBuscar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsavelBuscarRepository extends JpaRepository<ResponsavelBuscar, Long> {

    List<ResponsavelBuscar> findAllByAtivoTrue();

    Optional<ResponsavelBuscar> findByIdAndAtivoTrue(Long id);

    boolean existsByCpfAndAtivoTrue(String cpf);
}
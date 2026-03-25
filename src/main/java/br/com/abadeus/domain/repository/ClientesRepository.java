package br.com.abadeus.domain.repository;

import br.com.abadeus.domain.entity.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Long> {

    boolean existsByCpf(String cpf);
    boolean existsByTelefone(String telefone);

}
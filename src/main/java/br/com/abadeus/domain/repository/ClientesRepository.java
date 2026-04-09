package br.com.abadeus.domain.repository;

import br.com.abadeus.domain.entity.Clientes;
import br.com.abadeus.domain.enums.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Long> {

    Optional<Clientes> findByIdAndDeletadoEmIsNull(Long id);

    Optional<Clientes> findByUsuarioEmailAndDeletadoEmIsNull(String email);

    List<Clientes> findAllByDeletadoEmIsNull();

    List<Clientes> findAllByTipoClienteAndDeletadoEmIsNull(TipoCliente tipoCliente);

    boolean existsByCpf(String cpf);
    boolean existsByTelefone(String telefone);
}
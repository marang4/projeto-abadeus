package br.com.abadeus.application.dto.cliente;

import br.com.abadeus.application.dto.endereco.EnderecoResponseDTO;
import br.com.abadeus.domain.entity.Clientes;
import java.time.LocalDate;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String email,
        String role,
        String telefone,
        String cpf,
        LocalDate dataNascimento,
        EnderecoResponseDTO endereco
) {
    public ClienteResponseDTO(Clientes cliente) {
        this(
                cliente.getId(),
                cliente.getUsuario().getNome(),
                cliente.getUsuario().getEmail(),
                cliente.getUsuario().getRole(),
                cliente.getTelefone(),
                cliente.getCpf(),
                cliente.getDataNascimento(),
                cliente.getEnderecos() != null ? new EnderecoResponseDTO(cliente.getEnderecos()) : null
        );
    }
}
package br.com.abadeus.application.dto.cliente;

import br.com.abadeus.application.dto.endereco.EnderecoResponseDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelBuscarResponseDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelResponseDTO;
import br.com.abadeus.domain.entity.Clientes;
import br.com.abadeus.domain.enums.TipoCliente;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String email,
        String role,
        String telefone,
        String cpf,
        LocalDate dataNascimento,
        TipoCliente tipoCliente,
        String urlDocumento,
        EnderecoResponseDTO endereco,
        List<ResponsavelResponseDTO> responsaveis,
        List<ResponsavelBuscarResponseDTO> responsaveisBuscar
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
                cliente.getTipoCliente(),
                cliente.getUrlDocumento(),
                cliente.getEndereco() != null ? new EnderecoResponseDTO(cliente.getEndereco()) : null,
                cliente.getResponsaveis() != null ?
                        cliente.getResponsaveis().stream().map(ResponsavelResponseDTO::new).collect(Collectors.toList()) : null,
                cliente.getResponsaveisBuscar() != null ?
                        cliente.getResponsaveisBuscar().stream().map(ResponsavelBuscarResponseDTO::new).collect(Collectors.toList()) : null
        );
    }
}
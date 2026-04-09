package br.com.abadeus.application.dto.cliente;

import br.com.abadeus.application.dto.endereco.EnderecoRequestDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelBuscarRequestDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelRequestDTO;
import br.com.abadeus.domain.enums.TipoCliente;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ClienteUpdateDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O telefone é obrigatório.")
        String telefone,

        String senha,

        TipoCliente tipoCliente,

        String urlDocumento,

        EnderecoRequestDTO endereco,

        List<ResponsavelRequestDTO> responsaveis,

        List<ResponsavelBuscarRequestDTO> responsaveisBuscar
) {}
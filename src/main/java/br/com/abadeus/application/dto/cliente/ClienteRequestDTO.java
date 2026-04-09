package br.com.abadeus.application.dto.cliente;

import br.com.abadeus.application.dto.endereco.EnderecoRequestDTO;
import br.com.abadeus.domain.enums.TipoCliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record ClienteRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O telefone é obrigatório.")
        String telefone,

        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String senha,

        @NotBlank(message = "O CPF é obrigatório.")
        @CPF(message = "CPF inválido. Verifique os números.")
        String cpf,

        @NotNull(message = "A data de nascimento é obrigatória.")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,

        TipoCliente tipoCliente,

        EnderecoRequestDTO endereco
) {}
package br.com.abadeus.application.dto.reponsavel;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record ResponsavelBuscarRequestDTO(
        Long id,

        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O CPF é obrigatório.")
        @CPF(message = "CPF inválido.")
        String cpf,

        @NotBlank(message = "O telefone é obrigatório.")
        String telefone
) {}
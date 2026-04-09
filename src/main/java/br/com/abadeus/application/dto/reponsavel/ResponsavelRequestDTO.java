package br.com.abadeus.application.dto.reponsavel;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record ResponsavelRequestDTO(
        Long id,

        @NotBlank(message = "O nome do responsável é obrigatório.")
        String nome,

        @NotBlank(message = "O CPF do responsável é obrigatório.")
        @CPF(message = "CPF do responsável inválido.")
        String cpf,

        @NotBlank(message = "O telefone do responsável é obrigatório.")
        String telefone


) {}
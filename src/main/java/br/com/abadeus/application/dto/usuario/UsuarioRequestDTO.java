package br.com.abadeus.application.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(
        Long id,

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotBlank(message = "A role é obrigatória")
        String role
) {}
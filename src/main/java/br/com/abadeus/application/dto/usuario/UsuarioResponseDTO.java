package br.com.abadeus.application.dto.usuario;

import br.com.abadeus.domain.entity.Usuarios;

import java.time.LocalDate;

public record UsuarioResponseDTO (
    Long id,
    String nome,
    String cpf,
    String email,
    LocalDate dataNascimento,
    String role

) {

    public UsuarioResponseDTO(Usuarios usuario) {
        this (
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getDataNascimento(),
                usuario.getRole()
        );

    }

}

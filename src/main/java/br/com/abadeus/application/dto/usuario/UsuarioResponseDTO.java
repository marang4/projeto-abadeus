package br.com.abadeus.application.dto.usuario;

import br.com.abadeus.domain.entity.Usuarios;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String role
) {
    public UsuarioResponseDTO(Usuarios usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}
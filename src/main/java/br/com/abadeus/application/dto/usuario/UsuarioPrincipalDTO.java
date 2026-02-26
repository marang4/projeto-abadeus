package br.com.abadeus.application.dto.usuario;

import br.com.abadeus.domain.entity.Usuarios;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record UsuarioPrincipalDTO(Long id,
                                  String email,
                                  Collection <? extends GrantedAuthority> autorizacao,
                                  String role
                                  ) {
    public UsuarioPrincipalDTO(Usuarios usuario){
        this(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getAuthorities(),
                usuario.getRole()
        );
    }
}

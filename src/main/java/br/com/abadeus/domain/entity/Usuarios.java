package br.com.abadeus.domain.entity;

import br.com.abadeus.application.dto.usuario.UsuarioPrincipalDTO;
import br.com.abadeus.application.dto.usuario.UsuarioRequestDTO;
import br.com.abadeus.application.dto.usuario.UsuarioResponseDTO;
import br.com.abadeus.domain.interfaces.AuthUser;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuarios implements UserDetails, AuthUser {

    public Usuarios(UsuarioRequestDTO dto) {
        this.nome = dto.nome();
        this.email = dto.email();
        this.senha = dto.senha();
        this.role = dto.role();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    private String role;

    private LocalDateTime dataCriacao;

    private String tokenSenha;

    private LocalDateTime tokenSenhaExpiracao;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if ("ROLE_ADMIN".equals(this.role)) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }
        if ("ROLE_USER".equals(this.role)) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    @Override
    public String getPassword() { return this.senha; }

    @Override
    public String getUsername() { return this.email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public UsuarioResponseDTO toDtoResponse() {
        return new UsuarioResponseDTO(this);
    }

    public UsuarioPrincipalDTO toPrincipalDTO() {
        return new UsuarioPrincipalDTO(this);
    }
}
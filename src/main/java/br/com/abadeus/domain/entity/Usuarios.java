package br.com.abadeus.domain.entity;

import br.com.abadeus.application.dto.usuario.UsuarioRequestDTO;
import br.com.abadeus.application.dto.usuario.UsuarioResponseDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuarios implements UserDetails {

    public Usuarios (UsuarioRequestDTO usuarioRequestDTO) {
        this.setNome(usuarioRequestDTO.nome());
        this.setEmail(usuarioRequestDTO.email());
        this.setSenha(usuarioRequestDTO.senha());
        this.setCpf(usuarioRequestDTO.cpf());
        this.setDataNascimento(usuarioRequestDTO.dataNascimento());
        this.setRole(usuarioRequestDTO.role());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String senha;

    private String role;

    private String cpf;

    private LocalDate dataNascimento;

    private LocalDateTime dataCriacao;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if ("ROLE_ADMIN_GERAL".equals(this.role)) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );

        }else if ("ROLE_ADMIN".equals(this.role)) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USER")
            );

        }else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER")
            );
        }


    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UsuarioResponseDTO toDtoResponse() {
        return new UsuarioResponseDTO(this);
    }

}


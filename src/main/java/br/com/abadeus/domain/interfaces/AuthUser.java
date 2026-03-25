package br.com.abadeus.domain.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

public interface AuthUser extends UserDetails {
    Long getId();
    String getNome();

    String getEmail();
    String getSenha();
    void setSenha(String senha);
    String getTokenSenha();
    void setTokenSenha(String token);
    LocalDateTime getTokenSenhaExpiracao();
    void setTokenSenhaExpiracao(LocalDateTime data);
    String getRole();

    Object toPrincipalDTO();
}
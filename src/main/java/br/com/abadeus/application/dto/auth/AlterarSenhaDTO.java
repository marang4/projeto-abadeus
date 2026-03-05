package br.com.abadeus.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AlterarSenhaDTO(@Email String email,
                              @NotBlank String senhaAtual,
                              @NotBlank String novaSenha) {

}

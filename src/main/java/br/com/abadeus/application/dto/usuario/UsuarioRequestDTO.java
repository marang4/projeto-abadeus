package br.com.abadeus.application.dto.usuario;

import java.time.LocalDate;

public record UsuarioRequestDTO (Long id,
                                 String nome,
                                 String email,
                                 String senha,
                                 String cpf,
                                 String role,
                                 LocalDate dataNascimento

){
}

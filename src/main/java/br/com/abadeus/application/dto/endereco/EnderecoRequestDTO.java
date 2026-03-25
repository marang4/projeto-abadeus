package br.com.abadeus.application.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EnderecoRequestDTO(
        Long id,

        @NotBlank(message = "O CEP é obrigatório.")
        @Size(min = 8, max = 9, message = "CEP inválido.")
        String cep,

        @NotNull(message = "O número é obrigatório.")
        Long numero,

        String logradouro, // Opcional no DTO pois o Service vai buscar se estiver vazio
        String complemento,
        String bairro,
        String cidade,
        String uf
) {}
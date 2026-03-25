package br.com.abadeus.application.dto.endereco;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CepResponseDTO(
        String cep,
        String logradouro,
        String complemento,
        String bairro,

        @JsonProperty("localidade") // O ViaCEP manda "localidade", e o Jackson joga para "cidade"
        String cidade,

        String uf,
        Boolean erro
) {}
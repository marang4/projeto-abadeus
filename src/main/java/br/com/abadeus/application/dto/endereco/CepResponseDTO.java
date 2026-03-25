package br.com.abadeus.application.dto.endereco;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CepResponseDTO(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        Double latitude,
        Double longitude,
        Boolean erro
) {}
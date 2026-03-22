package br.com.abadeus.application.dto.endereco;

import java.time.LocalDateTime;

public record EnderecoRequestDTO(Long id,
                                 String cep,
                                 Long numero,
                                 String logradouro,
                                 String complemento,
                                 String bairro,
                                 String cidade,
                                 String uf) {
}

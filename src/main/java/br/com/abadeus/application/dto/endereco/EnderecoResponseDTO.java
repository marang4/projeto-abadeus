package br.com.abadeus.application.dto.endereco;

import br.com.abadeus.domain.entity.Enderecos;

public record EnderecoResponseDTO(Long id,
                                  String cep,
                                  Long numero,
                                  String logradouro,
                                  String complemento,
                                  String bairro,
                                  String cidade,
                                  String uf) {

    public EnderecoResponseDTO(Enderecos enderecos){
        this(
                enderecos.getId(),
                enderecos.getCep(),
                enderecos.getNumero(),
                enderecos.getLogradouro(),
                enderecos.getComplemento(),
                enderecos.getBairro(),
                enderecos.getCidade(),
                enderecos.getUf()
        );
    }
}

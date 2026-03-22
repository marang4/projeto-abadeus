package br.com.abadeus.domain.entity;

import br.com.abadeus.application.dto.endereco.EnderecoRequestDTO;
import br.com.abadeus.application.dto.endereco.EnderecoResponseDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "enderecos")
public class Enderecos {

    public Enderecos(EnderecoRequestDTO enderecoRequestDTO){
        this.setCep(enderecoRequestDTO.cep());
        this.setNumero(enderecoRequestDTO.numero());
        this.setLogradouro(enderecoRequestDTO.logradouro());
        this.setComplemento(enderecoRequestDTO.complemento());
        this.setBairro(enderecoRequestDTO.bairro());
        this.setCidade(enderecoRequestDTO.cidade());
        this.setUf(enderecoRequestDTO.uf());
    }

    @Id
    @GeneratedValue
    private Long id;
    private String cep;
    private Long numero;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public EnderecoResponseDTO toDtoResponse(){return new EnderecoResponseDTO(this);}
}

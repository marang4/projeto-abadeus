package br.com.abadeus.domain.entity;

import br.com.abadeus.application.dto.endereco.EnderecoRequestDTO;
import jakarta.persistence.*;
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

    public Enderecos(EnderecoRequestDTO dto) {
        this.cep = dto.cep() != null ? dto.cep().replaceAll("\\D", "") : null;
        this.numero = dto.numero();
        this.logradouro = dto.logradouro();
        this.complemento = dto.complemento();
        this.bairro = dto.bairro();
        this.cidade = dto.cidade();
        this.uf = dto.uf();
        this.dataCriacao = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8)
    private String cep;

    @Column(nullable = false)
    private Long numero;

    @Column(nullable = false)
    private String logradouro;

    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    private LocalDateTime dataCriacao;
}
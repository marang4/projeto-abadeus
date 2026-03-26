package br.com.abadeus.domain.entity;

import br.com.abadeus.application.dto.evento.EventoRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "evento")
public class Eventos {

    public Eventos(EventoRequestDTO dto){
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.quantidadeOcupacao = dto.quantidadeOcupacao();
        this.dataHora = dto.dataHora();
        this.valor = dto.valor();
        this.dataCriacao = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidadeOcupacao;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private Long valor;

    private LocalDateTime dataCriacao;
}
package br.com.abadeus.domain.entity;

import br.com.abadeus.application.dto.evento.EventoRequestDTO;
import br.com.abadeus.application.dto.evento.EventoResponseDTO;
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

    public Eventos(EventoRequestDTO eventoRequestDTO){
        this.setNome(eventoRequestDTO.nome());
        this.setDescricao(eventoRequestDTO.descricao());
        this.setDataHora(eventoRequestDTO.dataHora());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    private LocalDateTime dataHora;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    public EventoResponseDTO toDtoResponse(){
        return new EventoResponseDTO(this);
    }
}

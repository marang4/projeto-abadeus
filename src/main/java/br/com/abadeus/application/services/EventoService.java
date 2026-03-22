package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.evento.EventoRequestDTO;
import br.com.abadeus.application.dto.evento.EventoResponseDTO;
import br.com.abadeus.domain.entity.Eventos;
import br.com.abadeus.domain.repository.EventoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public EventoResponseDTO listarPorId(Long id) {
        return eventoRepository.findById(id)
                .map(EventoResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    }

    public List<EventoResponseDTO> listarTodosEventos() {
        return eventoRepository.findAll()
                .stream()
                .map(EventoResponseDTO::new)
                .collect(Collectors.toList());
    }


    public EventoResponseDTO criarEvento(EventoRequestDTO eventoRequestDTO) {
        Eventos novoEvento = new Eventos();

        novoEvento.setNome(eventoRequestDTO.nome());
        novoEvento.setDataCriacao(LocalDateTime.now());
        novoEvento.setDataHora(eventoRequestDTO.dataHora());
        novoEvento.setDescricao(eventoRequestDTO.descricao());

        eventoRepository.save(novoEvento);
        return novoEvento.toDtoResponse();
    }

    @Transactional
    public EventoResponseDTO atualizarEvento(Long id, EventoRequestDTO eventoRequestDTO) {
        Eventos eventos = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        eventos.setNome(eventoRequestDTO.nome());
        eventos.setDescricao(eventoRequestDTO.descricao());
        eventos.setDataHora(eventoRequestDTO.dataHora());

        eventoRepository.save(eventos);

        return eventos.toDtoResponse();
    }
}

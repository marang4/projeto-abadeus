package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.evento.EventoRequestDTO;
import br.com.abadeus.application.dto.evento.EventoResponseDTO;
import br.com.abadeus.domain.entity.Eventos;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public EventoResponseDTO listarEventoPorId(Long id) {
        return eventoRepository.findById(id)
                .map(EventoResponseDTO::new)
                .orElseThrow(() -> new RegraDeNegocioException("Evento não encontrado."));
    }

    public List<EventoResponseDTO> listarTodosEventos() {
        return eventoRepository.findAll()
                .stream()
                .map(EventoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventoResponseDTO criarEvento(EventoRequestDTO dto) {
        if (eventoRepository.existsByNomeIgnoreCase(dto.nome())) {
            throw new RegraDeNegocioException("Já existe um evento cadastrado com este nome.");
        }

        Eventos novoEvento = new Eventos(dto);
        eventoRepository.save(novoEvento);
        return new EventoResponseDTO(novoEvento);
    }

    @Transactional
    public EventoResponseDTO atualizarEvento(Long id, EventoRequestDTO dto) {
        Eventos evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Evento não encontrado."));

        if (!evento.getNome().equalsIgnoreCase(dto.nome()) && eventoRepository.existsByNomeIgnoreCase(dto.nome())) {
            throw new RegraDeNegocioException("Já existe outro evento com este nome.");
        }

        evento.setNome(dto.nome());
        evento.setDescricao(dto.descricao());
        evento.setQuantidadeOcupacao(dto.quantidadeOcupacao());
        evento.setDataHora(dto.dataHora());

        return new EventoResponseDTO(eventoRepository.save(evento));
    }
}
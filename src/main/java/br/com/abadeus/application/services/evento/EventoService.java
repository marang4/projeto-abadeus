package br.com.abadeus.application.services.evento;

import br.com.abadeus.application.dto.evento.EventoRequestDTO;
import br.com.abadeus.application.dto.evento.EventoResponseDTO;
import br.com.abadeus.domain.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;


    public EventoResponseDTO listarPorId(Long id) {
        return null;
    }

    public EventoResponseDTO listarTodosEventos() {
        return null;
    }


    public EventoResponseDTO criarEvento(EventoRequestDTO eventoRequestDTO) {
        return null;
    }
}

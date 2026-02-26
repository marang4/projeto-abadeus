package br.com.abadeus.presentation.evento;

import br.com.abadeus.application.dto.evento.EventoRequestDTO;
import br.com.abadeus.application.dto.evento.EventoResponseDTO;
import br.com.abadeus.application.services.evento.EventoService;
import br.com.abadeus.domain.repository.EventoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evento")
@Tag(name = "Controlador de eventos", description = "Gerencia o CRUD de eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EventoService eventoService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar evento por ID")
    public ResponseEntity<EventoResponseDTO> listarPorId(@PathVariable Long id){

        try {
            EventoResponseDTO eventoResponseDTO = eventoService.listarPorId(id);
            return ResponseEntity.ok(eventoResponseDTO);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos eventos")
    public ResponseEntity<EventoResponseDTO> listarTodosEventos(@RequestParam(required = false) String nome){
        return ResponseEntity.ok(eventoService.listarTodosEventos());
    }

    @PostMapping
    @Operation(summary = "Criar eventos")
    public ResponseEntity<EventoResponseDTO> criarEvento(@RequestBody EventoRequestDTO eventoRequestDTO){

        try {
            EventoResponseDTO eventoResponseDTO = eventoService.criarEvento(eventoRequestDTO);
            return ResponseEntity.ok(eventoResponseDTO);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}

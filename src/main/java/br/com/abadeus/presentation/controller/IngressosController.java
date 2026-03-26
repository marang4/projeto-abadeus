package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.dto.ingresso.IngressoRequestDTO;
import br.com.abadeus.application.dto.ingresso.IngressoResponseDTO;
import br.com.abadeus.application.services.IngressosService;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingresso")
@Tag(name = "Controle de ingressos", description = "Gerencia o CRUD de ingressos")
public class IngressosController {

    @Autowired
    private IngressosService ingressosService;


    @GetMapping("/{id}")
    @Operation(summary = "Consultar ingresso por ID")
    public ResponseEntity<?> listarIngressoPorId(@PathVariable Long id){

        try {
            return ResponseEntity.ok(ingressosService.listarIngressoPorId(id));
        } catch (RegraDeNegocioException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.response(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos os ingressos")
    public ResponseEntity<List<IngressoResponseDTO>> listarTodosIngressos(){

        return ResponseEntity.ok(ingressosService.listarTodosEventos());
    }

    @GetMapping("/validar/{codIngresso}")
    @Operation(summary = "Validar ingresso pelo QR Code (Portaria)")
    public ResponseEntity<?> validarIngresso(@PathVariable String codIngresso) {

        try {
            IngressoResponseDTO ingressoValidado = ingressosService.validarIngresso(codIngresso);
            return ResponseEntity.ok(ingressoValidado);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.response(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Criar ingressos")
    public ResponseEntity<?> criarIngressos(
            @Valid @RequestBody IngressoRequestDTO dto){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ingressosService.criarIngressos(dto));
        } catch (RegraDeNegocioException e){
            return ResponseEntity.badRequest().body(ResponseUtil.response(e.getMessage()));
        }
    }
}

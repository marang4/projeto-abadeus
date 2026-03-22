package br.com.abadeus.presentation;

import br.com.abadeus.application.dto.endereco.EnderecoRequestDTO;
import br.com.abadeus.application.dto.endereco.EnderecoResponseDTO;
import br.com.abadeus.application.services.EnderecoService;
import br.com.abadeus.domain.repository.EnderecoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/endereco")
@Tag(name = "Controlador de endereço" , description = "Gerencia o CRUD de endereço")
public class EnderecoController {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar endereço por ID")
    public ResponseEntity<EnderecoResponseDTO> listarPorId(@PathVariable Long id){

        try {
            EnderecoResponseDTO enderecoResponseDTO = enderecoService.listarPorId(id);
            return ResponseEntity.ok(enderecoResponseDTO);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos endereços")
    public ResponseEntity<List<EnderecoResponseDTO>> listarTodosEnderecos(@RequestParam(required = false) String cidade){
        return ResponseEntity.ok(enderecoService.listarTodosEnderecos());
    }

    @PostMapping
    @Operation(summary = "Criar endereços")
    public ResponseEntity<EnderecoResponseDTO> criarEndereco(@RequestBody EnderecoRequestDTO enderecoRequestDTO){

        try {
            EnderecoResponseDTO enderecoResponseDTO = enderecoService.criarEndereco(enderecoRequestDTO);
            return ResponseEntity.ok(enderecoResponseDTO);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    @Operation(summary = "Atualizar evento")
    public ResponseEntity<EnderecoResponseDTO> atualizarEndereco(@RequestBody EnderecoRequestDTO enderecoRequestDTO, @PathVariable Long id){

        try {
            EnderecoResponseDTO enderecoResponseDTO = enderecoService.atualizarEndereco(id, enderecoRequestDTO);
            return ResponseEntity.ok(enderecoResponseDTO);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }


}

package br.com.abadeus.presentation.controller;

import br.com.abadeus.application.services.PedidosService;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedido")
@Tag(name = "Cotnrole de pedidos", description = "Gerencia o CRUD de clientes")
public class PedidosController {

    @Autowired
    private PedidosService pedidosService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar evento por ID")
    public ResponseEntity<?> listarPedidosPorId(@PathVariable Long id){

        try {
            return ResponseEntity.ok(pedidosService.listarEnderecoPorId(id));
        } catch (RegraDeNegocioException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.response(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Consultar todos eventos")
    public ResponseEntity<?> listarTodosPedidos(){

        try {
            return Response
        } catch (){

        }
    }
}

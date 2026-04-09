package br.com.abadeus.application.dto.pedido;

public record PedidoRequestDTO (
        Long id,
        Long valorTotal,
        String status
) {
}

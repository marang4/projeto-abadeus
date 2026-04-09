package br.com.abadeus.application.dto.pedido;

import br.com.abadeus.domain.entity.Pedidos;

public record PedidoResponseDTO(
        Long id,
        Long valorTotal,
        String status
) {
    public PedidoResponseDTO(Pedidos pedidos){
        this(
                pedidos.getId(),
                pedidos.getValorTotal(),
                pedidos.getStatus()
        );
    }
}

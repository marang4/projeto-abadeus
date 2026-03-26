package br.com.abadeus.domain.repository;

import br.com.abadeus.domain.entity.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidosRepository extends JpaRepository<Pedidos, Long> {
}

package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.ingresso.IngressoRequestDTO;
import br.com.abadeus.application.dto.ingresso.IngressoResponseDTO;
import br.com.abadeus.domain.entity.Eventos;
import br.com.abadeus.domain.entity.Ingressos;
import br.com.abadeus.domain.entity.Pedidos;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.repository.EventoRepository;
import br.com.abadeus.domain.repository.IngressosRepository;
import br.com.abadeus.domain.repository.PedidosRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngressosService {

    @Autowired
    private IngressosRepository ingressosRepository;

    @Autowired
    private EventoRepository eventosRepository;

    @Autowired
    private PedidosRepository pedidosRepository;


    public IngressoResponseDTO listarIngressoPorId(Long id) {
        return ingressosRepository.findById(id)
                .map(IngressoResponseDTO::new)
                .orElseThrow(() -> new RegraDeNegocioException("Ingresso não encontrado."));
    }

    public List<IngressoResponseDTO> listarTodosEventos() {
        return ingressosRepository.findAll()
                .stream()
                .map(IngressoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public IngressoResponseDTO validarIngresso(String codIngresso) {

        Ingressos ingresso = ingressosRepository.findByCodIngresso(codIngresso)
                .orElseThrow(() -> new RegraDeNegocioException("Ingresso Inválido: Código não encontrado no sistema."));

        if (ingresso.isFoiUsado()) {
            throw new RegraDeNegocioException("Acesso Negado: Este ingresso já foi utilizado na portaria!");
        }

        ingresso.setFoiUsado(true);

        ingressosRepository.save(ingresso);

        return new IngressoResponseDTO(ingresso);
    }

    @Transactional
    public IngressoResponseDTO criarIngressos(@Valid IngressoRequestDTO dto) {

        Eventos evento = eventosRepository.findById(dto.eventoId())
                .orElseThrow(() -> new RegraDeNegocioException("Evento não encontrado com o ID informado."));

        Pedidos pedido = pedidosRepository.findById(dto.pedidoId())
                .orElseThrow(() -> new RegraDeNegocioException("Pedido não encontrado com o ID informado."));

        if (evento.getQuantidadeOcupacao() <= 0) {
            throw new RegraDeNegocioException("Atenção: Os ingressos para este evento estão esgotados!");
        }

        Ingressos novoIngresso = new Ingressos();
        novoIngresso.setEvento(evento);
        novoIngresso.setPedido(pedido);

        String codigoSeguro = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        novoIngresso.setCodIngresso(codigoSeguro);

        novoIngresso.setFoiUsado(false);

        Ingressos ingressoSalvo = ingressosRepository.save(novoIngresso);

        evento.setQuantidadeOcupacao(evento.getQuantidadeOcupacao() - 1);
        eventosRepository.save(evento);

        return new IngressoResponseDTO(ingressoSalvo);
    }
}

package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.reponsavel.ResponsavelRequestDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelResponseDTO;
import br.com.abadeus.domain.entity.Clientes;
import br.com.abadeus.domain.entity.Responsavel;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.repository.ClientesRepository;
import br.com.abadeus.domain.repository.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponsavelService {

    @Autowired
    private ResponsavelRepository responsavelRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    public ResponsavelResponseDTO buscarResponsavelPorId(Long id) {
        return responsavelRepository.findByIdAndAtivoTrue(id)
                .map(ResponsavelResponseDTO::new)
                .orElseThrow(() -> new RegraDeNegocioException("Responsável não encontrado ou inativo."));
    }

    public List<ResponsavelResponseDTO> listarTodosResponsaveis() {
        return responsavelRepository.findAllByAtivoTrue()
                .stream()
                .map(ResponsavelResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponsavelResponseDTO criarResponsavel(Long clienteId, ResponsavelRequestDTO dto) {
        Clientes cliente = clientesRepository.findById(clienteId)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado para vincular o responsável."));

        String cpfTratado = dto.cpf().replaceAll("\\D", "");

        if (responsavelRepository.existsByCpfAndAtivoTrue(cpfTratado)) {
            throw new RegraDeNegocioException("CPF já cadastrado para um responsável ativo.");
        }

        Responsavel responsavel = new Responsavel();
        responsavel.setNome(dto.nome());
        responsavel.setCpf(cpfTratado);
        responsavel.setTelefone(dto.telefone().replaceAll("\\D", ""));
        // URL DOCUMENTO REMOVIDA
        responsavel.setCliente(cliente);
        responsavel.setAtivo(true);

        return new ResponsavelResponseDTO(responsavelRepository.save(responsavel));
    }

    @Transactional
    public ResponsavelResponseDTO atualizarResponsavel(Long id, ResponsavelRequestDTO dto) {
        Responsavel responsavel = responsavelRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RegraDeNegocioException("Responsável não encontrado ou inativo."));

        String cpfTratado = dto.cpf().replaceAll("\\D", "");

        if (!responsavel.getCpf().equals(cpfTratado) && responsavelRepository.existsByCpfAndAtivoTrue(cpfTratado)) {
            throw new RegraDeNegocioException("O novo CPF já está em uso por outro responsável.");
        }

        responsavel.setNome(dto.nome());
        responsavel.setCpf(cpfTratado);
        responsavel.setTelefone(dto.telefone().replaceAll("\\D", ""));
        // URL DOCUMENTO REMOVIDA

        return new ResponsavelResponseDTO(responsavelRepository.save(responsavel));
    }

    @Transactional
    public void deletarResponsavel(Long id) {
        Responsavel responsavel = responsavelRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RegraDeNegocioException("Responsável não encontrado."));

        responsavel.setAtivo(false);
        responsavelRepository.save(responsavel);
    }
}
package br.com.abadeus.application.services;

import br.com.abadeus.application.dto.reponsavel.ResponsavelBuscarRequestDTO;
import br.com.abadeus.application.dto.reponsavel.ResponsavelBuscarResponseDTO;
import br.com.abadeus.domain.entity.Clientes;
import br.com.abadeus.domain.entity.ResponsavelBuscar;
import br.com.abadeus.domain.exception.RegraDeNegocioException;
import br.com.abadeus.domain.repository.ClientesRepository;
import br.com.abadeus.domain.repository.ResponsavelBuscarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponsavelBuscarService {

    @Autowired
    private ResponsavelBuscarRepository responsavelBuscarRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    public ResponsavelBuscarResponseDTO buscarResponsavelBuscarPorId(Long id) {
        return responsavelBuscarRepository.findByIdAndAtivoTrue(id)
                .map(ResponsavelBuscarResponseDTO::new)
                .orElseThrow(() -> new RegraDeNegocioException("Responsável de busca não encontrado ou inativo."));
    }

    public List<ResponsavelBuscarResponseDTO> listarTodosResponsaveisBuscar() {
        return responsavelBuscarRepository.findAllByAtivoTrue()
                .stream()
                .map(ResponsavelBuscarResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponsavelBuscarResponseDTO criarResponsavelBuscar(Long clienteId, ResponsavelBuscarRequestDTO dto) {
        Clientes cliente = clientesRepository.findById(clienteId)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado para vincular o responsável de busca."));

        String cpfTratado = dto.cpf().replaceAll("\\D", "");

        if (responsavelBuscarRepository.existsByCpfAndAtivoTrue(cpfTratado)) {
            throw new RegraDeNegocioException("CPF já cadastrado para um responsável de busca ativo.");
        }

        ResponsavelBuscar responsavel = new ResponsavelBuscar();
        responsavel.setNome(dto.nome());
        responsavel.setCpf(cpfTratado);
        responsavel.setTelefone(dto.telefone().replaceAll("\\D", ""));
        responsavel.setCliente(cliente);
        responsavel.setAtivo(true);

        return new ResponsavelBuscarResponseDTO(responsavelBuscarRepository.save(responsavel));
    }

    @Transactional
    public ResponsavelBuscarResponseDTO atualizarResponsavelBuscar(Long id, ResponsavelBuscarRequestDTO dto) {
        ResponsavelBuscar responsavel = responsavelBuscarRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RegraDeNegocioException("Responsável de busca não encontrado ou inativo."));

        String cpfTratado = dto.cpf().replaceAll("\\D", "");

        if (!responsavel.getCpf().equals(cpfTratado) && responsavelBuscarRepository.existsByCpfAndAtivoTrue(cpfTratado)) {
            throw new RegraDeNegocioException("O novo CPF já está em uso por outro responsável.");
        }

        responsavel.setNome(dto.nome());
        responsavel.setCpf(cpfTratado);
        responsavel.setTelefone(dto.telefone().replaceAll("\\D", ""));

        return new ResponsavelBuscarResponseDTO(responsavelBuscarRepository.save(responsavel));
    }

    @Transactional
    public void deletarResponsavelBuscar(Long id) {
        ResponsavelBuscar responsavel = responsavelBuscarRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RegraDeNegocioException("Responsável de busca não encontrado."));

        responsavel.setAtivo(false);
        responsavelBuscarRepository.save(responsavel);
    }
}
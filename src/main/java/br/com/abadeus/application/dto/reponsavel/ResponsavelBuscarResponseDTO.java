package br.com.abadeus.application.dto.reponsavel;

import br.com.abadeus.domain.entity.ResponsavelBuscar;

public record ResponsavelBuscarResponseDTO(
        Long id,
        String nome,
        String cpf,
        String telefone
) {
    public ResponsavelBuscarResponseDTO(ResponsavelBuscar responsavel) {
        this(
                responsavel.getId(),
                responsavel.getNome(),
                responsavel.getCpf(),
                responsavel.getTelefone()
        );
    }
}
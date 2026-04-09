package br.com.abadeus.application.dto.reponsavel;

import br.com.abadeus.domain.entity.Responsavel;

public record ResponsavelResponseDTO(
        Long id,
        String nome,
        String cpf,
        String telefone

) {
    public ResponsavelResponseDTO(Responsavel responsavel) {
        this(
                responsavel.getId(),
                responsavel.getNome(),
                responsavel.getCpf(),
                responsavel.getTelefone()

        );
    }
}
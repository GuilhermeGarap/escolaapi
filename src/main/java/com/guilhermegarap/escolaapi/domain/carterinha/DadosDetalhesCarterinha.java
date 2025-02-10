package com.guilhermegarap.escolaapi.domain.carterinha;

public record DadosDetalhesCarterinha(
    String cpf,
    String rg,
    String dataNascimento,
    String telefone,
    String curso,
    String instituicao,
    String linkImagem
) {
    public DadosDetalhesCarterinha(Carterinha carterinha) {
        this(
            carterinha.getCpf(),
            carterinha.getRg(),
            carterinha.getDataNascimento(),
            carterinha.getTelefone(),
            carterinha.getCurso(),
            carterinha.getInstituicao(),
            carterinha.getLinkImagem()
        );
    }
}

package com.guilhermegarap.escolaapi.domain.usuario;

public record DadosDetalhesUsuario(Long id, String email) {
    
    public DadosDetalhesUsuario(Usuario usuario) {
        this(
            usuario.getId(), 
            usuario.getEmail());
    }
}

package com.guilhermegarap.escolaapi.domainservices.carterinhaservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.guilhermegarap.escolaapi.domain.carterinha.Carterinha;
import com.guilhermegarap.escolaapi.domain.carterinha.CarterinhaRepository;
import com.guilhermegarap.escolaapi.domain.carterinha.DadosCadastroCarterinha;
import com.guilhermegarap.escolaapi.domain.carterinha.DadosDetalhesCarterinha;
import com.guilhermegarap.escolaapi.domain.usuario.UsuarioRepository;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class CarterinhaService {

    @Autowired
    private CarterinhaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Transactional
    public DadosDetalhesCarterinha cadastrar(@RequestBody @Valid DadosCadastroCarterinha dados) {
        // Verificar se o usuário existe
        if (!usuarioRepository.existsById(dados.usuario_id())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Usuario nao encontrado"
            );
        }
    
        var usuario = usuarioRepository.getReferenceById(dados.usuario_id());
    
        // Verificar se o usuário já tem uma carteirinha
        if (repository.existsByUsuarioId(dados.usuario_id())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Este usuario ja possui uma carteirinha cadastrada"
            );
        }
        if (repository.existsByCpf(dados.cpf())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ja existe um registro com este CPF"
            );
        }
        
        if (repository.existsByRg(dados.rg())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ja existe um registro com este RG"
            );
        }
        
        if (repository.existsByTelefone(dados.telefone())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ja existe um registro com este telefone"
            );
        }
    
        if (repository.existsByMatricula(dados.matricula())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ja existe um registro com esta matricula"
            );
        }
    
        // Criar a nova carteirinha
        var carteirinha = new Carterinha(dados);
        carteirinha.verificadorCpf(dados.cpf());
        carteirinha.setUsuario(usuario);
        repository.save(carteirinha);
    
        return new DadosDetalhesCarterinha(carteirinha);
    }
    
}



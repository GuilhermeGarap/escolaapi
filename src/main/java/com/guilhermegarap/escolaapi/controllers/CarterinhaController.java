package com.guilhermegarap.escolaapi.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.guilhermegarap.escolaapi.domain.carterinha.CarterinhaRepository;
import com.guilhermegarap.escolaapi.domain.carterinha.DadosCadastroCarterinha;
import com.guilhermegarap.escolaapi.domain.carterinha.DadosDetalhesCarterinha;
import com.guilhermegarap.escolaapi.domain.usuario.Usuario;
import com.guilhermegarap.escolaapi.domain.usuario.UsuarioRepository;
import com.guilhermegarap.escolaapi.domainservices.carterinhaservices.CarterinhaService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/carterinha")
public class CarterinhaController {
    
    @Autowired
    private CarterinhaRepository repository;

    @Autowired
    private CarterinhaService carterinhaservice;

    @Autowired
    private UsuarioRepository usuarioRepository;

    //Cadastrar Carterinha
    @PostMapping("/cadastrar")
    @Transactional
    public ResponseEntity<DadosDetalhesCarterinha> cadastrar(@RequestBody @Valid DadosCadastroCarterinha dados, UriComponentsBuilder uriBuilder){
        var dto = carterinhaservice.cadastrar(dados);  
        
        return ResponseEntity.ok(dto);
    }

    //Listar Carterinhas
    @Transactional
    @GetMapping("/listar")
    public ResponseEntity<Page<DadosDetalhesCarterinha>> listar(@PageableDefault(size=15, sort = {"rg"}) Pageable paginacao) {
        var page = repository.findAll(paginacao).map(DadosDetalhesCarterinha::new);
        return ResponseEntity.ok(page);
    }

    //Buscar Carterinha
    @Transactional
    @GetMapping("/buscar/{id}")
    public ResponseEntity<DadosDetalhesCarterinha> buscar(@PathVariable Long id) {
        var carterinha = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhesCarterinha(carterinha));
    }

    //Buscar Carterinha por Usuário
    @Transactional
    @GetMapping("/buscar/usuario/{usuario_id}")
    public ResponseEntity<DadosDetalhesCarterinha> buscarPorUsuario(@PathVariable Long usuario_id) {
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // O nome de usuário (email ou nome do usuário)
        
        System.out.println("Usuário autenticado: " + username); // Log para depuração

        // Obter as roles do usuário autenticado
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        System.out.println("É admin? " + isAdmin); // Log para depuração

        // Se o usuário for administrador, ele pode acessar qualquer carteirinha
        if (isAdmin) {
            var carterinha = repository.findByUsuarioId(usuario_id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carteirinha não encontrada"));

            return ResponseEntity.ok(new DadosDetalhesCarterinha(carterinha));
        }

        // Se o usuário não for administrador, apenas seu próprio ID pode ser acessado
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(username);

        if (usuarioAutenticado == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        System.out.println("ID do usuário autenticado: " + usuarioAutenticado.getId()); // Log para depuração
        System.out.println("ID solicitado na URL: " + usuario_id); // Log para depuração

        // Verificar se o ID do usuário autenticado corresponde ao ID na URL
        if (!usuarioAutenticado.getId().equals(usuario_id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Você não tem permissão para acessar a carteirinha de outro usuário.");
        }

        // Se o ID corresponder, buscar a carteirinha
        var carterinha = repository.findByUsuarioId(usuario_id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carteirinha não encontrada"));

        return ResponseEntity.ok(new DadosDetalhesCarterinha(carterinha));
    }

    }
    





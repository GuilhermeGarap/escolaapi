package com.guilhermegarap.escolaapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.guilhermegarap.escolaapi.domain.usuario.DadosAutenticacao;
import com.guilhermegarap.escolaapi.domain.usuario.DadosCadastroUsuario;
import com.guilhermegarap.escolaapi.domain.usuario.DadosDetalhesUsuario;
import com.guilhermegarap.escolaapi.domain.usuario.Usuario;
import com.guilhermegarap.escolaapi.domain.usuario.UsuarioRepository;
import com.guilhermegarap.escolaapi.infra.security.DadosTokenJWT;
import com.guilhermegarap.escolaapi.infra.security.TokenService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    // Cadastrar um novo usuário
    @Transactional
    @PostMapping("/cadastrar")
    public ResponseEntity<DadosDetalhesUsuario> cadastrarUsuario(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        logger.info("Recebendo solicitação de cadastro para o email: {}", dados.email());

        var usuario = new Usuario(dados);
        logger.info("Usuario: {}", usuario);
        // Verifica se o usuário já existe
        if (usuarioRepository.findByEmail(dados.email()) != null) {
            logger.error("Usuário com o email {} já existe!", dados.email());
            return ResponseEntity.status(400).body(null);
        }
        // Criptografa a senha
        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        // Cria um novo usuário
        usuario.setSenha(senhaCriptografada);
        usuario.setRole("ALUNO"); 

        // Salva o usuário no banco de dados
        usuarioRepository.save(usuario);

        logger.info("Usuário cadastrado com sucesso: {}", usuario.getEmail());
        var uri = uriBuilder.path("/usuario/cadastrarUsuario/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhesUsuario(usuario));
    }

    // Cadastrar um usuário ADMIN
    @Transactional
    @PostMapping("/cadastrarAdmin")
    public ResponseEntity<DadosDetalhesUsuario> cadastrarUsuarioADMIN(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        logger.info("Recebendo solicitação de cadastro para o email: {}", dados.email());

        var usuario = new Usuario(dados);

        // Verifica se o usuário já existe
        if (usuarioRepository.findByEmail(dados.email()) != null) {
            logger.error("Usuário com o email {} já existe!", dados.email());
            return ResponseEntity.status(400).body(null);
        }

        // Criptografa a senha
        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        // Cria um novo usuário
        usuario.setSenha(senhaCriptografada);
        usuario.setRole(dados.role() != null ? dados.role() : "ADMIN"); 

        // Salva o usuário no banco de dados
        usuarioRepository.save(usuario);

        logger.info("Usuário cadastrado com sucesso: {}", usuario.getEmail());
        var uri = uriBuilder.path("/usuario/cadastrarUsuario/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhesUsuario(usuario));
    }

    // Efetuar login
    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        logger.info("Recebendo solicitação de login para o email: {}", dados.email());

        try {
            // Tentativa de autenticação
            var authtoken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
            var authentication = manager.authenticate(authtoken);

            // Log de sucesso
            logger.info("Autenticação bem-sucedida para o usuário: {}", authentication.getName());

            // Retorna o token JWT
            var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));

        } catch (Exception e) {
            // Log de erro
            logger.error("Erro durante a autenticação para o email {}: {}", dados.email(), e.getMessage());
            return ResponseEntity.status(401).body("Falha na autenticação. Verifique suas credenciais.");
        }
    }
}

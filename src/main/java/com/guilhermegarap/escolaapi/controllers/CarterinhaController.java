package com.guilhermegarap.escolaapi.controllers;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.guilhermegarap.escolaapi.domain.carterinha.Carterinha;
import com.guilhermegarap.escolaapi.domain.carterinha.CarterinhaRepository;
import com.guilhermegarap.escolaapi.domain.carterinha.DadosCadastroCarterinha;
import com.guilhermegarap.escolaapi.domain.carterinha.DadosDetalhesCarterinha;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/carterinha")
public class CarterinhaController {
    
    @Autowired
    private CarterinhaRepository repository;

    @PostMapping("/cadastrar")
    @Transactional
    public ResponseEntity<DadosDetalhesCarterinha> cadastrar(
        @RequestPart DadosCadastroCarterinha dados,
        @RequestPart("imagem") MultipartFile imagem,
        UriComponentsBuilder uriBuilder
    ) throws IOException {
        // Salvar a imagem em um diretório
        String diretorioImagens = "src/main/Imagens";
        String nomeArquivo = UUID.randomUUID().toString() + "_" + imagem.getOriginalFilename();
        Path caminhoArquivo = Paths.get(diretorioImagens, nomeArquivo);
        Files.copy(imagem.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);
    
        // Criar a carterinha com o link da imagem
        var carterinha = new Carterinha(dados);
        carterinha.setLinkImagem(caminhoArquivo.toString());
    
        // Salvar a carterinha no banco de dados
        repository.save(carterinha);
    
        // Retornar a resposta com o DTO de detalhes
        var uri = uriBuilder.path("/carterinhas/cadastrar/{id}").buildAndExpand(carterinha.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhesCarterinha(carterinha));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<DadosDetalhesCarterinha>> listar(@PageableDefault(size=15, sort = {"rg"}) Pageable paginacao) {
        var page = repository.findAll(paginacao).map(DadosDetalhesCarterinha::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/Imagens/{nomeArquivo}")
    public ResponseEntity<Resource> servirImagem(@PathVariable String nomeArquivo) throws IOException {
        Path caminhoArquivo = Paths.get("src/main/Imagens").resolve(nomeArquivo).normalize();
        Resource resource = new UrlResource(caminhoArquivo.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) 
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

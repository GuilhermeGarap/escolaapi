package com.guilhermegarap.escolaapi.domain.carterinha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

public record DadosCadastroCarterinha(

    @NotNull(message = "O ID do usuário é obrigatório")
    Long usuario_id,

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
    String cpf,

    @NotBlank(message = "RG é obrigatório")
    @Pattern(regexp = "\\d{7,9}", message = "RG deve conter entre 7 e 9 dígitos numéricos")
    String rg,

    @NotBlank(message = "Data de nascimento é obrigatória")
    @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "Data de nascimento deve estar no formato DD/MM/AAAA")
    String dataNascimento,

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    String telefone,

    @NotBlank(message = "Curso é obrigatório")
    String curso,

    @NotBlank(message = "Instituição é obrigatória")
    String instituicao,

    @NotBlank(message = "Imagem é obrigatória")
    MultipartFile imagem

) {}

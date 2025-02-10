package com.guilhermegarap.escolaapi.domain.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosCadastroUsuario(
@NotBlank(message = "Nome completo é obrigatório")  
String nomeCompleto,


@NotBlank(message = "Email é obrigatório")
@Email
String email, 

@NotBlank(message = "Senha é obrigatória")
String senha, 

@jakarta.validation.constraints.Pattern(regexp = "ADMIN|ALUNO", message = "Role deve ser ADMIN ou ALUNO")
String role) {
    
}

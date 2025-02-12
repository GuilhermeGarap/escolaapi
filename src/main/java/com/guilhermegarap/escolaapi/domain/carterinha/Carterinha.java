package com.guilhermegarap.escolaapi.domain.carterinha;


import com.guilhermegarap.escolaapi.domain.usuario.Usuario;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "carterinhas")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Carterinha {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String cpf;
    private String rg;
    private String dataNascimento;
    private String telefone;
    private String curso;
    private String instituicao;
    private String matricula;
    private String linkImagem;
    
    public Carterinha(DadosCadastroCarterinha dadosCadastroCarterinha) {
        this.cpf = dadosCadastroCarterinha.cpf();
        this.rg = dadosCadastroCarterinha.rg();
        this.dataNascimento = dadosCadastroCarterinha.dataNascimento();
        this.telefone = dadosCadastroCarterinha.telefone();
        this.curso = dadosCadastroCarterinha.curso();
        this.instituicao = dadosCadastroCarterinha.instituicao();
        this.matricula = dadosCadastroCarterinha.matricula();
        this.linkImagem = dadosCadastroCarterinha.linkImagem();
    }

    public boolean verificadorCpf(String cpf) {
    CPFValidator cpfValidator = new CPFValidator();
    try {
        cpfValidator.assertValid(cpf);
        return true;
    } catch (InvalidStateException e) {
        return false;
    }
}

}

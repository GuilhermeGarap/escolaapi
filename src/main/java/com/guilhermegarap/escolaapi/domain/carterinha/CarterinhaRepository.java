package com.guilhermegarap.escolaapi.domain.carterinha;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CarterinhaRepository extends JpaRepository<Carterinha, Long> {
    Optional<Carterinha> findByUsuarioId(Long usuario_id);
    boolean existsByUsuarioId(Long usuarioId);
    boolean existsByCpf(String cpf);
    boolean existsByRg(String rg);
    boolean existsByTelefone(String telefone);
    boolean existsByMatricula(String matricula);
}

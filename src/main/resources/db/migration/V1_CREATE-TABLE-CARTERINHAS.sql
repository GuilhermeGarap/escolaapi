CREATE TABLE carterinhas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cpf VARCHAR(14) NOT NULL,
    rg VARCHAR(20) NOT NULL,
    data_nascimento VARCHAR(10) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    curso VARCHAR(255) NOT NULL,
    instituicao VARCHAR(255) NOT NULL,
    matricula VARCHAR(50) NOT NULL,
    link_imagem VARCHAR(500)
);

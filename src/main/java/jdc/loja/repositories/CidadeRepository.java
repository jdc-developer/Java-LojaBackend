package jdc.loja.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import jdc.loja.domain.Cidade;

public interface CidadeRepository extends JpaRepository<Cidade, Integer>{

}

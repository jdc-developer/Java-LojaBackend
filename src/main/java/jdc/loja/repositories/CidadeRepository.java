package jdc.loja.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jdc.loja.domain.Cidade;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Integer>{

	@Transactional(readOnly=true)
	@Query("select obj from Cidade obj where obj.estado.id = :estadoId order by obj.nome")
	List<Cidade> findCidades(@Param("estadoId") Integer estadoId);
}

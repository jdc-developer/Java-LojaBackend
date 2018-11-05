package jdc.loja.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jdc.loja.domain.Cidade;
import jdc.loja.repositories.CidadeRepository;

@Service
public class CidadeService {

	@Autowired
	private CidadeRepository rep;
	
	public List<Cidade> findByEstado(Integer estadoId) {
		return rep.findCidades(estadoId);
	}
}

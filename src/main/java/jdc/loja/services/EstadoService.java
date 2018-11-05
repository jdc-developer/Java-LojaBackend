package jdc.loja.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jdc.loja.domain.Estado;
import jdc.loja.repositories.EstadoRepository;

@Service
public class EstadoService {

	@Autowired
	private EstadoRepository rep;
	
	public List<Estado> findAll() {
		return rep.findAllByOrderByNome();
	}
}

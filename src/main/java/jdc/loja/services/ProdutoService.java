package jdc.loja.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import jdc.loja.domain.Categoria;
import jdc.loja.domain.Produto;
import jdc.loja.repositories.CategoriaRepository;
import jdc.loja.repositories.ProdutoRepository;
import jdc.loja.services.exceptions.ObjectNotFoundException;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository rep;
	
	@Autowired
	private CategoriaRepository categoriaRep;
	
	public Produto find(Integer id) {
		Optional<Produto> obj = rep.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
					"Objeto não encontrado! Id: " + id + ", Tipo: " + Produto.class.getName()
				));
	}
	
	public Page<Produto> search(String nome, List<Integer> ids, Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageReq = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		List<Categoria> categorias = categoriaRep.findAllById(ids);
		return rep.findDistinctByNomeContainingAndCategoriasIn(nome, categorias, pageReq);
	}

}

package jdc.loja.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jdc.loja.domain.Cidade;
import jdc.loja.domain.Cliente;
import jdc.loja.domain.Endereco;
import jdc.loja.domain.enums.Perfil;
import jdc.loja.domain.enums.TipoCliente;
import jdc.loja.dto.ClienteDTO;
import jdc.loja.dto.ClienteNewDTO;
import jdc.loja.repositories.ClienteRepository;
import jdc.loja.repositories.EnderecoRepository;
import jdc.loja.security.UserSS;
import jdc.loja.services.exceptions.AuthorizationException;
import jdc.loja.services.exceptions.DataIntegrityException;
import jdc.loja.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository rep;
	
	@Autowired
	private EnderecoRepository endRep;
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private S3Service s3;
	
	@Autowired
	private ImageService imgServ;
	
	@Value("${img.prefix.client.profile}")
	private String prefix;
	
	@Value("${img.profile.size}")
	private Integer size;
	
	public Cliente find(Integer id) {
		
		UserSS user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = rep.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
					"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()
				));
	}
	
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = rep.save(obj);
		endRep.saveAll(obj.getEnderecos());
		return obj;
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return rep.save(newObj);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			rep.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir um cliente que possui pedidos");
		}
	}
	
	public List<Cliente> findAll() {
		return rep.findAll();
	}
	
	public Cliente findByEmail(String email) {
		UserSS user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = rep.findByEmail(email);
		if (!obj.isPresent()) {
			throw new ObjectNotFoundException(
					"Objeto não encontradi! Id: " + user.getId() + ", Tipo: " + Cliente.class.getName());
		}
		return obj.get();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageReq = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return rep.findAll(pageReq);
	}

	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()), pe.encode(objDto.getSenha()));
		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
		cli.getEnderecos().addAll(Arrays.asList(end));
		cli.getTelefones().add(objDto.getTelefone1());
		
		if (objDto.getTelefone2() != null) {
			cli.getTelefones().add(objDto.getTelefone2());
		}
		if (objDto.getTelefone3() != null) {
			cli.getTelefones().add(objDto.getTelefone3());
		}
		
		return cli;
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		BufferedImage jpgImage = imgServ.getJpgImageFromFile(multipartFile);
		jpgImage = imgServ.cropSquare(jpgImage);
		jpgImage = imgServ.resize(jpgImage, size);
		String filename = prefix + user.getId() + ".jpg";
		
		return s3.uploadFile(imgServ.getInputStream(jpgImage, "jpg"), filename, "image");
	}
}

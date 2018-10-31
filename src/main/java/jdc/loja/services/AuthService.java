package jdc.loja.services;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jdc.loja.domain.Cliente;
import jdc.loja.repositories.ClienteRepository;
import jdc.loja.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {
	
	@Autowired
	private ClienteRepository cliRep;
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private EmailService emailServ;
	
	private Random rand = new Random();

	public void sendNewPassword(String email) {
		Optional<Cliente> cliOpt = cliRep.findByEmail(email);
		if(!cliOpt.isPresent()) {
			throw new ObjectNotFoundException("Email não encontrado");
		}
		Cliente cli = cliOpt.get();
		String newPass = newPassword();
		cli.setSenha(pe.encode(newPass));
		
		cliRep.save(cli);
		emailServ.sendNewPasswordEmail(cli, newPass);
	}

	private String newPassword() {
		char[] vet = new char[5];
		for (int i = 0; i < 5; i++) {
			vet[i] = randomChar();
		}
		return new String(vet);
	}

	private char randomChar() {
		int opt = rand.nextInt(3);
		if (opt == 0) {
			return (char) (rand.nextInt(10) + 48);
		} else if (opt == 1) {
			return (char) (rand.nextInt(26) + 65);
		} else {
			return (char) (rand.nextInt(26) + 97);
		}
	}
}

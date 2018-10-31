package jdc.loja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jdc.loja.services.S3Service;

@SpringBootApplication
public class LojaApplication implements CommandLineRunner {

	@Autowired
	private S3Service service;
	
	public static void main(String[] args) {
		SpringApplication.run(LojaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		service.uploadFile("C:\\dbz.jpg");
	}
}

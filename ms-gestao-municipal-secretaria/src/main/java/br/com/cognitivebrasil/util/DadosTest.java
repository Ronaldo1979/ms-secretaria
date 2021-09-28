package br.com.cognitivebrasil.util;

import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import br.com.cognitivebrasil.enumerator.Pasta;
import br.com.cognitivebrasil.enumerator.PerfilUser;
import br.com.cognitivebrasil.model.Secretaria;
import br.com.cognitivebrasil.model.Usuario;

@Service
public class DadosTest {
	
	Gson gson = new Gson();
	
	public void criaSecretariaTest() {
		
		try {
			
			Secretaria secretaria = new Secretaria();
			
			secretaria.setNotaPopulacao(4L);
			secretaria.setPasta(Pasta.EDUCACAO);
			secretaria.setResponsavel("Ronaldo Oliveira");
			secretaria.setSobInvestigacao(false);
			
			System.out.println("SECRETARIA TESTE: " + gson.toJson(secretaria));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void criaUsuarioTest() {
		
		try {
			
			Usuario user = new Usuario();
			
			user.setEmail("ronaldo.fjv@gmail.com");
			user.setPerfil(PerfilUser.ROLE_ADMIN);
			user.setSenha("vascodagama");

			System.out.println("USUARIO TESTE: " + gson.toJson(user));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}

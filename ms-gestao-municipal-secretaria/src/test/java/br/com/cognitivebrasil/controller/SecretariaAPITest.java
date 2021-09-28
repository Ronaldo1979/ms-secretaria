package br.com.cognitivebrasil.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.google.gson.Gson;
import br.com.cognitivebrasil.MsGestaoMunicipalSecretariaApplication;
import br.com.cognitivebrasil.enumerator.Pasta;
import br.com.cognitivebrasil.model.Secretaria;
import br.com.cognitivebrasil.security.JwtAuthenticationEntryPoint;
import br.com.cognitivebrasil.security.JwtAuthenticationTokenFilter;
import br.com.cognitivebrasil.security.JwtTokenUtil;
import br.com.cognitivebrasil.security.WebSecurityConfig;
import br.com.cognitivebrasil.service.SecretariaService;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(SecretariaAPI.class)
@ContextConfiguration(classes={MsGestaoMunicipalSecretariaApplication.class, 
							   WebSecurityConfig.class,
							   JwtAuthenticationEntryPoint.class,
							   WebSecurityConfig.class,
							   UserDetailsService.class,
							   JwtAuthenticationTokenFilter.class,
							   JwtTokenUtil.class})
class SecretariaAPITest {
	
	/*
	 Para realização dos testes a autenticação do Spring Security deverá ser ajustada para 
	 permitir acesso a todas as requisições, para isso deverá ser efetuado os seguintes ajustes antes de executar os testes
	 Na classe WebSecurityConfig substituir no metodo configure
	 .antMatchers("/resource/**").authenticated() por .antMatchers("/resource/**").permitAll()
	 */
	
	@Autowired
	private SecretariaAPI secretariaAPI;
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	@MockBean
	private SecretariaService secretariaService;
	
	private List<Secretaria> listaSecretaria = null;
	private Secretaria secretaria = null;
	private MockMvc mockMvc = null;
	
	@BeforeEach
	@SuppressWarnings("deprecation")
	public void setup() {
		
		 mockMvc = MockMvcBuilders
		            .standaloneSetup(secretariaAPI)
		            .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
		            .build();

		    MockitoAnnotations.initMocks(this);
		    
		    this.carregaDadosTest();
	}
	
	@Test
	public void listaSecretariasSucesso() {
		
		when(this.secretariaService.listaSecretaria())
			.thenReturn(listaSecretaria);
		
		ResultActions response;
		try {
			response = mockMvc.perform(
			        get("/resource/secretariats")
			                .contentType(MediaType.APPLICATION_JSON)
			               );
			
			response.andExpect(status().isOk());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void buscaSecretariaSucesso() {
		
		when(this.secretariaService.buscaSecretaria(listaSecretaria.get(0).getId()))
			.thenReturn(Optional.of(listaSecretaria.get(0)));
		
		ResultActions response;
		try {
			response = mockMvc.perform(
			        get("/resource/secretariats/{secretariaId}",listaSecretaria.get(0).getId())
			                .contentType(MediaType.APPLICATION_JSON)
			               );
			
			response.andExpect(status().isOk());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void cadastraSecretariaSucesso() {
		
		Gson gson = new Gson();
		String secretarioJson = gson.toJson(listaSecretaria.get(2));
		
		when(this.secretariaService.cadastraSecretaria(listaSecretaria.get(2)))
			.thenReturn(listaSecretaria.get(2));
		
		when(this.secretariaService.buscaSecretaria(listaSecretaria.get(2).getId()))
			.thenReturn(Optional.of(listaSecretaria.get(2)));
		
		ResultActions response;
		try {
			response = mockMvc.perform(
			        post("/resource/secretariats")
			                .contentType(MediaType.APPLICATION_JSON)
			                .content(secretarioJson)
			               );
			
			response.andExpect(status().isOk());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void atualizaSecretariaSucesso() {
		
		when(this.secretariaService.atualizaStatusSecretaria(listaSecretaria.get(2)))
			.thenReturn(listaSecretaria.get(2));
		
		when(this.secretariaService.buscaSecretaria(listaSecretaria.get(2).getId()))
		.thenReturn(Optional.of(listaSecretaria.get(2)));
		
		ResultActions response;
		try {
			response = mockMvc.perform(
					patch("/resource/secretariats/{secretariaId}",listaSecretaria.get(2).getId())
			                .contentType(MediaType.APPLICATION_JSON)
			               );
			
			response.andExpect(status().isOk());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo para carregar os dados utilizados no mock
	 */
	private void carregaDadosTest() {
		
		//Criando lista de Secretarias
		listaSecretaria = new ArrayList<Secretaria>();
		
		secretaria = new Secretaria();
		secretaria.setId(1L);
		secretaria.setNotaPopulacao(8L);
		secretaria.setPasta(Pasta.SAUDE);
		secretaria.setResponsavel("Ana Quintella");
		secretaria.setSobInvestigacao(false);
		
		listaSecretaria.add(secretaria);
		
		secretaria = new Secretaria();
		secretaria.setId(2L);
		secretaria.setNotaPopulacao(9L);
		secretaria.setPasta(Pasta.SPORT);
		secretaria.setResponsavel("Aroldo");
		secretaria.setSobInvestigacao(false);
		
		listaSecretaria.add(secretaria);
		
		secretaria = new Secretaria();
		secretaria.setId(3L);
		secretaria.setNotaPopulacao(7L);
		secretaria.setPasta(Pasta.EDUCACAO);
		secretaria.setResponsavel("Suzy Carla");
		secretaria.setSobInvestigacao(false);
		
		listaSecretaria.add(secretaria);
	}
	
	
	
	

	

}

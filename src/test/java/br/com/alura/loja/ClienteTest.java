package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;


public class ClienteTest {

	
	HttpServer server;
	private Client client;
	private WebTarget target;
	
	@Before
	public void initConection() {
		server = Servidor.initServer();
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(new LoggingFilter());
		this.client = ClientBuilder.newClient(clientConfig);
		this.target = client.target("http://localhost:9000");
	}
	
	@After
	public void stopConection() {
		server.stop();
	}
	
	@Test
	public void testaConexao() {
		
		client = ClientBuilder.newClient();
		target = client.target("http://localhost:9000");
		String response = target.path("/carrinho/1").request().get(String.class);
		Carrinho carrinho = new Gson().fromJson(response, Carrinho.class);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
		
	}
	
	@Test
	public void testePost() {
		Client client = ClientBuilder.newClient();
		target = client.target("http://localhost:9000");
		
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(007,"Jogo 007",150.00,1));
		carrinho.setRua("Dr José Maciel 1236");
		carrinho.setCidade("Taboão");
		String objJson = carrinho.toJson();
		
		Entity<String> entity = Entity.entity(objJson, MediaType.APPLICATION_JSON);
		
		Response post = target.path("/carrinho").request().post(entity);
		Assert.assertEquals(201, post.getStatus());
		String location = post.getLocation().toString();
		String response = client.target(location).request().get(String.class);
		Assert.assertTrue(response.contains("Jogo 007"));
	}
	
}

package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDao dao;
	
	@Mock
	private SPCService spc;
	
	@Parameter 
	public List<Filme> filmes;
	
	@Parameter (value=1)
	public Double valorLocacao;
	
	@Parameter (value=2)
	public String cenario;
	
	@Before
	public void setup() {
		MockitoAnnotations.openMocks(this);
		
	}
	private static Filme filme1 = umFilme().agora();
	private static Filme filme2 = umFilme().agora();
	private static Filme filme3 = umFilme().agora();
	private static Filme filme4 = umFilme().agora();
	private static Filme filme5 = umFilme().agora();
	private static Filme filme6 = umFilme().agora();
	
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros(){
		// o reorno seria algo como, o retorno de varios uma matriz:
		//{{filmes, valorLocacao},{filmes, valorLocacao},{filmes, valorLocacao}}
		
		//e metodo deve ser static e as variaveis que está sendo 
		//usado dentro desse metodo deve ser static tb
		
		return Arrays.asList(new Object[][] {
			// para dizer que esses dados serão a fonte de dados do JUnit, precisamos:
			// usar uma anotação
			
			//lista de filme é o primeiro parametro , e o valor é o segundo parametro, 
			//terceiro é para deixar o codigo mais legivel na hora que dar um retorno 
			{Arrays.asList(filme1,filme2,filme3),11.0,"3 filmes: 25%"},
			{Arrays.asList(filme1,filme2,filme3,filme4),13.0,"4 filmes: 50%"},
			{Arrays.asList(filme1,filme2,filme3,filme4,filme5),14.0,"5 filmes: 75%"},
			{Arrays.asList(filme1,filme2,filme3,filme4,filme5,filme6),14.0,"6 filmes: 100%"}
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Jose");
	
		//ação:
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificação:
		// 4+4+3+2+1	
		// primeiro filme 100 % no quarto filme com 25 % de desconto 
		
		assertThat(resultado.getValor(), is(valorLocacao));
		

	}

}

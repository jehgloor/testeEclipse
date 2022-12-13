package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDao dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService email;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
	MockitoAnnotations.openMocks(this);

	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//CENÁRIO:
		
		Usuario usuario = umUsuario().agora();
//essa Arrays.asList simplifica a criação de lista tudo que eu passar dentro do 
//parametro do arrays é criado um novo para dentro da minha lista de variavel filmes 
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.).agora());
		
		//AÇÃO:
		Locacao locacao = service.alugarFilme(usuario, filmes);
		//VERIFICAÇÃO://coletar o resultado do cenario:
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
			
//Assert.assertEquals(5.0 ,locacao.getValor(),  0.01);
//para verificar q ela pode ela mesma dar erro: 
			
//error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()),is(true));
			error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
//a data de retorno deve ser o dia seguinte:
//error.checkThat(isMesmaData(locacao.getDataRetorno(),obterDataComDiferencaDias(1)),is(true));
		
// cheque que, a data locação é amanha
			error.checkThat(locacao.getDataRetorno(),MatchersProprios.ehHojeComDiferencaDias(1));
			error.checkThat(locacao.getDataRetorno(),MatchersProprios.ehHojeComDiferencaDias(1));
	}
	//metodo ELEGANTE:
	@Test(expected=FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		//CENARIO
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilmeSemEstoque().agora());
		
		//AÇÃO:
		service.alugarFilme(usuario, filmes);
	}
	
	//METODO ROBUSTA
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//CENARIO
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//AÇÃO:
			try {
				service.alugarFilme(null, filmes);
				Assert.fail();
			} catch (LocadoraException e) {
				assertThat(e.getMessage(), is("Usuário vazio"));
			}
			
	}
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		//CENARIO
		Usuario usuario = umUsuario().agora();
		
		//AÇÃO:
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		service.alugarFilme(usuario, null);
	}

	
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
//nesse teste sempre dava erro pq ele conflitava com o de devolver no proximo dia, 
//só passaria se fosse sabado esse teste a linha de baixo foi feito para que a 
		//barra ficasse verde , porem q fique algo dinamico 
				Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
				
		//acao:
		Locacao retorno = service.alugarFilme(usuario,filmes);
		
		//verificacao:
		Assert.assertThat(retorno.getDataRetorno(), caiNumaSegunda());
//		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
//		Assert.assertTrue(ehSegunda);
		
//		Assert.assertEquals(retorno.getDataRetorno(), new DiaSemanaMatchers(Calendar.SUNDAY));
//		Assert.assertEquals(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
//		Assert.assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSpc() throws Exception  {
		Usuario usuario = umUsuario().agora();
		// aqui eu só to criando um novo usuario:
		Usuario usuario2 = umUsuario().comNome("usuario2").agora();
		
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//quando o spc possuir negativação do usuario 
		// retorne true , fizemos um caso que ele retorna só um usuario
		when(spc.possuiNegativacao(usuario)).thenReturn(true);
		
//		assim é do metodo novo usando a exception antes 
//		exception.expect(LocadoraException.class);
//		exception.expectMessage("Usuário negativado");
		
		// se eu passar o usuario2 onde eu não to fazendo a verificação no meu mockito o teste quebra
		try {
			service.alugarFilme(usuario, filmes);
			//verificação:
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuário negativado"));
		}
		
		//Mockito.verify(spc).possuiNegativacao(usuario);
		// para não passar , mas o teste passou e não deveria : 
		Mockito.verify(spc).possuiNegativacao(Mockito.any(Usuario.class));
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("usuario em dia").agora();
		Usuario usuario3 = umUsuario().comNome("outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
			umLocacao().comUsuario(usuario).atrasado().agora(),
			umLocacao().comUsuario(usuario2).agora(),
			umLocacao().comUsuario(usuario3).atrasado().agora(),
			umLocacao().comUsuario(usuario3).atrasado().agora());
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificao
		Mockito.verify(email).notificarAtraso(usuario);
		// eu quero verificar q o usuario recebeu o email q o usuario 3 tb recebeu o email
		//at leastOnce , mandar pelo menos 1 email. pois estou mandando 2 la no cenario
		Mockito.verify(email,Mockito.atLeastOnce()).notificarAtraso(usuario3);
		// eu quero garantir q o usuario 2 não recebeu o email
		// tenho que avisar o verify que essa expectativa nunca deveria acontecer 
		
		Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2);
		
		// nessa verificação é: deve ser enviado um email para usuario NÃO deve ser enviado ao usuario2
		// deve ser enviado ao usuario3 , tipo sem mais interações
		Mockito.verifyNoMoreInteractions(email);
		
		// verificação generica de quantos email foram enviados
		//verifique no email serão realizadas 2 execuções ao metodo de notificar atraso 
		// passando como parametro qquer instancia da classe usuario
		// ai aqui não distinção 
		Mockito.verify(email , Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
	}
  
	
	@Test
	public void deveTratarErroNoSpc() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catratrófica"));
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		//ação
		service.alugarFilme(usuario, filmes);
		
		//verificação
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario:
		Locacao locacao = LocacaoBuilder.umLocacao().agora();
		
		//ação 
		service.prorogarLocacao(locacao, 3);
		
		//verificação
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(3));
//		assertThat(locacaoRetornada.getDataLocacao(), MatcherProprios());
	}
	
	
	
	
	
	
	
	
	
	
//	
//	@Test
//	public void devePagar75pctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
//		//cenario
//		Usuario usuario = new Usuario("Jose");
//		List<Filme> filmes = Arrays.asList(new Filme("Filme 1",2,4.0),
//				new Filme("Filme 2",2,4.0),new Filme("Filme 3",2,4.0));
//	
//		//ação:
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		//verificação:
//		// 4+4+3 =11	
//		// primeiro filme 100 % no quarto filme com 25 % de desconto 
//		
//		assertThat(resultado.getValor(), is(11.0));
//	}
//	@Test
//	public void devePagar50pctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
//		//cenario
//		Usuario usuario = new Usuario("Jose");
//		List<Filme> filmes = Arrays.asList(new Filme("Filme 1",2,4.0),
//				new Filme("Filme 2",2,4.0),new Filme("Filme 3",2,4.0),
//				new Filme("Filme 4",2,4.0));
//	
//		//ação:
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		//verificação:
//		// 4+4+3+2 =13	
//		// primeiro filme 100 % no quarto filme com 25 % de desconto 
//		
//		assertThat(resultado.getValor(), is(13.0));
//	}
}
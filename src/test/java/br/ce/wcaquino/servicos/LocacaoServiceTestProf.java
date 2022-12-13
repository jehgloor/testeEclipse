//
//
//package br.ce.wcaquino.servicos;
//
//
//import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
//import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.not;
//import static org.junit.Assert.assertThat;
//
//
//import java.util.Date;
//
//
//import org.junit.Test;
//
//import br.ce.wcaquino.entidades.Filme;
//import br.ce.wcaquino.entidades.Locacao;
//import br.ce.wcaquino.entidades.Usuario;
//
//
//public class LocacaoServiceTestProf {
//	@Test
//	public void locacaoFilmeValorIgual() {
//		//CENARIO
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Jose");
//		Filme filme = new Filme("Os Batutinhas",2,50.);
//		
//		//AÇÃO:
//		Locacao locacao;
//		try {
//			locacao = service.alugarFilme(usuario, filme);
//			//VERIFICAÇÃO:
//			//coletar o resultado do cenario:
//			assertThat(locacao.getValor(), is(equalTo(50.)));
//			//a negação do equalTo
//			assertThat(locacao.getValor(), is(not(60.)));
//			//Assert.assertEquals(50. ,locacao.getValor(),  0.01);
//			//para verificar q ela pode ela mesma dar erro: 
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void locacaoFilmeNaData() {
//		//CENARIO
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Jose");
//		Filme filme = new Filme("Os Batutinhas",2,50.);
//		
//		//AÇÃO:
//		Locacao locacao;
//		try {
//			locacao = service.alugarFilme(usuario, filme);
//			assertThat(isMesmaData(locacao.getDataLocacao(), new Date()),is(true));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//
//		
//	}
//	@Test
//	public void locacaoFilmeRetornoNoDiaSeguinte() {
//		//CENARIO
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Jose");
//		Filme filme = new Filme("Os Batutinhas",2,50.);
//		
//		//AÇÃO:
//		Locacao locacao;
//		try {
//			locacao = service.alugarFilme(usuario, filme);
//			//a data de retorno deve ser o dia seguinte:
//			assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)),is(true));
//			//OBS: assert.assertTrue foi deixado menor pq dei um crtl +shift+m para importar a classe inteira 
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//}
//
//

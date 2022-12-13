package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDao dao;
	private SPCService spcService;
	private EmailService emailService;
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException  {
		
		if(usuario == null) {
			throw new LocadoraException("Usuário vazio");
		}
		if(filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}
		//aqui se chama interação:
		for(Filme filme : filmes) {
			if(filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}
		boolean negativado;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas com SPC, tente novamente");
		}
		
		if (negativado) {
			throw new LocadoraException("Usuário negativado");
		}

		
		
		
//		for(int i = 0 ; i < filmes.size();i++) {
//			if(filmes.get(i).getEstoque()==0) {
//				throw new FilmeSemEstoqueException();
//			}
//		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		//aqui se chama interação:
		Double valorTotal =0d;
		for(int i = 0 ; i < filmes.size();i++) {
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			switch(i) {
				case 2:valorFilme = valorFilme * 0.75 ; break;
				case 3:valorFilme = valorFilme * 0.50 ; break;
				case 4:valorFilme = valorFilme * 0.25 ; break;
				case 5:valorFilme = 0d ; break;
			}
			valorTotal +=valorFilme;
		}
		locacao.setValor(valorTotal);
		
		
//		for(int i = 0 ; i < filmes.size();i++) {
//			locacao.setValor(filmes.get(i).getPrecoLocacao());
//		}
	

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega,1 );
		}
		//aqui
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
	
		
		return locacao;
	}
	//deve fazer uma busca de locações
	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes(); 
		for(Locacao locacao: locacoes ) {
			
			//anteriores a data atual
			if(locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}
	
	public void prorogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao= new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() *dias);
		dao.salvar(novaLocacao);
	}
	
	
}
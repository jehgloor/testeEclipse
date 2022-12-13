package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class CalculadoraTest {
	//primeiro saber os comportamentos dos meus testes:
	//uma calculculadora deve somar 2 valores
	
	private  int a = 5;
	private  int b = 3;
	
	private Calculadora calc;
	
	@Before
	public void antes() {
		calc = new Calculadora(); 
	}
	
	@Test
	public void deveSomarDoisValores() {
		//cenario
//		int a = 5;
//		int b = 3;
		
		//ação
		int resultado = calc.somar(a,b);
		
		//verificação:
		Assert.assertEquals(8, resultado);
	}
	
	@Test 
	public void deveSubtrairDoisValores() {
		// ação
		int resultado = calc.subtrair(a,b);
		//verificação:
		Assert.assertEquals(2, resultado);
	}
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		//cenario:
		int a = 6;
		int b=3;
		
		//acao:
		int resultado = calc.divisao(a,b);
		
		//verificação
		Assert.assertEquals(2, resultado);
	}
	
	@Test(expected=NaoPodeDividirPorZeroException.class)
	public void deveLancarExcessaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		int a = 10;
		int b = 0;

		calc.divisao(a, b);
	}
}

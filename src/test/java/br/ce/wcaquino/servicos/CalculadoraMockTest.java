package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.entidades.Locacao;

public class CalculadoraMockTest {
	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	@Before
	public void setup() {
		MockitoAnnotations.openMocks(this);
		
	}
	
	@Test
	public void devoDemonstrarDiferencasEntreMockESpy() {
//		Mockito.when(calcMock.somar(1, 2)).thenReturn(8);
		// dai aqui ele retorno o retorno verdadeiro 
		Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod();
		Mockito.when(calcSpy.somar(1, 3)).thenReturn(8);
		
		System.out.println("mock: "+ calcMock.somar(1, 2));
		System.out.println("spy: "+ calcSpy.somar(1, 2));
	}
	

	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		
		
		
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		//expectativa
		//Mockito.when(calc.somar(1, 2)).thenReturn(5);
		
		
		// porem se eu pedir para ele somar no sysout nos parametros 1 e 8 dai o resultado sera 0
		// pois eu ensinei ao meu mock que a unica coisa q ele espera é o 1 e 2 , qquer coisa diferente
		// disso o resultado é diferente do esperado, dai como o mock retorna sempre valores genericos, 
		// conforme aquilo que o metodo está sendo instanciado, nesse caso como é a soma,
		// sobre valores inteiros , o retorno é de 0 ( um numero inteiro )
		
		//==ENTÃO BORA TENTAR ALGO DIFERENTE==//
		// vou pedir ao mock para q se ele somar 1 com qquer valor o resultado sera de 5: 
		// Para isso vamos usar o matcher dentro do segundo parametro, porem ele não aceita colocar um 
		//valor fixo e um matcher 
		
		
		//Mockito.when(calc.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5);
		
		
		//==ENTÃO BORA TENTAR ALGO DIFERENTE==//
		// e se eu quisesse restrigir , tipo um é valor fixo e outro é qquer valor
		
		// significa: quando eu fizer a soma de algo igual a 1 e outro qquer inteiro o resultado é igual a 5
		//Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
		
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		
		Assert.assertEquals(5, calc.somar(1, 10000000));
		//System.out.println(argCapt.getAllValues());
		
		

	}
}

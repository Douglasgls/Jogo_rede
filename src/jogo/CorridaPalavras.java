package jogo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CorridaPalavras {
	
	private static ArrayList<String> palavras = new ArrayList<>(Arrays.asList(
		    "traves",   
		    "amores",   
		    "carnes",   
		    "pintor",   
		    "torpes",   
		    "partes",   
		    "tremor",   
		    "casais",   
		    "formar",   
		    "pontos",   
		    "postos",   
		    "restos",   
		    "costas",   
		    "montes",   
		    "gritos"
		));
	
	public CorridaPalavras() {}
	
	public String palavrasParaJogadores() {
        Random random = new Random();
        int numeroAleatorio = random.nextInt(palavras.size()); 
        return palavras.get(numeroAleatorio); 
	}
	
	public Boolean comparaPalavras(String original,String chute) {
		if(original.equals(chute)) {
			return true;
		}
		return false;
	}
	
	
	
	
	
}

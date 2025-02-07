package jogo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CorridaPalavras {
	
	private static ArrayList<String> palavras = new ArrayList<>(Arrays.asList(
		    "Traves",   
		    "Amores",   
		    "Carnes",   
		    "Pintor",   
		    "Torpes",   
		    "Partes",   
		    "Tremor",   
		    "Casais",   
		    "Formar",   
		    "Pontos",   
		    "Postos",   
		    "Restos",   
		    "Costas",   
		    "Montes",   
		    "Gritos"
		));
	
	public CorridaPalavras() {}
	
	public String palavrasParaJogadores() {
        Random random = new Random();
        int numeroAleatorio = random.nextInt(palavras.size()); 
        return palavras.get(numeroAleatorio); 
	}
	
	public Boolean comparaPalavras(String original, String chute) {
	    return original.equalsIgnoreCase(chute); 
	}
}

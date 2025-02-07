package jogo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Embaralhador {
	public String shuffle(String s) {
	    List<String> stringDeLetras = new ArrayList<String>();
	    String stringEmbaralhada = "";

	    for (int i = 0; i < s.length(); i++) { //Colocando cada letra na Lista 
	    	stringDeLetras.add(String.valueOf(s.charAt(i)));
	    }
	    
	    Collections.shuffle(stringDeLetras); // Embaralhando a Lista
	    
	    for (int i = 0; i < s.length(); i++) {
	    	stringEmbaralhada += stringDeLetras.get(i);
	    }
	    return stringEmbaralhada;
	}

}

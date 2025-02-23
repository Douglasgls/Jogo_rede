package jogo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Embaralhador {
    public static String shuffle(String s) {
        if (s == null || s.length() <= 1) {
            return s;
        }
        boolean todasIguais = true;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) != s.charAt(0)) {
                todasIguais = false;
                break;
            }
        }
        if (todasIguais) {
            return s;
        }
        List<Character> letras = new ArrayList<>();
        for (char c : s.toCharArray()) {
            letras.add(c);
        }
        
        String embaralhada;
        do {
            Collections.shuffle(letras);
            StringBuilder sb = new StringBuilder();
            for (char c : letras) {
                sb.append(c);
            }
            embaralhada = sb.toString();
        } while (embaralhada.equals(s));
        
        return embaralhada;
    }
}

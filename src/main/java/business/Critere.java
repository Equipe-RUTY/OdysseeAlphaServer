package business;

import java.util.HashMap;
import java.util.Map;

// Enum des critere 6 possibilites. Critere utilisé dans le package Amak

public enum Critere {
	Isolement(0), Stabilite_etat(1), Stabilite_position(2), Heterogeneite(3), Murissement(4), FIN(5);
	private int value;
	private static Map<Integer, Critere> map = new HashMap<>();
	
	private Critere(int id){
		value = id;
	}
	
	static {
        for (Critere critere : Critere.values()) {
            map.put(critere.value, critere);
        }
    }

    public static Critere valueOf(int critere) {
        return map.get(critere);
    }

    public int getValue() {
        return value;
    }
}


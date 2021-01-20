package application;

import java.util.LinkedList;
import java.util.List;
import amak.Migrant;
import javafx.scene.Parent;
import pathfinding.Pathfinder;

public class VueExpo extends Parent {

	private LinkedList<Visiteur> visiteurs;

	public VueExpo() {
		visiteurs = new LinkedList<Visiteur>();
	}

	public void ajouterVisiteur(Migrant m, float x, float y, double rayonTerrain) {
		Visiteur v = new Visiteur(this, m, x, y, rayonTerrain);
		visiteurs.add(v);
		getChildren().add(v);
	}

	public void updateVisiteurs(List<int[]> listeCibles, Pathfinder pf) {
		for (Visiteur v : visiteurs) {
			v.update(listeCibles, pf);
		}
	}

	public void deselectionne() {
		for (Visiteur v : visiteurs) {
			v.deselectionne();
		}
	}
}

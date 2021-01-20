package application;

import java.util.List;

//import com.sun.tools.sjavac.server.SysInfo;

import amak.Migrant;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import pathfinding.Pathfinder;
import pathfinding.Vector2;
import pathfinding.Vector2i;

public class Visiteur extends Parent {
	private Circle circle;
	private Migrant migrant;
	private VueExpo vueExpo;
	private int[] ciblePrecedente = { -1, -1 }; // valeurs volontairement aberrantes
	private int[] cibleSuivante = { -1, -1 }; // pour l initialisation.
	private double rayonTerrain;
	private Vector2i prochaineCase;
	float speed = 0.3f;

	public Visiteur(VueExpo v, Migrant m, float x, float y, double rayonTerrain) {
		vueExpo = v;
		this.rayonTerrain = rayonTerrain;
		circle = new Circle();
		circle.setCenterX(x);
		circle.setCenterY(y);
		circle.setRadius(6.f);
		getChildren().add(circle);

		migrant = m;
		if (migrant != null) {
			migrant.t0_to_tr();
			updateMigrant();
		}

		this.setOnMousePressed(this::onMousePressedHandler);
		this.setOnMouseDragged(this::onMouseDraggedHandler);
	}

	private void onMousePressedHandler(MouseEvent e) {
		e.setDragDetect(true);
		vueExpo.deselectionne();
		selectionne();
	}

	private void selectionne() {
		circle.setFill(Color.GREEN);
		if (migrant != null) migrant.selectionne();
	}

	public void deselectionne() {
		circle.setFill(Color.BLACK);
	}

	private void onMouseDraggedHandler(MouseEvent e) {
		circle.setCenterX(e.getX());
		circle.setCenterY(e.getY());
		updateMigrant();
	}

	// appeler cette fonction apr�s avoir d�plac� le visiteur
	private void updateMigrant() {
		if (migrant == null)
			return;
		double[] coords = { circle.getCenterX() / 400.0 * rayonTerrain, circle.getCenterY() / 210.0 * rayonTerrain };
		if (migrant.getAmas().getEnvironment().isValideInTi(coords))
			migrant.getBlob().setCoordonnee(coords);
	}

	public void update(List<int[]> listeCibles, Pathfinder pf) {
		// choisir cible
		if (ciblePrecedente[0] == cibleSuivante[0] && ciblePrecedente[1] == cibleSuivante[1]) {
			speed = (float)(Math.random() * (0.7f - 0.1f)) + 0.1f ;
			choisirCible(listeCibles, pf);
			
		} else {
			if (testDistanceCible()) {
				ciblePrecedente = cibleSuivante.clone();
				return;
			}
		}

		// bouger vers cible
		Vector2i depart = new Vector2i((int) circle.getCenterX() / 10, (int) circle.getCenterY() / 10);
		if (prochaineCase == null || depart.equals(prochaineCase)) {			
			Vector2i dst = new Vector2i(cibleSuivante[0], cibleSuivante[1]);
			prochaineCase = pf.getProchaineCase(depart, dst);
			
		}

		if (prochaineCase == null) {
			return;
		}
		
		Vector2 coord_centre = new Vector2((float) prochaineCase.x * 10.f + 5.f, (float) prochaineCase.y * 10.f + 5.f);
		Vector2 vel = new Vector2(coord_centre.x - (float) circle.getCenterX(),
				coord_centre.y - (float) circle.getCenterY());
		vel = vel.normalized().multiplyScalar(speed);
		vel = pf.corrigeMouvement(new Vector2((float) circle.getCenterX(), (float) circle.getCenterY()), vel);

		circle.setCenterX(circle.getCenterX() + vel.x);
		circle.setCenterY(circle.getCenterY() + vel.y);

		updateMigrant();
	}

	private void choisirCible(List<int[]> listeCibles, Pathfinder pf) {

		if (listeCibles.size() != 0) {
			if (ciblePrecedente[0] == -1 && ciblePrecedente[1] == -1) {
				int random = ((int) (Math.random() * ((listeCibles.size()) - 0)));
				cibleSuivante = listeCibles.get(random);
			} else {
				int sommetActuel = listeCibles.indexOf(cibleSuivante);
				int nombreDeSommets = listeCibles.size();
				cibleSuivante = listeCibles.get(resultatMarkov(sommetActuel, nombreDeSommets));
			}
		} else {
			Vector2i depart = new Vector2i((int) circle.getCenterX() / 10, (int) circle.getCenterY() / 10);
			do {
				cibleSuivante[0] = (int) (Math.random() * 80.0);
				cibleSuivante[1] = (int) (Math.random() * 42.0);
			} while (pf.getProchaineCase(depart, new Vector2i(cibleSuivante[0] / 10, cibleSuivante[1] / 10)) == null);
		}
	}

	private int resultatMarkov(int sommetActuel, int nombreDeSommets) {
		int resultat = (sommetActuel + 1) % nombreDeSommets;
		while (Math.round(Math.random()) == 0) {
			resultat = (resultat + 1) % nombreDeSommets;
		}
		if (resultat == sommetActuel)
			return resultatMarkov(sommetActuel, nombreDeSommets);
		else
			return resultat;
	}

	private boolean testDistanceCible() {
		int monX = (int) circle.getCenterX() / 10;
		int monY = (int) circle.getCenterY() / 10;
		int cibleX = cibleSuivante[0];
		int cibleY = cibleSuivante[1];
		return (Math.abs(monX - cibleX) <= 1 && Math.abs(monY - cibleY) <= 1);
	}

}

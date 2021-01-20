package application;

import java.util.ArrayList;

import business.Blob;
import javafx.scene.Parent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

// https://openclassrooms.com/courses/les-applications-web-avec-javafx/les-noeuds-graphiques

public class BlobForm extends Parent {
	Circle fond_blob;
	ArrayList<Circle> globules;
	Rectangle selection = null;
	private int tailleBlob;
	BoxBlur boxBlur;// = new BoxBlur(5, 5, 5);

	private void generateBoxBlur() {
		if (tailleBlob > 100)
			boxBlur = new BoxBlur(0.15 * tailleBlob, tailleBlob * 0.15, tailleBlob / 200);
		else
			boxBlur = new BoxBlur(tailleBlob * 0.2, tailleBlob * 0.2, tailleBlob / 2);
	}

	public BlobForm(Blob b, double[] coo, int tailleBlob) {
		// blobList = new HashMap<Blob, BlobForm>();
		{

			this.tailleBlob = tailleBlob;
			globules = new ArrayList<Circle>();
			generateBoxBlur();
			selection = new Rectangle(tailleBlob, tailleBlob);
			selection.setFill(Color.TRANSPARENT);
			selection.setStrokeType(StrokeType.CENTERED);
			selection.setStroke(Color.TRANSPARENT);
			this.getChildren().add(selection);

			this.setTranslateX(coo[0]);// on positionne le groupe
			this.setTranslateY(coo[1]);

			globules.clear();
			
			Color couleur = b.getMaSuperCouleurPreferee();
			if (couleur == null)
				couleur = Color.BLUE;
			fond_blob = new Circle(b.getCoordonnee()[0],b.getCoordonnee()[1], tailleBlob / 6,couleur);
			fond_blob.setEffect(boxBlur);
			globules.add(fond_blob);
			this.getChildren().add(fond_blob);
			
		}
	}

	// cette fonction est appelee si le globule n'est pas mur et doit etre
	// represente blanc.
	// la couleur blanche est donc donnee en parametre.
	public void changeBlob(Blob b, double[] coo, Color couleur, int tailleBlob) {

		{
			this.tailleBlob = tailleBlob;
			this.setTranslateX(coo[0]);// positionnement du blob
			this.setTranslateY(coo[1]);
			for (int i = 0; i < globules.size(); i++) {
				this.getChildren().remove(globules.get(i));
			}
			globules.clear();

			Color couleurGlobule = b.getMaSuperCouleurPreferee();
			
				Color couleur2 = (couleur == null) ? couleurGlobule : couleur;
				fond_blob = new Circle(b.getCoordonnee()[0],b.getCoordonnee()[1], tailleBlob / 6,couleur2);
				fond_blob.setEffect(boxBlur);
				globules.add(fond_blob);
				this.getChildren().add(fond_blob);// ajout du globule
			
		}
	}

	public void showSelection() {

		assert (selection != null);
		selection.setStroke(Color.ANTIQUEWHITE);
	}

	public void deleteSelection() {
		selection.setStroke(Color.TRANSPARENT);
	}

}

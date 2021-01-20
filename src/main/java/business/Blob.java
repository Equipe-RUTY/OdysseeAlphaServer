package business;

import javafx.scene.paint.Color;

public class Blob {
	
	// en pourcents dans t0
	// en m�tres dans tr
	// je crois (wilhem)
	private double[] coordonnee;

	private Color maSuperCouleurPreferee;
	final static Color[] couleurListe = { Color.WHITE, Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.AQUA, Color.DARKORANGE, Color.BISQUE, Color.BLUEVIOLET, Color.DARKSALMON, Color.CYAN, Color.BURLYWOOD, Color.CORAL }; 


	public Blob(double xcor, double ycor, Color couleur) {
		coordonnee = new double[2];
		coordonnee[0] = xcor;
		coordonnee[1] = ycor;
		
		if (couleur == null) {
			System.err.println("couleur null");
			System.exit(-1);
		}
		maSuperCouleurPreferee = couleur;
	}

	// on cr�e un blob � la position (xcor, ycor) de couleur et de forme al�atoire.
	public Blob(double xcor, double ycor) {
		coordonnee = new double[2];
		coordonnee[0] = xcor;
		coordonnee[1] = ycor;
		maSuperCouleurPreferee = Color.WHITE; 
	}
	
	public void choisirCouleurAleatoire() {
		int indiceCouleur = (int) (Math.random() * (couleurListe.length));
		maSuperCouleurPreferee =  couleurListe[indiceCouleur];
	}

	public Blob copy_blob() {
		Blob res = new Blob(coordonnee[0], coordonnee[1], maSuperCouleurPreferee);
		return (res);
	}


	// genere des coordonn�es cart�siennes aleatoires dans carr� de cot� D
	public double[] genererCoordonneesAleaDansCarre(double D) {
		double[] res = new double[2];
		res[0] = Math.random() * (D);
		res[1] = Math.random() * (D);
		return res;
	}


	public double[] getCoordonnee() {
		return coordonnee;
	}

	public void setCoordonnee(double[] coordonnee) {
		this.coordonnee = coordonnee;
	}


	/* calcule la distance euclidienne entre 2 points cooA et cooB */
	private double calculeDistance(double[] cooA, double[] cooB) {
		double sum = 0;
		for (int i = 0; i < cooA.length; i++)
			sum += ((cooB[i] - cooA[i]) * (cooB[i] - cooA[i]));
		return Math.sqrt(sum);

	}

	public boolean isVoisin(Blob b, int radius) {
		if (calculeDistance(b.coordonnee, this.coordonnee) < radius)
			return true;
		return false;
	}


	public Color getMaSuperCouleurPreferee() {
		return maSuperCouleurPreferee;
		// TODO  FONCTION A EFFACER
	}
	
	public void setMaSuperCouleurPreferee(Color couleur)
	{
		this.maSuperCouleurPreferee = couleur;
	}

}

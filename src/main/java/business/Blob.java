package business;

import javafx.scene.paint.Color;

public class Blob {
	
	// en pourcents dans t0 et en metres dans tr
	// je crois (wilhem)
	private double[] coordonnee;

	private Color maCouleur;
	final static Color[] couleurListe = { Color.WHITE, Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.AQUA, Color.DARKORANGE, Color.BISQUE, Color.BLUEVIOLET, Color.DARKSALMON, Color.CYAN, Color.BURLYWOOD, Color.CORAL }; 

	// Creation d'un blob avec un couleur en entree.
	public Blob(double xcor, double ycor, Color couleur) {
		coordonnee = new double[2];
		coordonnee[0] = xcor;
		coordonnee[1] = ycor;
		// verification que la couleur en entree n'est pas null
		if (couleur == null) {
			System.err.println("couleur null");
			System.exit(-1);
		}
		maCouleur = couleur;
	}

	// On cree un blob a la position (xcor, ycor) de forme aleatoire ?
	public Blob(double xcor, double ycor) {
		coordonnee = new double[2];
		coordonnee[0] = xcor;
		coordonnee[1] = ycor;
		maCouleur = Color.WHITE;
	}

	// Attribut une couleur au hasard -> Appel dans Migrants
	public void choisirCouleurAleatoire() {
		int indiceCouleur = (int) (Math.random() * (couleurListe.length));
		maCouleur =  couleurListe[indiceCouleur];
	}

	// Cree une copie du blob avec les coordonnees et la couleur actuellement en mémoire -> Appel Migrants
	public Blob copy_blob() {
		return (new Blob(coordonnee[0], coordonnee[1], maCouleur));
	}

	// Genere des coordonnees cartesiennes aleatoires dans carre de cote D -> Appel dans Migrants
	public double[] genererCoordonneesAleaDansCarre(double D) {
		double[] res = new double[2];
		res[0] = Math.random() * (D);
		res[1] = Math.random() * (D);
		return res;
	}

	// Retourne les coordonnees du Blob -> Appel dans de tres nombreuses classes
	public double[] getCoordonnee() {
		return coordonnee;
	}

	// Modifie les coordonnees du blob -> Appel dans toute les classes entrainant un mouvement
	public void setCoordonnee(double[] coordonnee) {
		this.coordonnee = coordonnee;
	}

	// Calcule la distance euclidienne entre 2 points cooA et cooB
	private double calculeDistance(double[] cooA, double[] cooB) {
		double sum = 0;
		for (int i = 0; i < cooA.length; i++)
			sum += ((cooB[i] - cooA[i]) * (cooB[i] - cooA[i]));
		return Math.sqrt(sum);
	}

	// Vérifie si le blob b est voisin grace au radius -> Radius fixé a 7 de maniere arbitraire appel dans MyEnvironment
	public boolean isVoisin(Blob b, int radius) {
		return (calculeDistance(b.coordonnee, this.coordonnee) < radius);
	}

	// Getter sur maCouleur -> Appel dans beaucoup de classes. Pourquoi // TODO FONCTION A EFFACER ?
	public Color getMaSuperCouleurPreferee() {
		return maCouleur;
		// TODO  FONCTION A EFFACER
	}

	// Setter sur maCouleur
	public void setMaSuperCouleurPreferee(Color couleur)
	{
		this.maCouleur = couleur;
	}
}
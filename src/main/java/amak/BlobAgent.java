package amak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import application.Controller;
import application.ExceptionHandler;
import business.Blob;
import business.Critere;
//import business.CriticalityFunction;
import fr.irit.smac.amak.Agent;
import fr.irit.smac.amak.tools.Log;
import javafx.scene.paint.Color;

enum Action {
	CREER, SE_DEPLACER, SE_SUICIDER, RESTER, CHANGER_COULEUR, CHANGER_FORME, MURIR
};

public abstract class BlobAgent extends Agent<MyAMAS, MyEnvironment> {

	protected Blob blob;
	protected ArrayList<BlobAgent> voisins;

	protected Action currentAction;
	protected Immaginaire newFils;
	protected Action actionPassive;
	protected double[] pastDirection;

	// criticite : par convention : negative si en manque, positive si trop nombreux.
	protected double[] criticite; // TODO la criticite semble etre comrpise entre 0 et 8

	protected double criticite_globale;
	protected double[] directGeneral;
	protected double epsilon = 0.05;
	protected double degreChangement;
	protected double nbChangements;
	protected double moyenneChangements;
	protected Controller controller;

	// lie aux decisions 'passives' : en fonction de l'etat du voisinage

	private HashMap<BlobAgent, Integer> connaissance; // repertorie le temps passe avec un agent
	private int tpsConnaissanceRequise = 2;
	private Color couleurCible;

	protected long tps;

	@Override
	protected void onInitialization() {
		this.blob = (Blob) params[0];
		couleurCible = this.blob.getMaSuperCouleurPreferee();
		criticite = new double[Critere.FIN.getValue()];
		for (int i = 0; i < Critere.FIN.getValue(); i++)
			criticite[i] = 0;
		controller = (Controller) params[1];
		voisins = new ArrayList<>();
		connaissance = new HashMap<>();
		directGeneral = new double[2];
		directGeneral[0] = 0;
		directGeneral[1] = 0;
		degreChangement = 0;
		actionPassive = Action.SE_DEPLACER;
		currentAction = Action.SE_DEPLACER;
		super.onInitialization();
		tps = System.currentTimeMillis();
	}

	public BlobAgent(MyAMAS amas, Blob b, Controller controller) {
		super(amas, b, controller);
	}

	@Override
	protected void onPerceive() {
		try {
			getAmas().getEnvironment().generateNeighbours(this);
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}
	}
	
	double[] RGBToHSV(Color c) {
		/* double[] trash = new double[3];
		trash[0] = c.getRed();
		trash[1] = c.getGreen();
		trash[2] = c.getBlue();
		return trash; */
		// https://en.wikipedia.org/wiki/HSL_and_HSV#From_RGB
		double max = Math.max(Math.max(c.getRed(), c.getGreen()), c.getBlue());
		double min = Math.min(Math.min(c.getRed(), c.getGreen()), c.getBlue());
		double[] hsv = new double[3];
		if (c.getRed() == c.getBlue() && c.getBlue() == c.getGreen()) {
			hsv[0] = 0.f;
		} else if (max == c.getRed()) {
			hsv[0] = 60.f * (c.getGreen() - c.getBlue()) / (max - min); 
		} else if (max == c.getGreen()) {
			hsv[0] = 60.f * (2.f + (c.getBlue() - c.getRed()) / (max - min));
		} else {
			hsv[0] = 60.f * (4.f + (c.getRed() - c.getGreen()) / (max - min));
		}
		if (hsv[0] < 0.f) hsv[0] += 360.f;
		
		if (max == 0.f) {
			hsv[1] = 0.f;
		} else {
			hsv[1] = (max - min) / max;
		}
		
		hsv[2] = max;
		return hsv;
	}
	
	double auxil(double n, double[] hsv) {
		double k = (n + hsv[0] / 60.f) % 6.f;
		return hsv[2] - hsv[1] * hsv[2] * Math.max(Math.min(k, Math.min(4.f - k, 1.0)), 0.0);
	}
	
	Color HSVToRGB(double[] hsv) {
//		return new Color(hsv[0], hsv[1], hsv[2], 1.0);
		return new Color(auxil(5.0, hsv), auxil(3.0, hsv), auxil(1.0, hsv), 1.0);
	}
	
	protected void updateColor() {
		Color init = this.blob.getMaSuperCouleurPreferee();
		double[] initHSV = RGBToHSV(init);
		double[] cibleHSV = RGBToHSV(couleurCible);
		double[] colHSV = new double[3];
		double coef = 0.97;
		double unmoinscoef = 1.0 - coef;
		for (int i = 0; i < 3; i++) {
			colHSV[i] = coef * initHSV[i] + unmoinscoef * cibleHSV[i];
		}
		Color nvColor = HSVToRGB(colHSV);
		this.blob.setMaSuperCouleurPreferee(nvColor);
	}
	/*
	 * **************************************************************** * **********
	 * ACTION ******************* *
	 * ****************************************************************
	 */

	protected void action_se_suicider() {

		Log.debug("quela", "imag decide suicide");
		try {
			currentAction = Action.SE_SUICIDER;
			getAmas().getEnvironment().removeAgent(this);
//			getAmas().getController().remove_blobImmaginaire((Immaginaire)this);
			destroy();
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}
	}

	protected void action_creer() {

		Log.debug("quela", "imag decide creer");
		try {
			currentAction = Action.CREER;
			Log.debug("quela", "imag decide copy");
			Blob newBlob = blob.copy_blob();
			// newBlob.setCoordonnee(convert(newBlob.getCoordonnee()));
			Log.debug("quela", "imag decide setcoord");
			Log.debug("coord", "coord " + newBlob.getCoordonnee());

			newBlob.setCoordonnee(
					(getAmas().getEnvironment().nouvellesCoordonneesTT(this, 2, newBlob.getCoordonnee())));
			Log.debug("quela", "imag decide newfils");
			newFils = new Immaginaire(getAmas(), newBlob, controller);
			Log.debug("quela", "imag decide addagent");
			getAmas().getEnvironment().addAgent(newFils);
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}

	}

//	private double[] convert(double[] nouvellesCoordonnees) {
//		return new double[] {
//				nouvellesCoordonnees[0]*12.5/50.0,
//				nouvellesCoordonnees[1]*12.5/50.0
//		};
//	}

	protected void action_se_deplacer(float delta) {
		try {
			double[] tmp = getAmas().getEnvironment().nouvellesCoordonnees(this, Math.random() * delta * 5.f,
					pastDirection);
			blob.setCoordonnee(tmp);
			currentAction = Action.SE_DEPLACER;

			directGeneral[0] = 0.6 * pastDirection[0] + 0.4 * directGeneral[0];
			directGeneral[1] = 0.6 * pastDirection[1] + 0.4 * directGeneral[1];
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}
	}

	// CHANGEMENT DE COULEUR .... Pour ne pas perdre les couleurs aquises par
	// experience,
	// je choisis de changer la couleur qui est la plus frequente parmi mes
	// globules.
	// action de changer de couleur en prenant une couleur aleatoire
	protected void action_changerCouleur() {
		// choix d'une nouvelle couleur
		Color[] couleurListe = { Color.RED, Color.BLUE, Color.YELLOW, Color.AQUA, Color.GREEN,
				Color.DARKORANGE, Color.BISQUE, Color.BLUEVIOLET, Color.DARKSALMON, Color.CYAN, Color.BURLYWOOD,
				Color.CORAL };
		int indiceCouleur = (int) (Math.random() * (couleurListe.length));
		Color nvlleCouleur = couleurListe[indiceCouleur];
		action_changerCouleur(nvlleCouleur);
	}

	// action de changer de couleur en prenant celle la plus presente dans
	// l'environnement,
	// laquelle est donnee en argument.
	protected void action_changerCouleur(Color couleur) {
		double r = getBlob().getMaSuperCouleurPreferee().getRed() - couleurCible.getRed();
		double g = getBlob().getMaSuperCouleurPreferee().getGreen() - couleurCible.getGreen();
		double b = getBlob().getMaSuperCouleurPreferee().getBlue() - couleurCible.getBlue();
		if (Math.sqrt(r*r + g*g + b*b) >= 0.1) {
			return;
		}
		try {
			couleurCible = couleur;
			nbChangements++;
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
			// TODO : WAT
		}
	}

	/*
	 * ************************************************************************ *
	 * ************** CRITICALITY *************************** *
	 * ************************************************************************
	 */

	protected double computeCriticalityIsolement() {
		double res = 0;
		try {
			res = getAmas().getEnvironment().getIsolement() - voisins.size();
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}

		return (res);
	}
	
	double colorDiff(Color c1, Color c2) {
		double r = c1.getRed() - c2.getRed();
		double g = c1.getGreen() - c2.getGreen();
		double b = c1.getBlue() - c2.getBlue();
		
		return r*r + g*g + b*b;
	}
	
	protected Blob getPlusProcheVoisin() {
		Blob b = null;
		double dst = Double.POSITIVE_INFINITY;
		for (int i = 0; i < voisins.size(); i++) {
			BlobAgent v = voisins.get(i);
			double x = blob.getCoordonnee()[0] - v.getBlob().getCoordonnee()[0];
			double y = blob.getCoordonnee()[1] - v.getBlob().getCoordonnee()[1];
			double d = Math.sqrt(x*x + y*y);
			if (d < dst) {
				dst = d;
				b = v.getBlob();
			}
		}
		return b;
	}

	protected double computeCriticalityHeterogeneite() {

		int nbpareils = 0;
		for (int i = 0; i < voisins.size(); i++) {
			Color couleur = voisins.get(i).getBlob().getMaSuperCouleurPreferee();
			if (colorDiff(couleur, blob.getMaSuperCouleurPreferee()) <= 0.1f) nbpareils++;
		}

		double nbVoisinsOptimal = ((100 - getAmas().getEnvironment().getHeterogeneite()) / 100) * voisins.size();
		return nbpareils - nbVoisinsOptimal;
	}

	protected double computeCriticalityStabilitePosition() {
		// calcul du nombre de voisins qui "bougent".
		// chaque voisin bouge ssi DirectGeneral > (eps,eps)
		double nbBougent = 0;
		for (int i = 0; i < voisins.size(); i++) {
			if (Math.abs(voisins.get(i).getDirectGeneral()[0])
					+ Math.abs(voisins.get(i).getDirectGeneral()[1]) > epsilon) // wtf??
				nbBougent++;
		}

		// nb de voisins optimal qui devraient bouger, selon le curseur.
		double nbOptimal = (getAmas().getEnvironment().getStabilite_position() / 100) * voisins.size();

		if (nbBougent < nbOptimal)
			return (nbOptimal - nbBougent);

		// le problème, si trop de blobs bougent autour, je ne veux pas lever la
		// criticité, afin d'espérer agir pour une autre criticité.
		return (0);
	}

	/*
	 * protected double computeCriticalityStabiliteEtat(){ // calcule de la moyenne
	 * des changements effectu�s alentour: double moyenne = 0; for (int i = 0; i<
	 * voisins.size(); i++){ moyenne += voisins.get(i).getNbChangements(); } moyenne
	 * /= voisins.size();
	 * 
	 * //double res = fctCriticalityStabiliteEtat.compute(moyenneChangements -
	 * moyenne); moyenneChangements = moyenne; return(res);
	 * 
	 * }
	 */

	protected double computeCriticalityInTideal() {

		try {
			criticite[Critere.Isolement.getValue()] = computeCriticalityIsolement();
			criticite[Critere.Heterogeneite.getValue()] = computeCriticalityHeterogeneite();
			criticite[Critere.Stabilite_etat.getValue()] = 0;
			criticite[Critere.Stabilite_position.getValue()] = computeCriticalityStabilitePosition();

			criticite_globale = criticite[Critere.Heterogeneite.getValue()] + criticite[Critere.Isolement.getValue()]
					+ criticite[Critere.Stabilite_etat.getValue()] + criticite[Critere.Stabilite_position.getValue()];
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}
		return criticite_globale;
	}

	// retourne le critere qui a une plus grande criticite
	public Critere Most_critical_critere(double[] subjectCriticity) {
		// return (Collections.max(criticite.entrySet(),
		// Map.Entry.comparingByValue()).getKey());
		double max_valeur = subjectCriticity[0];
		int max_critere = 0;
		for (int i = 0; i < subjectCriticity.length; i++)
			if (Math.abs(max_valeur) < Math.abs(subjectCriticity[i])) {
				max_valeur = subjectCriticity[i];
				max_critere = i;
			}
		return Critere.valueOf(max_critere);
	}

	/* renvoie l'agent le plus critique parmi ses voisins, incluant lui-meme */
	protected BlobAgent getMoreCriticalAgent() {
		Iterator<BlobAgent> itr = voisins.iterator();
		double criticiteMax = criticite_globale;
		BlobAgent res = this;
		while (itr.hasNext()) {
			BlobAgent blobagent = itr.next();
			if (blobagent.criticite_globale > criticiteMax) {
				criticiteMax = blobagent.criticite_globale;
				res = blobagent;
			}
		}
		return (res);
	}

	public double[] getCriticite() {
		return criticite;
	}

	/*
	 * ****************************************** **************************
	 * *********** GETTER / SETTER **************************
	 * ***********************************************************************
	 */

	public Blob getBlob() {
		return blob;
	}

	public void setBlob(Blob blob) {
		this.blob = blob;
	}

	public void addVoisin(BlobAgent blobToAdd) {
		this.voisins.add(blobToAdd);
	}

	public void clearVoisin() {
		this.voisins.clear();
	}

	public ArrayList<BlobAgent> getVoisins() {
		return voisins;
	}

	public void setVoisins(ArrayList<BlobAgent> voisins) {
		this.voisins = voisins;

	}

	public double getCriticite_globale() {
		return criticite_globale;
	}

	public void setCriticite_globale(int criticite_globale) {
		this.criticite_globale = criticite_globale;
	}

	public double[] getPastDirection() {
		return pastDirection;
	}

	public void setPastDirection(double[] pastDirection) {
		this.pastDirection = pastDirection;
	}

	public double[] getDirectGeneral() {
		return directGeneral;
	}

	public void setDirectGeneral(double[] directGeneral) {
		this.directGeneral = directGeneral;
	}

	public double getNbChangements() {
		return nbChangements;
	}

}

package amak;

import java.util.ArrayList;

import application.Controller;
import application.ExceptionHandler;
import business.Blob;
import business.Critere;

public class Migrant extends BlobAgent {

	private boolean isHome;
	private boolean isRiped;
	private boolean rentrer = false;
	private double tauxMurissement = 5;
	private float tauxDeBlanc;
	
	public Migrant(MyAMAS amas, Blob b, Controller controller) {
		super(amas, b, controller);
		isHome = true;
		isRiped = false;
	}

	public boolean isHome() {
		return isHome;
	}

	public void setHome(boolean isHome) {
		this.isHome = isHome;
	}

	// boolean renvoyant true avec une probabilit� de 'tauxMurissement' g�r� dans
	// l'IHM.
	private boolean mustRipe() {
		return (Math.random() * 100 < tauxMurissement);
	}

	/*
	 * private double computeCriticalityMurissement(){ // je compte le nombre de
	 * voisins murs autour de moi. double cpt = 0; for (int i = 0; i<
	 * voisins.size(); i++){ if( ( (Migrant)(voisins.get(i))).isRiped) cpt++; }
	 * return(nbRipedIdeal - cpt); }
	 * 
	 * private double computeCriticalityPositionTo(){ // je compte le nombre de
	 * voisins qui bougent autour de moi. double cpt = 0; for (int i = 0; i<
	 * voisins.size(); i++){ if( ( (Migrant)(voisins.get(i))).isRiped) cpt++; }
	 * return(nbRipedIdeal - cpt); }
	 * 
	 * private double computeCriticalityIsolementTo(){ //if (nbBlobs / 2 > 1 )
	 * 
	 * return(getAmas().getEnvironment().getIsolement() - voisins.size()); }
	 */
	
	
	private void recalculTauxDeBlanc()
	{
		int cpt = 0;
		ArrayList<Migrant> Hibernants = getAmas().getEnvironment().getHibernants();
		for (Migrant actuel : Hibernants)
		{
			if (!(actuel.isRiped()))
			{
				cpt+=1;
			}
		}
		tauxDeBlanc = (float)cpt/(float)Hibernants.size();
	}
	

	private void action_murir() {	
		recalculTauxDeBlanc();
		if (!isRiped) {			
			if (tauxDeBlanc > 0.7)
			{
				isRiped = true;
				blob.choisirCouleurAleatoire();	
			}
			
		}
	}

	@Override
	protected void onDecideAndAct() {
		long millis = System.currentTimeMillis() - tps;
		tps = System.currentTimeMillis();
		try {

			nbChangements = 0;
			currentAction = Action.RESTER; // to initialise
			if (isHome) {
			    if (mustRipe()) {
			    	action_murir();
			    }
				action_se_deplacer(millis/1000.f);
			} else {
				updateColor();
			}
			BlobAgent agentNeedingHelp = super.getMoreCriticalAgent();
			Critere most_critic = Most_critical_critere(agentNeedingHelp.getCriticite());

			// Si je suis sans TR/TI ne peux pas me mouvoir. Je ne peux donc pas gerer la
			// criticite de position
			// Je vais aider le plus critique sur une autre de ses criticites.
			if (!isHome && most_critic == Critere.Stabilite_position) {
				double[] tmp = agentNeedingHelp.getCriticite();
				tmp[Critere.Heterogeneite.getValue()] = 0;
				most_critic = Most_critical_critere(tmp);
			}

			switch (most_critic) {
			case Isolement:
				// too few neighboors -> criticite.ISOLEMENT > 0 -> I have procreate
				if (criticite[Critere.Isolement.getValue()] > 3.f)
					action_creer();
				break;

			case Stabilite_position:
				// only in To
				break;

			case Heterogeneite:
				// if >0 then it's too homogeneous. --> I change the color in a random one.
				// else it's too heterogeneous. -> I change my color to the most present color
				if (criticite[Critere.Heterogeneite.getValue()] > 0) {
					action_changerCouleur();
				} else {
					Blob v = getPlusProcheVoisin();
					if (v == blob) System.out.println("putain");
					if (v == null) {
						action_changerCouleur();
					} else {				
						action_changerCouleur(getPlusProcheVoisin().getMaSuperCouleurPreferee());
					}
				}
				break;

			default:
				break;
			}
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}
	}

	public void t0_to_tr() {
		try {
			isHome = false;
			blob.setCoordonnee(blob.genererCoordonneesAleaDansCarre(getAmas().getEnvironment().rayonTerrain * 2));
			getAmas().getEnvironment().t0_to_tr(this);
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}
	}

	public void t0_to_tr(double[] coo) {
		try {
			isHome = false;
			blob.setCoordonnee(coo);
			getAmas().getEnvironment().t0_to_tr(this);
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}
	}

	public void tr_to_t0() {

		{
			try {
				isHome = true;
//			blob.setCoordonnee(blob.genererCoordonneeAleaDansCercle(100));
				getAmas().getEnvironment().tr_to_t0(this);
				final Migrant m = this;

//				controller.add_blobHibernant(m);
//				controller.remove_blobMigrant(m);
			} catch (Exception e) {
				ExceptionHandler eh = new ExceptionHandler();
				eh.showError(e);
			}
		}
	}

//	public void tr_to_t0(double[] coo){
//		try {
//			isHome = true;
//			blob.setCoordonnee(coo);
//			getAmas().getEnvironment().tr_to_t0(this);
//			controller.add_blobHibernant(this);
//			controller.remove_blobMigrant(this);
//		} catch(Exception e)
//		{
//			ExceptionHandler eh = new ExceptionHandler();
//			eh.showError(e);
//		}
//	}

	/*
	 * private double computeCriticalityInTo(){
	 * criticite[Critere.Murissement.getValue()] = computeCriticalityMurissement();
	 * criticite[Critere.Isolement.getValue()] = computeCriticalityIsolementTo();
	 * criticite[Critere.Stabilite_position.getValue()] =
	 * computeCriticalityPositionTo();
	 * 
	 * return criticite[Critere.Murissement.getValue()]; // TODO }
	 */

	@Override
	protected double computeCriticality() {
		double res = 0;
		try {
			if (!isHome)
				res = computeCriticalityInTideal();
		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}
		return (res);
	}

	public boolean isRiped() {
		return isRiped;
	}

	public void setRiped(boolean isRiped) {
		this.isRiped = isRiped;
	}

	public void rentrerBlob() {
		rentrer = true;
	}

	public void selectionne() {
		controller.unselect();
		controller.select(this);
	}

	@Override
	protected void onAgentCycleEnd() {
		if (rentrer) {
			tr_to_t0();
			rentrer = false;
		}
	}

}

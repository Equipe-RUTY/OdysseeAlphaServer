package amak;

import java.util.ArrayList;	


import application.Controller;
//import fr.irit.smac.amak.Agent;
//import business.CriticalityFunction;
import fr.irit.smac.amak.Environment;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.tools.Log;

public class MyEnvironment extends Environment {
	private ArrayList<BlobAgent> agents;
	private ArrayList<Migrant> hibernants;
	

	
	
	private int radius = 7; // était à 7 dans un autre code copié collé.
	private double isolement = 10;
	private double stabilite_position = 75;
	private double heterogeneite = 50;
	public double rayonTerrain = 12.5; // exprim� en metres

	public MyEnvironment(Controller controller) {
		super(Scheduling.DEFAULT, controller);
	}
	
	
	@Override
	public void onInitialization(){
		agents = new ArrayList<BlobAgent>();
		hibernants = new ArrayList<Migrant>();
	}

	
	public ArrayList<BlobAgent> getAgents() {
		return agents;
	}

	private void generateNeighboursTideal(BlobAgent subject){
		for (int j = 0; j < agents.size(); j++ )
		{
			if(subject != agents.get(j) && subject.getBlob().isVoisin(agents.get(j).getBlob(), radius))
				subject.addVoisin(agents.get(j));
		}
	}
	private void generateNeighboursToriginel(BlobAgent subject){
		for (int j = 0; j < hibernants.size(); j++ )
		{
			if(subject != hibernants.get(j) && subject.getBlob().isVoisin(hibernants.get(j).getBlob(), 2*radius))
				subject.addVoisin(hibernants.get(j));
		}
	}
	
	
	public void generateNeighbours(BlobAgent subject){
		subject.clearVoisin();
		if( (subject instanceof Migrant) && ((Migrant)subject).isHome())
			generateNeighboursToriginel(subject);
		else
			generateNeighboursTideal(subject);
	}


	public ArrayList<Migrant> getHibernants() {
		return hibernants;
	}


	//Cette fonction est appel�e par l'agent apr�s avoir fait le changement.
	// il ne reste donc ici qu'� mettre � jour les listes dans l'environnement
	public void addAgent(BlobAgent agent) {
		synchronized(agents) {			
			agents.add(agent);
		}
	}

	//Cette fonction est appel�e par l'agent apr�s avoir fait le changement.
	// il ne reste donc ici qu'� mettre � jour les listes dans l'environnement
	public void removeAgent(BlobAgent agent) {
		synchronized(agents) {			
			agents.remove(agent);
		}
	}
	
	//Cette fonction est appel�e par l'agent apr�s avoir fait le changement.
	// il ne reste donc ici qu'� mettre � jour les listes dans l'environnement
	public void addMigrant(Migrant migrant) {
		synchronized(hibernants) {			
			hibernants.add(migrant);
		}
	}	

	//Cette fonction est appel�e par l'agent apr�s avoir fait le changement.
	// il ne reste donc ici qu'� mettre � jour les listes dans l'environnement
	public void t0_to_tr(Migrant migrant){
		synchronized(hibernants) {
			hibernants.remove(migrant);
		}
		
		synchronized(agents) {			
			agents.add(migrant);
		}
	}
	
	//Cette fonction est appel�e par l'agent apr�s avoir fait le changement.
	// il ne reste donc ici qu'� mettre � jour les listes dans l'environnement
	public void tr_to_t0(Migrant migrant){
		synchronized(agents) {			
			agents.remove(migrant);
		}
		
		synchronized(hibernants) {			
			hibernants.add(migrant);
		}
	}
	
	
	// indique si la coordonn�e entr�e en param�tre est valide, ie si elle n'est pas hors terrain.
	// returne true if ok. 
	//Ici il s'agit de To : valide si compris dans un carr� de 100*100
	private boolean isValideInTo(double[] coo){
		
		// si dans un carr� de 100*100
		return  (0 < coo[0] && coo[0] < 100 && 0 < coo[1] && coo[1] < 100);
			
		
		// si dans un cercle de diametre 100 ie de rayon 50
//		return ((coo[0] - 50)*(coo[0] - 50) + (coo[1] - 50) * (coo[1] - 50) <= 50 * 50);

	}
	
	// indique si la coordonn�e entr�e en param�tre est valide, ie si elle n'est pas hors terrain.
	// returne true if ok. 
	//Ici il s'agit de Tr ou Ti : valide si compris dans un cercle de rayon RayonTerrain et de centre (RayonTerrain;RayonTerrain)
	public boolean isValideInTi(double[] coo){
		
		return  (0 < coo[0] && coo[0] < 2*rayonTerrain && 0 < coo[1] && coo[1] < 2*rayonTerrain);
		
//		boolean b = (coo[0] - rayonTerrain)*(coo[0] - rayonTerrain) + (coo[1] - rayonTerrain) * (coo[1] - rayonTerrain) <= rayonTerrain * rayonTerrain;
//		Log.debug("quela", "%f %f %f "+b, coo[0],coo[1], rayonTerrain);
//		if (b)
//			return true;
//		return false;
	}
	
	
	
	
	
	// fonction qui � partir de coordonn�es initiales, propose de nouvelles coordonn�es � un certain rayon (le pas).
		public double[] nouvellesCoordonneesTT(BlobAgent agent, double pas, double[] coordonnee){
			double[] res = new double[2];
			
			// Je dois prendre en compte les bordures. Je d�cide de ne pas compliquer les calculs : 
			// Je mets le tout dans une boucle, et je relance l'al�atoire si je suis en dehors du terrain.
			boolean isOK = false;
			int count=0;
			
			
			while(!isOK){
				if (count++>1000) {
				
					Log.error("quela", "more than 1000 loop");	
					double angle = Math.random()*Math.PI*2;
					return new double[] {12.5+Math.cos(angle)*getRandom().nextDouble()*12.5, 12.5+Math.sin(angle)*getRandom().nextDouble()*12.5};
				}
				
				//normalement : coo[0] - pas < res[0] < coo[0] + pas
				res[0] = (Math.random() * 2 * pas) - pas + coordonnee[0];
			
				// j'utilise l'equation d'un cercle de rayon pas.
				// (res[0] - coo[0])� + (res[1] - coo[1])� = pas�
				// � partir de res[0], j'ai 2 solutions possible pour res[1]. 1 positive, une n�gative. choisissons al�atoirement.
				double sign = 1;
				if (Math.random() < 0.5)
					sign = -1;
				res[1] =  coordonnee[1] + (sign * Math.sqrt(pas * pas + (res[0] - coordonnee[0]) * (res[0] - coordonnee[0]) ));
				if( (agent instanceof Migrant) && ((Migrant)agent).isHome())
					isOK = isValideInTo(res);
				else
					isOK = isValideInTi(res);
			}	
			return res;
		}
		
		public double[] nouvellesCoordonnees(BlobAgent agent, double pas, double[] pastDirection){
			double[] res = new double[2];
			double[] coordonnee = agent.getBlob().getCoordonnee();
			boolean isOK = false;
			// Je dois prendre en compte les bordures. Je d�cide de ne pas compliquer les calculs : 
			// Je mets le tout dans une boucle, et je relance l'al�atoire si je suis en dehors du terrain.
			
			
			
			if ( pastDirection != null && Math.random()*100 < 90)
			{
				// je maintiens ma direction pr�c�dente, dont j'ai stock� le vecteur dans pastDirection
				res[0] = coordonnee[0] + pastDirection[0];
				res[1] = coordonnee[1] + pastDirection[1];
				
				if( (agent instanceof Migrant) && ((Migrant)agent).isHome())
					isOK = isValideInTo(res);
				else
					isOK = isValideInTi(res);
			}
			
			
			if(!isOK) // l'ancienne direction ne me dirige pas comme il se doit.
				res = nouvellesCoordonneesTT(agent, pas, agent.getBlob().getCoordonnee());
			
			// cette fonction est appel� pour bouger et on pour cr�er.
			// Je remets donc � jour la variable pastDirection du blob en question.
			if(pastDirection == null)
				pastDirection = new double[2];
			pastDirection[0] = res[0] - coordonnee[0];
			pastDirection[1] = res[1] - coordonnee[1];
			
			//double[] nvlleDirection = new double[2];
			
			agent.setPastDirection(pastDirection);
			
			
			return res;
		}
	

	
	/* *****************************************************************************************
	 * *********************   getter / setter			****************************************
	 * ************************************************************************************* * */
	
	public double getIsolement() {
		return isolement;
	}

	public void setIsolement(int isolement) {
		this.isolement = isolement;
		System.out.println("la nouvelle valeur d'isolement a �t� prise en compte");
		//majFctCriticalityStabiliteEtat();
	}


	public double getStabilite_position() {
		return stabilite_position;
	}

	public void setStabilite_position(int stabilite_position) {
		this.stabilite_position = stabilite_position;
		System.out.println("la nouvelle valeur de stabilit� de la position a �t� prise en compte");
	}

	public double getHeterogeneite() {
		return heterogeneite;
	}

	public void setHeterogeneite(int heterogeneite) {
		this.heterogeneite = heterogeneite;
		System.out.println("la nouvelle valeur " + heterogeneite + " d'h�t�rog�n�it� a �t� prise en compte");
	}


	public void setRadiusVoisins(double radiusVoisins) {
		this.radius = (int)radiusVoisins;
	}

	public Migrant adopter() {
		for (int i = 0; i < hibernants.size(); i++) {
			if(hibernants.get(i).isRiped())
				return hibernants.get(i);
		}
		return null;
		
	}
}

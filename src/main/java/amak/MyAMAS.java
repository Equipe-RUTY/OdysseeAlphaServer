package amak;

import application.Controller;	

import business.Blob;
import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.tools.Log;


public class MyAMAS extends Amas<MyEnvironment>{
	
	private Controller controller;
	
	// genere des coordonn�es cart�siennes aleatoires dans carr� de cot� 100
	public double[] genererCoordonneeAleaDansCercle() {
		double[] res = new double[2];
		res[0] = Math.random() * (100);
		res[1] = Math.random() * (100);
		return res;
	}
	
	@Override
	protected void onInitialConfiguration() {
		int nbBlobs = (int) params[1];
		Migrant migrant;
		//double xcor;
		//double ycor;
		Blob blob;
		controller = (Controller) params[0];		
		for(int i = nbBlobs ; i > 0 ; i--){
			
			// si dans un cercle
			double[] coo = genererCoordonneeAleaDansCercle();
			blob = new Blob(coo[0],coo[1]);

			
			// si dans un carr� :
			//xcor = Math.random() * ( 100 );
			//ycor = Math.random() * ( 100 );
			//blob = new Blob(xcor,ycor, true);
			
			migrant = new Migrant(this, blob, controller);
			getEnvironment().addMigrant(migrant);
		}
		super.onInitialConfiguration();
		System.out.println("fin de l'initilisation de MyAmas");
	}
	
	public MyAMAS(MyEnvironment env, Controller controller, int nbBlobs) {
		super(env, Scheduling.DEFAULT, controller, nbBlobs);
	}

	
	protected void moveAgent(Blob b, BlobAgent agent){
//		Platform.runLater(new Runnable() {
//			public void run() {
				agent.setBlob(b);
				
				// normalement, pr�vient donc l'environnement
//			}
//		});
	}
	
	protected void removeAgent(Blob b, BlobAgent agent){
//		Platform.runLater(new Runnable() {
//			public void run() {
				getEnvironment().getAgents().remove(agent);
				//agents.remove(agent);
				// normalement, pr�vient donc l'environnement
//			}
//		});
		
	}
	
	public Controller getController() {
		return controller;
	}
	
	@Override
	protected void onSystemCycleEnd() {
		Log.debug("quela", "cycle end");
	}
	
	@Override
	protected void onUpdateRender() {
		controller.updateRender();
	}
	
}
package position;

import java.util.ArrayList; 	
import java.util.Timer;
import java.util.TimerTask;

import amak.BlobAgent;
import amak.Migrant;
import amak.MyAMAS;

public class PositionSimulationThread extends Thread{

	private MyAMAS tAmas;
	private Timer timer;
	public boolean is_interrupt;
	public ArrayList<Migrant> blobImmobilises;
	
	public PositionSimulationThread(MyAMAS tAmas){
		super();
		
		this.tAmas = tAmas;
		blobImmobilises = new ArrayList<Migrant>();
	}
	
	public void moveBlob(Migrant b, double[] coo){
		b.getBlob().setCoordonnee(coo);
	}
	
	
	private void bouger_blobs() {
		double[] coo;
		
		for(BlobAgent blob : tAmas.getEnvironment().getAgents()) {
			if (blob instanceof Migrant) {
				Migrant migrant = (Migrant)blob;
				coo = migrant.getAmas().getEnvironment().nouvellesCoordonnees(blob, 1, blob.getPastDirection());
				moveBlob(migrant, coo);
			}
		}
	}
	
	public void interruption() {
        timer.cancel();
		is_interrupt = true;
		
    }
	
	//@Override
	public void demarrer() {
		
		timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
			    // Your database code here
				  bouger_blobs();
			  }
			}, 1*1000, 1*1000);
		
		System.out.println("hey !");
		is_interrupt = false;
	}
	
	
	
	@Override
	public void run(){
		
		is_interrupt = true;
		
	}

	public boolean isIs_interrupt() {
		return is_interrupt;
	}

	public void setIs_interrupt(boolean is_interrupt) {
		this.is_interrupt = is_interrupt;
	}
	
	public void add_immobilise(Migrant m) {
		blobImmobilises.add(m);
	}
	
	public void remove_immobilise(Migrant m) {
		blobImmobilises.remove(m);
	}
}

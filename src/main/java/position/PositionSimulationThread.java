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
	
        /**
         *
         * @param tAmas 
         * 
         */
	public PositionSimulationThread(MyAMAS tAmas){
		super();
		
		this.tAmas = tAmas;
		blobImmobilises = new ArrayList<Migrant>();
	}
	
        /**
         * 
         * @param b
         * @param coo 
         * set new coord
         */
	public void moveBlob(Migrant b, double[] coo){
		b.getBlob().setCoordonnee(coo);
	}
	
	/**
         * 
         * get the new coord from all neighbor
         */
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
	
        /**
         * stop the movement of the blob
         */
	public void interruption() {
                timer.cancel();
		is_interrupt = true;
	}
	
        /**
         * start the movement of the blob
         */
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
		
		//System.out.println("hey !");
		is_interrupt = false;
	}
	
	
	/**
         * ??
         */
	@Override
	public void run(){
		
		is_interrupt = true;
		
	}

        /**
         * 
         * @return if is interrupt 
         */
	public boolean isIs_interrupt() {
		return is_interrupt;
	}

        /**
         * 
         * @param is_interrupt 
         * set is_interrupt
         */
	public void setIs_interrupt(boolean is_interrupt) {
		this.is_interrupt = is_interrupt;
	}
	
        /**
         * 
         * @param m
         * add a blob
         */
	public void add_immobilise(Migrant m) {
		blobImmobilises.add(m);
	}
	/**
         * 
         * @param m 
         * remove a blob
         */
	public void remove_immobilise(Migrant m) {
		blobImmobilises.remove(m);
	}
}

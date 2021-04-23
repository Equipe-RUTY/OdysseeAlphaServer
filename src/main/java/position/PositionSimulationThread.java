package position;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import amak.BlobAgent;
import amak.Migrant;
import amak.MyAMAS;

/**
 * PositionSimulationThread est la classe permettant.
 * de simuler la vie des blobs
 * @author inconnu, busca
 * @version 1.0
 * @see Thread la classe PositionSimulationThread en heritant
 */

public class PositionSimulationThread extends Thread {

/**
 * La variable tAmas represente l'environnement.
 * @see MyAMAS
 */
private MyAMAS tAmas;
/**
 * La variable timer represente le temps.
 * @see Timer
 */
private Timer timer;
/**
 * isInterrupted est le boolean permettant de savoir si.
 * ??? est interrompu
 */
private boolean isInterrupted;
/**
 * blobsImmobilises est la liste des blobs ne se bougeant pas.
 * @see Migrant
 */
private ArrayList<Migrant> blobImmobilises;
/**
 * Constante de delai pour le Timer.
 */
private final int delai = 1000;

/**
 * Constructeur du Thread Position.
 * @param tAmasEntry Nouvelle valeur de tAmas
 */
public PositionSimulationThread(final MyAMAS tAmasEntry) {
	super();
	this.tAmas = tAmasEntry;
	blobImmobilises = new ArrayList<Migrant>();
}

/**
 * Deplace un blob.
 * @param migrant Le blob que l'on veut bouger
 * @param coo Les coordonnees auxquelles on desire le placer
 */
public final void moveBlob(final Migrant migrant, final double[] coo) {
	migrant.getBlob().setCoordonnee(coo);
}

/**
 * Deplace tout les blobs de la l'environnement.
 */
private void moveAllBlobs() {
	double[] coo;
	for (BlobAgent blob : tAmas.getEnvironment().getAgents()) {
		if (blob instanceof Migrant) {
			Migrant migrant = (Migrant) blob;
			coo = migrant.getAmas().getEnvironment().
					nouvellesCoordonnees(blob, 1,
						blob.getPastDirection());
			moveBlob(migrant, coo);
		}
	}
}

/**
 * Met fin au thread.
 * Met a jour isInterrupted
 * @return Boolean qui met a jour isInterrupted
 */
public final boolean end() {
    timer.cancel();
	return true;
}

/**
 * Lance le timer.
 * Lance le deplacement de tout les blobs.
 * @return Boolean qui met a jour isInterrupted
*/
public final boolean begin() {
	timer = new Timer();
	timer.scheduleAtFixedRate(new TimerTask() {
		  @Override
		  public void run() {
			  moveAllBlobs();
		  }
		}, 1 * delai, 1 * delai);
	System.out.println("hey !");
	return false;
}

/**
 * La fonction run est activee automatiquement.
 * a la creation de l'objet donc doit etre Override
 */
@Override
public final void run() {
	this.isInterrupted = true;
}

/**
 * Getter sur la variable isInterrupted.
 * @return boolean isInterrupted
 */
public final  boolean isInterrupted() {
	return this.isInterrupted;
}

/**
 * Setter sur la variable isInterrupted.
 * @param isInterruptedEntry La nouvelle valeur pour isInterrupted
*/
public final void setIsInterrupted(final boolean isInterruptedEntry) {
	this.isInterrupted = isInterruptedEntry;
}
}

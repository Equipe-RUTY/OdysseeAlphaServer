package position;

import java.io.IOException;

//import java.util.Map;
//import java.util.TreeMap;
import java.net.ServerSocket;
import java.net.Socket;


//import amak.BlobAgent;
import amak.Migrant;
import amak.MyAMAS;

/* https://openclassrooms.com/courses/java-et-
 la-programmation-reseau/les-sockets-cote-serveur
https://gfx.developpez.com/tutoriel/java/network/
https://gfx.developpez.com/tutoriel/java/network/#L4


permet de lancer un thread pour etablir la connexion
et recolter les donnees en temps reel
pour les transmettres aux environnements correspondants */

/**
 *PositionSimulationThread est la classe permettant.
 * de simuler la vie des blobs
 * @author inconnu, busca
 * @version 1.0
 * @see Thread la classe PositionSimulationThread en heritant
 */
public class ServerThreadAcceleration extends Thread {
	/**
	 * L'envrionnement.
	 * @see MyAMAS
	 */
	private MyAMAS tAmas;
	/**
	 * Socket du serveur.
	 */
	private ServerSocket socket;
	/**
	 * Boolean servant d'etat du serveur.
	 */
	private boolean running = false;
	/**
	 * Port du serveur.
	 */
	private final int serverPort = 8100;

	/**
	 * Constructeur de l'environnement.
	 * @param tAmasEntry Nouvelle valeur pour tAmas
	 */
	public ServerThreadAcceleration(final MyAMAS tAmasEntry) {
		this.tAmas = tAmasEntry;
	}

	/**
	 * Permet pour chaque requete d�ouvrir.
	 * une instance pour communiquer avec le
	 * smartphone qui cherche a communiquer
	 */
	@Override
	public final void run() {
		try {
			socket = new ServerSocket(serverPort);
		running = true;
		while (running) {
			try {
				System.out.println("j'ecoute");
				final Socket clientSocket = socket.accept();
				System.out.println("Je viens d'entendre qqn");
				new ConnectedClientAcceleration(clientSocket,
						this);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Permet d'adopter un blob.
	 * @param coo Les coordonnees auxquels le blob adopte va etre place
	 * @return Le blob adopte, de type migrant
	 * @see Migrant
	 */
	public Migrant adopterBlob(final double[] coo) {
		Migrant migrant = tAmas.getEnvironment().adopter();
		if (migrant == null) {
			return null;
		}
		migrant.t0ToTr(coo);
		return migrant;
	}

	/**
	 * Permet de bouger un blob.
	 * @param migrant Le blob que l'on veut bouger
	 * @param coo Les coordonnees auxquelles on desire placer le blob
	 */
	public void moveBlob(final Migrant migrant, final double[] coo) {
		migrant.getBlob().setCoordonnee(coo);
	}
}

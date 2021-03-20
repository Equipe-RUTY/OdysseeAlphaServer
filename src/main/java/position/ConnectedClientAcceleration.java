package position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import amak.Migrant;

/**
 * Instance du serveur qui permet de regulariser.
 * les informations entre un acteur de l'expo
 *
 * @author inconnu, busca
 * @version 1.0
 * @see Runnable la classe implemente l'interface Runnable
 */
public class ConnectedClientAcceleration implements Runnable {

	/**
	 * Toutes les communications entrantes.
	 */
	private BufferedReader in;
	/**
	 * Toutes les communications sortantes.
	 */
	private PrintWriter out;
	/**
	 * Le serveur.
	 */
	private ServerThreadAcceleration server;
	/**
	 * Blob dans l'environnement.
	 * @see Migrant
	 */
	private Migrant agent;
	/**
	 * Coordonnees initiales.
	 */
	private final double[] cooInitiale;
	/**
	 * Socket.
	 */
	private Socket socket;

	/**
	 * Initialisation de la connection et des coordonnées initiales.
	 * @param clientSocket
	 * @param serverEntry
	 */
	public ConnectedClientAcceleration(final Socket clientSocket,
				final ServerThreadAcceleration serverEntry) {
		System.out.println("j'initialise le socket");
		socket = clientSocket;
		cooInitiale = new double[2];
		cooInitiale[0] = 2;
		cooInitiale[1] = 12.5;
		initConnection(clientSocket, serverEntry);
	}

	/**
	 * Fonction initialisant le dialogue avec le smartphone.
	 * @param clientSocket
	 * @param serverEntry
	 */
	private void initConnection(final Socket clientSocket,
				final ServerThreadAcceleration serverEntry) {
		try {
			server = serverEntry;
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream());
			new Thread(this).start();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * La methode run est un heritage de Thread.
	 * Elle est activee automatiquement a la creation de
	 * l’objet donc doit etre Override.
	 * Cette methode permet de lire les reponses
	 * du smartphone et modifier la position
	 * du blob en fonction
	 */
	@Override
	public void run() {
		double[] coo = new double[2];
		String line;
		try {
			while ((line = in.readLine()) != null) {
				final String[] res = line.split(";");
				if (res[0].equals("move")) {
					double[] resDouble = getResponses(res);
					if (agent == null) {
						getNewAgent();
					} else {
						setNewCoord(resDouble);
					}
					sendResponse();
				}
			}
			System.out.println("sortie de la boucle while");
			agent.rentrerBlob();
		} catch (final IOException e) {
			System.out.println("erreur Connexion");
			agent.rentrerBlob();
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Taille de ResDouble. Constante.
	 */
	private final int tailleResDouble = 6;
	/**
	 * Renvoit les reponses du smartphone.
	 * @param res
	 * @return resDouble
	 */
	private double[] getResponses(final String[] res) {
		double[] resDouble = new double[tailleResDouble];
		for (int i = 1; i < res.length; i++) {
			resDouble[i - 1] = Double.parseDouble(res[i]);
		}
		System.out.println("Je recopie " + resDouble[0]
				+ ";" + resDouble[1] + ";" + resDouble[2] + ";"
				+ resDouble[3] + ";" + resDouble[4]
				+ ";" + resDouble[5]);
		return resDouble;
	}

	/**
	 * Fonction permettant de mettre a jour.
	 * les nouvelles coordonneess
	 * @param resDouble
	 */
	private void setNewCoord(final double[] resDouble) {
		double[] coo;
		/* Les trois 1eres valeurs me donne l'info
		de l'orientation du telephone
		les 3 suivantes sur l'acceleration dans ce repere. */
		coo = agent.getBlob().getCoordonnee().clone();
		coo[0] += resDouble[3];
		coo[1] += resDouble[4];
		server.moveBlob(agent, coo);
	}

	/**
	 * Adopte un blob et le place aux coordonnees initiales.
	 */
	private void getNewAgent() {
		System.out.println("Je n'ai pas encore de blobs."
				+ " Je vais donc en prendre un");
		System.out.println("Que je place en "
				+ cooInitiale[0] + ";" + cooInitiale[1]);
		agent = server.adopterBlob(cooInitiale);
	}

	/**
	 * Renvoit une reponse.
	 */
	private void sendResponse() {
		String str = "" + agent.getBlob().getCoordonnee()[0]
				+ ";" + agent.getBlob() + ";"
		+ agent.getBlob().getMaSuperCouleurPreferee().toString();
		out.println(str);
		out.flush();
	}
}

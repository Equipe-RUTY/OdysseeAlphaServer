package position;
import java.io.IOException;	

//import java.util.Map;
//import java.util.TreeMap;
import java.net.ServerSocket;
import java.net.Socket;

//import amak.BlobAgent;
import amak.Migrant;
import amak.MyAMAS;

//https://openclassrooms.com/courses/java-et-la-programmation-reseau/les-sockets-cote-serveur
// https://gfx.developpez.com/tutoriel/java/network/

// https://gfx.developpez.com/tutoriel/java/network/#L4


// permet de lancer un thread pour �tablir la connexion
// et r�colter les donn�es en temps r�el
// pour les transmettres aux environnements correspondants




public class ServerThreadAcceleration extends Thread{

	private MyAMAS tAmas;
			
	private ServerSocket socket;
	private boolean running = false;
	
	private static int serverPort = 8100;
	
	
	

	public ServerThreadAcceleration(MyAMAS tAmas) {
		//this(" - localhost:" + serverPort);
		this.tAmas = tAmas;
			//
		
		//blobHibernants = migrants;
	//	blobActifs = new ArrayList<>();
		
	}

	
	
	
	@Override
	public void run() {

		try {
			socket = new ServerSocket(serverPort);
		running = true;
		while (running) {
			try {
				System.out.println("j'�coute");
				final Socket clientSocket = socket.accept();
				System.out.println("Je viens d'entendre qqn");
				new ConnectedClientAcceleration(clientSocket, this);
			} catch (final IOException e) {
				e.printStackTrace();
			}

		}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	
	public Migrant adopterBlob(double[] coo) {
		Migrant migrant = tAmas.getEnvironment().adopter();
		if(migrant==null)
		{
			//lock.unlock();
			return null;
		}
		migrant.t0ToTr(coo);
		return migrant;
	}
	
	public void rentrerBlob(Migrant b){
		System.out.println("hehehehehe");
		b.trToT0();
	}
	
	public void moveBlob(Migrant b, double[] coo){
		b.getBlob().setCoordonnee(coo);
	}    
}


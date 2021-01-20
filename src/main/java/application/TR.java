package application;

import java.util.ArrayList;

import com.jogamp.opengl.awt.GLJPanel;

import amak.BlobAgent;
import amak.Migrant;
import amak.MyEnvironment;
import business.Blob;

public class TR extends Terrain {
	MyEnvironment env;
	
	public TR(GLJPanel glpanel, float spaceWidth, float spaceHeight, MyEnvironment env) {
		super(glpanel, spaceWidth, spaceHeight);
		this.env = env;
	}

	@Override
	public ArrayList<Blob> getBlobs() {
		ArrayList<Blob> blobs = new ArrayList<>();
		ArrayList<BlobAgent> agents = env.getAgents();
		synchronized(agents) {			
			for (BlobAgent agent : env.getAgents()) {
				if (agent instanceof Migrant) {
					blobs.add(agent.getBlob());	
				}
			}
		}
		
		return blobs;
	}
}

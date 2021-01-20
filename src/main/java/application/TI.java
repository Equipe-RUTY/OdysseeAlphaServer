package application;

import java.util.ArrayList;

import com.jogamp.opengl.awt.GLJPanel;

import amak.BlobAgent;
import amak.Immaginaire;
import amak.MyEnvironment;
import business.Blob;

public class TI extends Terrain {
	MyEnvironment env;

	public TI(GLJPanel glpanel, float spaceWidth, float spaceHeight, MyEnvironment env) {
		super(glpanel, spaceWidth, spaceHeight);
		this.env = env;
	}

	@Override
	public ArrayList<Blob> getBlobs() {
		ArrayList<Blob> blobs = new ArrayList<>();
		ArrayList<BlobAgent> agents = env.getAgents();
		synchronized (agents) {
			for (BlobAgent agent : env.getAgents()) {
				blobs.add(agent.getBlob());
			}
		}
		return blobs;
	}
}

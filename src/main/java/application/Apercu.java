package application;

import java.util.ArrayList;

import com.jogamp.opengl.awt.GLJPanel;

import amak.BlobAgent;
import amak.Migrant;
import business.Blob;


public class Apercu extends Terrain {
private BlobAgent agent;

	Apercu(GLJPanel glpanel, float width, float height) {
		super(glpanel, width, height);
		agent = null;
	}

	void setAgent(BlobAgent b) {
		this.agent = b;
	}

	@Override
	public ArrayList<Blob> getBlobs() {
		ArrayList<Blob> res = new ArrayList<Blob>();
		if (agent == null)
			return res;
		res.add(new Blob(getSpaceWidth() / 2.f, getSpaceHeight() / 2.f, agent.getBlob().getMaSuperCouleurPreferee()));
		for (BlobAgent v : agent.getNeighbour()) {
			if (v instanceof Migrant) {
				res.add(new Blob(v.getBlob().getCoordonnee()[0] - agent.getBlob().getCoordonnee()[0] + getSpaceWidth()/2.f,
								 v.getBlob().getCoordonnee()[1] - agent.getBlob().getCoordonnee()[1] + getSpaceHeight()/2.f,
								 v.getBlob().getMaSuperCouleurPreferee()));
			}
		}
		return res;
	}
}

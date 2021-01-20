package application;

import java.util.ArrayList;

import com.jogamp.opengl.awt.GLJPanel;

import amak.Migrant;
import amak.MyEnvironment;
import business.Blob;

public class T0 extends Terrain {
	MyEnvironment env;

	public T0(GLJPanel glpanel, float spaceWidth, float spaceHeight, MyEnvironment env) {
		super(glpanel, spaceWidth, spaceHeight);
		this.env = env;
	}

	@Override
	public ArrayList<Blob> getBlobs() {
		ArrayList<Blob> blobs = new ArrayList<>();
		ArrayList<Migrant> hibernants = env.getHibernants();
		synchronized (hibernants) {
			for (Migrant m : hibernants) {
				blobs.add(m.getBlob());
			}
		}

		return blobs;
	}
}

package application;

import java.util.ArrayList;

import com.jogamp.opengl.awt.GLJPanel;

import amak.BlobAgent;
import amak.Migrant;
import amak.MyEnvironment;
import business.Blob;

/**
 * TR est la fenêtre qui représente les blobs adoptés.
 *
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see Terrain
 */
public class TR extends Terrain {
  /**
   * Environnement où se trouve les blobs.
   */
  private MyEnvironment env;

  /**
   * Constructeur.
   *
   * @param glpanel     fenêtre.
   * @param spaceWidth  largeur de la fenêtre.
   * @param spaceHeight hauteur de la fenêtre.
   * @param pEnv        environnement à afficher.
   */
  public TR(final GLJPanel glpanel, final float spaceWidth,
            final float spaceHeight, final MyEnvironment pEnv) {
    super(glpanel, spaceWidth, spaceHeight);
    this.env = pEnv;
  }

  /**
   * Rend l'ensemble des blobs adoptés.
   *
   * @return liste de blobs.
   */
  @Override
  public ArrayList<Blob> getBlobs() {
    ArrayList<Blob> blobs = new ArrayList<>();
    ArrayList<BlobAgent> agents = env.getAgents();
    synchronized (agents) {
      for (BlobAgent agent : env.getAgents()) {
        if (agent instanceof Migrant) {
          blobs.add(agent.getBlob());
        }
      }
    }

    return blobs;
  }
}

package application;

import java.util.ArrayList;

import com.jogamp.opengl.awt.GLJPanel;

import amak.BlobAgent;
import amak.MyEnvironment;
import business.Blob;

/**
 * TI est la fenêtre qui représente [l'idéel](https://fr.wiktionary.org/wiki/id%C3%A9el).
 *
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see Terrain
 */
public class TI extends Terrain {
  /**
   * Environnement où se trouve les blobs adoptés.
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
  public TI(final GLJPanel glpanel, final float spaceWidth,
            final float spaceHeight, final MyEnvironment pEnv) {
    super(glpanel, spaceWidth, spaceHeight);
    this.env = pEnv;
  }

  /**
   * Rend un ensemble de blobs.
   *
   * @return liste de blobs.
   */
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

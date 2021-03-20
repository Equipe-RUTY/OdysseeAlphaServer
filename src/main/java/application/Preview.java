package application;

import java.util.ArrayList;

import com.jogamp.opengl.awt.GLJPanel;

import amak.BlobAgent;
import amak.Migrant;
import business.Blob;

/**
 * Preview est la fenêtre qui représente le blob sélectionné et ses voisins.
 *
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see Terrain
 */
public class Preview extends Terrain {
  /**
   * Blob actuellement observé.
   */
  private BlobAgent agent;

  /**
   * Constructeur.
   *
   * @param glpanel fenêtre qui affichera l'aperçu du blob.
   * @param width   largeur de la fenêtre.
   * @param height  hauteur de la fenêtre.
   */
  Preview(final GLJPanel glpanel, final float width, final float height) {
    super(glpanel, width, height);
    agent = null;
  }

  /**
   * @param b blob qui sera observé.
   */
  void setAgent(final BlobAgent b) {
    this.agent = b;
  }

  /**
   * Rend la liste des blobs obsersevable par le blob actuel.
   *
   * @return liste de blobs.
   */
  @Override
  public ArrayList<Blob> getBlobs() {
    ArrayList<Blob> res = new ArrayList<Blob>();
    if (agent == null) {
      return res;
    }
    res.add(new Blob(getSpaceWidth() / 2.f,
        getSpaceHeight() / 2.f, agent.getBlob().getMaSuperCouleurPreferee()));
    for (BlobAgent v : agent.getVoisins()) {
      if (v instanceof Migrant) {
        res.add(new Blob(v.getBlob()
            .getCoordonnee()[0] - agent.getBlob().getCoordonnee()[0]
            + getSpaceWidth() / 2.f,
            v.getBlob()
                .getCoordonnee()[1] - agent.getBlob().getCoordonnee()[1]
                + getSpaceHeight() / 2.f,
            v.getBlob().getMaSuperCouleurPreferee()));
      }
    }
    return res;
  }
}

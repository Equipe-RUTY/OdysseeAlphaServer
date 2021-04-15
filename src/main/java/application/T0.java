package application;

import java.util.ArrayList;

import com.jogamp.opengl.awt.GLJPanel;

import amak.Migrant;
import amak.MyEnvironment;
import business.Blob;

/**
 * T0 est la fenêtre qui représente les blobs qui attendent d'être adopté.
 *
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see Terrain
 */
public class T0 extends Terrain {
  /**
   * Environnement où se trouve les blobs inactifs.
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
  public T0(final GLJPanel glpanel, final float spaceWidth,
            final float spaceHeight, final MyEnvironment pEnv) {
    super(glpanel, spaceWidth, spaceHeight);
    this.env = pEnv;
  }

  /**
   * Rend l'ensemble des blobs inactifs.
   *
   * @return liste de blobs.
   */
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

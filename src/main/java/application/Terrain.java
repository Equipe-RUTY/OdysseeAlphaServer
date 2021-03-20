package application;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import business.Blob;
import pathfinding.Vector2;

/**
 * Terrain est l'interface pour toutes les fenêtres.
 *
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see GLEventListener
 */
public abstract class Terrain implements GLEventListener {

  /**
   * Necessaire pour la librairie GL.
   */
  private int program;
  /**
   * Taille des blobs affichés.
   */
  private float radius;
  /**
   * "Blobitude" des blobs affichés.
   * (à quel point un blob est blob).
   */
  private float blobbiness;
  /**
   * Fenêtre graphique.
   */
  private GLJPanel glpanel;
  /**
   * Largeur de la fenêtre.
   */
  private float spaceWidth;
  /**
   * Hauteur de la fenêtre.
   */
  private float spaceHeight;

  /**
   * Constructeur.
   *
   * @param pGlpanel     fenêtre.
   * @param pSpaceWidth  largeur de la fenêtre.
   * @param pSpaceHeight hauteur de la fenêtre.
   */
  public Terrain(final GLJPanel pGlpanel,
                 final float pSpaceWidth, final float pSpaceHeight) {
    super();
    this.spaceWidth = pSpaceWidth;
    this.spaceHeight = pSpaceHeight;
    this.glpanel = pGlpanel;
    this.glpanel.addGLEventListener(this);
  }

  /**
   * Recupère un ensemble de blobs.
   *
   * @return une liste de blobs.
   */
  public abstract ArrayList<Blob> getBlobs();

  /**
   * Affichage du contenu de la fenêtre.
   * Necessaire pour la librairie GL.
   *
   * @param drawable Necessaire pour la librairie GL.
   */
  @Override
  public void display(final GLAutoDrawable drawable) {
    final GL2 gl = drawable.getGL().getGL2();

    // il faut aussi modifier le shader (simple.frag)
    final int maxBlobs = 100;

    gl.glUseProgram(program);

    ArrayList<Blob> blobs = getBlobs();

    for (int i = 0; i < Math.min(blobs.size(), maxBlobs); i++) {
      String base = "blobs[" + Integer.toString(i) + "]";
      int cloc = gl.glGetUniformLocation(program, base + ".c");
      int rloc = gl.glGetUniformLocation(program, base + ".r");
      int bloc = gl.glGetUniformLocation(program, base + ".b");
      int colorloc = gl.glGetUniformLocation(program, base + ".color");
      Vector2 newcoords = new Vector2(0.f, 0.f);
      newcoords.x = (float) (blobs.get(i)
          .getCoordonnee()[0] * drawable.getSurfaceWidth() / spaceWidth);
      newcoords.y = (float) (blobs.get(i)
          .getCoordonnee()[1] * drawable.getSurfaceHeight() / spaceHeight);
      gl.glUniform2f(cloc, newcoords.x,
          drawable.getSurfaceHeight() - newcoords.y);
      gl.glUniform1f(rloc, radius); // default: 500
      gl.glUniform1f(bloc, -blobbiness); // default: -0.1
      gl.glUniform3f(colorloc, (float) blobs.get(i)
              .getMaSuperCouleurPreferee().getRed(),
          (float) blobs.get(i).getMaSuperCouleurPreferee().getGreen(),
          (float) blobs.get(i).getMaSuperCouleurPreferee().getBlue());
    }
    gl.glUniform1i(gl.glGetUniformLocation(program, "blobCount"),
        Math.min(blobs.size(), maxBlobs));
    gl.glBegin(GL2.GL_QUADS); // static field
    gl.glVertex3f(-1.0f, -1.0f, 0.0f); // The bottom left corner
    gl.glVertex3f(-1.0f, 1.0f, 0.0f); // The top left corner
    gl.glVertex3f(1.0f, 1.0f, 0.0f); // The top right corner
    gl.glVertex3f(1.0f, -1.0f, 0.0f); // The bottom right corner
    gl.glEnd();
  }

  /**
   * Necessaire pour la librairie GL.
   *
   * @param arg0 Necessaire pour la librairie GL.
   */
  @Override
  public void dispose(final GLAutoDrawable arg0) {
  }

  /**
   * Initialise le contenu de la fenêtre.
   * Necessaire pour la librairie GL.
   *
   * @param drawable Necessaire pour la librairie GL.
   */
  @Override
  public void init(final GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    ShaderCode vert = ShaderCode.create(gl, GL2.GL_VERTEX_SHADER,
        this.getClass(), "", null,
        "src/main/java/simple", "vert", null, true);
    ShaderCode frag = ShaderCode.create(gl, GL2.GL_FRAGMENT_SHADER,
        this.getClass(), "", null,
        "src/main/java/simple", "frag", null, true);

    ShaderProgram sprogram = new ShaderProgram();
    sprogram.add(vert);
    sprogram.add(frag);
    sprogram.link(gl, System.out);
    program = sprogram.program();
  }

  /**
   * Necessaire pour la librairie GL.
   *
   * @param arg0 Necessaire pour la librairie GL.
   * @param arg1 Necessaire pour la librairie GL.
   * @param arg2 Necessaire pour la librairie GL.
   * @param arg3 Necessaire pour la librairie GL.
   * @param arg4 Necessaire pour la librairie GL.
   */
  @Override
  public void reshape(final GLAutoDrawable arg0, final int arg1,
                      final int arg2, final int arg3, final int arg4) {
  }

  /**
   * @param r taille des blobs affichés.
   */
  public void setRadius(final float r) {
    this.radius = r;
  }

  /**
   * @param b "blobintude" des blobs affichés.
   */
  public void setBlobbiness(final float b) {
    this.blobbiness = b;
  }

  /**
   * @return la fenêtre.
   */
  public GLJPanel getPanel() {
    return glpanel;
  }

  /**
   * @return la largeur de la fenêtre.
   */
  public float getSpaceWidth() {
    return spaceWidth;
  }

  /**
   * @return la hauteur de la fenêtre.
   */
  public float getSpaceHeight() {
    return spaceHeight;
  }
}

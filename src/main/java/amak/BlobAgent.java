package amak;

import java.util.ArrayList;
import java.util.Iterator;

import application.Controller;
import application.ExceptionHandler;
import business.Blob;
import business.Critere;
import fr.irit.smac.amak.Agent;
import javafx.scene.paint.Color;

/**
 * Enumeration of all actions a blob can do.
 */
enum Action {
  /**
   * Create the blob in the environment.
   */
  CREATE,
  /**
   * The blob is moving.
   */
  MOVE,
  /**
   * Remove the blob from the environment.
   */
  DELETE,
  /**
   * The blob doesn't do anything.
   */
  IDLE
}

/**
 * Blob agent is the representation of the blob on his environment.
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see Agent<MyAMAS, MyEnvironment>
 */
public abstract class BlobAgent extends Agent<MyAMAS, MyEnvironment> {
  /**
   * The blob that the blobAgent refer to.
   */
  private Blob blob;

  /**
   * Current neighbour of the blob.
   */
  private ArrayList<BlobAgent> neighbour;

  /**
   * Current action the blob is doing.
   */
  private Action currentAction;

  /**
   * Old set of coordinates, used to calculate the new one.
   */
  private double[] pastDirection;

  /**
   * Criticality is < 0 if there is not enough blob
   * and > 0 if there are to many.
   */
  private double[] criticality;

  /**
   * GlobalCriticality is calculate from heterogeneity, isolation, stability.
   */
  private double globalCriticality;

  /**
   * General direction calculate from pastDirection.
   */
  private double[] generalDirection;

  /**
   * Get incremented each time the blob change color.
   */
  private double numberOfChange;


  /**
   * Representation of the controller.
   */
  private Controller controller;

  /**
   * Current color of a blob.
   */
  private Color currentColor;

  /**
   * Time spend between two action.
   */
  private long tps;


  /**
   * Initialized all attributes of the blob.
   */
  @Override
  protected void onInitialization() {
    this.blob = (Blob) params[0];
    currentColor = this.blob.getMaSuperCouleurPreferee();
    criticality = new double[Critere.FIN.getValue()];
    for (int i = 0; i < Critere.FIN.getValue(); i++) {
      criticality[i] = 0;
    }
    controller = (Controller) params[1];
    neighbour = new ArrayList<>();
    generalDirection = new double[2];
    generalDirection[0] = 0;
    generalDirection[1] = 0;
    currentAction = Action.MOVE;
    super.onInitialization();
    tps = System.currentTimeMillis();
  }

  /**
   *
   *
   * @param amas
   * @param b
   * @param pController
   */
  public BlobAgent(final MyAMAS amas, final Blob b,
                   final Controller pController) {
    super(amas, b, pController);
  }


  /**
   * Getter on the environment and on generated neighbour.
   */
  @Override
  protected void onPerceive() {
    try {
      getAmas().getEnvironment().generateNeighbours(this);
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
  }


  /**
   * Convert rgb color to hsv color.
   *
   * @param c
   * @return a set of 3 value representing a color with hsv protocol.
   * @see <a href="https://en.wikipedia.org/wiki/HSL_and_HSV#From_RGB">
   * wikipedia: From rgb to hsv</a>
   */
  private double[] rgbToHsv(final Color c) {
    // sixtyDegree, four and threeHundredAndSixty need a rename.
    double max = Math.max(Math.max(c.getRed(), c.getGreen()), c.getBlue());
    double min = Math.min(Math.min(c.getRed(), c.getGreen()), c.getBlue());
    final int hsvSize = 3;
    double[] hsv = new double[hsvSize];
    final float sixtyDegree = 60.f;
    if (c.getRed() == c.getBlue() && c.getBlue() == c.getGreen()) {
      hsv[0] = 0.f;
    } else if (max == c.getRed()) {
      hsv[0] = sixtyDegree * (c.getGreen() - c.getBlue()) / (max - min);
    } else if (max == c.getGreen()) {
      hsv[0] = sixtyDegree * (2.f + (c.getBlue() - c.getRed()) / (max - min));
    } else {
      final float four = 4.f;
      hsv[0] = sixtyDegree * (four + (c.getRed() - c.getGreen()) / (max - min));
    }
    if (hsv[0] < 0.f) {
      final float threeHundredAndSixty = 360.f;
      hsv[0] += threeHundredAndSixty;
    }

    if (max == 0.f) {
      hsv[1] = 0.f;
    } else {
      hsv[1] = (max - min) / max;
    }

    hsv[2] = max;
    return hsv;
  }

  /**
   * used for hsvToRgb.
   *
   * @param n
   * @param hsv
   * @return a value for r, g, b or a (color).
   */
  private double auxil(final double n, final double[] hsv) {
    final float sixty = 60.f;
    final float six = 6.f;
    final float four = 4.f;
    double k = (n + hsv[0] / sixty) % six;
    return hsv[2] - hsv[1] * hsv[2] * Math.max(
        Math.min(k, Math.min(four - k, 1.0)), 0.0);
  }

  /**
   * Convert hsv color to rgb color.
   *
   * @param hsv
   * @return a rgb color.
   */
  private Color hsvToRgb(final double[] hsv) {
    final float five = 5.f;
    final float three = 3.f;
    return new Color(auxil(five, hsv), auxil(three, hsv), auxil(1.0, hsv), 1.0);
  }

  /**
   * Update the color of the blob.
   */
  protected void updateColor() {
    Color init = this.blob.getMaSuperCouleurPreferee();
    double[] initHSV = rgbToHsv(init);
    double[] targetHSV = rgbToHsv(currentColor);
    final int three = 3;
    double[] colHSV = new double[three];
    final double coefficient = 0.97;
    double oneMinusCoefficient = 1.0 - coefficient;
    for (int i = 0; i < three; i++) {
      colHSV[i] = coefficient * initHSV[i] + oneMinusCoefficient * targetHSV[i];
    }
    Color nvColor = hsvToRgb(colHSV);
    this.blob.setMaSuperCouleurPreferee(nvColor);
  }
  /*
   * ***************************************************************************
   ********************************** ACTION ***********************************
   * ***************************************************************************
   *  */

  /**
   * Remove the blob from the environment.
   */
  protected void actionRemove() {
    try {
      currentAction = Action.DELETE;
      getAmas().getEnvironment().removeAgent(this);
      destroy();
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
  }

  /**
   * Add the blob on the environment.
   */
  protected void actionCreate() {
    try {
      currentAction = Action.CREATE;
      Blob newBlob = blob.copy_blob();

      newBlob.setCoordonnee(
          (getAmas().getEnvironment().nouvellesCoordonneesTT(
              this, 2, newBlob.getCoordonnee())));

      Imaginary newSon = new Imaginary(getAmas(), newBlob, controller);
      getAmas().getEnvironment().addAgent(newSon);
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
  }

  /**
   * Movement of the blob.
   *
   * @param delta
   */
  protected void actionMove(final float delta) {
    try {
      final float five = 5.f;
      double[] tmp = getAmas().getEnvironment().nouvellesCoordonnees(
          this, Math.random() * delta * five, pastDirection);
      blob.setCoordonnee(tmp);
      currentAction = Action.MOVE;

      final double six = 0.6;
      final double four = 0.4;
      generalDirection[0] = six * pastDirection[0] + four * generalDirection[0];
      generalDirection[1] = six * pastDirection[1] + four * generalDirection[1];
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
  }

  /**
   * Change the color of the blob by a random color.
   */
  protected void actionChangeColor() {
    Color[] colourList = {Color.RED, Color.BLUE, Color.YELLOW, Color.AQUA,
        Color.GREEN, Color.DARKORANGE, Color.BISQUE, Color.BLUEVIOLET,
        Color.DARKSALMON, Color.CYAN, Color.BURLYWOOD, Color.CORAL};
    int indicesColour = (int) (Math.random() * (colourList.length));
    Color newColour = colourList[indicesColour];
    actionChangeColor(newColour);
  }

  /**
   * Changes the color of the blob by a given color.
   * This color is changed according to the most present color.
   *
   * @param color
   */
  protected void actionChangeColor(final Color color) {
    double r = getBlob().getMaSuperCouleurPreferee().getRed()
        - currentColor.getRed();
    double g = getBlob().getMaSuperCouleurPreferee().getGreen()
        - currentColor.getGreen();
    double b = getBlob().getMaSuperCouleurPreferee().getBlue()
        - currentColor.getBlue();
    final double zeroDecimalOne = 0.1;
    if (Math.sqrt(r * r + g * g + b * b) >= zeroDecimalOne) {
      return;
    }
    try {
      currentColor = color;
      numberOfChange++;
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
  }

  /*
   * ************************************************************************ *
   * **************************** CRITICALITY ******************************* *
   * ************************************************************************ *
   */

  /**
   * @return the isolation criterion of the blob
   */
  private double computeIsolationCriterion() {
    double res = 0;
    try {
      res = getAmas().getEnvironment().getIsolation() - neighbour.size();
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
    return (res);
  }

  /**
   * @param c1
   * @param c2
   * @return difference between two colors
   */
  private double colorDiff(final Color c1, final Color c2) {
    double r = c1.getRed() - c2.getRed();
    double g = c1.getGreen() - c2.getGreen();
    double b = c1.getBlue() - c2.getBlue();

    return r * r + g * g + b * b;
  }

  /**
   * @return the most close neighbour
   */
  protected Blob getClosestNeighbour() {
    Blob b = null;
    double dst = Double.POSITIVE_INFINITY;
    for (BlobAgent v : neighbour) {
      double x = blob.getCoordonnee()[0] - v.getBlob().getCoordonnee()[0];
      double y = blob.getCoordonnee()[1] - v.getBlob().getCoordonnee()[1];
      double d = Math.sqrt(x * x + y * y);
      if (d < dst) {
        dst = d;
        b = v.getBlob();
      }
    }
    return b;
  }

  /**
   * @return the heterogeneity criterion
   */
  private double computeHeterogeneityCriterion() {

    int nbpareils = 0;
    for (BlobAgent blobAgent : neighbour) {
      Color colour = blobAgent.getBlob().getMaSuperCouleurPreferee();
      final float one = 0.1f;
      if (colorDiff(colour, blob.getMaSuperCouleurPreferee()) <= one) {
        nbpareils++;
      }
    }
    final int hundred = 100;
    double nbVoisinsOptimal = ((hundred - getAmas().getEnvironment().
        getHeterogeneity()) / hundred) * neighbour.size();
    return nbpareils - nbVoisinsOptimal;
  }

  /**
   * @return the stability criterion
   */
  protected double computeStabilityCriterion() {
    double nbBougent = 0;
    for (BlobAgent blobAgent : neighbour) {
      final double epsilon = 0.05;
      if (Math.abs(blobAgent.getGeneralDirection()[0])
          + Math.abs(blobAgent.getGeneralDirection()[1]) > epsilon) {
        nbBougent++;
      }
    }

    // nb de voisins optimal qui devraient bouger, selon le curseur.
    final int hundred = 100;
    double nbOptimal = (getAmas().getEnvironment()
        .getStability() / hundred) * neighbour.size();

    if (nbBougent < nbOptimal) {
      return (nbOptimal - nbBougent);
    }

    // le problème, si trop de blobs bougent autour, je ne veux pas lever la
    // criticité, afin d'espérer agir pour une autre criticité.
    return (0);
  }

  /**
   * @return global criticality
   */
  protected double computeGlobalCriticality() {

    try {
      criticality[Critere.Isolement.getValue()]
          = computeIsolationCriterion();
      criticality[Critere.Heterogeneite.getValue()]
          = computeHeterogeneityCriterion();
      criticality[Critere.Stabilite_etat.getValue()]
          = 0;
      criticality[Critere.Stabilite_position.getValue()]
          = computeStabilityCriterion();

      globalCriticality = criticality[Critere.Heterogeneite.getValue()]
          + criticality[Critere.Isolement.getValue()]
          + criticality[Critere.Stabilite_etat.getValue()]
          + criticality[Critere.Stabilite_position.getValue()];
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
    return globalCriticality;
  }

  /**
   * @param subjectCriticality
   * @return the most critical criterion
   */
  protected Critere mostCriticalCriterion(final double[] subjectCriticality) {
    double maxValeur = subjectCriticality[0];
    int maxCritere = 0;
    for (int i = 0; i < subjectCriticality.length; i++) {
      if (Math.abs(maxValeur) < Math.abs(subjectCriticality[i])) {
        maxValeur = subjectCriticality[i];
        maxCritere = i;
      }
    }
    return Critere.valueOf(maxCritere);
  }

  /**
   * @return the most critical neighbour
   */
  protected BlobAgent getMostCriticalNeighbour() {
    Iterator<BlobAgent> itr = neighbour.iterator();
    double criticiteMax = globalCriticality;
    BlobAgent res = this;
    while (itr.hasNext()) {
      BlobAgent blobagent = itr.next();
      if (blobagent.globalCriticality > criticiteMax) {
        criticiteMax = blobagent.globalCriticality;
        res = blobagent;
      }
    }
    return (res);
  }


  /*
   * ***********************************************************************
   * ************************** GETTER / SETTER ****************************
   * ***********************************************************************
   */

  /**
   * @return controller
   */
  public Controller getController() {
    return controller;
  }

  /**
   * @return tps
   */
  public long getTps() {
    return tps;
  }

  /**
   * @param pTps
   */
  public void setTps(final long pTps) {
    this.tps = pTps;
  }

  /**
   * @param pNumberOfChange
   */
  public void setNumberOfChange(final double pNumberOfChange) {
    this.numberOfChange = pNumberOfChange;
  }

  /**
   * @return currentAction
   */
  @Deprecated
  public Action getCurrentAction() {
    return currentAction;
  }

  /**
   * @param pCurrentAction
   */
  public void setCurrentAction(final Action pCurrentAction) {
    this.currentAction = pCurrentAction;
  }

  /**
   * @return blob
   */
  public Blob getBlob() {
    return blob;
  }

  /**
   * @param pBlob
   */
  public void setBlob(final Blob pBlob) {
    this.blob = pBlob;
  }

  /**
   * @param blobToAdd
   */
  public void addNeighbour(final BlobAgent blobToAdd) {
    this.neighbour.add(blobToAdd);
  }

  /**
   * @return criticality
   */
  public double[] getCriticality() {
    return criticality;
  }

  /**
   * clear neighbour.
   */
  public void clearNeighbour() {
    this.neighbour.clear();
  }

  /**
   * @return a set of neighbour
   */
  public ArrayList<BlobAgent> getNeighbour() {
    return neighbour;
  }

  /**
   * @return the old direction
   */
  public double[] getPastDirection() {
    return pastDirection;
  }

  /**
   * @param pPastDirection
   */
  public void setPastDirection(final double[] pPastDirection) {
    this.pastDirection = pPastDirection;
  }

  /**
   * @return the general direction
   */
  public double[] getGeneralDirection() {
    return generalDirection;
  }

  /**
   * @param pNeighbour
   */
  @Deprecated
  public void setNeighbour(final ArrayList<BlobAgent> pNeighbour) {
    this.neighbour = pNeighbour;

  }

  /**
   * @return global criticality
   */
  @Deprecated
  public double getGlobalCriticality() {
    return globalCriticality;
  }

  /**
   * @param pGlobalCriticality
   */
  @Deprecated
  public void setGlobalCriticality(final int pGlobalCriticality) {
    this.globalCriticality = pGlobalCriticality;
  }

  /**
   * @param pGeneralDirection
   */
  @Deprecated
  public void setGeneralDirection(final double[] pGeneralDirection) {
    this.generalDirection = pGeneralDirection;
  }

  /**
   * @return number of change
   */
  @Deprecated
  public double getNumberOfChange() {
    return numberOfChange;
  }

}

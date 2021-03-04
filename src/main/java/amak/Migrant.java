package amak;

import java.util.ArrayList;

import application.Controller;
import application.ExceptionHandler;
import business.Blob;
import business.Critere;

/**
 * Migrant blob.
 *
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see BlobAgent
 */
public class Migrant extends BlobAgent {
  /**
   * The home of the blob.
   */
  private boolean isHome;
  /**
   * If the blob is riped.
   */
  private boolean isRiped;
  /**
   * If the blob is inside.
   */
  private boolean rentrer = false;
  /**
   * Rate for the riped.
   */
  private final double tauxMurissement = 5;
  /**
   * Rate of "white" or blank.
   */
  private float tauxDeBlanc;

  /**
   * Constructor.
   *
   * @param amas
   * @param b
   * @param controller
   */
  public Migrant(final MyAMAS amas, final Blob b, final Controller controller) {
    super(amas, b, controller);
    isHome = true;
    isRiped = false;
  }

  /**
   * @return if the blob is home
   */
  public boolean isHome() {
    return isHome;
  }

  /**
   * @param pIsHome
   */
  @Deprecated
  public void setHome(final boolean pIsHome) {
    this.isHome = pIsHome;
  }

  /**
   * @return a random ripe rate.
   */
  private boolean mustRipe() {
    final int hundred = 100;
    return (Math.random() * hundred < tauxMurissement);
  }

  /**
   * compute the rate of "white" (or blank).
   */
  private void computeBlancRate() {
    int cpt = 0;
    ArrayList<Migrant> hibernants = getAmas().getEnvironment().getHibernants();
    for (Migrant actuel : hibernants) {
      if (!(actuel.isRiped())) {
        cpt += 1;
      }
    }
    tauxDeBlanc = (float) cpt / (float) hibernants.size();
  }

  /**
   * The blob is now riped.
   */
  private void actionRipe() {
    computeBlancRate();
    final double seven = 0.7;
    if (!isRiped) {
      if (tauxDeBlanc > seven) {
        isRiped = true;
        getBlob().choisirCouleurAleatoire();
      }

    }
  }

  /**
   * From decision to action.
   */
  @Override
  protected void onDecideAndAct() {
    long millis = System.currentTimeMillis() - getTps();
    setTps(System.currentTimeMillis());
    try {

      setNumberOfChange(0);
      setCurrentAction(Action.IDLE); // to initialise
      if (isHome) {
        if (mustRipe()) {
          actionRipe();
        }
        final float thousand = 1000.f;
        actionMove(millis / thousand);
      } else {
        updateColor();
      }
      BlobAgent agentNeedingHelp = super.getMostCriticalNeighbour();
      Critere mostCritic =
          mostCriticalCriterion(agentNeedingHelp.getCriticality());

      // Si je suis sans TR/TI ne peux pas me mouvoir.
      // Je ne peux donc pas gerer la criticite de position
      // Je vais aider le plus critique sur une autre de ses criticites.
      if (!isHome && mostCritic == Critere.Stabilite_position) {
        double[] tmp = agentNeedingHelp.getCriticality();
        tmp[Critere.Heterogeneite.getValue()] = 0;
        mostCritic = mostCriticalCriterion(tmp);
      }

      switch (mostCritic) {
        case Isolement:
          // too few neighboors -> criticite.ISOLEMENT > 0 -> I have procreate
          final float three = 3.0f;
          if (getCriticality()[Critere.Isolement.getValue()] > three) {
            actionCreate();
          }
          break;

        case Stabilite_position:
          // only in To
          break;

        case Heterogeneite:
          // if >0 then it's too homogeneous.
          // --> I change the color in a random one.
          // else it's too heterogeneous.
          // -> I change my color to the most present color
          if (getCriticality()[Critere.Heterogeneite.getValue()] > 0) {
            actionChangeColor();
          } else {
            Blob v = getClosestNeighbour();
            if (v == null) {
              actionChangeColor();
            } else {
              actionChangeColor(getClosestNeighbour()
                  .getMaSuperCouleurPreferee());
            }
          }
          break;

        default:
          break;
      }
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
  }

  /**
   * move from To to TR.
   */
  public void t0ToTr() {
    try {
      isHome = false;
      getBlob().setCoordonnee(getBlob().genererCoordonneesAleaDansCarre(
          getAmas().getEnvironment().getRayonTerrain() * 2));
      getAmas().getEnvironment().t0ToTr(this);
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
  }

  /**
   * move from TO to TR at a given coordinate.
   *
   * @param coordinate
   */
  public void t0ToTr(final double[] coordinate) {
    try {
      isHome = false;
      getBlob().setCoordonnee(coordinate);
      getAmas().getEnvironment().t0ToTr(this);
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
  }

  /**
   * move from TR to TO.
   */
  public void trToT0() {
    try {
      isHome = true;
      getAmas().getEnvironment().trToT0(this);
      final Migrant m = this;
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }

  }

  /**
   *
   * @return criticality
   */
  @Override
  protected double computeCriticality() {
    double res = 0;
    try {
      if (!isHome) {
        res = computeGlobalCriticality();
      }
    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
    return (res);
  }

  /**
   *
   * @return if is riped
   */
  public boolean isRiped() {
    return isRiped;
  }

  /**
   *
   * @param pIsRiped
   */
  @Deprecated
  public void setRiped(final boolean pIsRiped) {
    this.isRiped = pIsRiped;
  }

  /**
   * set "the blob inside".
   */
  public void rentrerBlob() {
    rentrer = true;
  }

  /**
   * get new selection.
   */
  public void selectionne() {
    getController().deleteSelection();
    getController().selectionne(this);
  }

  /**
   * kill the blob.
   */
  @Override
  protected void onAgentCycleEnd() {
    if (rentrer) {
      trToT0();
      rentrer = false;
    }
  }

}

package amak;

import application.Controller;
import application.ExceptionHandler;
import business.Blob;
import business.Critere;

/**
 * Imaginary blob.
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see BlobAgent
 */
public class Imaginary extends BlobAgent {
  /**
   * @param amas
   * @param b
   * @param controller
   */
  public Imaginary(final MyAMAS amas, final Blob b,
                   final Controller controller) {
    super(amas, b, controller);
  }

  // Fait juste un appel a computeCriticalityInTideal.
  @Override
  protected double computeCriticality() {
    return (computeGlobalCriticality());
  }

  // @Override
  protected void actionMove(final float delta) {
    final double three = 3.0;
    double[] tmp = getAmas().getEnvironment().nouvellesCoordonnees(
        this, Math.random() * delta * three, getPastDirection());
    Blob blob = getBlob();
    blob.setCoordonnee(tmp);
    setCurrentAction(Action.MOVE);
    final double six = 0.6;
    final double four = 0.4;
    getGeneralDirection()[0] = six * getPastDirection()[0]
        + four * getGeneralDirection()[0];
    getGeneralDirection()[1] = six * getPastDirection()[1]
        + four * getGeneralDirection()[1];
  }

  @Override
  protected void onDecideAndAct() {
    float delta = System.currentTimeMillis() - getTps();
    setTps(System.currentTimeMillis());
    try {
      final float thousand = 1000.f;
      actionMove(delta / thousand);
      setNumberOfChange(0);
      updateColor();
      setCurrentAction(Action.IDLE); // to initialise
      BlobAgent agentNeedingHelp =
          super.getMostCriticalNeighbour();
      Critere mostCritic =
          mostCriticalCriterion(agentNeedingHelp.getCriticality());

      switch (mostCritic) {
        case Isolement:
          // too many neighboors ->
          // criticite.ISOLEMENT<0 -> I have to kill myself
          final float three = 3.f;
          if (getCriticality()[Critere.Isolement.getValue()] < -three) {
            actionRemove();
          } else if (getCriticality()[Critere.Isolement.getValue()] > three) {
            actionCreate();
          }
          break;

        case Stabilite_etat:
          break;

        case Stabilite_position:
          actionMove((float) delta / thousand);
          break;

        case Heterogeneite:
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

      super.onDecideAndAct();

    } catch (Exception e) {
      ExceptionHandler eh = new ExceptionHandler();
      eh.showError(e);
    }
  }

}

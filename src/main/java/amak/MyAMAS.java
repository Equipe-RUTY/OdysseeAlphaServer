package amak;

import application.Controller;

import business.Blob;
import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.tools.Log;

/**
 * Imaginary blob.
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see Amas<MyEnvironment>
 */
public class MyAMAS extends Amas<MyEnvironment> {

  /**
   * The controller of the amas.
   */
  private Controller controller;

  /**
   *
   * @return random coordinate in a hundred by hundred square
   */
  public double[] genererCoordonneeAleaDansCercle() {
    final int hundred = 100;
    double[] res = new double[2];
    res[0] = Math.random() * (hundred);
    res[1] = Math.random() * (hundred);
    return res;
  }

  /**
   * Initialisation.
   */
  @Override
  protected void onInitialConfiguration() {
    int nbBlobs = (int) params[1];
    Migrant migrant;
    //double xcor;
    //double ycor;
    Blob blob;
    controller = (Controller) params[0];
    for (int i = nbBlobs; i > 0; i--) {

      // si dans un cercle
      double[] coo = genererCoordonneeAleaDansCercle();
      blob = new Blob(coo[0], coo[1]);


      // si dans un carr� :
      //xcor = Math.random() * ( 100 );
      //ycor = Math.random() * ( 100 );
      //blob = new Blob(xcor,ycor, true);

      migrant = new Migrant(this, blob, controller);
      getEnvironment().addMigrant(migrant);
    }
    super.onInitialConfiguration();
    System.out.println("fin de l'initilisation de MyAmas");
  }

  /**
   * Constructor.
   * @param env
   * @param pController
   * @param nbBlobs
   */
  public MyAMAS(final MyEnvironment env,
                final Controller pController, final int nbBlobs) {
    super(env, Scheduling.DEFAULT, pController, nbBlobs);
  }

  /**
   *
   * @return controller
   */
  @Deprecated
  public Controller getController() {
    return controller;
  }

  /**
   * Print "cycle end".
   */
  @Override
  protected void onSystemCycleEnd() {
    Log.debug("quela", "cycle end");
  }

  /**
   * Update render.
   */
  @Override
  protected void onUpdateRender() {
    controller.updateRender();
  }

}
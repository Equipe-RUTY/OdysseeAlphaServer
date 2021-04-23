package amak;

import java.util.ArrayList;


import application.Controller;
import fr.irit.smac.amak.Environment;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.tools.Log;

/**
 * The environment of the blob.
 *
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see Environment
 */
public class MyEnvironment extends Environment {

  /**
   * Set of adopted blob.
   */
  private ArrayList<BlobAgent> agents;

  /**
   * Set of waiting blob.
   */
  private ArrayList<Migrant> hibernants;

  /**
   * Valeur utilisé pour le calcul des voisins
   * (Distance max a laquelle se trouve un voisin).
   */
  private static final int RADIUS = 7;

  /**
   * Radius(meter) used to check if given coordinate seams ok.
   */
  private static final double RAYON_TERRAIN = 12.5;

  /**
   * Used to compute the isolation criterion.
   */
  private static final double ISOLATION = 10;

  /**
   * Used to compute the stability criterion.
   */
  private static final double STABILITY = 75;

  /**
   * Used to compute the heterogeneity criterion.
   */
  private static final double HETEROGENEITY = 50;

  /**
   * Constructor.
   *
   * @param controller
   */
  public MyEnvironment(final Controller controller) {
    super(Scheduling.DEFAULT, controller);
  }

  /**
   * Initialisation.
   */
  @Override
  public void onInitialization() {
    agents = new ArrayList<BlobAgent>();
    hibernants = new ArrayList<Migrant>();
  }

  /**
   * @return agent
   */
  public ArrayList<BlobAgent> getAgents() {
    return agents;
  }


  /**
   * Generate ideal neighbour for a given blob.
   *
   * @param subject
   */
  private void generateIdealNeighbours(final BlobAgent subject) {
    for (int j = 0; j < agents.size(); j++) {
      if (subject != agents.get(j) && subject.getBlob()
          .isVoisin(agents.get(j).getBlob(), RADIUS)) {
        subject.addNeighbour(agents.get(j));
      }
    }
  }

  /**
   * Generate neighbour waiting in TO.
   *
   * @param subject
   */
  private void generateNeighboursTOriginel(final BlobAgent subject) {
    for (int j = 0; j < hibernants.size(); j++) {
      if (subject != hibernants.get(j) && subject.getBlob()
          .isVoisin(hibernants.get(j).getBlob(), 2 * RADIUS)) {
        subject.addNeighbour(hibernants.get(j));
      }
    }
  }

  /**
   * Generate neighbour active in TR.
   *
   * @param subject
   */
  public void generateNeighbours(final BlobAgent subject) {
    subject.clearNeighbour();
    if ((subject instanceof Migrant) && ((Migrant) subject).isHome()) {
      generateNeighboursTOriginel(subject);
    } else {
      generateIdealNeighbours(subject);
    }
  }


  /**
   * Add a given blob in TR.
   *
   * @param agent
   */
  public void addAgent(final BlobAgent agent) {
    synchronized (agents) {
      agents.add(agent);
    }
  }

  /**
   * Remove a given blob out of TR.
   *
   * @param agent
   */
  public void removeAgent(final BlobAgent agent) {
    synchronized (agents) {
      agents.remove(agent);
    }
  }

  /**
   * Add a given blob to TO.
   *
   * @param migrant
   */
  public void addMigrant(final Migrant migrant) {
    synchronized (hibernants) {
      hibernants.add(migrant);
    }
  }

  /**
   * Move a given blob from TO to TR.
   *
   * @param migrant
   */
  public void t0ToTr(final Migrant migrant) {
    synchronized (hibernants) {
      hibernants.remove(migrant);
    }
    synchronized (agents) {
      agents.add(migrant);
    }
  }

  /**
   * Move a given blob from TR to TO.
   *
   * @param migrant
   */
  public void trToT0(final Migrant migrant) {
    synchronized (agents) {
      agents.remove(migrant);
    }
    synchronized (hibernants) {
      hibernants.add(migrant);
    }
  }


  /**
   * Check if a given coordinate is valid in TO.
   * (here in a square of 100 by 100).
   *
   * @param coordinate
   * @return true if ok else false.
   */
  private boolean isValidInTo(final double[] coordinate) {
    final int hundred = 100;
    return (0 < coordinate[0] && coordinate[0] < hundred
        && 0 < coordinate[1] && coordinate[1] < hundred);
  }

  /**
   * Check if a given coordinate is valid in TR.
   *
   * @param coordinate
   * @return True if ok else False
   */
  public boolean isValidInTi(final double[] coordinate) {
    return (0 < coordinate[0] && coordinate[0] < 2 * RAYON_TERRAIN
        && 0 < coordinate[1] && coordinate[1] < 2 * RAYON_TERRAIN);
  }


  /**
   * @param agent
   * @param step
   * @param coordinate
   * @return new coordinate
   */
  //@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
  public double[] nouvellesCoordonneesTT(
      final BlobAgent agent, final double step, final double[] coordinate) {
    double[] res = new double[2];
    // Je dois prendre en compte les bordures.
    // Je d�cide de ne pas compliquer les calculs :
    // Je mets le tout dans une boucle,
    // et je relance l'al�atoire si je suis en dehors du terrain.
    boolean isOK = false;
    int count = 0;

    final int thousand = 1000;
    while (!isOK) {
      if (count++ > thousand) {

        Log.error("quela", "more than 1000 loop");
        double angle = Math.random() * Math.PI * 2;

        final double twelveAndHalf = 12.5;
        return new double[]{twelveAndHalf
            + Math.cos(angle) * getRandom().nextDouble() * twelveAndHalf,
            twelveAndHalf
                + Math.sin(angle) * getRandom().nextDouble() * twelveAndHalf};
      }

      //normalement : coo[0] - pas < res[0] < coo[0] + pas
      res[0] = (Math.random() * 2 * step) - step + coordinate[0];

      // j'utilise l'equation d'un cercle de rayon pas.
      // (res[0] - coo[0])� + (res[1] - coo[1])� = pas�
      // � partir de res[0], j'ai 2 solutions possible pour res[1].
      // 1 positive, une n�gative. choisissons al�atoirement.
      double sign = 1;
      final double half = 0.5;
      if (Math.random() < half) {
        sign = -1;
      }
      res[1] = coordinate[1] + (sign * Math.sqrt(
          step * step + (res[0] - coordinate[0]) * (res[0] - coordinate[0])));
      if ((agent instanceof Migrant) && ((Migrant) agent).isHome()) {
        isOK = isValidInTo(res);
      } else {
        isOK = isValidInTi(res);
      }
    }
    return res;
  }

  /**
   * @param agent
   * @param step
   * @param pastDirection
   * @return new coordinate
   */
  public double[] nouvellesCoordonnees(
      final BlobAgent agent, final double step, final double[] pastDirection) {
    double[] res = new double[2];
    double[] coordonnee = agent.getBlob().getCoordonnee();
    boolean isOK = false;
    // Je dois prendre en compte les bordures.
    // Je d�cide de ne step compliquer les calculs :
    // Je mets le tout dans une boucle,
    // et je relance l'al�atoire si je suis en dehors du terrain.

    final int hundred = 100;
    final int ninety = 90;
    if (pastDirection != null && Math.random() * hundred < ninety) {
      // je maintiens ma direction pr�c�dente,
      // dont j'ai stock� le vecteur dans pastDirection
      res[0] = coordonnee[0] + pastDirection[0];
      res[1] = coordonnee[1] + pastDirection[1];

      if ((agent instanceof Migrant) && ((Migrant) agent).isHome()) {
        isOK = isValidInTo(res);
      } else {
        isOK = isValidInTi(res);
      }
    }


    if (!isOK) {
      res = nouvellesCoordonneesTT(
          agent, step, agent.getBlob().getCoordonnee());
    }

    // cette fonction est appel� pour bouger et on pour cr�er.
    // Je remets donc � jour la variable pastDirection du blob en question.
    double[] pastDir = new double[2];
    if (pastDirection != null) {
      pastDir = pastDirection;
    }


    pastDir[0] = res[0] - coordonnee[0];
    pastDir[1] = res[1] - coordonnee[1];

    //double[] nvlleDirection = new double[2];

    agent.setPastDirection(pastDir);

    return res;
  }



  // ***********************************************************
  // *********************  getter / setter  *******************
  // ***********************************************************
  /**
   * @return isolation
   */
  public double getIsolation() {
    return ISOLATION;
  }
  /**
   * @return stability
   */
  public double getStability() {
    return STABILITY;
  }
  /**
   * @return heterogeneity
   */
  public double getHeterogeneity() {
    return HETEROGENEITY;
  }
  /**
   * @return hibernants
   */
  public ArrayList<Migrant> getHibernants() {
    return hibernants;
  }

  /**
   * @return a blob to adopt
   */
  public Migrant adopter() {
    for (int i = 0; i < hibernants.size(); i++) {
      if (hibernants.get(i).isRiped()) {
        return hibernants.get(i);
      }
    }
    return null;
  }

  /**
   * @return rayonTerrain
   */
  public final double getRayonTerrain() {
    return RAYON_TERRAIN;
  }
}

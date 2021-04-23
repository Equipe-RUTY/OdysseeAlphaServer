package application;

import java.util.List;

//import com.sun.tools.sjavac.server.SysInfo;

import amak.Migrant;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import pathfinding.Pathfinder;
import pathfinding.Vector2;
import pathfinding.Vector2i;

public class Visiteur extends Parent {

    /**
     * Cercle associé au Visiteur.
     */
    private Circle circle;

    /**
     * Migrant associé au Visiteur.
     */
    private Migrant migrant;

    /**
     * VueExpo associée au Visiteur.
     */
    private VueExpo vueExpo;

    /**
     * Point d'intérêt visité précédemment.
     *
     * Commentaire initial : valeurs volontairement aberrantes
     * pour l'initialisation
     */
    private int[] ciblePrecedente = {-1, -1};

    /**
     * Point d'intérêt suivant.
     *
     * Commentaire initial : valeurs volontairement aberrantes
     * pour l'initialisation
     */
    private int[] cibleSuivante = {-1, -1};

    /**
     * Champ d'action du Visiteur sur le Terrain.
     */
    private double rayonTerrain;

    /**
     * ???.
     */
    private Vector2i prochaineCase;

    /**
     * Vitesse du visiteur.
     */
    private float speed = 0.3f;

    /**
     * Constructeur de la classe Visiteur.
     *
     * @param v
     * @param m
     * @param x
     * @param y
     * @param pRayonTerrain
     */
    public Visiteur(final VueExpo v, final Migrant m, final float x,
                    final float y, final double pRayonTerrain) {
        vueExpo = v;
        this.rayonTerrain = pRayonTerrain;
        circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(6.f);
        getChildren().add(circle);

        migrant = m;
        if (migrant != null) {
            migrant.t0ToTr();
            updateMigrant();
        }

        this.setOnMousePressed(this::onMousePressedHandler);
        this.setOnMouseDragged(this::onMouseDraggedHandler);
    }

    /**
     * Méthode permettant d'associer la sélection d'un Visiteur
     * à un clic sur le migrant du Visiteur depuis la vue de l'exposition.
     *
     * @param e
     */
    private void onMousePressedHandler(final MouseEvent e) {
        e.setDragDetect(true);
        vueExpo.unselectVisitors();
        select();
    }

    /**
     * Méthode permettant de déplacer via "Drag and Drop"
     * un Visiteur depuis la vue de l'exposition.
     *
     * @param e
     */
    private void onMouseDraggedHandler(final MouseEvent e) {
        circle.setCenterX(e.getX());
        circle.setCenterY(e.getY());
        updateMigrant();
    }

    /**
     * Méthode permettant de sélectionner un Visiteur.
     */
    private void select() {
        circle.setFill(Color.GREEN);
        if (migrant != null) {
            migrant.selectionne();
        }
    }

    /**
     * Méthode permettant de dé-sélectionner un Visiteur.
     */
    public void unselect() {
        circle.setFill(Color.BLACK);
    }

    /**
     * Méthode permettant de mettre à jour le migrant associé
     * au Visiteur.
     *
     * Commentaire initial : appeler cette fonction après
     * avoir déplacé le visiteur
     */
    private void updateMigrant() {
        if (migrant != null) {
            double[] coords = {circle.getCenterX() / 400.0 * rayonTerrain,
                    circle.getCenterY() / 210.0 * rayonTerrain};
            if (migrant.getAmas().getEnvironment().isValidInTi(coords)) {
                migrant.getBlob().setCoordonnee(coords);
            }
        }
    }

    /**
     * Méthode permettant de simuler le déplacement des migrants des visiteurs
     * entre les différents points d'intérêt du territoire.
     *
     * @param listeCibles
     * @param pf
     */
    public void update(final List<int[]> listeCibles, final Pathfinder pf) {
        // choisir cible
        if (ciblePrecedente[0] == cibleSuivante[0]
                && ciblePrecedente[1] == cibleSuivante[1]) {
            speed = (float) (Math.random() * (0.7f - 0.1f)) + 0.1f;
            chooseTarget(listeCibles, pf);

        } else {
            if (testDistanceCible()) {
                ciblePrecedente = cibleSuivante.clone();
                return;
            }
        }

        // bouger vers cible
        Vector2i depart = new Vector2i((int) circle.getCenterX() / 10,
                (int) circle.getCenterY() / 10);
        if (prochaineCase == null || depart.equals(prochaineCase)) {
            Vector2i dst = new Vector2i(cibleSuivante[0], cibleSuivante[1]);
            prochaineCase = pf.getProchaineCase(depart, dst);

        }

        if (prochaineCase == null) {
            return;
        }

        Vector2 coordCentre = new Vector2(
                (float) prochaineCase.x * 10.f + 5.f,
                (float) prochaineCase.y * 10.f + 5.f);
        Vector2 vel = new Vector2(coordCentre.x - (float) circle.getCenterX(),
                coordCentre.y - (float) circle.getCenterY());
        vel = vel.normalized().multiplyScalar(speed);
        vel = pf.corrigeMouvement(new Vector2((float) circle.getCenterX(),
                (float) circle.getCenterY()), vel);

        circle.setCenterX(circle.getCenterX() + vel.x);
        circle.setCenterY(circle.getCenterY() + vel.y);

        updateMigrant();
    }

    /**
     * Méthode permettant de déterminer le prochain point d'intérêt
     * du Visiteur durant une simulation.
     *
     * @param listeCibles
     * @param pf
     */
    private void chooseTarget(final List<int[]> listeCibles,
                              final Pathfinder pf) {
        if (listeCibles.size() != 0) {
            if (ciblePrecedente[0] == -1 && ciblePrecedente[1] == -1) {
                int random = ((int) (Math.random() *
                        ((listeCibles.size()) - 0)));
                cibleSuivante = listeCibles.get(random);
            } else {
                int sommetActuel = listeCibles.indexOf(cibleSuivante);
                int nombreDeSommets = listeCibles.size();
                cibleSuivante = listeCibles.get(resultatMarkov(sommetActuel,
                        nombreDeSommets));
            }
        } else {
            Vector2i depart = new Vector2i((int) circle.getCenterX() / 10,
                    (int) circle.getCenterY() / 10);
            do {
                cibleSuivante[0] = (int) (Math.random() * 80.0);
                cibleSuivante[1] = (int) (Math.random() * 42.0);
            } while (pf.getProchaineCase(depart,
                    new Vector2i(cibleSuivante[0] / 10,
                            cibleSuivante[1] / 10)) == null);
        }
    }

    /**
     * Méthode permettant de choisir le prochain point d'intérêt
     * parmi une liste de points d'intérêt donnée.
     *
     * @param sommetActuel
     * @param nombreDeSommets
     * @return le numéro du prochain centre d'intérêt
     */
    private int resultatMarkov(final int sommetActuel,
                               final int nombreDeSommets) {
        int resultat = (sommetActuel + 1) % nombreDeSommets;
        while (Math.round(Math.random()) == 0) {
            resultat = (resultat + 1) % nombreDeSommets;
        }
        if (resultat == sommetActuel) {
            return resultatMarkov(sommetActuel, nombreDeSommets);
        } else {
            return resultat;
        }
    }

    /**
     * ???.
     *
     * @return booléen
     */
    private boolean testDistanceCible() {
        int monX = (int) circle.getCenterX() / 10;
        int monY = (int) circle.getCenterY() / 10;
        int cibleX = cibleSuivante[0];
        int cibleY = cibleSuivante[1];
        return (Math.abs(monX - cibleX) <= 1 && Math.abs(monY - cibleY) <= 1);
    }
}

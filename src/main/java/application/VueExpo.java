package application;

import java.util.LinkedList;
import java.util.List;
import amak.Migrant;
import javafx.scene.Parent;
import pathfinding.Pathfinder;

public class VueExpo extends Parent {
    /**
     * Liste des visiteurs de la VueExpo.
     */
    private LinkedList<Visiteur> visiteurs;

    /**
     * Constructeur de la classe VueExpo.
     */
    public VueExpo() {
        visiteurs = new LinkedList<>();
    }

    /**
     * Méthode permettant d'ajouter un visiteur à la liste des visiteurs
     * présents lors de l'exposition.
     *
     * @param m
     * @param x
     * @param y
     * @param rayonTerrain
     */
    public void addVisitor(final Migrant m, final float x,
                           final float y, final double rayonTerrain) {
        Visiteur v = new Visiteur(this, m, x, y, rayonTerrain);
        visiteurs.add(v);
        getChildren().add(v);
    }

    /**
     * Méthode permettant de mettre à jour chacun des visiteurs de la VueExpo.
     *
     * @param listeCibles
     * @param pf
     */
    public void updateVisitors(final List<int[]> listeCibles,
                               final Pathfinder pf) {
        for (Visiteur v : visiteurs) {
            v.update(listeCibles, pf);
        }
    }

    /**
     * Méthode permettant de déselectionner les visiteurs de la VueExpo.
     */
    public void unselectVisitors() {
        for (Visiteur v : visiteurs) {
            v.unselect();
        }
    }
}

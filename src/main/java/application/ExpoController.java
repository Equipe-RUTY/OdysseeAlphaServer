package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;

import amak.Migrant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pathfinding.Pathfinder;

public class ExpoController implements Initializable {

    /**
     * Panneau de l'ExpoController.
     */
    @FXML
    private AnchorPane panelVueExpo;

    /**
     * Espace d'affichage de l'ExpoController.
     */
    @FXML
    private Canvas leCanvas;

    /**
     * Les différents états de l'ExpoController.
     */
    private enum Mode {
        /**
         * Mode permettant la création de murs pour définir une exposition.
         */
        MUR,
        /**
         * Mode permettant la création des points d'intérêt d'une exposition.
         */
        CIBLE,
        /**
         * Mode permettant de supprimer des murs d'une expostion.
         */
        GOMME,
        /**
         * Mode permettant d'ajouter des visiteurs à une exposition.
         */
        VISITEUR,
        /**
         * Mode "neutre".
         */
        RIEN
    }

    /**
     * Grille de l'ExpoController.
     */
    private boolean[][] grille;

    /**
     * Mode de l'ExpoController.
     */
    private Mode mode;

    /**
     * Liste des points d'intérêts de l'exposition.
     */
    private LinkedList<int[]> listeCibles = new LinkedList<>();

    /**
     * VueExpo de l'ExpoController.
     */
    private VueExpo vueExpo;

    /**
     * Controller de l'ExpoController.
     */
    private Controller controller;

    /**
     * Timer de l'ExpoController.
     */
    private UpdateTimer timer;

    /**
     * Pathfinder de l'ExpoController (en mode test).
     */
    private Pathfinder pf;

    /**
     * ???.
     */
    private String backgroundPath;

    /**
     * Méthode permettant de modifier le controller
     * de l'ExpoController.
     *
     * @param controller
     */
    public void setControl(final Controller controller) {
        this.controller = controller;
    }

    /**
     * Méthode permettant de passer en mode "Visiteur".
     *
     * @param e
     */
    @FXML
    void onClickAddVisitorButton(final MouseEvent e) {
        if (mode != Mode.RIEN) {
            mode = Mode.VISITEUR;
        }
    }

    /**
     * Méthode permettant de passer en mode "Mur".
     *
     * @param e
     */
    @FXML
    void onClickWallButton(final MouseEvent e) {
        if (mode != Mode.RIEN) {
            mode = Mode.MUR;
        }
    }

    /**
     * Méthode permettant de passer en mode "Cible".
     *
     * @param e
     */
    @FXML
    void onClickTargetButton(final MouseEvent e) {
        if (mode != Mode.RIEN) {
            mode = Mode.CIBLE;
        }
    }

    /**
     * Méthode permettant de passer en mode "Gomme".
     *
     * @param e
     */
    @FXML
    void onClickEraserButton(final MouseEvent e) {
        if (mode != Mode.RIEN) {
            mode = Mode.GOMME;
        }
    }

    /**
     * Méthode permettant de lancer/de stopper la simulation.
     *
     * @param event
     */
    @FXML
    void onClickStartStopButton(final MouseEvent event) {
        switchCreationMode();
        switchVisibility();
        if (mode == Mode.RIEN) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    /**
     * Méthode permettant de sélectionner une image.
     * (Non fonctionnelle)
     *
     * @param event
     */
    @FXML
    void onClickSelectImageButton(final MouseEvent event) {
        this.selectImage(vueExpo.getScene().getWindow());
    }

    /**
     * Méthode permettant de sauvegarder l'exposition.
     * (Non fonctionelle)
     *
     * @param event
     */
    @FXML
    void onClickSaveButton(final MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(vueExpo.getScene().getWindow());
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode permettant de charger une exposition.
     * (Non fonctionnelle)
     *
     * @param event
     */
    @FXML
    void onClickLoadButton(final MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(vueExpo.getScene().getWindow());
        try {
            load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode permettant de sélectionner une image et modifier
     * l'arrière plan de l'exposition.
     *
     * @param window
     */
    public void selectImage(final Window window) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(window);
        changeBackground(file);
    }

    /**
     * Méthode permettant de changer l'arrière plan d'une exposition.
     *
     * @param file
     */
    private void changeBackground(final File file) {
        if (file != null) {
            backgroundPath = file.getAbsolutePath();
            Image image1 = new Image(file.toURI().toString());

            BackgroundFill backgroundFill = new BackgroundFill(
                    new ImagePattern(image1),
                    CornerRadii.EMPTY,
                    Insets.EMPTY);
            panelVueExpo.setBackground(new Background(backgroundFill));
        }
    }

    /**
     * Méthode permettant de vérifier si un point de coordonnées (x,y)
     * est bien un point d'intérêt ou non.
     *
     * @param x
     * @param y
     * @return booléen
     */
    private boolean isTarget(final int x, final int y) {
        for (int[] tuple : listeCibles) {
            if (tuple[0] == x && tuple[1] == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Méthode permettant d'ajouter un point d'intérêt à l'exposition.
     *
     * @param x
     * @param y
     */
    private void addTarget(final int x, final int y) {
        if (mode == Mode.CIBLE) {
            int[] tuple = {x, y};
            listeCibles.add(tuple);
            leCanvas.getGraphicsContext2D().setFill(Color.RED);
            leCanvas.getGraphicsContext2D().fillRect(
                    (float) x * 10.f, (float) y * 10.f, 10.f, 10.f);
            leCanvas.getGraphicsContext2D().setFill(Color.BLUE);
            grille[x][y] = true;
        } else if (mode == Mode.MUR) {
            leCanvas.getGraphicsContext2D().fillRect(
                    (float) x * 10.f, (float) y * 10.f, 10.f, 10.f);
            grille[x][y] = true;
        } else if (mode == Mode.GOMME) {
            leCanvas.getGraphicsContext2D().clearRect(
                    (float) x * 10.f, (float) y * 10.f, 10.f, 10.f);
            grille[x][y] = false;
            deleteTarget(x, y);
        }
    }

    /**
     * Méthode permettant de supprimer un point d'intérêt d'une exposition.
     *
     * @param x
     * @param y
     */
    private void deleteTarget(final int x, final int y) {
        Iterator<int[]> it = listeCibles.iterator();
        while (it.hasNext()) {
            int[] coords = it.next();
            if (coords[0] == x && coords[1] == y) {
                it.remove();
            }
        }
    }

    /**
     * Méthode permettant de passer en mode "Mur"
     * ou de repasser en mode "Rien".
     */
    void switchCreationMode() {
        if (mode == Mode.RIEN) {
            mode = Mode.MUR;
        } else {
            mode = Mode.RIEN;
        }
    }

    /**
     * Méthode permettant d'afficher la zone de modifications de l'exposition.
     */
    void switchVisibility() {
        if (mode == Mode.RIEN) { // on switch en start
            leCanvas.getGraphicsContext2D().clearRect(0.f, 0.f, 800.f, 450.f);
        } else {
            displayAll();
        }
    }

    /**
     * Méthode permettant d'afficher toute la zone de modification
     * de l'exposition ainsi que ses caractéristiques.
     */
    void displayAll() {
        for (int i = 0; i < 80; i++) {
            for (int j = 0; j < 42; j++) {
                if (grille[i][j]) {
                    if (isTarget(i, j)) {
                        leCanvas.getGraphicsContext2D().setFill(Color.RED);
                        leCanvas.getGraphicsContext2D().fillRect(
                                (float) i * 10.f,
                                (float) j * 10.f, 10.f, 10.f);
                        leCanvas.getGraphicsContext2D().setFill(Color.BLUE);
                    } else {
                        leCanvas.getGraphicsContext2D().fillRect(
                                (float) i * 10.f,
                                (float) j * 10.f, 10.f, 10.f);
                    }
                }
            }
        }
    }

    @FXML
    void onCanvasMousePressed(final MouseEvent event) {
        event.setDragDetect(true);
        int gridX = (int) event.getX() / 10;
        int gridY = (int) event.getY() / 10;
        if (mode == Mode.VISITEUR) {
            if (controller != null) {
                Migrant m;
                if (controller.getAmas() != null) {
                    m = controller.getAmas().getEnvironment().adopter();
                } else {
                    m = null;
                }
                vueExpo.addVisitor(m, (float) event.getX(),
                        (float) event.getY(), 12.5);
            }
        } else {
            addTarget(gridX, gridY);
        }
    }

    @FXML
    void onCanvasMouseDragged(final MouseEvent event) {
        int gridX = (int) event.getX() / 10;
        int gridY = (int) event.getY() / 10;
        addTarget(gridX, gridY);
    }

    /**
     * Méthode permettant d'initialiser un nouveau ExpoController.
     *
     * @param arg0
     * @param arg1
     */
    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {
        grille = new boolean[80][42];
        vueExpo = new VueExpo();
        timer = new UpdateTimer(this);
        pf = new Pathfinder(grille);
        backgroundPath = "";
        mode = Mode.MUR;
        panelVueExpo.getChildren().add(vueExpo);
        leCanvas.getGraphicsContext2D().setFill(Color.BLUE);
    }

    /**
     * Méthode permettant de mettre à jour les Visiteurs d'une exposition.
     */
    public void updateVisitors() {
        vueExpo.updateVisitors(listeCibles, pf);
    }

    /**
     * Méthode permettant d'enregistrer une expostion.
     *
     * @param file
     * @throws IOException
     */
    private void save(final File file) throws IOException {
        FileOutputStream stream = new FileOutputStream(file);
        String content = "";
        content += backgroundPath;
        content += "\r\n";
        for (int y = 0; y < 42; y++) {
            for (int x = 0; x < 80; x++) {
                char c = grille[x][y] ? '1' : '0';
                content += c;
            }
            content += "\r\n";
        }
        for (int[] coords : listeCibles) {
            content += String.format("%02d", coords[0]) + ":"
                    + String.format("%02d", coords[1]) + " ";
        }
        content += "\r\n";
        stream.write(content.getBytes());
        stream.close();
    }

    /**
     * Méthode permettant de charger une exposition.
     *
     * @param file
     * @throws IOException
     */
    private void load(final File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));

        backgroundPath = br.readLine();
        if (!backgroundPath.isEmpty()) {
            changeBackground(new File(backgroundPath));
        }

        for (int y = 0; y < 42; y++) {
            char[] line = br.readLine().toCharArray();
            for (int x = 0; x < 80; x++) {
                boolean b = line[x] == '1';
                grille[x][y] = b;
            }
        }

        String line = br.readLine();
        listeCibles.clear();
        for (int i = 0; i < line.length() / 6; i++) {
            int[] coords = {Integer.parseInt(line.substring(6 * i, 6 * i + 2)),
                    Integer.parseInt(line.substring(6 * i + 3, 6 * i + 5)) };
            listeCibles.add(coords);
        }

        displayAll();
        br.close();
    }
}

package application;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jogamp.opengl.awt.GLJPanel;

import amak.Migrant;
import amak.MyAMAS;
import amak.MyEnvironment;
import business.Blob;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import position.PositionSimulationThread;
import position.ServerThreadAcceleration;
import javafx.fxml.Initializable;

/**
 * Controller est l'interface qui permet d'observer les différents terrains
 * et de modifier certaines caractéristiques.
 *
 * @author inconnu, [ugo](https://github.com/gogonouze)
 * @version 1.0
 * @see Initializable
 */
public class Controller implements Initializable {

  /**
   * Indique si il s'agit du mode "test" ou "experience".
   */
  private boolean experience;
  /**
   * Prochain blob à faire bouger.
   */
  private Migrant blobToMove;
  /**
   * Serveur qui récupère les données des utilisateurs smartphone.
   */
  private PositionSimulationThread tSimuPosition;
  /**
   * Fenêtre qui affiche T0 dans le controleur.
   */
  private T0 drawOriginel;
  /**
   * Fenêtre qui affiche TR dans le controleur.
   */
  private TR drawReel;
  /**
   * Fenêtre qui affiche TI dans le controleur.
   */
  private TI drawIdeel;
  /**
   * Fenêtre qui affiche l'aperçu dans le controleur.
   */
  private Preview preview;
  /**
   * Fenêtre qui affiche T0.
   */
  private T0 drawOriginelExp;
  /**
   * Fenêtre qui affiche TR.
   */
  private TR drawReelExp;
  /**
   * Fenêtre qui affiche TI.
   */
  private TI drawIdeelExp;

  /**
   * Composant graphique.
   */
  @FXML
  private TableView<?> testtableview;

  /**
   * Composant graphique.
   */
  @FXML
  private Label AffichDiso;

  /**
   * Composant graphique.
   */
  @FXML
  private AnchorPane panelTideal;

  /**
   * Composant graphique.
   */
  @FXML
  private AnchorPane panelTreel;

  /**
   * Composant graphique.
   */
  @FXML
  private AnchorPane panelToriginel;

  /**
   * Composant graphique.
   */
  @FXML
  private AnchorPane panelBlobSelectione;

  /**
   * Composant graphique.
   */
  @FXML
  private Label labelAide;

  /**
   * Composant graphique.
   */
  @FXML
  private Button buttonSortirBlob;

  /**
   * Composant graphique.
   */
  @FXML
  private Button buttonChangerBlob;

  /**
   * Composant graphique.
   */
  @FXML
  private Button buttonOKNbBlobs;

  /**
   * Composant graphique.
   */
  @FXML
  private TextField textFieldNbBlobs;

  /**
   * Composant graphique.
   */
  @FXML
  private Pane paneAppercuBlob;

  /**
   * Composant graphique.
   */
  @FXML
  private Button buttonMouvementAleatoire;

  /**
   * Composant graphique.
   */
  @FXML
  private Slider sdTaille;

  /**
   * Composant graphique.
   */
  @FXML
  private Slider sdBlob;

  /**
   * Gestion de l'environnement des Blobs.
   */
  private MyAMAS tAmas;

  /**
   * Décalage graphique en x.
   */
  private double xOffset = 0;
  /**
   * Décalage graphique en y.
   */
  private double yOffset = 0;

  /**
   * Fait passer un blob de T0 vers TR.
   *
   * @param event sur un clique.
   */
  @FXML
  void onClickPickABlob(final MouseEvent event) {
    // va sortir un Blob mur, pris au hasard dans To
    Migrant migrant = tAmas.getEnvironment().adopter();
    if (migrant != null) {
      pullOutBlob(migrant);
    }
  }

  /**
   * Fait bouger aléatoirement les blobs.
   *
   * @param event
   */
  @Deprecated
  @FXML
  void onClickButtonRandomMouvement(final MouseEvent event) {
    if (!tSimuPosition.isInterrupted()) {
      tSimuPosition.end();
    } else {
      tSimuPosition.begin();
    }

  }

  /**
   * Permet de gerer les actions clavier.
   *
   * @param event touche appuyé.
   */
  @FXML
  void onKeyPressed(final KeyEvent event) {
    KeyCode kcode = event.getCode();

    if (textFieldNbBlobs.getText().equals("")) {
      if (kcode.isDigitKey()) {
        textFieldNbBlobs.setText(kcode.getName());
      }
      return;
    }

    if (blobToMove == null || experience) {
      return;
    }
    if (!tAmas.getEnvironment().getAgents().contains(blobToMove)) {
      return;
    }

    if (kcode.isArrowKey()) {
      double[] coo = blobToMove.getBlob().getCoordonnee().clone();

      if (kcode.equals(KeyCode.UP)) {
        coo[1] -= 1;
      } else if (kcode.equals(KeyCode.DOWN)) {
        coo[1] += 1;
      } else if (kcode.equals(KeyCode.RIGHT)) {
        coo[0] += 1;
      } else {
        coo[0] -= 1;
      }

      if (!isValidInTI(coo)) {
        return;
      }
      moveBlob(blobToMove, coo);
    } else if (kcode.isLetterKey()) {
      Migrant tmp = blobToMove;
      unselect();
      pushInBlob(tmp);
    } else if (kcode.equals(KeyCode.ESCAPE)) {
      unselect();
    }


  }

  /**
   * @param event
   */
  @Deprecated
  @FXML
  void onClickTR(final MouseEvent event) {

  }

  /**
   * Permet de selectionner un blob.
   *
   * @param m
   */
  public void select(final Migrant m) {
    blobToMove = m;
    preview.setAgent(m);
  }

  /**
   * Permet d'ajouter un nombre de blobs donné et lancer le serveur.
   * Permet en mode experience d'ouvrir des fenêtres suplémentaires.
   *
   * @param event
   * @throws FileNotFoundException
   */
  @FXML
  void onClickButtonOkNbBlobs(final MouseEvent event)
      throws FileNotFoundException {
    System.out.println(textFieldNbBlobs.textProperty().getValue());
    int nbBlobs = Integer.parseInt(textFieldNbBlobs.textProperty().getValue());

    tAmas = new MyAMAS(new MyEnvironment(this), this, nbBlobs);


    buttonOKNbBlobs.setDisable(true);
    textFieldNbBlobs.setDisable(true);

    if (!experience) {
      tSimuPosition = new PositionSimulationThread(tAmas);
      tSimuPosition.start();
    } else {
      System.out.println("Je cree le serveur");
      ServerThreadAcceleration server = new ServerThreadAcceleration(tAmas);
      System.out.println("Je le run");
      server.start();
      System.out.println("j'ai fini de traiter ce bouton");

    }

    SwingNode swing = new SwingNode();
    drawOriginel = new T0(new GLJPanel(),
        100.f, 100.f, getAmas().getEnvironment());
    swing.setContent(drawOriginel.getPanel());
    AnchorPane.setTopAnchor(swing, 0.0);
    AnchorPane.setBottomAnchor(swing, 0.0);
    AnchorPane.setLeftAnchor(swing, 0.0);
    AnchorPane.setRightAnchor(swing, 0.0);
    panelToriginel.getChildren().add(swing);

    swing = new SwingNode();
    drawReel = new TR(new GLJPanel(),
        25.f, 25.f, getAmas().getEnvironment());
    swing.setContent(drawReel.getPanel());
    AnchorPane.setTopAnchor(swing, 0.0);
    AnchorPane.setBottomAnchor(swing, 0.0);
    AnchorPane.setLeftAnchor(swing, 0.0);
    AnchorPane.setRightAnchor(swing, 0.0);
    panelTreel.getChildren().add(swing);

    swing = new SwingNode();
    drawIdeel = new TI(new GLJPanel(),
        25.f, 25.f, getAmas().getEnvironment());
    swing.setContent(drawIdeel.getPanel());
    AnchorPane.setTopAnchor(swing, 0.0);
    AnchorPane.setBottomAnchor(swing, 0.0);
    AnchorPane.setLeftAnchor(swing, 0.0);
    AnchorPane.setRightAnchor(swing, 0.0);
    panelTideal.getChildren().add(swing);

    if (experience) {
      drawOriginelExp = new T0(new GLJPanel(),
          100.f, 100.f, getAmas().getEnvironment());
      openInWindow("T0", drawOriginelExp);
      drawReelExp = new TR(new GLJPanel(),
          25.f, 25.f, getAmas().getEnvironment());
      openInWindow("TR", drawReelExp);
      drawIdeelExp = new TI(new GLJPanel(),
          25.f, 25.f, getAmas().getEnvironment());
      openInWindow("TI", drawIdeelExp);
    }
    preview = new Preview(new GLJPanel(), 10.f, 10.f);
    swing = new SwingNode();
    swing.setContent(preview.getPanel());
    AnchorPane.setTopAnchor(swing, 0.0);
    AnchorPane.setBottomAnchor(swing, 0.0);
    AnchorPane.setLeftAnchor(swing, 0.0);
    AnchorPane.setRightAnchor(swing, 0.0);
    paneAppercuBlob.getChildren().add(swing);

    onChangeSetRadius();
    onChangeSetBlobbiness();
  }

  /**
   * Permet d'ouvrir une nouvelle fenêtre avec son titre et son type.
   *
   * @param title titre de la fenêtre.
   * @param t     type de la fenêtre.
   * @throws FileNotFoundException
   */
  private void openInWindow(final String title, final Terrain t)
      throws FileNotFoundException {
    Stage window = new Stage();
    window.setTitle(title);
    window.getIcons().add(new Image(
        new FileInputStream("src/main/java/application/icon_blob.png")));
    SwingNode swing = new SwingNode();
    swing.setContent(t.getPanel());
    AnchorPane.setTopAnchor(swing, 0.0);
    AnchorPane.setBottomAnchor(swing, 0.0);
    AnchorPane.setLeftAnchor(swing, 0.0);
    AnchorPane.setRightAnchor(swing, 0.0);
    AnchorPane pane = new AnchorPane();
    pane.getChildren().add(swing);
    Scene scene = new Scene(pane, 600, 600);
    window.setScene(scene);
    window.show();
  }

  /**
   * Necessaire pour l'interface Initializable.
   *
   * @param arg0 Necessaire pour l'interface Initializable.
   * @param arg1 Necessaire pour l'interface Initializable.
   */
  @Override
  public void initialize(final URL arg0, final ResourceBundle arg1) {
  }

  /**
   * Initialise T0.
   */
  public void initTO() {
  }

  /**
   * Initialise TI.
   */
  public void initTI() {
  }

  /**
   * @param pExperience
   */
  public void setexperience(final boolean pExperience) {
    this.experience = pExperience;
  }

  /**
   * Deselectionne le blob actuellement selectionné.
   */
  public void unselect() {
    blobToMove = null;
  }


  /* **************************************************************** *
   * ********   METHODES DE POSITION_THREAD      ******************** *
   * **************************************************************** */

  /**
   * Verifie qu'une coordonnée se trouve dans une zone.
   *
   * @param coo coordonnée.
   * @return True si les coordonnées sont valides.
   */
  private boolean isValidInTI(final double[] coo) {
    final double maxX = 12.5;
    final double maxY = 12.5;
    return ((coo[0] - maxX) * (coo[0] - maxX) + (coo[1] - maxY)
        * (coo[1] - maxY) <= maxX * maxY);
  }


  /**
   * Permet de retirer un blob donné de TR.
   * Utile uniquement pour le mode test.
   *
   * @param b blob.
   */
  public void pullOutBlob(final Migrant b) {
    Blob tmp = b.getBlob();
    double[] coo = new double[2];
    coo[0] = Math.random() * 25;
    boolean isOk = false;
    while (!isOk) {
      coo[1] = Math.random() * 25;
      isOk = isValidInTI(coo);
    }

    tmp.setCoordonnee(coo);
    b.setBlob(tmp);
    b.t0ToTr();
  }

  /**
   * Permet d'ajouter un blob donné dans TR.
   * Utile uniquement pour le mode test.
   *
   * @param b blob.
   */
  public void pushInBlob(final Migrant b) {
    System.out.println("je suis le 1 :"
        + b.getBlob().getMaSuperCouleurPreferee().toString());
    if (b == blobToMove) {
      unselect();
    }
    System.out.println("je suis le blob :"
        + b.getBlob().getMaSuperCouleurPreferee().toString());
    b.trToT0();
  }

  /**
   * Permet de faire bouger un blob donné à une coordonnée donnée.
   * Utile uniquement pour le mode test.
   *
   * @param b   blob.
   * @param coo coordonnée.
   */
  public void moveBlob(final Migrant b, final double[] coo) {
    b.getBlob().setCoordonnee(coo);

  }

  /**
   * @return Amas
   */
  public MyAMAS getAmas() {
    return tAmas;
  }

  /**
   * Permet l'affichage graphique du contenu des fenêtres.
   */
  public void updateRender() {
    if (drawOriginel != null) {
      drawOriginel.getPanel().repaint();
    }
    if (drawReel != null) {
      drawReel.getPanel().repaint();
    }
    if (drawIdeel != null) {
      drawIdeel.getPanel().repaint();
    }
    if (experience) {
      if (drawOriginelExp != null) {
        drawOriginelExp.getPanel().repaint();
      }
      if (drawReelExp != null) {
        drawReelExp.getPanel().repaint();
      }
      if (drawIdeelExp != null) {
        drawIdeelExp.getPanel().repaint();
      }
    }

  }

  /**
   * Change la taille des blobs.
   */
  @FXML
  public void onChangeSetRadius() {
    this.drawOriginel.setRadius((float) sdTaille.getValue());
    this.drawReel.setRadius((float) sdTaille.getValue());
    this.drawIdeel.setRadius((float) sdTaille.getValue());
    this.preview.setRadius((float) sdTaille.getValue());
    if (experience) {
      this.drawOriginelExp.setRadius((float) sdTaille.getValue());
      this.drawReelExp.setRadius((float) sdTaille.getValue());
      this.drawIdeelExp.setRadius((float) sdTaille.getValue());
    }
  }

  /**
   * Change la "blobitude" des blobs.
   */
  @FXML
  public void onChangeSetBlobbiness() {
    this.drawOriginel.setBlobbiness((float) sdBlob.getValue());
    this.drawReel.setBlobbiness((float) sdBlob.getValue());
    this.drawIdeel.setBlobbiness((float) sdBlob.getValue());
    this.preview.setBlobbiness((float) sdBlob.getValue());
    if (experience) {
      this.drawOriginelExp.setBlobbiness((float) sdBlob.getValue());
      this.drawReelExp.setBlobbiness((float) sdBlob.getValue());
      this.drawIdeelExp.setBlobbiness((float) sdBlob.getValue());
    }
  }
}

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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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

public class Controller implements Initializable{
	
	private boolean experience;
	private Migrant blobToMove;
	private PositionSimulationThread tSimuPosition;
	private T0 drawOriginel;
	private TR drawReel;
	private TI drawIdeel;
	private Apercu apercu;
	private T0 drawOriginelExp;
	private TR drawReelExp;
	private TI drawIdeelExp;
	
	//TODO
//	private int radiusVoisin = 7;

    @FXML
    private TableView<?> testtableview;


    @FXML
    private Label AffichDiso;


    @FXML
    private AnchorPane panelTideal;

    @FXML
    private AnchorPane panelTreel;
    
    @FXML
    private AnchorPane panelToriginel;
    
    @FXML
    private AnchorPane panelBlobSelectione;
    
    @FXML
    private Label labelAide;
    
    @FXML
    private Button buttonSortirBlob;
    
    @FXML
    private Button buttonChangerBlob;
    
    @FXML
    private Button buttonOKNbBlobs;
    
    @FXML
    private TextField textFieldNbBlobs;
    
    @FXML
    private Pane paneAppercuBlob;
    
    @FXML
    private Button buttonMouvementAleatoire;
    
    @FXML
    private Slider sdTaille;
    
    @FXML
    private Slider sdBlob;
    
    private MyAMAS tAmas;
    
	//décalage fenêtre pour déplacer To et Ti
    private double xOffset = 0;
    private double yOffset = 0;
    

	
    @FXML
    void onClicButtonSortirBlob(MouseEvent event) {
    	// va sortir un Blob mur, pris au hasard dans To
    	Migrant migrant = tAmas.getEnvironment().adopter();
    	if (migrant != null) sortirBlob(migrant);
    }
	
    @FXML
    void onClicButtonMouvementAleatoire(MouseEvent event) {

    	if(!tSimuPosition.isInterrupted())
    		tSimuPosition.end();
    	else
    		tSimuPosition.begin();
    	   	
    }
    
    @FXML
    void onKeyPressed(KeyEvent event) {
    	
    	
    	KeyCode kcode = event.getCode();
    	//System.out.println("je viens d'appuyer sur une touche !");
    	
    	if (textFieldNbBlobs.getText().equals(""))
    	{
    		if (kcode.isDigitKey())
    			textFieldNbBlobs.setText(kcode.getName());
    		return;
    	}
    		
    		
    	
    	
    	
    	if(blobToMove == null || experience)
    		return;
    	if(!tAmas.getEnvironment().getAgents().contains(blobToMove))
    		return;
    	
    	if(kcode.isArrowKey())
    	{
    		double[] coo = blobToMove.getBlob().getCoordonnee().clone();
    	
    		if (kcode.equals(KeyCode.UP))
    			coo[1] -= 1;
    		else if (kcode.equals(KeyCode.DOWN))
    			coo[1] += 1;
    		else if (kcode.equals(KeyCode.RIGHT))
    			coo[0] += 1;
    		else
    			coo[0] -= 1;
    		
    		if(!isValideInTi(coo))
    			return;
    		moveBlob(blobToMove, coo);
    	}
    	else if (kcode.isLetterKey())
    	{
    		Migrant tmp = blobToMove;
    		deleteSelection();
    		rentrerBlob(tmp);
    	}
    	else if (kcode.equals(KeyCode.ESCAPE))
    		deleteSelection();
    	

    }
    
    
    /* calcule la distance euclidienne entre 2 points cooA et cooB */
	private double calculeDistance(double[] cooA, double[] cooB){
		double sum = 0;
		for(int i = 0; i < cooA.length ; i++)
			sum += ((cooB[i] - cooA[i])*(cooB[i] - cooA[i]));
		return Math.sqrt(sum);		
		
	}
    
    
    @FXML
    void onClicTr(MouseEvent event) {
    	
    	/* if (blobToMove != null)
    		deleteSelection();
    	
    	
    	// Trouvons les coordonnes du clic au niveau de Tr
    	double xcor = event.getSceneX();
    	double ycor = event.getSceneY();
    	System.out.println("on a cliqué sur les coordonnées : " + xcor + " ; " + ycor);
    	
    	// la scene prend en compte le 1er xpanel. j'enlève donc sa largeur fixe de 500pxl
    	xcor -= 500;
    	
    	// les coordonnees des Blobs sont exprimés en metres ... je transforme donc les pxls en metres.
    	double[] tmp = new double[2];
    	tmp[0] = xcor;
    	tmp[1] = ycor;
    	tmp = treel.PxlTometre(tmp);
    	System.out.println("equivalent en metre à  : " + tmp[0] + " ; " + tmp[1]);

    	
    	
    	
    	
    	//deleteSelection();
    	
    	// Trouvons le blob le plus proche de l'endroit cliqué.
    	
    	List<Migrant> blobActifs = tAmas.getEnvironment().getAgents().stream()
    							  .filter(a -> a instanceof Migrant)
    							  .map(a -> (Migrant)a)
    							  .collect(Collectors.toList());
    	
    	if(blobActifs.size() == 0)
    	{
    		System.out.println("Il n'y a rien a selectionner");
    		return;
    	}
    	
    	blobToMove = blobActifs.get(0);
    	double distanceMin = calculeDistance(tmp, blobToMove.getBlob().getCoordonnee());
    	double distance;
    	
    	for (int i = 0; i < blobActifs.size(); i++){
    		distance = calculeDistance(tmp, blobActifs.get(i).getBlob().getCoordonnee());
    		if(distance < distanceMin)
    		{
    			distanceMin = distance;
    			blobToMove = blobActifs.get(i);
    		}
    	}
    	
    	showSelection(); */
    	
    }
    
    public void selectionne(Migrant m) {
    	blobToMove = m;
    	apercu.setAgent(m);
    	showSelection();
    }
    

    @FXML
    void onClicButtonOKnbBlobs(MouseEvent event) throws FileNotFoundException {
		System.out.println(textFieldNbBlobs.textProperty().getValue());
		int nbBlobs = Integer.parseInt(textFieldNbBlobs.textProperty().getValue());
		
    	tAmas = new MyAMAS(new MyEnvironment(this), this, nbBlobs);

		 
		buttonOKNbBlobs.setDisable(true);
		
		if(!experience)
		{
			tSimuPosition = new PositionSimulationThread(tAmas);
			tSimuPosition.start();
		}
		else
		{
			System.out.println("Je cree le serveur");
			ServerThreadAcceleration server = new ServerThreadAcceleration(tAmas);
			System.out.println("Je le run");
			server.start();
			System.out.println("j'ai fini de traiter ce bouton");

		}
		
		SwingNode swing = new SwingNode();
		drawOriginel = new T0(new GLJPanel(), 100.f, 100.f, getAmas().getEnvironment());
		swing.setContent(drawOriginel.getPanel());
		AnchorPane.setTopAnchor(swing, 0.0);
		AnchorPane.setBottomAnchor(swing, 0.0);
		AnchorPane.setLeftAnchor(swing, 0.0);
		AnchorPane.setRightAnchor(swing, 0.0);
		panelToriginel.getChildren().add(swing);
		
		swing = new SwingNode();
		drawReel = new TR(new GLJPanel(), 25.f, 25.f, getAmas().getEnvironment());
		swing.setContent(drawReel.getPanel());
		AnchorPane.setTopAnchor(swing, 0.0);
		AnchorPane.setBottomAnchor(swing, 0.0);
		AnchorPane.setLeftAnchor(swing, 0.0);
		AnchorPane.setRightAnchor(swing, 0.0);
		panelTreel.getChildren().add(swing);
		
		swing = new SwingNode();
		drawIdeel = new TI(new GLJPanel(), 25.f, 25.f, getAmas().getEnvironment());
		swing.setContent(drawIdeel.getPanel());
		AnchorPane.setTopAnchor(swing, 0.0);
		AnchorPane.setBottomAnchor(swing, 0.0);
		AnchorPane.setLeftAnchor(swing, 0.0);
		AnchorPane.setRightAnchor(swing, 0.0);
		panelTideal.getChildren().add(swing);
		
		if (experience) {
			drawOriginelExp = new T0(new GLJPanel(), 100.f, 100.f, getAmas().getEnvironment());
			openInWindow("T0", drawOriginelExp);
			drawReelExp = new TR(new GLJPanel(), 25.f, 25.f, getAmas().getEnvironment());
			openInWindow("TR", drawReelExp);
			drawIdeelExp = new TI(new GLJPanel(), 25.f, 25.f, getAmas().getEnvironment());
			openInWindow("TI", drawIdeelExp);
		}
		apercu = new Apercu(new GLJPanel(), 10.f, 10.f);
		swing = new SwingNode();
		swing.setContent(apercu.getPanel());
		AnchorPane.setTopAnchor(swing, 0.0);
		AnchorPane.setBottomAnchor(swing, 0.0);
		AnchorPane.setLeftAnchor(swing, 0.0);
		AnchorPane.setRightAnchor(swing, 0.0);
		paneAppercuBlob.getChildren().add(swing);

		onChangeTaille();
		onChangeBlobitude();
    }
    
    private void openInWindow(String title, Terrain t) throws FileNotFoundException {
    	Stage window = new Stage();
    	window.setTitle(title);
//    	window.initStyle(StageStyle.UNDECORATED);
    	window.getIcons().add(new Image(new FileInputStream("src/main/java/application/icon_blob.png")));
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
    

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
    
	public void configTerrain(Stage stage, Parent root)
	{
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
		
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	stage.setX(event.getScreenX() - xOffset);
            	stage.setY(event.getScreenY() - yOffset);
            }
        });
	}
	
	public void initTO()
	{	
		/* Stage towindow = new Stage();

		towindow.initStyle(StageStyle.UNDECORATED);

		towindow.setTitle("Territoire Originel");
		towindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon_blob.png")));
		
		towindow.show(); */
	}
	
	public void initTI()
	{
		/* Stage tiwindow = new Stage();

		tiwindow.initStyle(StageStyle.UNDECORATED);
		tiwindow.setTitle("Territoire Ideal");
		tiwindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon_blob.png")));
		tiwindow.show(); */
	}
	
	public void remove_blobMigrant(Migrant b){
		if (b == blobToMove)
			deleteSelection();
	}
	
	public void setexperience(boolean experience) {
		this.experience = experience;
	}
	
	private void showSelection(){
		// TODO refaire pour nouveau design
	}
	
	public void deleteSelection(){
		// TODO refaire pour nouveau design
		blobToMove = null;
	}
	
	
	/* ***************************************************************************** *
	 *  ******** 		METHODES DE POSITION_THREAD			************************ *
	 *	**************************************************************************** */
	
	// indique si la coordonnée entrée en paramètre est valide, ie si elle n'est pas hors terrain.
	// returne true if ok. 
	//Ici il s'agit de Tr ou Ti : valide si compris dans un cercle de rayon RayonTerrain et de centre (RayonTerrain;RayonTerrain)
	private boolean isValideInTi(double[] coo){
		if ((coo[0] - 12.5)*(coo[0] - 12.5) + (coo[1] - 12.5) * (coo[1] - 12.5) <= 12.5 * 12.5)
			return true;
		return false;
	}
		

	// cette fonction n'est appelée que si nous sommes en mode test
	public void sortirBlob(Migrant b){
		Blob tmp = b.getBlob();
		double[] coo = new double[2];
		coo[0] = Math.random() * 25;
		boolean isOk = false;
		while(!isOk){
			coo[1] = Math.random() * 25;
			if ((coo[0] - 12.5)*(coo[0] - 12.5) + (coo[1] - 12.5) * (coo[1] - 12.5) <= 12.5 * 12.5)
				isOk = true;
		}
		
		tmp.setCoordonnee(coo);
		b.setBlob(tmp);
		b.t0ToTr();
	}
	
	// cette fonction n'est appelée que si nous sommes en mode test
	public void rentrerBlob(Migrant b){
		System.out.println("je suis le 1 :" + b.getBlob().getMaSuperCouleurPreferee().toString());
		if (b == blobToMove)
			deleteSelection();
		System.out.println("je suis le blob :" + b.getBlob().getMaSuperCouleurPreferee().toString());
		b.trToT0();
	}
	
	// cette fonction n'est appelée que si nous sommes en mode test
	public void moveBlob(Migrant b, double[] coo){
		b.getBlob().setCoordonnee(coo);

	}

	public MyAMAS getAmas() {
		return tAmas;
	}
	
	public void updateRender() {
		if (experience) {
			if (drawOriginelExp != null) drawOriginelExp.getPanel().repaint();
			if (drawReelExp != null) drawReelExp.getPanel().repaint();
			if (drawIdeelExp != null) drawIdeelExp.getPanel().repaint();
		} else {
			if (drawOriginel != null) drawOriginel.getPanel().repaint();
			if (drawReel != null) drawReel.getPanel().repaint();
			if (drawIdeel != null) drawIdeel.getPanel().repaint();
			if (apercu != null) apercu.getPanel().repaint();
		}
	}
	
	@FXML
	public void onChangeTaille() {
		this.drawOriginel.setTaille((float)sdTaille.getValue());
		this.drawReel.setTaille((float)sdTaille.getValue());
		this.drawIdeel.setTaille((float)sdTaille.getValue());
		this.apercu.setTaille((float)sdTaille.getValue());
		if (experience) {
			this.drawOriginelExp.setTaille((float)sdTaille.getValue());
			this.drawReelExp.setTaille((float)sdTaille.getValue());
			this.drawIdeelExp.setTaille((float)sdTaille.getValue());
		}
	}
	
	@FXML
	public void onChangeBlobitude() {
		this.drawOriginel.setBlobitude((float)sdBlob.getValue());
		this.drawReel.setBlobitude((float)sdBlob.getValue());
		this.drawIdeel.setBlobitude((float)sdBlob.getValue());
		this.apercu.setBlobitude((float)sdBlob.getValue());
		if (experience) {
			this.drawOriginelExp.setBlobitude((float)sdBlob.getValue());
			this.drawReelExp.setBlobitude((float)sdBlob.getValue());
			this.drawIdeelExp.setBlobitude((float)sdBlob.getValue());
		}
	}
}
/*
il suffit de construire une BufferedImage (format d'image standard de Java) et de la passer ÃÂÃÂ  un ImagePlus ou ImageProcessor (format ImageJ).

BufferedImage monimage = new BufferedImage(width, height, BufferedImage.LeTypeVoulu) ;

//Puis en fonction du type de l'image
return new BinaryProcessor(new ByteProcessor((java.awt.Image)source)) ;
return new ByteProcessor(source) ;
return new ShortProcessor(source) ;
*/

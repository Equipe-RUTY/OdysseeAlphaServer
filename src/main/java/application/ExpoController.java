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
import javafx.scene.control.Button;
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
	@FXML
	private AnchorPane panelVueExpo;

	@FXML
	private Button buttonStartStop;
	@FXML
	private Canvas leCanvas;
	
	enum Mode {
		MUR,
		CIBLE,
		GOMME,
		VISITEUR,
		RIEN
	}

	private boolean[][] grille;
	private Mode mode;
	private LinkedList<int[]> listeCibles = new LinkedList<int[]>();
	private VueExpo vueExpo;
	private Controller controller;
	private UpdateTimer timer;
	private Pathfinder pf;
	private String backgroundPath;


	public void setControl(Controller controller) {
		this.controller = controller;
	}

	@FXML
	void onClickAjouterVisiteur(MouseEvent event) {
		/* if (controller != null)
			vueExpo.ajouterVisiteur(controller.candidat()); */
		if (mode != Mode.RIEN) mode = Mode.VISITEUR;
	}

	@FXML
	void onClickModeMur(MouseEvent e) {
		if (mode != Mode.RIEN) {			
			mode = Mode.MUR;
		}
	}

	@FXML
	void onClickModeCible(MouseEvent e) {
		if (mode != Mode.RIEN) {
			mode = Mode.CIBLE;
		}
	}
	
	@FXML
	void onClickModeGomme(MouseEvent e) {
		if (mode != Mode.RIEN) {
			mode = Mode.GOMME;
		}
	}

	@FXML
	void onClickStartStop(MouseEvent event) {
		switchCreationMode();
		switchVisibility();
		if (mode == Mode.RIEN) timer.start();
		else timer.stop();
	}

	@FXML
	void onClickSelectionImage(MouseEvent event) {
		this.selectionnerImage(vueExpo.getScene().getWindow());
	}
	
	@FXML
	void onClickSauvegarder(MouseEvent event) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(vueExpo.getScene().getWindow());
		try {			
			sauver(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	void onClickCharger(MouseEvent event) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(vueExpo.getScene().getWindow());
		try {
			charger(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void selectionnerImage(Window window) {

		final FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(window);
		mettreEnFond(file);
	}
	
	private void mettreEnFond(File file) {
		if (file != null) {
			backgroundPath = file.getAbsolutePath();
			Image image1 = new Image(file.toURI().toString());

			BackgroundFill backgroundFill = new BackgroundFill(new ImagePattern(image1), CornerRadii.EMPTY,
					Insets.EMPTY);
			panelVueExpo.setBackground(new Background(backgroundFill));
		}
	}

	private boolean cibleExiste(int x, int y) {
		for (int[] tuple : listeCibles) {
			if (tuple[0] == x && tuple[1] == y)
				return true;
		}
		return false;
	}

	private void ajouterPoint(int x, int y) {
		if (mode == Mode.CIBLE) {
			int[] tuple = { x, y };
			listeCibles.add(tuple);
			leCanvas.getGraphicsContext2D().setFill(Color.RED);
			leCanvas.getGraphicsContext2D().fillRect((float) x * 10.f, (float) y * 10.f, 10.f, 10.f);
			leCanvas.getGraphicsContext2D().setFill(Color.BLUE);
			grille[x][y] = true;
		} else if (mode == Mode.MUR) {
			leCanvas.getGraphicsContext2D().fillRect((float) x * 10.f, (float) y * 10.f, 10.f, 10.f);
			grille[x][y] = true;
		} else if (mode == Mode.GOMME) {
			leCanvas.getGraphicsContext2D().clearRect((float) x * 10.f, (float) y * 10.f, 10.f, 10.f);
			grille[x][y] = false;
			supprimerCible(x, y);
		}
	}
	
	private void supprimerCible(int x, int y) {
		Iterator<int[]> it = listeCibles.iterator();
		while (it.hasNext()) {
			int[] coords = it.next();
			if (coords[0] == x && coords[1] == y) {
				it.remove();
			}
		}
	}

	void switchCreationMode() {
		if (mode == Mode.RIEN) mode = Mode.MUR;
		else {
			mode = Mode.RIEN;
		}
	}

	void switchVisibility() {
		if (mode == Mode.RIEN) { // on switch en start
			leCanvas.getGraphicsContext2D().clearRect(0.f, 0.f, 800.f, 450.f);
		} else {
			afficherTout();
		}
	}
	
	void afficherTout() {
		for (int i = 0; i < 80; i++) {
			for (int j = 0; j < 42; j++) {
				if (grille[i][j]) {
					if (cibleExiste(i, j)) {
						leCanvas.getGraphicsContext2D().setFill(Color.RED);
						leCanvas.getGraphicsContext2D().fillRect((float) i * 10.f, (float) j * 10.f, 10.f, 10.f);
						leCanvas.getGraphicsContext2D().setFill(Color.BLUE);
					} else {
						leCanvas.getGraphicsContext2D().fillRect((float) i * 10.f, (float) j * 10.f, 10.f, 10.f);
					}
				}
			}
		}
	}

	@FXML
	void onCanvasMousePressed(MouseEvent event) {
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
				vueExpo.ajouterVisiteur(m, (float)event.getX(), (float)event.getY(), 12.5);
			}
		} else {
			ajouterPoint(gridX, gridY);			
		}
	}

	@FXML
	void onCanvasMouseDragged(MouseEvent event) {
		int gridX = (int) event.getX() / 10;
		int gridY = (int) event.getY() / 10;
		ajouterPoint(gridX, gridY);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		grille = new boolean[80][42];
		vueExpo = new VueExpo();
		timer = new UpdateTimer(this);
		pf = new Pathfinder(grille);
		backgroundPath = "";
		mode = Mode.MUR;
		panelVueExpo.getChildren().add(vueExpo);
		leCanvas.getGraphicsContext2D().setFill(Color.BLUE);
	}

	public void updateVisiteurs() {
		vueExpo.updateVisiteurs(listeCibles, pf);
	}
	
	private void sauver(File file) throws IOException {
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
			content += String.format("%02d", coords[0]) + ":" + String.format("%02d", coords[1]) + " ";
		}
		content += "\r\n";
		stream.write(content.getBytes());
		stream.close();
	}
	
	private void charger(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		backgroundPath = br.readLine();
		if (!backgroundPath.isEmpty()) mettreEnFond(new File(backgroundPath));
		
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
			int[] coords = {Integer.parseInt(line.substring(6*i, 6*i+2)), Integer.parseInt(line.substring(6*i+3, 6*i+5)) };
			listeCibles.add(coords);
		}
		
		afficherTout();
		br.close();
	}
}

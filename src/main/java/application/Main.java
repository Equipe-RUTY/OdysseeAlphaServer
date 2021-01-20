package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import fr.irit.smac.amak.tools.Log;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			Log.debugTagFilter = "^$";
			Log.enabled = false;

			Stage menu = new Stage();

			Button b_test = new Button("Test");
			Button b_exp = new Button("Expérience");

			primaryStage.setTitle("Comme un blob");
			primaryStage.getIcons().add(new Image(new FileInputStream("src/main/java/application/icon_blob.png")));
			FXMLLoader loader = new FXMLLoader(new File("src/main/java/ControlPanelProto2.fxml").toURI().toURL());
			Parent root;
			root = loader.load();
			Scene scene = new Scene(root, 1300, 700);

			b_test.setOnAction((event) -> {
				menu.close();
				primaryStage.setScene(scene);
				primaryStage.show();
				Stage expoStage = new Stage();
				expoStage.setResizable(false);
				FXMLLoader expoLoader = null;
				try {
					expoLoader = new FXMLLoader(new File("src/main/java/ExpoPanel.fxml").toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				try {
					Scene expoScene = new Scene(expoLoader.load(), 800, 450);
					expoStage.setScene(expoScene);
	    			expoStage.show();
	    			ExpoController expoController = expoLoader.getController();
					expoController.setControl(loader.getController());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			b_exp.setOnAction((event) -> {
				menu.close();
				primaryStage.setScene(scene);
				primaryStage.show();
				Controller control = loader.getController();
				control.setexperience(true);
				control.initTO();
				control.initTI();
			});

			VBox vbox = new VBox();
			vbox.setSpacing(10);
			vbox.setAlignment(Pos.CENTER);
			vbox.getChildren().addAll(b_test, b_exp);

			menu.setTitle("Menu");
			menu.getIcons().add(new Image(new FileInputStream("src/main/java/application/icon_blob.png")));
			menu.setScene(new Scene(vbox, 200, 200));
			menu.show();

			/*
			 * Controller runnerActivityController = loader.getController(); AmasThread
			 * tAmas = new AmasThread(runnerActivityController);
			 * runnerActivityController.settAmas(tAmas); tAmas.start();
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* boolean[][] grille = new boolean[80][42];
		for (int x = 0; x < 80; x++) {
			for (int y = 0; y < 42; y++) {
				grille[x][y] = false;
			}
		}
		Pathfinder pf = new Pathfinder(grille);
		
		System.out.println(pf.getProchaineCase(new Vector2i(10, 10), new Vector2i(30, 30))); */
	}

	public static void main(String[] args) {
		launch(args);
	}

}

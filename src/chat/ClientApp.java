package chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// созание сцены
		Parent root = FXMLLoader.load(getClass().getResource("view/AuthorizationView.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("  Authorization");
		Image icon = new Image(getClass().getResourceAsStream("view/img/iconAuthorization.png"));
		primaryStage.getIcons().add(icon); // установка иконки
		primaryStage.centerOnScreen();
		primaryStage.show();

		primaryStage.setOnCloseRequest((WindowEvent e) -> {
			Platform.exit();
			System.exit(0);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

}

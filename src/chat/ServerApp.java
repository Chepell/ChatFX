package chat;

import chat.model.database.DatabaseHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// созание сцены
		Parent root = FXMLLoader.load(getClass().getResource("view/ServerStartView.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("  Start Server");
		Image icon = new Image(getClass().getResourceAsStream("view/img/iconServer.png"));
		primaryStage.getIcons().add(icon);

		primaryStage.centerOnScreen();
		primaryStage.show();

		// метод переопределения действия кнопки крестика
		primaryStage.setOnCloseRequest((WindowEvent e) -> {
			// закрытие коннекшена к БД
			DatabaseHandler.closeSessionFactory();
			Platform.exit();
			System.exit(0);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}

package chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/userAuth.fxml"));
        primaryStage.setTitle("Authorization");
        primaryStage.setScene(new Scene(root));
//        Image icon = new Image(getClass().getResourceAsStream("view/icon.png"));
//        primaryStage.getIcons().add(icon); // установка иконки
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

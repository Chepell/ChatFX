package chat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class UserAuthController {

	@FXML
	private TextField userFileld;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button cancelBtn;

	@FXML
	private Button loginBtn;

	@FXML
	private Text errorLabel;

	@FXML
	void initialize() {
		// при открытии окна авторизации метку ошибки делаю пустой
		errorLabel.setText("");
	}

	// открытие главного окна чата в случае успешной авторизации в БД после нажатия кнопки Login
	public void openChatAfterLogin(ActionEvent event) {
		// провека авторизации в БД
		if (authorizationCheck()) {
			// получение текущей платформы по элементу
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			try {
				// загрузка вью новой сцены
				Parent root = FXMLLoader.load(getClass().getResource("view/chat.fxml"));
				stage.setTitle("Chat");
				stage.setScene(new Scene(root));
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			errorLabel.setText("Incorrect user/password");
		}
	}

	// закрытие программы при нажатии кнопки Cancel
	public void closeProgram(ActionEvent event) {
		// получение текущей платформы по элементу
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		// закрытие платформы
		stage.hide();
		// завершение программы
		Platform.exit();
		System.exit(0);
	}

	// провека присутствия пользователя в БД
	private boolean authorizationCheck() {
		return true;
	}
}

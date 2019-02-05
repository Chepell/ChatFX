package chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AddNewUserController {

	@FXML
	private TextField userFileld;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button cancelBtn;

	@FXML
	private Button addUserBtn;

	@FXML
	private Text errorLabel;

	@FXML
	void initialize() {
		// при открытии окна авторизации метку ошибки делаю пустой
		errorLabel.setText("");
	}

	// закрытие окна при нажатии на кнопку Cancel
	@FXML
	void closeScene(ActionEvent event) {
		// получение текущей платформы по элементу
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.hide();
	}

	// закрытие окна при нажатии на кнопку Add User
	@FXML
	void addNewUser(ActionEvent event) {
		// если добавление прошло успешно
		if (addUserInDB()) {
			// закрываю окно
			closeScene(event);
		} else {
			// иначе вывожу сообщение
			errorLabel.setText("User already exist!");
		}
	}

	// метод добавляет пользователя в БД
	private boolean addUserInDB() {
		return true;
	}
}

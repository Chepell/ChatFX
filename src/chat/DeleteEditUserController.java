package chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DeleteEditUserController {

	@FXML
	private Button cancelBtn;

	@FXML
	private Button saveChangesBtn;

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

	// закрытие окна при нажатии на кнопку Save Changes
	@FXML
	void saveChanges(ActionEvent event) {
		// если сохранение изменений в БД прошло успешно
		if (saveChangeInDB()) {
			// закрываю окно
			closeScene(event);
		} else {
			// иначе вывожу сообщение
			errorLabel.setText("Can't save changes in DB!");
		}
	}

	// метод созраняет изменения в БД
	private boolean saveChangeInDB() {
		// тут реализация сохранения изменений в БД
		return true;
	}
}
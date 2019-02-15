package chat;

import chat.model.client_server.Listener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;

public class AddNewUserController {
	private Listener listener;

	@FXML
	private TextField loginField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private PasswordField passwordRepeatField;

	@FXML
	private Label errorLabel;

	@FXML
	void initialize() {
		// при открытии окна авторизации метку ошибки делаю пустой
		errorLabel.setText("");
	}

	/**
	 * получение значения поля login с подрезкой пробелов
	 *
	 * @return
	 */
	public String getLogin() {
		return loginField.getText().trim();
	}

	/**
	 * получение значения поля password с подрезкой пробелов
	 *
	 * @return
	 */
	public String getPassword() {
		return passwordField.getText().trim();
	}

	/**
	 * метод для передачи с предыдущей сцены на текущую слушателя
	 *
	 * @param listener
	 */
	public void initListener(Listener listener) {
		this.listener = listener;
	}

	/**
	 * метод обрабатывает нажатие кнопки "Cancel"
	 *
	 * @param event
	 */
	@FXML
	void closeScene(ActionEvent event) {
		// получение текущей платформы по элементу
		// прячу текущую сцену
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.hide();
	}

	/**
	 * метод обрабатывает нажатие кнопки "Add UserDB"
	 *
	 * @param event
	 */
	@FXML
	void addNewUser(ActionEvent event) {
		// если поля заполнены корректно
		if (checkNewUserFields()) {

			// отправляю нового юзера добавляться в БД
			listener.sendNewUserOnServer(getLogin(), getPassword());

			//todo тут использовать callable and future
			// цикл работает пока поле пустое
			// и проверяет поле с периодичностью в пол секунды
			do {
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// в зависмости от того какое значение в поле
				switch (listener.getAddingResult()) {
					case SERVER_USER_SUCCESSFULLY_ADD_IN_DB:
						closeScene(event); // просто закрываю окно
						break;
					case SERVER_USER_ALREADY_EXIST_IN_DB:
						errorLabel.setText("UserDB already exist!");
						break;
					case SERVER_USER_ADDING_ERROR_IN_DB:
						errorLabel.setText("Error in DB!");
						break;
				}
			} while (listener.getAddingResult() == null);
		}
	}

	/**
	 * метод проверка корректности заполнения полей создания нового юзера
	 *
	 * @return возвращает true при корректном заполнении полей
	 */
	private boolean checkNewUserFields() {
		boolean result = false;

		// получаю из полей значения
		String loginText = loginField.getText().trim();
		String passwordText = passwordField.getText().trim();
		String passwordRepeatText = passwordRepeatField.getText().trim();

		// проверка заполнены ли все поля
		if (!loginText.isEmpty() && !passwordText.isEmpty() && !passwordRepeatText.isEmpty()) {
			// если пароли совпадают
			if (passwordText.equals(passwordRepeatText)) {
				result = true;
			} else {
				errorLabel.setText("Passwords don't match!");
			}
		} else if (loginText.isEmpty()) {
			errorLabel.setText("Login is empty!");
		} else {
			errorLabel.setText("Password is empty!");
		}
		return result;
	}
}

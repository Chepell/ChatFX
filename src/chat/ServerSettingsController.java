package chat;

import chat.model.handlers.PropertiesHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerSettingsController {
	private PropertiesHandler properties = new PropertiesHandler("hibernate");

	@FXML
	private TextField databaseIpField;

	@FXML
	private TextField databasePortField;

	@FXML
	private TextField databaseUsernameField;

	@FXML
	private PasswordField databasePasswordField;

	@FXML
	private TextField databaseSchemaField;

	@FXML
	private Label errorLabel;

	@FXML
	void initialize() {
		// заполняет при старте поля окна значениями из файла
		getFromPropertiesFileToFields();
	}

	/**
	 * метод вставляет значения из файла в поля формы
	 */
	private void getFromPropertiesFileToFields() {
		// получаю значения полей из файла
		String url = properties.getProperty("hibernate.connection.url");
		String username = properties.getProperty("hibernate.connection.username");
		String password = properties.getProperty("hibernate.connection.password");

		String regex = ".+//(.+):(\\d+)/(.+)\\?.+";

		String login = "";
		String ip = "";
		String schema = "";

		Pattern hostFinderPattern = Pattern.compile(regex);
		final Matcher match = hostFinderPattern.matcher(url);
		if (match.find()) {
			login = match.group(1);
			ip = match.group(2);
			schema = match.group(3);
		}

		// установка значений в поля
		databaseIpField.setText(login);
		databasePortField.setText(ip);
		databaseUsernameField.setText(username);
		databasePasswordField.setText(password);
		databaseSchemaField.setText(schema);
		errorLabel.setText("");
	}

	/**
	 * метод сохраняет текущие значения из полей в файл
	 */
	private void saveFromFieldsToPropertiesFile() {
		// получаю текущие значения полей
		String ip = databaseIpField.getText();
		String port = databasePortField.getText();
		String username = databaseUsernameField.getText();
		String password = databasePasswordField.getText();
		String schema = databaseSchemaField.getText();

		String url = String.format("jdbc:mysql://%s:%s/%s?useSLL=false&serverTimezone=UTC", ip, port, schema);

		// сохраняю значения в файл свойств
		properties.setProperty("hibernate.connection.url", url);
		properties.setProperty("hibernate.connection.username", username);
		properties.setProperty("hibernate.connection.password", password);

		properties.savePropertiesToFile();
	}

	/**
	 * метод обрабатывает нажатие кнопки "Cancel"
	 *
	 * @param event
	 */
	@FXML
	void closeSettings(ActionEvent event) {
		// получение текущей платформы по элементу
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		// прячу текущую сцену
		stage.hide();
	}

	@FXML
	void saveSettings(ActionEvent event) {
		saveFromFieldsToPropertiesFile();
		closeSettings(event);
	}

}

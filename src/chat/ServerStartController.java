package chat;

import chat.model.database.DatabaseHandler;
import chat.model.handlers.PropertiesHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ServerStartController {
	private PropertiesHandler properties = new PropertiesHandler("server");

	@FXML
	private TextField portField;

	@FXML
	private Label errorLabel;

	@FXML
	void initialize() {
		// установка занчение порта в поле
		portField.setText(properties.getProperty("port"));

		// метку ошибки делаю пустой
		errorLabel.setText("");
	}

	@FXML
	void closeProgram(ActionEvent event) {
		// закрытие коннекшена к БД
		DatabaseHandler.closeSessionFactory();
		Platform.exit();
		System.exit(0);
	}

	/**
	 * метод вызывается при нажатии кнопки Start Server
	 *
	 * @param event
	 */
	@FXML
	void startServer(ActionEvent event) {
		// проверка что в поле введен корректный порт
		if (checkPort() != 0) {
			// сохранение значения порта в поле для того что бы при последующем запуске подставлялось последнее
			// корректное использовавшееся значение
			properties.setProperty("port", checkPort() + "");
			properties.savePropertiesToFile();

			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("view/ServerRunningView.fxml"));
				Parent root = loader.load();

				// получение текущей платформы по элементу
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.setScene(new Scene(root));
				stage.setTitle("  Server Running");

				// метод переопределения действия кнопки крестика
				stage.setOnCloseRequest((WindowEvent e) -> {
					// закрытие коннекшена к БД
					DatabaseHandler.closeSessionFactory();
					Platform.exit();
					System.exit(0);
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * метод проверки введенного порта и конвертации его в число
	 * если не число или отрицательное число, то возвращаю ноль
	 *
	 * @return
	 */
	private int checkPort() {
		int port = 0;
		try {
			port = Integer.parseInt(portField.getText().trim());
		} catch (NumberFormatException e) {
			errorLabel.setText("Incorrect port number!");
		}

		if (port < 0) {
			port = 0;
			errorLabel.setText("Port can't be negative!");
		}
		return port;
	}
}
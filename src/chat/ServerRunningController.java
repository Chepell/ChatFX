package chat;

import chat.model.client_server.Server;
import chat.model.database.DatabaseHandler;
import chat.model.handlers.PropertiesHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ServerRunningController {
	private PropertiesHandler properties = new PropertiesHandler("server");

	@FXML
	private Label portLabel;

	@FXML
	void initialize() {
		// подключение к БД в отдельном потоке демоне
		Thread databaseThread = new Thread(DatabaseHandler::initSessionFactory);
		databaseThread.setDaemon(true);
		databaseThread.start();

		// получаю значение порта из файла и установка значение порта в метке
		int port = Integer.parseInt(properties.getProperty("port"));
		portLabel.setText(port + "");

		// запуск в отдельном потоке сервера
		Thread serverThread = new Thread(() -> new Server(port));
		serverThread.setDaemon(true);
		serverThread.start();
	}

	/**
	 * метод закрытия программы при нажатии кнопки Stop Server
	 *
	 * @param event
	 */
	@FXML
	void closeProgram(ActionEvent event) {
		// закрытие коннекшена к БД
		DatabaseHandler.closeSessionFactory();
		Platform.exit();
		System.exit(0);
	}
}

package chat;

import chat.model.client_server.Listener;
import chat.model.handlers.PropertiesHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class AuthorizationController {
	private static ChatController chatController;
	private Scene scene;
	private final PropertiesHandler properties = new PropertiesHandler("client");

	@FXML
	private TextField loginField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField ipField;

	@FXML
	private TextField portField;

	@FXML
	private Label versionLabel;

	@FXML
	private Label errorLabel;

	@FXML
	void initialize() {
		// заполняет при старте поля окна значениями из файла
		getFromPropertiesFileToFields();
	}

	public String getLogin() {
		return loginField.getText().trim();
	}

	public String getPassword() {
		return passwordField.getText().trim();
	}

	public String getIp() {
		return ipField.getText().trim();
	}

	public String getPort() {
		return portField.getText().trim();
	}

	/**
	 * метод обрабатывает нажатие кнопки Login и открывает главное окно чата
	 *
	 * @param event
	 * @throws IOException
	 */
	@FXML
	void openChatAndLogin(ActionEvent event) throws IOException {
		// если все поля заполнены правильно
		if (fieldsCheck()) {
			// сохраняю значений из полей в файл свойств
			saveFromFieldsToPropertiesFile();

			// загрузка новой сцены с получением loader для доступа к controller
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/ChatView.fxml"));
			Parent window = loader.load();

			// инициирую контроллер, что бы получать к нему доступ из слушателя слушатель
			// что-то выполяет и затем вызвает методы объекта chatController,
			// которые визуализируют что нужно
			chatController = loader.getController();

			// создаю слушатель принимающий параметром текущий контроллер
			Listener listener = Listener.getInstance(chatController, this);
			// запускаю в потоке
			new Thread(listener).start();

			// поле контролера инициирую созданной сценой чата ChatView
			scene = new Scene(window);
		}
	}

	/**
	 * метод запускается из listener когда он успешно стартует в отдельной ните
	 */
	public void showScene() {
		Platform.runLater(() -> {
			// если юзер не админ, то прячу меню добавления и удаления юзеров
			String user = Listener.getLogin();
			if (!"admin".equals(user)) {
				chatController.hideMenu();
			}
			// имя текущего юзера помещаю в тайтл окна
			String title = String.format("  ChatFX		 User: %s", user);

			Stage stage = (Stage) loginField.getScene().getWindow();
			stage.close();
			stage.setScene(scene);
			stage.setTitle(title);
			stage.centerOnScreen();
			stage.show();

			stage.setOnCloseRequest((WindowEvent e) -> {
				// удаление юзера из списка
				Listener instance = Listener.getInstance();
				instance.sendRequestToOffline();
				Listener.deleteInstance();
				Platform.exit();
				System.exit(0);
			});
		});
	}

	/**
	 * закрытие программы при нажатии кнопки Cancel
	 *
	 * @param event
	 */
	@FXML
	void closeProgram(ActionEvent event) {
		Platform.exit();
		System.exit(0);
	}

	/**
	 * прверка корректности заполнения всех полей
	 *
	 * @return
	 */
	private boolean fieldsCheck() {
		boolean result;
		// получаю из полей логин и пароль их значения, подрезаю пробелы
		String login = getLogin();
		String password = getPassword();
		String ip = getIp();
		int port = checkPort();

		// проверка заполнены ли поля
		if (!login.isEmpty() && !password.isEmpty()
				&& !ip.isEmpty() && port != 0) {
			result = true;
		} else if (login.isEmpty()) {
			errorLabel.setText("Login is empty!");
			result = false;
		} else if (password.isEmpty()) {
			errorLabel.setText("Password is empty!");
			result = false;
		} else if (ip.isEmpty()) {
			errorLabel.setText("IP is empty!");
			result = false;
		} else {
			errorLabel.setText("Wrong or empty port!");
			result = false;
		}
		return result;
	}

	/**
	 * метод проверки введенного порта и конвертации его в число
	 *
	 * @return если не число или отрицательное число, то возвращаю ноль
	 */
	private int checkPort() {
		int port = 0;
		try {
			port = Integer.parseInt(getPort());
		} catch (NumberFormatException e) {
			errorLabel.setText("Incorrect port number!");
		}

		if (port < 0) {
			port = 0;
			errorLabel.setText("Port can't be negative!");
		}
		return port;
	}

	/**
	 * метод вставляет значения из файла в поля формы
	 */
	private void getFromPropertiesFileToFields() {
		// получаю значения полей из файла
		String login = properties.getProperty("login");
		String ip = properties.getProperty("ip");
		String port = properties.getProperty("port");
		String version = properties.getProperty("version");

		// установка значений в поля
		loginField.setText(login);
		ipField.setText(ip);
		portField.setText(port);
		versionLabel.setText("Version: " + version);
		errorLabel.setText("");
	}

	/**
	 * метод сохраняет текущие значения из полей в файл
	 */
	private void saveFromFieldsToPropertiesFile() {
		// получаю текущие значения полей
		String login = getLogin();
		String ip = getIp();
		String port = getPort();

		// сохраняю значения в файл свойств
		properties.setProperty("login", login);
		properties.setProperty("ip", ip);
		properties.setProperty("port", port);
		properties.savePropertiesToFile();
	}

	/**
	 * метод для обновления значения метки, вызывается из listener
	 *
	 * @param message
	 */
	public void updateErrorLabel(String message) {
		errorLabel.setText(message);
	}
}

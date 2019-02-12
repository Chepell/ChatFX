package chat;

import chat.model.client_server.Listener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Artem Voytenko
 * 08.02.2019
 */

public class ChatController {
	private Listener listener;
	// сет онлайн юзеров сразу отсоритрованный лексиграфически без учета регистра
	private Set<String> usersOnline = new TreeSet<>(Comparator.comparing(String::toLowerCase));

	@FXML
	private TextArea messages;

	@FXML
	private Label infoLabel;

	@FXML
	private MenuBar menuBar;

	@FXML
	private MenuItem closeChatMenu;

	@FXML
	private Menu editMenu;

	@FXML
	private MenuItem addNewUserChatMenu;

	@FXML
	private MenuItem deleteEditUserMenu;

	@FXML
	private MenuItem aboutMenu;

	@FXML
	public TextArea messageBox;

	@FXML
	private TextArea online;

	@FXML
	public Button sendButton;

	// объект комбинации клавиш для отправки сообщений через Ctrl+ENTER
	public final KeyCombination keyComb = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);

	@FXML
	void initialize() {
	}

	/**
	 * метод для передачи listener с предыдущей сцены на текущую
	 *
	 * @param listener
	 */
	public void initListener(Listener listener) {
		this.listener = listener;
	}

	/**
	 * метод добавляет подключившегося юзера в список чата
	 *
	 * @param name
	 */
	public void addUserInOnlineSet(String name) {
		usersOnline.add(name);
	}

	/**
	 * метод удаляет отключившегося юзера из списка чата
	 *
	 * @param name
	 */
	public void deleteUserFromOnlineSet(String name) {
		usersOnline.remove(name);
	}

	/**
	 * разлогиниваюсь и возвращаюсь на окно авторизации при выборе Menu -> Logoff
	 *
	 * @param event
	 */
	@FXML
	void logoffAndBackToAuthorization(ActionEvent event) throws IOException {
		// отправка на сервер команды о уходе в оффлайн
		listener.sendRequestToOffline();

		// созание сцены
		Stage primaryStage = (Stage) menuBar.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("view/AuthorizationView.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("  Authorization");
		Image icon = new Image("chat/view/icon/iconAuthorization.png");
		primaryStage.getIcons().add(icon); // установка иконки
		primaryStage.centerOnScreen();
		primaryStage.show();

		primaryStage.setOnCloseRequest((WindowEvent e) -> {
			Platform.exit();
			System.exit(0);
		});
	}

	/**
	 * закрытие программы при выборе Menu -> Quit
	 *
	 * @param event
	 */
	@FXML
	public void closeProgram(ActionEvent event) {
		// отправка на сервер команды о уходе в оффлайн
		listener.sendRequestToOffline();
		Platform.exit();
		System.exit(0);
	}

	/**
	 * открываю окно добавления нового пользователя Edit -> Add New User
	 *
	 * @param event
	 */
	@FXML
	public void openAddNewUserScene(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/AddNewUserView.fxml"));
			Parent root = loader.load();

			AddNewUserController addNewUserController = loader.getController();
			addNewUserController.initListener(listener);

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("  Add New User");
			stage.setResizable(false);
			// настройка новой сцены в качестве модальной
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * открываю окно информации о программе Help -> About
	 *
	 * @param event
	 */
	@FXML
	public void openAboutScene(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/AboutView.fxml"));
			Parent root = loader.load();

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("  About ClientApp");
			stage.setResizable(false);

			// настройка новой сцены в качестве модальной
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * метод обновления списка пользователей чата
	 */
	public void updateOnline() {
		Set<String> setOnline = Collections.unmodifiableSet(usersOnline);
		StringBuilder list = new StringBuilder();

		for (String s : setOnline) {
			list.append(s).append("\n");
		}
		online.setText(list.toString());
	}

	/**
	 * обновление данных в основном окне чата
	 *
	 * @param message
	 */
	public void updateMessages(String message) {
		messages.appendText(message + "\n");
	}

	/**
	 * обновление данных в информационной панеле
	 *
	 * @param message
	 */
	public void updateInfoLabel(String message) {
		infoLabel.setText(message);
	}

	/**
	 * прячу админские меню
	 */
	public void hideMenu() {
		editMenu.setVisible(false);
	}
}

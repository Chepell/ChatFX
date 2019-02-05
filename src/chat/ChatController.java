package chat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ChatController {

	@FXML
	private MenuBar menuBar;

	@FXML
	private MenuItem closeChatMenu;

	@FXML
	private MenuItem addNewUserChatMenu;

	@FXML
	private MenuItem deleteEditUserMenu;

	@FXML
	private MenuItem aboutMenu;

	@FXML
	void initialize() {}

	// разлогиниваюсь и возвращаюсь на окно авторизации при выборе Menu -> Logoff
	@FXML
	void logoffAndBackToUserAuth(ActionEvent event) {
		// разлогиниваюсь
		logoff();
		// получение текущей платформы из объекта меню
		Stage stage = (Stage) menuBar.getScene().getWindow();
		// закрытие платформы
		stage.hide();
		try {
			// загрузка вью новой сцены
			Parent root = FXMLLoader.load(getClass().getResource("view/userAuth.fxml"));
			stage.setTitle("Authorization");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// закрытие программы при выборе Menu -> Quit
	@FXML
	void closeProgram(ActionEvent event) {
		// разлогиниваюсь
		logoff();
		// получение текущей платформы из объекта меню
		Stage stage = (Stage) menuBar.getScene().getWindow();
		// закрытие платформы
		stage.hide();
		// завершение программы
		Platform.exit();
		System.exit(0);
	}


	// открываю окно добавления нового пользователя Edit -> Add New User
	@FXML
	void openAddNewUserScene(ActionEvent event) {
		popupScene("addNewUser", "Add New User");
	}

	// открываю окно удаления/изменения пользователя Edit -> Delete/Edit User
	@FXML
	void openDeleteEditUserScene(ActionEvent event) {
		popupScene("deleteEditUser", "Delete/Edit User");
	}

	// открываю окно информации о программе Help -> About
	@FXML
	void openAboutScene(ActionEvent event) {
		popupScene("about", "About App");
	}

	// сервисный метод открытия всплывающих окон из меню
	private void popupScene(String fxmlFileName, String SceneTitle) {
		// полный путь к вью файлу сцены
		String file = "view/" + fxmlFileName + ".fxml";
		try {
			// загрузка вью новой сцены
			Parent root = FXMLLoader.load(getClass().getResource(file));
			// создаю новую площадку, окно появится поверх существующего
			Stage stage = new Stage();
			stage.setTitle("  " + SceneTitle);
			stage.setScene(new Scene(root));
			stage.setResizable(false);
			stage.initStyle(StageStyle.UTILITY);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// метод разлогинивания
	private void logoff() {
		// обновить в бд инфу по юзеру
		// поле статуса поменять на оффлайн
	}
}


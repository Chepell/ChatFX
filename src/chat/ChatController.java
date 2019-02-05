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

import java.io.IOException;

public class ChatController {

	@FXML
	private MenuBar menuBar;

	@FXML
	private MenuItem closeChatMenu;

	@FXML
	private MenuItem addNewUserChatMenu;

	@FXML
	private MenuItem delitEditUser;

	@FXML
	void initialize() {}

	// разлогиниваюсь и возвращаюсь на окно авторизации при выборе Menu -> Logoff
	@FXML
	void logoffAndBackToUserAuth(ActionEvent event) {
		// разлогиниваюсь
		logoff();
		// получение текущей платформы из объекта меню
		Stage stage = (Stage) menuBar.getScene().getWindow();
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
	public void closeProgram(ActionEvent event) {
		// разлогиниваюсь
		logoff();
		// завершение программы
		Platform.exit();
		System.exit(0);
	}


	// открываю окно добавления нового пользователя Edit -> Add New User
	@FXML
	public void openAddNewUserScene(ActionEvent event) {
		try {
			// загрузка вью новой сцены
			Parent root = FXMLLoader.load(getClass().getResource("view/addNewUser.fxml"));
			// создаю новую площадку, окно появится поверх существующего
			Stage stage = new Stage();
			stage.setTitle("Add New User");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// открываю окно удаления/изменения пользователя Edit -> Delete/Edit User
	@FXML
	public void openDeleteEditUserScene(ActionEvent event) {
		try {
			// загрузка вью новой сцены
			Parent root = FXMLLoader.load(getClass().getResource("view/deleteEditUser.fxml"));
			// создаю новую площадку, окно появится поверх существующего
			Stage stage = new Stage();
			stage.setTitle("Delete/Edit User");
			stage.setScene(new Scene(root));
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


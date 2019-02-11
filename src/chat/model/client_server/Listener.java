package chat.model.client_server;

import chat.AuthorizationController;
import chat.ChatController;
import chat.model.database.entity.User;
import chat.model.handlers.Connection;
import chat.model.handlers.Message;
import chat.model.handlers.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static chat.model.handlers.ConsoleHelper.writeMessage;
import static chat.model.handlers.MessageType.CLIENT_ADD_USER_IN_DB;
import static chat.model.handlers.MessageType.CLIENT_CONNECT_RESPONSE;
import static chat.model.handlers.MessageType.CLIENT_SEND_MESSAGE;
import static chat.model.handlers.MessageType.SERVER_CONNECT_REQUEST;
import static chat.model.handlers.MessageType.SERVER_USER_ACCEPTED;


// Обмен сообщениями будет происходить в двух параллельно работающих потоках.
// Один будет заниматься чтением из консоли и отправкой прочитанного серверу,
// а второй поток будет получать данные от сервера и выводить их в консоль.
// объект в виде сингтона, один на клиентсвое приложение

public class Listener implements Runnable {
	private static Listener instance;

	// поле инициируется во внутреннем классе в отдельном потоке
	protected Connection connection;
	// флаг подключения/отключения пользователя от сервера потокобезопасный
	private volatile boolean clientConnected;

	// поле в которое помещается ответ от сервера при попытке добавить нового юзера
	private MessageType addingResult;
	private ChatController chatController;
	private static AuthorizationController authorizationController;

	private Listener(ChatController chatController, AuthorizationController authorizationController) {
		this.chatController = chatController;
		this.authorizationController = authorizationController;
	}

	public static Listener getInstance(ChatController chatController, AuthorizationController authorizationController) {
		if (instance == null) {
			instance = new Listener(chatController, authorizationController);
		}
		return instance;
	}

	public static Listener getInstance() {
		return instance;
	}

	public static void deleteInstance() {
		instance = null;
	}

	public static String getLogin() {
		return authorizationController.getLogin();
	}

	public String getPassword() {
		return authorizationController.getPassword();
	}

	public String getIp() {
		return authorizationController.getIp();
	}

	public int getPort() {
		return Integer.parseInt(authorizationController.getPort());
	}

	public ChatController getChatController() {
		return chatController;
	}

	public void setChatController(ChatController chatController) {
		this.chatController = chatController;
	}

	public AuthorizationController getAuthorizationController() {
		return authorizationController;
	}

	public void setAuthorizationController(AuthorizationController authorizationController) {
		Listener.authorizationController = authorizationController;
	}

	public MessageType getAddingResult() {
		return addingResult;
	}

	public void setAddingResult(MessageType addingResult) {
		this.addingResult = addingResult;
	}

	/**
	 * метод для отправки обычного сообщения на сервер
	 *
	 * @param text
	 */
	public void sendTextMessage(String text) {
		// создаю объект сообщения на основе переданного в параметре текста
		Message message = new Message(CLIENT_SEND_MESSAGE, text);
		try {
			// отправляю сообщение используя метод коннекшна
			connection.send(message);
		} catch (IOException e) {
			writeMessage("Can't send message");
			chatController.updateInfoLabel("Can't send message");
			// если ошибка отправки, то меняю флаг коннекшена к серверу
			clientConnected = false;
		}
	}

	/**
	 * метод для отправки на сервер данных нового пользователя для регистрации
	 *
	 * @param login
	 * @param password
	 */
	public void sendNewUserOnServer(String login, String password) {
		// создаю объект сообщения на основе переданных параметров
		Message message = new Message(CLIENT_ADD_USER_IN_DB, login, password);
		try {
			// отправляю сообщение используя метод коннекшна
			connection.send(message);
		} catch (IOException e) {
			writeMessage("Can't send message");
			chatController.updateInfoLabel("Can't send message");
			// если ошибка отправки, то меняю флаг коннекшена к серверу
			clientConnected = false;
		}
	}

	/**
	 * метод для отправки на сервер сервисного сообщения ухода юзера офлайн
	 */
	public void sendRequestToOffline() {
		// создаю объект сообщения на основе переданных параметров
		Message message = new Message(getLogin());
		try {
			// отправляю сообщение используя метод коннекшна
			connection.send(message);
		} catch (IOException e) {
			writeMessage("Can't send message");
			chatController.updateInfoLabel("Can't send message");
			// если ошибка отправки, то меняю флаг коннекшена к серверу
			clientConnected = false;
		}
	}

	/**
	 * метод создает и возвращает объект внутреннего класса
	 *
	 * @return
	 */
	public SocketThread getSocketThread() {
		return new SocketThread();
	}

	/**
	 * метод реализации интерфейса Runnable
	 */
	@Override
	public void run() {
		// вспомогательный поток демон, завершится вместе с главным потоком
		SocketThread socketThread = getSocketThread();
		socketThread.setDaemon(true);
		socketThread.start();
		try {
			// синхронизация на текущем объекте Client
			synchronized (this) {
				// отдаю мьютекс объекта и жду notify из другого потока
				wait();
			}
		} catch (InterruptedException e) {
			writeMessage("Synchronized thread error!");
			System.exit(1);
		}

		// если есть подключение
		if (clientConnected) {
			// запускаю окно с чатом
			authorizationController.showScene();
			// в чате в статус баре пишу
			chatController.updateInfoLabel("Connect to server");

			// цикл продолжается, пока клиент подключен
			while (clientConnected) {
				// слушатель отправки сообщений по нажатию кнопки
				chatController.sendButton.setOnAction(event -> messageHandler());

				// слушатель отправки сообщений из окна ввода нажатием Ctrl+ENTER
				chatController.messageBox.setOnKeyPressed(keyEvent -> {
					if (chatController.keyComb.match(keyEvent)) {
						messageHandler();
					}
				});

				// слушаю события нажатия кнопки SEND или Ctrl+ENTER каждую секунду в данном цикле
				// врядли человеку нужно отправлять сообщения чаще )))
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//TODO Тут нужно как-то сделать прерывание потока (break) на основе закрытия окна чата или
				// разлогинивания что бы к тому же на сервер полетело сообщение что пользователь ушел оффлайн

			}
		} else {
			authorizationController.updateErrorLabel("Connection error!");
			writeMessage("Произошла ошибка во время работы клиента.");
		}
	}

	/**
	 * метод для обработки получения содержания окна ввода сообщения
	 */
	private void messageHandler() {
		// получаю содержимое из окно ввода сообщений,
		// подрезаю пробелы что бы исключить отправку пустых сообщений
		String message = chatController.messageBox.getText().trim();
		// и если значение после этого не пустое
		if (!message.isEmpty()) {
			// то отправляю сообщение на сервер
			sendTextMessage(message);
			// отчищаю окно ввода
			chatController.messageBox.clear();
		}
	}

	/**
	 * Класс отвечает за поток, устанавливающий сокетное соединение и читающий сообщения сервера
	 */
	public class SocketThread extends Thread {
		/**
		 * метод обновляет текст в окне чата
		 *
		 * @param message
		 */
		protected void processIncomingMessage(String message) {
//			writeMessage(message);
			chatController.updateMessages(message);
		}

		/**
		 * метод добавляет в список online присоединившегося участника
		 *
		 * @param userName
		 */
		public void informAboutAddingNewUser(String userName) {
//			writeMessage(userName + " joint to chat!");
			chatController.addUserInOnlineSet(userName);
			chatController.updateOnline();
		}

		/**
		 * метод убирает из списка online отсоединившегося участника
		 *
		 * @param userName
		 */
		public void informAboutDeletingNewUser(String userName) {
			chatController.deleteUserFromOnlineSet(userName);
			chatController.updateOnline();
		}

		/**
		 * метод обновляет поле внешнего класса
		 *
		 * @param type
		 */
		protected void informAboutAddingNewUserInDB(MessageType type) {
			Listener.this.setAddingResult(type);

		}

		/**
		 * метод устанавливает значение поля объекта внешнего класса
		 *
		 * @param clientConnected
		 */
		protected void notifyConnectionStatusChanged(boolean clientConnected) {
			// синхронизация на текущем объекте внешнего класса
			synchronized (Listener.this) {
				Listener.this.clientConnected = clientConnected;
				// оповещает (пробуждать ожидающий) основной поток класса Client
				Listener.this.notify();
			}
		}

		/**
		 * метод для знакомства с сервером
		 *
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
		protected void clientHandshake() throws IOException, ClassNotFoundException {
			while (true) {
				// получаю сообщение от сервера из коннекшена
				Message receiveMessage = connection.receive();
				// получаю тип сообщения из объекта сообщения
				MessageType type = receiveMessage.getType();

				// если это было сервисное сообщение от сервера с запросом имени
				if (type == SERVER_CONNECT_REQUEST) {
					// формирую сообщение для отправка
					Message message = new Message(CLIENT_CONNECT_RESPONSE, getLogin(), getPassword());
					connection.send(message);
					// если же сообщение с подтверждением имени
				} else if (type == SERVER_USER_ACCEPTED) {
					// то вызываю метод, который статус коннекшена меняет
					notifyConnectionStatusChanged(true);
					break; // и преываю цикл, первоначальное соединение с сервером успешно установлено
				} else {
					throw new IOException("Unexpected MessageType");
				}
			}
		}

		/**
		 * метод с главным циклом обработки сообщений сервера
		 *
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
		protected void clientMainLoop() throws IOException, ClassNotFoundException {
			// бесконечный цикл будет прерван в случае исключения или завершения потока
			while (true) {
				// получаю сообщение от сервера из коннекшена
				Message receiveMessage = connection.receive();
				MessageType type = receiveMessage.getType();
				String data = receiveMessage.getData();
				String login = receiveMessage.getLogin();
				List<User> allUsers = receiveMessage.getAllUsers();

				switch (type) {
					case CLIENT_SEND_MESSAGE: // если получено текстовое сообщение
						// обрабатываю его методом вывода сообщения на консоль
						processIncomingMessage(data);
						break;
					case SERVER_USER_ONLINE: // если это добавление нового пользователя
						informAboutAddingNewUser(data);
						break;
					case SERVER_USER_OFFLINE: // в случае удаления пользователя
						informAboutDeletingNewUser(login);
						break;
					case SERVER_USER_ALREADY_EXIST_IN_DB:
						informAboutAddingNewUserInDB(type);
						break;
					case SERVER_USER_SUCCESSFULLY_ADD_IN_DB:
						informAboutAddingNewUserInDB(type);
						break;
					case SERVER_USER_ADDING_ERROR_IN_DB:
						informAboutAddingNewUserInDB(type);
					default: // если сообщение любого другого типа, то бросаю исключение
						throw new IOException("Unexpected MessageType");
				}
			}
		}



		/**
		 * переопределение метода многопоточности
		 */
		@Override
		public void run() {
			try {
				// создаю клиентский сокет
				Socket socket = new Socket(getIp(), getPort());
				// инициирую поле объекта внешнего класса новым коннекшеном
				// в конструктор которого передаю созданный сокет
				connection = new Connection(socket);
				// вызываю метод первичного знакомства с сервером, который вызывает ввод имени с консоли
				clientHandshake();
				// далее запускаю основной цикл
				clientMainLoop();
			} catch (IOException | ClassNotFoundException e) {
				// если словил исключение, то меняю состояниие поля внешнего класса
				notifyConnectionStatusChanged(false);
			}
		}
	}


}

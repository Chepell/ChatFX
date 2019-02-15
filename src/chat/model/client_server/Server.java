package chat.model.client_server;

import chat.model.database.DatabaseHandler;
import chat.model.database.entity.MessageDB;
import chat.model.database.entity.User;
import chat.model.database.entity.UserDB;
import chat.model.handlers.Connection;
import chat.model.database.entity.Message;
import chat.model.handlers.MessageType;
import chat.model.handlers.PropertiesHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static chat.model.handlers.ConsoleHelper.writeMessage;
import static chat.model.handlers.MessageType.*;

// Сервер должен поддерживать множество соединений с разными клиентами одновременно
// Реалтзовано с помощью следующего алгоритма:
//- Сервер создает серверное сокетное соединение
//- В цикле ожидает, когда какой-то клиент подключится к сокету
//- Создает новый поток обработчик Handler, в котором будет происходить обмен сообщениями с клиентом
//- Ожидает следующее соединение

// основной класс сервера
public class Server {
	// потокобезопасный мэп для хранения пар имя:коннекшн
	private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
	private static List<User> userList = new CopyOnWriteArrayList<>();
	private static PropertiesHandler propertiesHandler = new PropertiesHandler("server");

	// порт
	private int port;

	public Server(int port) {
		this.port = port;
		initUserList();
		begin();
	}

	/**
	 * метод для старта сервера
	 */
	private void begin() {
		// в блоке try-with-resources создаю серверный сокет на основе порта полученного при создании объекта
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			writeMessage("Server start!"); // сообщаю что сервер успешно запущен
			writeMessage("Server port " + port); // сообщаю что сервер успешно запущен
			// в бесконечном цикле жду вызовы на сокет
			while (true) {
				Socket newSocket = serverSocket.accept();
				// и передаю их в многопоточный обработчик и сразу запускаю нить
				new Handler(newSocket).start();
			}
		} catch (Exception e) { // если сервер не запустился ловлю исключение и вывожу сообщение
			writeMessage("ServerSocket building error");
		}
	}

	/**
	 * первоначальная загрузка списка всех пользователей из БД в userList
	 */
	private static void initUserList() {
		List<UserDB> allUsersFromDB = DatabaseHandler.getAllUsers();

		for (UserDB userDB : allUsersFromDB) {
			userList.add(new User(userDB.getLogin()));
		}
	}

	/**
	 * метод для изменения статуса пользователя в списке userList
	 *
	 * @param userName
	 * @param onlineStatus
	 */
	private static void changeUserStatus(String userName, boolean onlineStatus) {
		for (User user : userList) {
			if (user.getLogin().equals(userName)) {
				user.setOnlineStatus(onlineStatus);
			}
		}
	}

	/**
	 * метод для отправки сообщений сразу всем пользователям из мэпа
	 *
	 * @param message
	 */
	private static void sendBroadcastMessage(Message message) {
		for (Map.Entry<String, Connection> connections : connectionMap.entrySet()) {
			try {
				// по value у объекта класса Connection из мепа вызываю метод отправки
				connections.getValue().send(message);
			} catch (IOException e) {
				writeMessage("Error! Can't send message");
			}
		}
	}

	/**
	 * многопоточный обработчик для каждого нового клиента обращающегося к серверу
	 * реализует протокол общения с клиентом
	 */
	private static class Handler extends Thread {
		private Socket socket;

		Handler(Socket socket) {
			this.socket = socket;
		}

		/**
		 * метод для первоначального контакта клиента с сервером
		 *
		 * @param connection
		 * @return
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
		private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
			while (true) {
				// серевер отправляет клиенту запрос имени
				connection.send(new Message(SERVER_CONNECT_REQUEST));
				// полученный ответ от клиента сохраняю в переменную
				Message receiveMessage = connection.receive();
				// из полученного сообщения достаю его тип в виде инама и само содержание
				MessageType receiveType = receiveMessage.getType();

				// если сообщение имеет неверный тип
				// то начинаю цикл сначала с запроса имени пользователя
				if (receiveType != CLIENT_CONNECT_RESPONSE) continue;

				// достаю из полученного сообщения логин и пароль
				String login = receiveMessage.getLogin();
				String password = receiveMessage.getData();

				// если поля пустые, то начинаю цикл сначала
				if (login == null || password == null) continue;

				// Если в БД в таблице user такой пользователь не найден, то опять делаю continue
				// получаю запросом объект юзера по логину
				List<UserDB> user = DatabaseHandler.searchUser(login);
				// пользователь с таким логином не был найден, возвращаюсь в начало цикла
				if (user == null || user.isEmpty()) continue;

				// если пользователь в БД найден, то проверяю пароли
				if (!password.equals(user.get(0).getPassword())) continue;

				// Если дошел сюда, то можно добавить пользователя в мэп с ключем в виде
				// полученного имени в сообщении и значением конеекшн переданный параметром в методе
				connectionMap.put(login, connection);
				// отправляю клиенту команду информирующую, что его имя принято
				connection.send(new Message(SERVER_USER_ACCEPTED, login, null));

				// нужно отправить вновь подключенному клиенту весь массив сообщений
//				List<MessageDB> allMessages = DatabaseHandler.getAllMessages();

				// загружать только определенное количество записей
				List<MessageDB> allMessages =
						DatabaseHandler.getMessages(propertiesHandler.getIntProperty("history_message_amount"));

				// если в БД есть история сообщений
				if (allMessages != null && !allMessages.isEmpty()) {
					// конвертирую все в одно сообщение
					String messages = convertHistoryMessagesFromDBtoString(allMessages);
					connection.send(new Message(messages));
				}
				// возвращаю принятое имя
				return login;
			}
		}

		/**
		 * метод для отправки всему списку пользователей онлайн имени текущего пользователя
		 *
		 * @param connection
		 * @param userName
		 * @throws IOException
		 */
		private void notifyUsers(Connection connection, String userName) throws IOException {
			// циклом иду по множеству ключей мэпа
			for (String user : connectionMap.keySet()) {
				// если текущий ключ из мэпа не равен пользователю переданном в сигнатуре метода
				// то отправляю на коннекшн текущего юзера объект сообщения с типом SERVER_USER_ONLINE и имя
				if (!user.equals(userName)) connection.send(new Message(SERVER_USER_ONLINE, user, null));
			}
		}

		/**
		 * метод с помощью которого сервер транслирует полученное от пользователя текстовое сообщение всем участникам
		 *
		 * @param connection
		 * @param userName
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
		private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
			while (true) {
				// получаю сообщение от пользователя
				Message receiveMessage = connection.receive();
				// забираю данные
				MessageType type = receiveMessage.getType();
				String login = receiveMessage.getLogin();
				String data = receiveMessage.getData();

				switch (type) {
					case CLIENT_SEND_MESSAGE:
						// создаю объект сообщения для сохранения в БД
						MessageDB messageDB = new MessageDB(userName, data);
						DatabaseHandler.saveMessage(messageDB);

						// создаю сообщение для отправки
						Message message = new Message(messageDB.toString());

						// отправке сообщений всем пользователям
						sendBroadcastMessage(message);
						break;
					case CLIENT_ADD_USER_IN_DB:
						// запрашиваю из БД пользователя с лигином как у запроса на создание
						List<UserDB> user = DatabaseHandler.searchUser(login);
						// если пользователя с таким логином нет в БД
						if (user == null || user.size() == 0) {
							// создаю объект сущности
							UserDB newUser = new UserDB(login, data);
							// сохраняю юзера в БД
							int saveUserId = DatabaseHandler.saveUser(newUser);

							// провека по id корректности добавления пользователя
							if (saveUserId == 0) {
								connection.send(new Message(SERVER_USER_ADDING_ERROR_IN_DB));
							} else {
								connection.send(new Message(SERVER_USER_SUCCESSFULLY_ADD_IN_DB));
							}
						} else {
							connection.send(new Message(SERVER_USER_ALREADY_EXIST_IN_DB));
						}
						break;
					case CLIENT_DISCONNECT_REQUEST:
						// удаляю юзера из мэпа
						connectionMap.remove(login);

						sendBroadcastMessage(new Message(SERVER_USER_OFFLINE, login, null));
						break;
					default:
						// если тип сообщения другой, то вывожу ошибку
						writeMessage("Error in main loop");
				}
			}
		}

		@Override
		public void run() {
			// установлено новое соединение с удаленным адресом
			SocketAddress address = socket.getRemoteSocketAddress();
			writeMessage("Setup new connection to " + address);

			// создаю коннекшн с клиентом по сокету
			try (Connection connection = new Connection(socket)) {
				// первоначальный контакт сервера с клиентом, запрос имени пользователя
				// дальше не иду пока не будет идентифицирован пользователь и добавлен в мэп
				String newUserName = serverHandshake(connection);

				// рассылка всем участникам сообщения о добавлении нового юзера
				sendBroadcastMessage(new Message(SERVER_USER_ONLINE, newUserName, null));

				// отправка всем пользователям имени вновь подключившегося пользователя
				notifyUsers(connection, newUserName);

				// главный цикл общаения клиента с сервером
				// тут кручусь в вечном цикле получая от пользователя сообщения и рассылая их всем участникам
				serverMainLoop(connection, newUserName);

				// вышли из главного цикла, пользователь разлогинился удаляю его из мэпа
				connectionMap.remove(newUserName);
				// информирование всех остальных участников, что юзер удален
				sendBroadcastMessage(new Message(SERVER_USER_OFFLINE, newUserName, null));

			} catch (IOException | ClassNotFoundException e) {
				writeMessage("Error connection with remote server");
			}
			writeMessage("Connection was closed");
		}

		/**
		 * метод конвертирует историю сообщений в виде списка объектов в одну строку
		 *
		 * @param list
		 * @return
		 */
		private String convertHistoryMessagesFromDBtoString(List<MessageDB> list) {
			StringBuilder builder = new StringBuilder();
			int size = list.size();

			for (int i = 0; i < size; i++) {
				MessageDB messageDB = list.get(i);
				builder.append(messageDB);
				if (i == size - 1) break;
				builder.append("\n");
			}

			return builder.toString();
		}
	}
}


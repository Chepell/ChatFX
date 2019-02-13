package chat.model.handlers;

import java.io.Serializable;

import static chat.model.handlers.MessageType.CLIENT_DISCONNECT_REQUEST;

// класс отвечающий за пересылаемы сообщения
public class Message implements Serializable {
	// тип сообщения
	private final MessageType type;
	// данные сообщения
	private final String data;
	private final String login;
	private final String password;


	public Message(MessageType type, String data, String login, String password) {
		this.type = type;
		this.data = data;
		this.login = login;
		this.password = password;
	}

	/**
	 * конструктор сообщений с пустыми полямти, кроме типа, для сервисных сообщений сервера
	 * запрос имени и подтверждение
	 *
	 * @param type SERVER_CONNECT_REQUEST, SERVER_USER_ACCEPTED
	 */
	public Message(MessageType type) {
		this.type = type;
		this.data = null;
		this.login = null;
		this.password = null;
	}

	/**
	 * конструктор для отправки обычных сообщений от пользователя на сервер
	 *
	 * @param type CLIENT_SEND_MESSAGE
	 * @param data сообщение пользователя
	 */
	public Message(MessageType type, String data) {
		this.type = type;
		this.data = data;
		this.login = null;
		this.password = null;
	}

	/**
	 * конструктор для отправки сервисных сообщений на сервер
	 * @param type CLIENT_CONNECT_RESPONSE, CLIENT_ADD_USER_IN_DB
	 * @param login
	 * @param password
	 */
	public Message(MessageType type, String login, String password) {
		this.type = type;
		this.data = null;
		this.login = login;
		this.password = password;
	}

	/**
	 * конструктор для отпрвки сообщения на разлогинивание юзера
	 * @param login
	 */
	public Message(String login) {
		this.type = CLIENT_DISCONNECT_REQUEST;
		this.data = null;
		this.login = login;
		this.password = null;
	}

	//region гетеры
	public MessageType getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	//endregion
}

// Сообщение Message - это данные, которые одна сторона отправляет, а вторая принимает.
// Каждое сообщение должно иметь тип MessageType, а некоторые и дополнительные
// данные, например, текстовое сообщение должно содержать текст. Т.к. сообщения будут
// создаваться в одной программе, а читаться в другой, удобно воспользоваться механизмом
// сериализации для перевода класса в последовательность битов и наоборот.
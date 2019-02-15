package chat.model.database.entity;

import chat.model.handlers.MessageType;

import java.io.Serializable;

import static chat.model.handlers.MessageType.*;

// класс отвечающий за пересылаемы сообщения
public class Message implements Serializable {
	// тип сообщения
	private final MessageType type;
	// данные сообщения
	private final String login;
	private final String data;


	/**
	 * конструктор для отправки сервисных сообщений на сервер
	 *
	 * @param type  CLIENT_CONNECT_RESPONSE, CLIENT_ADD_USER_IN_DB, SERVER_USER_ONLINE, SERVER_USER_OFFLINE
	 * @param login
	 */
	public Message(MessageType type, String login, String data) {
		this.type = type;
		this.login = login;
		this.data = data;
	}

	/**
	 * конструктор сообщений с пустыми полями, кроме типа, для сервисных сообщений сервера
	 * запрос имени и подтверждение
	 *
	 * @param type SERVER_CONNECT_REQUEST, SERVER_USER_ACCEPTED, SERVER_USER_ADDING_ERROR_IN_DB,
	 *             SERVER_USER_SUCCESSFULLY_ADD_IN_DB, SERVER_USER_ALREADY_EXIST_IN_DB
	 */
	public Message(MessageType type) {
		this.type = type;
		this.login = null;
		this.data = null;
	}

	/**
	 * конструктор для отправки обычных сообщений от пользователя на сервер
	 *
	 * @param data сообщение пользователя
	 */
	public Message(String data) {
		this.type = CLIENT_SEND_MESSAGE;
		this.login = null;
		this.data = data;
	}

	//region гетеры
	public MessageType getType() {
		return type;
	}

	public String getLogin() {
		return login;
	}

	public String getData() {
		return data;
	}
	//endregion
}

// Сообщение Message - это данные, которые одна сторона отправляет, а вторая принимает.
// Каждое сообщение должно иметь тип MessageType, а некоторые и дополнительные
// данные, например, текстовое сообщение должно содержать текст. Т.к. сообщения будут
// создаваться в одной программе, а читаться в другой, удобно воспользоваться механизмом
// сериализации для перевода класса в последовательность битов и наоборот.
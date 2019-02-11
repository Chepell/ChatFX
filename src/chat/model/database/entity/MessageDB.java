package chat.model.database.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Artem Voytenko
 * 08.02.2019
 */

@Entity
@Table(name = "message")
public class MessageDB {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "login")
	private String login;

	@Column(name = "date_time")
	private LocalDateTime dateTime;

	@Column(name = "text")
	private String text;

	public MessageDB() {}

	public MessageDB(String userName, String data) {
		this.login = userName;
		this.dateTime = LocalDateTime.now();
		this.text = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return formatMessage(login, dateTime, text);
	}

	/**
	 * метод форматрирования сообщения от пользователя
	 * для сохранения в БД и броадкастинга всем пользователям онлайн
	 *
	 * @param userName
	 * @param dateTime
	 * @param message
	 * @return
	 */
	private String formatMessage(String userName, LocalDateTime dateTime, String message) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		String time = dateTime.format(formatter);
		return String.format("%s [%s]:%n%s%n", userName, time, message);
	}
}

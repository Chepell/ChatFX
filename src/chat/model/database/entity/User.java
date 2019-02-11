package chat.model.database.entity;

import javax.persistence.*;

/**
 * Artem Voytenko
 * 05.02.2019
 */

@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "login")
	private String login;

	@Column(name = "password")
	private String password;

	@Column(name = "online")
	private boolean online;

	public User() {}

	public User(String login, String password) {
		this.login = login;
		this.password = password;
		this.online = false;
	}

	public User(String login, String password, boolean online) {
		this.login = login;
		this.password = password;
		this.online = online;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", login='" + login + '\'' +
				", password='" + password + '\'' +
				", online=" + online +
				'}';
	}
}

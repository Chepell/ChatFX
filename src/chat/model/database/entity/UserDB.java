package chat.model.database.entity;

import javax.persistence.*;

/**
 * Artem Voytenko
 * 05.02.2019
 */

@Entity
@Table(name = "user")
public class UserDB {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "login")
	private String login;

	@Column(name = "password")
	private String password;

	public UserDB() {}

	public UserDB(String login, String password) {
		this.login = login;
		this.password = password;
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

	@Override
	public String toString() {
		return "UserDB{" +
				"id=" + id +
				", login='" + login + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}

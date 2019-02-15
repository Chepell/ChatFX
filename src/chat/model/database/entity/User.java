package chat.model.database.entity;

import java.io.Serializable;

/**
 * Artem Voytenko
 * 15.02.2019
 */

public class User implements Serializable, Comparable<User> {
	String login;
	boolean onlineStatus;

	public User(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public boolean isOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(boolean onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	@Override
	public String toString() {
		return "User{" +
				"login='" + login + '\'' +
				", onlineStatus=" + onlineStatus +
				'}';
	}

	@Override
	public int compareTo(User o) {
		return getLogin().toLowerCase().compareTo(o.getLogin().toLowerCase());
	}
}

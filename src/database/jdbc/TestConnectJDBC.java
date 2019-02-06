package database.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Artem Voytenko
 * 05.02.2019
 */

public class TestConnectJDBC {
	public static void main(String[] args) {
		// линк к нужной БД
		String jdbcUrl = "jdbc:mysql://localhost:3306/chat_base?useSLL=false&serverTimezone=UTC";
		String user = "springstudent";
		String password = "springstudent";

		try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
			System.out.println("Connecting to DB: " + jdbcUrl);
			System.out.println("Connection successful!!!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("DB connection close.");
	}
}

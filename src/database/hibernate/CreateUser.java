package database.hibernate;

import database.hibernate.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Artem Voytenko
 * 05.02.2019
 */

public class CreateUser {
	public static void main(String[] args) {
		// создание SessionFactory
		try (SessionFactory factory = new Configuration()
				.configure("hibernate.cfg.xml")
				.addAnnotatedClass(User.class)
				.buildSessionFactory()) {

			// открытие текущей сессии
			Session session = factory.getCurrentSession();

			// создание объекта
			User user = new User("chepell", "123456", true);

			// начало транзацкии
			session.beginTransaction();
			// сохранение объекта
			session.save(user);
			// комит транзакции в бд
			session.getTransaction().commit();
		}
	}
}

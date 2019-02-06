package database.hibernate;

import database.hibernate.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * Artem Voytenko
 * 05.02.2019
 */

public class GetUser {
	public static void main(String[] args) {
		// создание SessionFactory
		try (SessionFactory factory = new Configuration()
				.configure("hibernate.cfg.xml")
				.addAnnotatedClass(User.class)
				.buildSessionFactory()) {

			// открытие текущей сессии
			Session session = factory.getCurrentSession();

			// начало транзацкии
			session.beginTransaction();

			List<User> resultList = session.createQuery("from User u where u.login='admin'").getResultList();


			// комит транзакции в бд
			session.getTransaction().commit();

			// показать
			if (resultList.isEmpty()) {
				System.out.println("Пользователь не найден");
			} else {
				resultList.forEach(System.out::println);
			}
		}
	}
}

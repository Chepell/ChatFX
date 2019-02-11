package chat.model.database;

import chat.model.database.entity.MessageDB;
import chat.model.database.entity.User;
import chat.model.handlers.PropertiesHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Artem Voytenko
 * 06.02.2019
 */

// класс обработчик общения с БД в виде сингтона
public class DatabaseHandler {
	private static final PropertiesHandler properties = new PropertiesHandler("hibernate");
	private static volatile SessionFactory sessionFactory;

	private DatabaseHandler() {}

	/**
	 * метод первичной инициализации
	 */
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			Configuration hibernateConfig = new Configuration();
			hibernateConfig.setProperties(properties.loadProperties());

			// build session factory
			sessionFactory = hibernateConfig
					.addAnnotatedClass(User.class)
					.addAnnotatedClass(MessageDB.class)
					.buildSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * полное закрытие сессии
	 */
	public static void closeSessionFactory() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	/**
	 * получить всех пользователей из таблицы user в БД
	 *
	 * @return
	 */
	public static List<User> getAllUsers() {
		// открытие текущей сессии
		Session currentSession = getSessionFactory().openSession();

		// начало транзацкии
		currentSession.beginTransaction();

		// получени списка пользователей из БД
		List<User> users = currentSession.createQuery("FROM User", User.class).getResultList();

		// комит транзакции в бд
		currentSession.getTransaction().commit();

		// возвращаю результирующий список
		return users;
	}

	/**
	 * поиск пользователя в БД по логину
	 *
	 * @param login
	 * @return возвращает список состоящий из одного пользователея, либо пустой
	 */
	public static List<User> searchUser(String login) {
		// открытие текущей сессии
		Session currentSession = getSessionFactory().openSession();

		Query query;

		// начало транзацкии
		currentSession.beginTransaction();

		// поиск пользователя в БД по логину
		// binary для соблюдения чувствительности к регистру
		query = currentSession.createQuery("FROM User u WHERE u.login=binary(:login)");

		// вставка в запрос параметра метода
		query.setParameter("login", login);

		// выполняю запрос
		List<User> user = query.getResultList();

		// комит транзакции в бд
		currentSession.getTransaction().commit();

		// возвращаю результирующий список
		return user;
	}

	/**
	 * Метод сохраняет объект в таблицу user в БД
	 *
	 * @param user сущность для добалвения в БД
	 * @return индекс добавленного элемента, в случае успеха больше нуля
	 */
	// сохранение объекта в БД
	public static int saveUser(User user) {
		// открытие текущей сессии
		Session currentSession = getSessionFactory().openSession();

		// начало транзацкии
		currentSession.beginTransaction();
		// сохранение объекта
		int id = (Integer) currentSession.save(user);
		// комит транзакции в бд
		currentSession.getTransaction().commit();
		return id;
	}

	/**
	 * Метод сохраняет объект в таблицу messageDB в БД
	 *
	 * @param messageDB сущность для добавления в БД
	 * @return индекс добавленного элемента, в случае успеха больше нуля
	 */
	// сохранение объекта в БД
	public static int saveMessage(MessageDB messageDB) {
		// открытие текущей сессии
		Session currentSession = getSessionFactory().openSession();

		// начало транзацкии
		currentSession.beginTransaction();
		// сохранение объекта
		int id = (Integer) currentSession.save(messageDB);
		// комит транзакции в бд
		currentSession.getTransaction().commit();
		return id;
	}

	/**
	 * Запрос к таблице message
	 *
	 * @return возвращает список всех объектов
	 */
	public static List<MessageDB> getAllMessages() {
		// открытие текущей сессии
		Session currentSession = getSessionFactory().openSession();

		// начало транзацкии
		currentSession.beginTransaction();

		// получени списка пользователей из БД
		List<MessageDB> messageDBS = currentSession.createQuery("FROM MessageDB", MessageDB.class).getResultList();

		// комит транзакции в бд
		currentSession.getTransaction().commit();

		// возвращаю результирующий список
		return messageDBS;
	}

	/**
	 * получить всех онлайн юзеров
	 *
	 * @return
	 */
	public static List<User> getOnlineUsers() {
		// открытие текущей сессии
		Session currentSession = getSessionFactory().openSession();

		Query query;

		// начало транзацкии
		currentSession.beginTransaction();

		// поиск пользователя в БД по логину
		// binary для соблюдения чувствительности к регистру
		query = currentSession.createQuery("FROM User u WHERE u.online=:val");

		// вставка в запрос параметра метода
		query.setParameter("val", true);

		// выполняю запрос
		List<User> users = query.getResultList();

		// комит транзакции в бд
		currentSession.getTransaction().commit();

		// возвращаю результирующий список
		return users;
	}

	/**
	 * получить всех оффлайн юзеров
	 *
	 * @return
	 */
	public static List<User> getOfflineUsers() {
		// открытие текущей сессии
		Session currentSession = getSessionFactory().openSession();

		Query query;

		// начало транзацкии
		currentSession.beginTransaction();

		// поиск пользователя в БД по логину
		// binary для соблюдения чувствительности к регистру
		query = currentSession.createQuery("FROM User u WHERE u.online=:val");

		// вставка в запрос параметра метода
		query.setParameter("val", false);

		// выполняю запрос
		List<User> users = query.getResultList();

		// комит транзакции в бд
		currentSession.getTransaction().commit();

		// возвращаю результирующий список
		return users;
	}

	/**
	 * сменить статус найденному пользователю
	 *
	 * @param login
	 * @param isOnline
	 */
	public static void setUserStatus(String login, boolean isOnline) {
		// открытие текущей сессии
		Session currentSession = getSessionFactory().openSession();

		Query query;

		// начало транзацкии
		currentSession.beginTransaction();

		// поиск пользователя в БД по логину
		// binary для соблюдения чувствительности к регистру
		query = currentSession.createQuery("UPDATE User SET online = :status WHERE login = binary(:login)");

		// вставка в запрос параметра метода
		query.setParameter("login", login);
		query.setParameter("status", isOnline);

		// выполняю запрос
		query.executeUpdate();

		// комит транзакции в бд
		currentSession.getTransaction().commit();
	}
}
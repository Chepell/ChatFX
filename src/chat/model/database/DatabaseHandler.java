package chat.model.database;

import chat.model.database.entity.MessageDB;
import chat.model.database.entity.User;
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
	private static volatile SessionFactory sessionFactory;

	private DatabaseHandler() {}

	/**
	 * метод первичной инициализации
	 */
	public static void initSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = new Configuration()
					.configure("hibernate.cfg.xml")
					.addAnnotatedClass(User.class)
					.addAnnotatedClass(MessageDB.class)
					.buildSessionFactory();
		}
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
		// создаю синглтон если еще не создан
		if (sessionFactory == null) initSessionFactory();

		// открытие текущей сессии
		Session currentSession = sessionFactory.getCurrentSession();

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
		// создаю синглтон если еще не создан
		if (sessionFactory == null) initSessionFactory();

		// открытие текущей сессии
		Session currentSession = sessionFactory.getCurrentSession();
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
		// создаю синглтон если еще не создан
		if (sessionFactory == null) initSessionFactory();

		// открытие текущей сессии
		Session currentSession = sessionFactory.getCurrentSession();
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
		// создаю синглтон если еще не создан
		if (sessionFactory == null) initSessionFactory();

		// открытие текущей сессии
		Session currentSession = sessionFactory.getCurrentSession();
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
		// создаю синглтон если еще не создан
		if (sessionFactory == null) initSessionFactory();

		// открытие текущей сессии
		Session currentSession = sessionFactory.getCurrentSession();

		// начало транзацкии
		currentSession.beginTransaction();

		// получени списка пользователей из БД
		List<MessageDB> messageDBS = currentSession.createQuery("FROM MessageDB", MessageDB.class).getResultList();

		// комит транзакции в бд
		currentSession.getTransaction().commit();

		// возвращаю результирующий список
		return messageDBS;
	}

	//TODO метод некорректен, онлай статус так не определяется

	/**
	 * получить всех онлайн юзеров
	 *
	 * @return
	 */
	public static List<User> getOnlineUsers() {
		// создаю синглтон если еще не создан
		if (sessionFactory == null) initSessionFactory();

		// открытие текущей сессии
		Session currentSession = sessionFactory.getCurrentSession();
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
		// создаю синглтон если еще не создан
		if (sessionFactory == null) initSessionFactory();

		// открытие текущей сессии
		Session currentSession = sessionFactory.getCurrentSession();
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
		// создаю синглтон если еще не создан
		if (sessionFactory == null) initSessionFactory();

		// открытие текущей сессии
		Session currentSession = sessionFactory.getCurrentSession();
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

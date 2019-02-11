package chat.model.database;

import chat.model.database.entity.MessageDB;

import java.util.List;

/**
 * Artem Voytenko
 * 06.02.2019
 */

public class TestHibernate {
	public static void main(String[] args) {

//		SessionFactory sessionFactory = DatabaseHandler.getSessionFactory();

//		List<User> list = DatabaseHandler.getAllUsers();
//
//		Collections.sort(list, Comparator
//				.comparing(User::isOnline, Comparator.reverseOrder())
//				.thenComparing(User::getLogin)
//		);
//
//		System.out.println();
//		System.out.println();
//		list.forEach(System.out::println);


//		MessageDB messageDB = new MessageDB("admin", "Все норм! А ты как?");

//		System.out.println(messageDB);


//		DatabaseHandler.saveMessage(messageDB);


		List<MessageDB> allMessageDBS = DatabaseHandler.getAllMessages();

		String s = historyMessagesFromDB(allMessageDBS);


		System.out.println(s);

//		DatabaseHandler.closeSessionFactory();


	}

	private static String historyMessagesFromDB(List<MessageDB> list) {
		StringBuilder builder = new StringBuilder();
		for (MessageDB messageDB : list) {
			builder.append(messageDB).append("\n");
		}
		return builder.toString();
	}

}

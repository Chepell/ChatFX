package chat.model.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// вспомогательный класс для чтения/записи в консоль
public class ConsoleHelper {
	// статическое поле читалки из консоли
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * метод для вывода сообщения на консоль
	 *
	 * @param message
	 */
	public static void writeMessage(String message) {
		System.out.println(message);
	}

	/**
	 * чтение строки из консоли
	 *
	 * @return
	 */
	public static String readString() {
		// вечный цикл, который повторяется пока не будет считана строка
		// если строка будет успешно считана, то тут же произойтет ее возврат и цикл завершится
		while (true) {
			try {
				return reader.readLine().trim();
			} catch (IOException e) {
				System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
			}
		}
	}

	/**
	 * чтение целого числа из консоли
	 *
	 * @return
	 */
	public static int readInt() {
		// опять вечный цикл
		while (true) {
			try {
				// использую уже готовый метод чтения строки
				// и пробую отпарсить в число, если получилось то сразу возврат
				return Integer.parseInt(reader.readLine().trim());
			} catch (IOException e) {
				System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
			}
		}
	}
}
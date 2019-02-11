package chat.model.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesHandler {
	// имя файла по умолчанию
	private String file;
	// мэп для хранения свойств полученных из файла
	private Properties properties = new Properties();


	/**
	 * получить объект всех свойств
	 * @return
	 */
	public Properties loadProperties() {
		return properties;
	}

	/**
	 * конструктор
	 *
	 * @param fileName имя файла (без расширения)
	 */
	public PropertiesHandler(String fileName) {

		file = "resources/" + fileName + ".properties";
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file))) {
			properties.load(reader);
		} catch (IOException e) {
			System.out.println("Не могу прочитать файл " + file);
		}
	}

	/**
	 * метод получение свойства из файла по имени
	 *
	 * @param propertiesName
	 * @return
	 */
	public String getProperty(String propertiesName) {
		return properties.getProperty(propertiesName);
	}

	/**
	 * метод для изменения параметра в файле
	 *
	 * @param propertiesName имя свойства
	 * @param value          новое значение
	 */
	public void setProperty(String propertiesName, String value) {
		properties.setProperty(propertiesName, value);
	}

	/**
	 * метод возвращает свойство в виде числа
	 *
	 * @param propertiesName имя свойства
	 * @return
	 */
	public int getIntProperty(String propertiesName) {
		return Integer.parseInt(properties.getProperty(propertiesName));
	}

	/**
	 * Метод производит сохранение настроек из поля properties в файл и закрытие потока
	 */
	public void savePropertiesToFile() {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file))) {
			properties.store(writer, null);
		} catch (IOException e) {
			System.out.println("Не могу записать обновленные свойства в файл " + file);
		}
	}
}

package chat;

import chat.model.handlers.PropertiesHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Artem Voytenko
 * 05.02.2019
 */

public class AboutController {
	private PropertiesHandler properties = new PropertiesHandler("client");

	@FXML
	private Label authorLabel;

	@FXML
	private Label versionLabel;

	@FXML
	private Label yearLabel;

	@FXML
	void initialize() {
		// получаю значение полей из файла
		String author = properties.getProperty("author");
		String version = properties.getProperty("version");
		String year = properties.getProperty("year");

		// устанавливаю значения в метках
		authorLabel.setText(author);
		versionLabel.setText(version);
		yearLabel.setText(year);
	}
}

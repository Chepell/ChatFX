package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Artem Voytenko
 * 12.02.2019
 */

public class Test {
	public static void main(String[] args) throws IOException {
		InputStream is = Test.class.getResourceAsStream("client.properties");

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String s;
		while ((s = reader.readLine()) != null) {
			System.out.println(s);
		}


	}
}

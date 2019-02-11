package chat.model.handlers;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

// Клиент и сервер будут общаться через сокетное соединение.
// Одна сторона будет записывать данные в сокет, а другая читать.
// Их общение представляет собой обмен сообщениями MessageDB.
// Класс Connection будет выполнять роль обертки над классом java.net.Socket,
// которая должна будет уметь сериализовать и десериализовать объекты типа MessageDB в сокет.
// Методы этого класса должны быть готовы к вызову из разных потоков.

// соединение между клиентом и сервером
public class Connection implements Closeable {
	private final Socket socket;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;


	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		// инициалиазация потоков в конструкторе на основе socket
		// Создать объект класса ObjectOutputStream нужно до того, как будет создаваться объект класса
		// ObjectInputStream, иначе может возникнуть взаимная блокировка потоков,
		// которые хотят установить соединение через класс Connection.
		out = new ObjectOutputStream(this.socket.getOutputStream());
		in = new ObjectInputStream(this.socket.getInputStream());
	}

	/**
	 * метод отправки объекта через сокет
	 *
	 * @param message
	 * @throws IOException
	 */
	public void send(Message message) throws IOException {
		synchronized (out) {
			out.writeObject(message);
		}
	}

	/**
	 * метод получения данных в виде объекта MessageDB из сокета
	 *
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Message receive() throws IOException, ClassNotFoundException {
		synchronized (in) {
			return (Message) in.readObject();
		}
	}

	/**
	 * Метод возвращающий удаленный адрес сокетного соединения
	 *
	 * @return
	 */
	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}

	/**
	 * реализация метода интерфейса для автоматического закрытия
	 * ресурсов при использовании конструкции try-with-resources
	 *
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException {
		in.close();
		out.close();
		socket.close();
	}
}



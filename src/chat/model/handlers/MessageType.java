package chat.model.handlers;

// перечисление которое отвечает за тип сообщений
// пересылаемых между клиентом и сервером
public enum MessageType {
	// Когда новый клиент хочет подсоединиться к серверу, сервер должен запросить имя клиента
	SERVER_CONNECT_REQUEST,

	// Когда клиент получает запрос имени от сервера он должен отправить свое имя и пароль серверу
	CLIENT_CONNECT_RESPONSE,

	// Когда сервер получает имя клиента и пароль он в случае успешной проверки
	// значений в таблице пользователей в БД должен принять это имя или запросить новое
	SERVER_USER_ACCEPTED,

	// Когда сервер получает имя нового пользователя и пароль он в случае успешной проверки остутствия такого логина
	// в БД добавляет его
	CLIENT_ADD_USER_IN_DB,

//	// пользователь запрашивает список юзеров с сервера
//	CLIENT_REQUEST_USER_LIST,
//
//	// сервер возвращает клиенту список пользователей
//	SERVER_RESPONSE_USER_LIST,

	// сервер отправляет сообщение конкретному пользововатлю, что юзер успешно добавлен в БД
	SERVER_USER_SUCCESSFULLY_ADD_IN_DB,

	// сервер отправляет пользователю сообщение если добавляемы юзер уже есть в БД
	SERVER_USER_ALREADY_EXIST_IN_DB,

	// сервер отправляет пользователю сообщение, что произошла ошибка при добавлении юзера в БД
	SERVER_USER_ADDING_ERROR_IN_DB,

	// когда юзер хочет удалиться он сообщает об этом серверу
	CLIENT_DISCONNECT_REQUEST,

	// Когда новый клиент добавился к чату, сервер должен сообщить остальным участникам о новом клиенте
	SERVER_USER_ONLINE,

	// Когда клиент покидает чат, сервер должен сообщить остальным участникам об этом
	SERVER_USER_OFFLINE,

	// Когда сервер получает текстовое сообщение от клиента, он должен переслать его всем остальным участникам чата
	CLIENT_SEND_MESSAGE,
}

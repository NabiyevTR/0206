package ru.geekbrains.ntr_0206.server;

import ru.geekbrains.ntr_0206.helpers.ConsoleHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        int port = ConsoleHelper.readInt("Введите номер порта:");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Сервер запущен");
            Socket socket = serverSocket.accept();
            ConsoleHelper.writeMessage("Подключен клиент" + socket.getRemoteSocketAddress());
            try (Connection connection = new Connection(socket)) {
                // Отправка сообщения
                new Thread(() -> {
                    while (true) {
                        String message = ConsoleHelper.readString();
                        try {
                            if (message.equals("/exit")) {
                                connection.send("Server: Сервер отключен");
                                break;
                            }
                            connection.send("Server: " + message);
                            ConsoleHelper.writeMessage("Client: " + message);
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при попытке отправить сообщение.");
                        }
                    }
                }
                ).start();

                // Прием сообщения
                new Thread(() -> {
                    while (true) {
                        String message = null;
                        try {
                            message = connection.receive();
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при получении сообщения от клиента.");
                        }
                        try {
                            connection.send("Client: " + message);
                            ConsoleHelper.writeMessage("Client: " + message);
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при отправке сообщения клиенту.");
                        }
                    }
                }
                ).start();
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Соединение с клиентом разорвано.");
            }
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Ошибка при запуске сервера. Программа остановлена.");
            return;
        }
    }
}

package ru.geekbrains.ntr_0206.server;

import ru.geekbrains.ntr_0206.helpers.ConsoleHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static Thread recieve;
    private static Thread transmit;
    private static int port;


    public static void main(String[] args) {

        port = ConsoleHelper.readInt("Введите номер порта:");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Сервер запущен");
            Socket socket = serverSocket.accept();
            ConsoleHelper.writeMessage("Подключен клиент " + socket.getRemoteSocketAddress());
            try (Connection connection = new Connection(socket)) {
                // Прием сообщения
                recieve = new Thread(() -> {
                    while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                        String message = null;
                        try {
                            message = connection.receive();
                            if (message == null) continue;
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при получении сообщения от клиента.");
                            transmit.interrupt();
                            break;
                        }
                        try {
                            connection.send("Client: " + message);
                            ConsoleHelper.writeMessage("Client: " + message);
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при отправке сообщения клиенту.");
                            transmit.interrupt();
                            break;
                        }
                    }
                }
                );

                // Отправка сообщения
                transmit = new Thread(() -> {
                    while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                        String message = ConsoleHelper.readString();
                        try {
                            if (message.equals("/exit")) {
                                connection.send("Server: Сервер отключен");
                                recieve.interrupt();
                                break;
                            }
                            connection.send("Server: " + message);
                            ConsoleHelper.writeMessage("Server: " + message);
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при попытке отправить сообщение.");
                            recieve.interrupt();
                        }
                    }
                }
                );

                transmit.start();
                recieve.setDaemon(true);
                recieve.start();

                try {
                    transmit.join();
                } catch (InterruptedException e) {
                    ConsoleHelper.writeMessage("Сервер остановлен.");
                }

            } catch (IOException e) {
                ConsoleHelper.writeMessage("Соединение с клиентом разорвано.");
            }
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Ошибка при запуске сервера. Программа остановлена.");
            return;
        }
    }
}

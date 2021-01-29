package ru.geekbrains.ntr_0206.client;

import ru.geekbrains.ntr_0206.helpers.ConsoleHelper;


import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {

        String ipAddress = ConsoleHelper.readString("Введите IP-адрес сервера:");
        int port = ConsoleHelper.readInt("Введите номер порта:");

        try (Socket socket = new Socket(ipAddress, port)) {
            ConsoleHelper.writeMessage("Клиент подключился к серверу " + socket.getRemoteSocketAddress());
            try (Connection connection = new Connection(socket)) {
                // Отправка сообщения
                Thread transmit = new Thread(() -> {
                    while (!socket.isClosed()) {
                        String message = ConsoleHelper.readString();

                        try {
                            if (message.equals("/exit")) {
                                connection.send("Соединение с клиентом разорвано");
                                break;
                            }
                            connection.send(message);
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при попытке отправить сообщение.");
                        }
                    }
                }
                );

                // Прием сообщения
                Thread recieve = new Thread(() -> {
                    while (!socket.isClosed()) {
                        try {
                            ConsoleHelper.writeMessage(connection.receive());
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при получении сообщения от сервера.");
                        }
                    }
                }
                );

                transmit.start();
                recieve.start();

                try {
                    transmit.join();
                    recieve.join();
                } catch (InterruptedException e) {
                    ConsoleHelper.writeMessage("Аварийная остановка выполнения программы");
                }


            } catch (IOException e) {
                ConsoleHelper.writeMessage("Соединение с сервером разорвано.");
            }
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Ошибка при подключении к серверу");
            return;
        }

    }
}


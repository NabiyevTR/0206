package ru.geekbrains.ntr_0206.client;

import ru.geekbrains.ntr_0206.helpers.ConsoleHelper;


import java.io.IOException;
import java.net.Socket;

public class Client {
    private static Thread recieve;
    private static Thread transmit;
    private static String ipAddress;
    private static int port;

    public static void main(String[] args) {

        ipAddress = ConsoleHelper.readString("Введите IP-адрес сервера:");
        port = ConsoleHelper.readInt("Введите номер порта:");

        try (Socket socket = new Socket(ipAddress, port)) {
            ConsoleHelper.writeMessage("Клиент подключился к серверу " + socket.getRemoteSocketAddress());
            try (Connection connection = new Connection(socket)) {

                // Прием сообщения
                recieve = new Thread(() -> {
                    while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                        try {
                            ConsoleHelper.writeMessage(connection.receive());
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при получении сообщения от сервера.");
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
                                recieve.interrupt();
                                break;
                            }
                            connection.send(message);
                        } catch (IOException e) {
                            ConsoleHelper.writeMessage("Произошла ошибка при попытке отправить сообщение.");
                            recieve.interrupt();
                            break;
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
                    ConsoleHelper.writeMessage("Работа приложения завершена");
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


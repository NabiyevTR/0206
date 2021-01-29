package ru.geekbrains.ntr_0206.server;

import java.io.*;
import java.net.Socket;

class Connection implements Closeable {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public  void send(String message) throws IOException {
        out.writeUTF(message);
    }

    public   String receive() throws IOException {
        return in.readUTF();
    }

    @Override
    public void close() throws IOException {
      socket.close();
      out.close();
      in.close();
    }
}


package ru.geekbrains.ntr_0206.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

    }

    public synchronized void send(String message) throws IOException {
        out.writeUTF(message);
    }

    public synchronized String receive() throws IOException {
        return  in.readUTF();
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException {
        socket.close();
        out.close();
        in.close();
    }
}

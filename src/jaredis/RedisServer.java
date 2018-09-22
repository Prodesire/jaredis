package jaredis;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class RedisServer {
    private static final int PORT = 6379;


    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println(String.format("Serving jaredis on http://localhost:%d", PORT));

            while (true) {
                Socket socket = serverSocket.accept();

                InputStream in = socket.getInputStream();
                handleRequest(in);

                socket.close();
            }
        } catch (Exception e) {
            System.out.println("ProtocolException:" + e);
        }
    }

    private static void handleRequest(InputStream input) throws IOException, ProtocolException {
        System.out.println("---- Handle request ------");
        int cmdLen = readInt(input, '*');
        String[] cmd = new String[cmdLen];
        for (int i = 0; i < cmdLen; i++) {
            int argLen = readInt(input, '$');
            String arg = readString(input, argLen);
            cmd[i] = arg;
            System.out.println(arg);
        }
    }

    private static int readInt(InputStream input, char start) throws IOException, ProtocolException {
        StringBuilder sb = new StringBuilder();
        byte b = (byte) input.read();
        int i;
        String istr;

        if ((char) b != start) {
            throw new ProtocolException(String.format("Require '%s' when parsing int", start));
        }

        while ((b = (byte) input.read()) != -1) {
            if (b != '\r') {
                sb.append((char) b);
            } else if ( (byte) input.read() == '\n'){
                break;
            } else {
                throw new ProtocolException("CRLF not complete");
            }
        }

        istr = sb.toString();
        try {
            i = Integer.parseInt(istr);
        } catch (NumberFormatException e) {
            throw new ProtocolException(String.format("Convert '%s' to int fail", istr));
        }

        return i;
    }

    private static String readString(InputStream input, int len) throws IOException, ProtocolException{
        byte[] bytes = new byte[len];
        input.read(bytes, 0, len);

        if (input.read() != '\r') {
            throw new ProtocolException("CRLF not supply");
        } else if (input.read() != '\n') {
            throw new ProtocolException("CRLF not complete");
        }
        return new String(bytes);
    }
}

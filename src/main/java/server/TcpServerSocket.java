package server;

import util.PropertyReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by bk on 11/04/2017.
 */
public class TcpServerSocket {

    private PropertyReader propertyReader;
    ServerSocket serverSocket;

    public TcpServerSocket() {
        propertyReader = new PropertyReader();
    }

    public void run() {
        String port = "";
        try {
            port = propertyReader.getProperty("server-port");

            serverSocket = new ServerSocket(Integer.parseInt(port));
            serverSocket.setSoTimeout(20000);

            while (true) {
                System.out.println("waiting for client connection on port " + serverSocket.getLocalPort());
                Socket server = serverSocket.accept();
                OutputStream outputStream = server.getOutputStream();
                PrintWriter toClient =
                        new PrintWriter(outputStream,true);
                BufferedReader fromClient =
                        new BufferedReader(
                                new InputStreamReader(server.getInputStream()));
                String line = fromClient.readLine();
                System.out.println("Server received: " + line);
                toClient.println("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!");

                BufferedImage image = ImageIO.read(new File("./resource/ex.jpeg"));

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpeg", byteArrayOutputStream);

                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                outputStream.write(size);
                outputStream.write(byteArrayOutputStream.toByteArray());
                outputStream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TcpServerSocket server = new TcpServerSocket();
        server.run();
    }
}

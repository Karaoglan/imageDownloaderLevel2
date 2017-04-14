package client;

import util.PropertyReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by bk on 11/04/2017.
 */
public class TcpClientSocket {

    private PropertyReader propertyReader;

    public TcpClientSocket() {
        propertyReader = new PropertyReader();
    }

    public void run() {
        try {
            int serverPort = Integer.parseInt(propertyReader.getProperty("server-port"));
            InetAddress host = InetAddress.getByName("localhost");
            System.out.println("Connecting to server on port " + serverPort);

            Socket socket = new Socket(host, serverPort);
            //Socket socket = new Socket("127.0.0.1", serverPort);
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());
            PrintWriter toServer =
                    new PrintWriter(socket.getOutputStream(),true);
            InputStream inputStream = socket.getInputStream();
            BufferedReader fromServer =
                    new BufferedReader(
                            new InputStreamReader(inputStream));
            toServer.println("Hello from " + socket.getLocalSocketAddress());
            String line = fromServer.readLine();
            System.out.println("Client received: " + line + " from Server");

            byte[] sizeAr = new byte[4];
            inputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            inputStream.read(imageAr);

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

            System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
            ImageIO.write(image, "jpg", new File("./resource/exWrite.jpg"));


            toServer.close();
            fromServer.close();
            socket.close();
        }
        catch(UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TcpClientSocket tcpClientSocket = new TcpClientSocket();
        tcpClientSocket.run();
    }

}

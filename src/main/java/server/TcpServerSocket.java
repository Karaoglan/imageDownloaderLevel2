package server;

import util.PropertyReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bk on 11/04/2017.
 */
public class TcpServerSocket {

    private PropertyReader propertyReader;
    ServerSocket serverSocket;
    Socket server;


    public TcpServerSocket() {
        propertyReader = new PropertyReader();
    }

    public void run() {
        String port;
        try {
            //read port information from resource folder
            port = propertyReader.getProperty("server-port");

            //open connection socket in port
            serverSocket = new ServerSocket(Integer.parseInt(port));
            serverSocket.setSoTimeout(50000);

            while (true) {
                System.out.println("waiting for client connection on port " + serverSocket.getLocalPort());
                server = serverSocket.accept();
                System.out.println("client connected!");

                sendBytes(extractBytesFromImages(), 100);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<byte[]> extractBytesFromImages() throws IOException {

        List<byte[]> list = new ArrayList<>();

        File f=new File("./resource");
        File[] allSubFiles = f.listFiles();

        for (File file : allSubFiles) {
            // open image
            BufferedImage image = ImageIO.read(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(image, "png", outputStream);
            outputStream.flush();

            byte[] data = outputStream.toByteArray();
            System.out.println("first image byte size:" + data.length);
            outputStream.close();

            list.add(data);
        }

        return list;
    }

    public static void main(String[] args) {
        TcpServerSocket server = new TcpServerSocket();
        server.run();
    }

    public int calculateTotalPackage(List<byte[]> myByteArrayList, int packageLength) {
        int packageSize = 0;
        for (byte[] byteArr : myByteArrayList) {
            double totalNumberOfPackagesDouble = (double)byteArr.length / (double)packageLength;
            packageSize += (int) Math.ceil(totalNumberOfPackagesDouble);
        }
        return packageSize;
    }

    /**
     * sends image as a byte array divided into smaller packets given by package length
     *
     * @param myByteArrayList list of all images as a byte array
     * @param packageLength
     * @throws IOException
     */
    public void sendBytes(List<byte[]> myByteArrayList, int packageLength) throws IOException {

        OutputStream out = server.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        byte imageIndex = 0;

        //first write how many packages will be send
        dos.writeInt(calculateTotalPackage(myByteArrayList, packageLength));

        for (byte[] byteArr : myByteArrayList) {

            double totalNumberOfPackagesDouble = (double)byteArr.length / (double)packageLength;
            int totalNumberOfPackagesForOneImage = (int) Math.ceil(totalNumberOfPackagesDouble);

            for (int i = 0; i < totalNumberOfPackagesForOneImage; i++) {
                int index = 0;
                byte[] array;

                //last package, determine length of byte
                if (i == totalNumberOfPackagesForOneImage - 1) {
                    int moduloForLastPackage = byteArr.length % packageLength;
                    int arrayLength = (moduloForLastPackage == 0) ? packageLength + 1 : moduloForLastPackage + 1;
                    array = new byte[arrayLength];
                } else {
                    array = new byte[packageLength + 1];
                }

                array[0] = imageIndex;

                //create package
                for (int j = 1; j < array.length; j++) {

                    array[j] = byteArr[index];
                    index++;
                }

                System.out.println("first elem in arr :" + array[0]);

                //then write each byte for each package
                sendBytes(array, 0, array.length, dos);
            }

            imageIndex++;
        }

        dos.close();
        out.close();
    }

    /**
     *
     * write bytes by given length into client socket - dataoutputstream
     *
     * @param myByteArray
     * @param start
     * @param len
     * @param dos
     * @throws IOException
     */
    public void sendBytes(byte[] myByteArray, int start, int len,
                          DataOutputStream dos) throws IOException {
        if (len < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if (start < 0 || start >= myByteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);

        dos.writeInt(len);
        if (len > 0) {
            dos.write(myByteArray, start, len);
        }

        dos.flush();
    }
}

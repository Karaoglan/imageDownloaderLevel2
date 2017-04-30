package client;

import org.apache.commons.io.FileUtils;
import util.Helper;
import util.PropertyReader;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());

            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);

            List<byte[]> bytesReadList = readBytes(dis);
            InputStream inputStreamForImage = null;

            int counterForImageName = 0;
            for (byte[] byteRead : bytesReadList) {
                System.out.println("read byte array length :" +byteRead.length);
                inputStreamForImage = new ByteArrayInputStream(byteRead);
                String pathStr = "./output/convertedPic"+ counterForImageName +".png";

                File fileDirectory = new File("./output");
                if (!fileDirectory.exists()) {
                    FileUtils.forceMkdir(fileDirectory);
                }

                Path path = Paths.get(pathStr);
                Files.write(path, byteRead);

                counterForImageName++;
            }

            inputStreamForImage.close();
            dis.close();
            in.close();
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

    public List<byte[]> readBytes(DataInputStream dataInputStream) throws IOException {

        Map<Byte, List<byte[]>> mapOfBytesList = new HashMap<>();

        List<byte[]> bytesList = new ArrayList<>();

        int totalOfPackages = dataInputStream.readInt();
        System.out.println("total :"+ totalOfPackages);

        while (totalOfPackages > 0) {
            int len = dataInputStream.readInt();
            System.out.println("length of byte arr :" +len);
            byte[] data = new byte[len];
            if (len > 0) {
                dataInputStream.readFully(data);
            }
            System.out.println("read first element :" + data[0]);
            byte[] byteArr = Arrays.copyOfRange(data, 1, data.length);
            if (mapOfBytesList.containsKey(data[0])) {
                mapOfBytesList.get(data[0]).add(byteArr);
            } else {
                bytesList.add(byteArr);
                mapOfBytesList.put(data[0], bytesList);
                bytesList = new ArrayList<>();
            }
            totalOfPackages --;
        }


        return generateListFromMap(mapOfBytesList);

    }


    public List<byte[]> generateListFromMap(Map<Byte, List<byte[]>> mapOfBytesList) {
        List<byte[]> generatedList = new ArrayList<>();

        for ( Byte byteKey : mapOfBytesList.keySet()) {
            List<byte[]> listByte = mapOfBytesList.get(byteKey);
            generatedList.add(Helper.convertListToByteArr(listByte));
        }

        return generatedList;
    }

}

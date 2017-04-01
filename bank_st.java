import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Dong on 17/3/31.
 */
public class bank_st {
    public static void main(String argv[]) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(6666);
            //serverSocket.setSoTimeout(8000);
            System.out.println("Waiting for client on port " +
                    serverSocket.getLocalPort() + "...");
            Socket server = serverSocket.accept();
            System.out.println("Just connected to " + server.getRemoteSocketAddress());
            DataInputStream in = new DataInputStream(server.getInputStream());

            System.out.println("Received from client");

            String receive = in.readUTF();
            System.out.println(receive);
            BufferedWriter bw = null;
            FileWriter fw = null;
            fw = new FileWriter("/Users/Dong/Desktop/receive.xml");
            bw = new BufferedWriter(fw);
            bw.write(receive);
            bw.flush();



        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parser p = new Parser();
        p.parse("/Users/Dong/Desktop/receive.xml","/Users/Dong/Desktop/output.xml");//input xml and output xml,could be modified with your setting
        return;
    }
}

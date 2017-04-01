import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Dong on 17/4/1.
 */
public class client {
    public static void main(String argv[]) {
        try {
            File file = new File("/Users/Dong/Desktop/c.xml");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            fileReader.close();
            System.out.println("Contents of file:");
            String content = stringBuffer.toString();
            System.out.println(content);
            System.out.println("Connecting to " + "127.0.0.1" + " on port " + 6666);
            Socket client = new Socket("127.0.0.1", 6666);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF(content);
//            InputStream inFromServer = client.getInputStream();
//            DataInputStream in = new DataInputStream(inFromServer);
//
//            System.out.println("Server says " + in.readUTF());
            client.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

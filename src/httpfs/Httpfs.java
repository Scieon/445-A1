package httpfs;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Httpfs {
    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(999);
        Socket SocketConnection;
        System.out.println("Serer is running...");

        while (true) {
            SocketConnection = s.accept();
            InputStreamReader in =  new InputStreamReader(SocketConnection.getInputStream());
            BufferedReader reader = new BufferedReader(in);
            String line = reader.readLine();

            while (line != null && !line.isEmpty()) {
                System.out.println(line);
                line = reader.readLine();
            }

        }
    }
}
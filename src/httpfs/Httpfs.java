package httpfs;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Httpfs {

    private final String HTTP_ERROR_404 = "HTTP/1.1 404 not found\r\n";
    private final String HTTP_SUCCESS_200 = "HTTP/1.1 200 OK\r\n";
    private boolean debug = false;
    private int port = 999; // todo change to 8080 for submission
    private String PATH = "./src/httpfs/data/";

    public static void main(String[] args) throws IOException {
        new Httpfs().run(args);

    }

    private void run(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socketConnection;

        System.out.println("Server is running...");


        debug = true; // todo set debug through args
        // todo Also set PORT!
        // todo set port if specified

        while (true) {
            socketConnection = serverSocket.accept();
            InputStreamReader in = new InputStreamReader(socketConnection.getInputStream());
            BufferedReader reader = new BufferedReader(in);
            String line = reader.readLine();
            String urlPath = "";

            while (line != null && !line.isEmpty()) {
                if (line.contains("GET") || line.contains("POST")) {
                    urlPath = line.substring(line.indexOf("/") + 1, line.indexOf("HTTP/"));
                }
                line = reader.readLine();
            }

            if (!urlPath.equals("")) {
                PrintWriter out = new PrintWriter(socketConnection.getOutputStream(), true);
                handleGetPath(out, serverSocket, urlPath);
                socketConnection.close();
            }
        }
    }

    /**
     * Handles GET route with resource
     *
     * @param out output stream
     * @param serverSocket
     * @param urlPath file path resource
     */
    private void handleGetPath(PrintWriter out, ServerSocket serverSocket, String urlPath) {
        if (printFileContent(urlPath)) {
            out.print(HTTP_SUCCESS_200);
            out.print("\r\n");
            out.println();
//            socketConnection.close();
        } else {
            // File does not exist
            out.print(HTTP_ERROR_404);
            out.print("\r\n");
            out.println();
//            socketConnection.close();
        }
    }

    /**
     * Prints content of file
     *
     * @param filePath - Path of url
     * @return true if file exists
     */
    private boolean printFileContent(String filePath) {
        try {
            filePath = filePath.trim();
            Scanner sc = new Scanner(new FileInputStream(PATH + filePath + ".txt"));
            String line = sc.nextLine();

            while (sc.hasNextLine()) {
                System.out.println(line);
                line = sc.nextLine();
            }
        } catch (FileNotFoundException e) {
            if (debug) {
                System.out.println("File '" + filePath + "' was not found.");
            }
            return false;
        }
        return true;
    }

}
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class Httpc {

    private final String USER_AGENT = "Concordia-HTTP/1.0";
    private final int PORT = 80;

    private Map<String, String> headerArgs = new HashMap<>();
    private String data;

    public static void main(String[] args) {
        new Httpc().run(args);
    }

    private void run(String[] args) {

        List<String> argsList = new ArrayList<>(Arrays.asList(args));

        if (args[0].equals("help")) {
            if (args.length == 1) {
                help();
            } else if (args.length == 2) {
                help(args[1]);
            }
        }

        try {
            printArgs(args);
            String url = args[args.length - 1];
            getHeaderArguments(args);

            if (args[0].equals("get")) {
                sendGet(url, args[1].equals("-v"));
            }

            if (args[0].equals("post")) {
                if (argsList.contains("-f") && argsList.contains("-d")) {
                    throw new Exception("Cannot have -f and -d");
                }
                if (argsList.contains("-d")) {
                    setData(args);
                } else if (argsList.contains("-f")) {
                    parseFile(argsList.get(argsList.indexOf("-f") + 1));
                }
                sendPost(url, args[1].equals("-v"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // TODO REMOVE TEST PRINT
    private void printArgs(String[] args) {
        System.out.println("_____");
        for (int i = 0; i < args.length; i++) {
            System.out.println(i + args[i]);
        }
        System.out.println("_____");
    }

    private void parseFile(String file) {
        // pass the path to the file as a parameter
        try {
            Scanner sc = new Scanner(new FileInputStream("../" + file));
            this.data = sc.nextLine();
            sc.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setData(String[] args) {
        List<String> argsList = new ArrayList<>(Arrays.asList(args));

        if (this.headerArgs.get("Content-Type").equals("application/json") || this.headerArgs.get("Content-Type").equals("application/x-www-form-urlencoded")) {
            this.data = argsList.get(argsList.indexOf("-d") + 1);
        }
    }


    /**
     * Retrieve all header arguments
     *
     * @param args cli input
     */
    private void getHeaderArguments(String args[]) {
        List<String> argsList = new ArrayList<>(Arrays.asList(args));

        int endOfHeaders = args.length - 1; // Stop at url

        if (argsList.indexOf("-f") != -1) {
            endOfHeaders = argsList.indexOf("-f");
        }

        if (argsList.indexOf("-d") != -1) {
            endOfHeaders = argsList.indexOf("-d");
        }

        if (args[1].equals("-h") || args[1].equals("-v") && args[2].equals("-h")) {
            int index = argsList.indexOf("-h");

            // Begin after '-h' and all the headers stop at -f -d or url
            for (index += 1; index < endOfHeaders; index++) {
                String[] header = args[index].split(":");
                headerArgs.put(header[0], header[1]);
            }
        }
    }

    private void sendGet(String url, boolean verbose) throws Exception {
        URL urlObj = new URL(url);
        String hostname = urlObj.getHost();

        Socket s = new Socket(hostname, PORT);
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        out.print("GET /get?course=networking&assignment=1 HTTP/1.0\r\n");
        out.print("Host: " + hostname + "\r\n");
        out.print("User-agent: " + USER_AGENT + "\r\n");

        for (Map.Entry<String, String> entry : this.headerArgs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            out.print(key + ": " + value + "\r\n");
        }
        out.print("\r\n");
        out.println();

        // Read server response
        String response = getResponse(in).toString();

        if (!verbose) {
            response = response.substring(response.indexOf("{"));
        }

        System.out.println(response);
    }

    private StringBuilder getResponse(BufferedReader in) throws Exception {
        StringBuilder sb = new StringBuilder(8096);
        boolean loop = true;

        while (loop) {
            if (in.ready()) {
                String line = in.readLine();
                while (line != null) {
                    if (!line.isEmpty()) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    line = in.readLine();
                }
                loop = false;
            }
        }

        return sb;
    }

    private void sendPost(String url, boolean verbose) throws Exception {
        URL urlObj = new URL(url);
        String hostname = urlObj.getHost();

        Socket socket = new Socket(hostname, PORT);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
        String data = this.data;

        // HTTP Headers & Request
        bw.write("POST " + urlObj.getPath() + " HTTP/1.0\r\n");
        bw.write("Host: " + hostname + "\r\n");
        bw.write("User-agent: " + USER_AGENT + "\r\n");

        for (Map.Entry<String, String> entry : this.headerArgs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            bw.write(key + ": " + value + "\r\n");
        }

        bw.write("Content-Length: " + data.length() + "\r\n");
        bw.write("\r\n");

        // Send Data
        bw.write(data);
        bw.flush();

        // Read response and then print
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = getResponse(br).toString();
        bw.close();

        if (!verbose) {
            response = response.substring(response.indexOf("{"));
        }

        System.out.println(response);
    }

    private void help() {
        System.out.println("httpc is a curl-like application but supports HTTP protocol only.\n" +
                "Usage:");
        System.out.println("    httpc command [arguments]");
        System.out.println("The commands are:");
        System.out.println("    get executes a HTTP GET request and prints the response.\n" +
                "   post executes a HTTP POST request and prints the response.\n" +
                "   help prints this screen.");
        System.out.println("Use \"httpc help [command]\" for more information about a command.");
    }

    private void help(String method) {
        if (method.equals("get")) {
            System.out.println("usage: httpc get [-v] [-h key:value] URL\n" +
                    "Get executes a HTTP GET request for a given URL.\n" +
                    "   -v Prints the detail of the response such as protocol, status,\n" +
                    "and headers.\n" +
                    "   -h key:value Associates headers to HTTP Request with the format\n" +
                    "'key:value'.");
        }

        if (method.equals("post")) {
            System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n" +
                    "Post executes a HTTP POST request for a given URL with inline data or from\n" +
                    "file.\n" +
                    "   -v Prints the detail of the response such as protocol, status,\n" +
                    "and headers.\n" +
                    "   -h key:value Associates headers to HTTP Request with the format\n" +
                    "'key:value'.\n" +
                    "   -d string Associates an inline data to the body HTTP POST request.\n" +
                    "   -f file Associates the content of a file to the body HTTP POST\n" +
                    "request.\n" +
                    "Either [-d] or [-f] can be used but not both.");
        }
    }
}

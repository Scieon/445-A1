import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class Httpc {

    private final String USER_AGENT = "Concordia-HTTP/1.0";
    private final int PORT = 80;

    private Map<String, String> headerArgs = new HashMap<>();

    public static void main(String[] args) {
        new Httpc().run(args);
    }

    private void run(String[] args) {

        if (args[0].equals("help")) {
            if (args.length == 1) {
                help();
            } else if (args.length == 2) {
                help(args[1]);
            }
        }

        try {
            String url = args[args.length - 1];

            if (args[0].equals("get")) {
                getHeaderArguments(args);
                sendGet(url, args[1].equals("-v"));
            }
        } catch (Exception e) { // todo handle error
            System.out.println("something went wrong");
        }
    }

    /**
     * Retrieve all header arguments
     *
     * @param args cli input
     */
    private void getHeaderArguments(String args[]) {
        List<String> argsList = new ArrayList<>(Arrays.asList(args));

        if (args[0].equals("get") && args[1].equals("-h") || args[1].equals("-v") && args[2].equals("-h")) {
            int index = argsList.indexOf("-h");

            // Begin after '-h' and get everything after except URL
            for (index += 1; index < args.length - 1; index++) {
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

        out.println("GET /get?course=networking&assignment=1 HTTP/1.0");
        out.println("Host: " + hostname);
        out.println("User-agent: " + USER_AGENT);

        for (Map.Entry<String, String> entry : this.headerArgs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            out.println(key + ": " + value);
        }
//        out.println("Connection: Close");
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
                    "-v Prints the detail of the response such as protocol, status,\n" +
                    "and headers.\n" +
                    "-h key:value Associates headers to HTTP Request with the format\n" +
                    "'key:value'.\n" +
                    "-d string Associates an inline data to the body HTTP POST request.\n" +
                    "-f file Associates the content of a file to the body HTTP POST\n" +
                    "request.\n" +
                    "Either [-d] or [-f] can be used but not both.");
        }
    }
}

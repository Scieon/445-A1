public class Httpc {


    public static void main(String[] args) {
        new Httpc().run(args);
    }

    private void run(String[] args) {
        Httpc httpc = new Httpc();

        if (args[0].equals("help")) {
            if (args.length == 1) {
                httpc.help();
            } else if (args.length == 2) {
                httpc.help(args[1]);
            }
        }
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
                    "-v Prints the detail of the response such as protocol, status,\n" +
                    "and headers.\n" +
                    "-h key:value Associates headers to HTTP Request with the format\n" +
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

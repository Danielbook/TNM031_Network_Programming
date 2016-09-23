import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class Server {
    private int port;

    static final int DEFAULT_PORT = 8189;
    static final String LABKEYSTORE = "src/server/resources/LIUkeystore.ks";
    static final String LABTRUSTSTORE = "src/server/resources/LIUtruststore.ks";
    static final String LABSTOREPASSWD = "123456";
    static final String LABALIASPASSWD = "123456";

    static final String CMD_DELETE = "cmd:delete";
    static final String CMD_UPLOAD = "cmd:upload";
    static final String CMD_DOWNLOAD = "cmd:download";
    static final String CMD_END = "cmd:end";
    static final String OUTPUT_FOLDER = "src/server/server_files/";

    BufferedReader inputStream;
    PrintWriter outputStream;

    Server () {
        this.port = DEFAULT_PORT;
    }

    Server (int port) {
        this.port = port;
    }

    public void runServer() {
        try {
            KeyStore ks = KeyStore.getInstance("JCEKS");
            ks.load(new FileInputStream(LABKEYSTORE), LABSTOREPASSWD.toCharArray());

            KeyStore ts = KeyStore.getInstance("JCEKS");
            ts.load(new FileInputStream(LABTRUSTSTORE), LABSTOREPASSWD.toCharArray());

            // setup key managers
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, LABALIASPASSWD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ts);

            // setup ssl
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLServerSocketFactory sslServer = sslContext.getServerSocketFactory();

            SSLServerSocket sss = (SSLServerSocket) sslServer.createServerSocket(this.port);
            sss.setEnabledCipherSuites(sss.getSupportedCipherSuites());

            sss.setNeedClientAuth(true);

            System.out.println("SecureServer running on port " + this.port);
            // prepare incoming connections
            SSLSocket incoming = (SSLSocket) sss.accept();
            inputStream = new BufferedReader(
                    new InputStreamReader(incoming.getInputStream()));
            outputStream = new PrintWriter(incoming.getOutputStream(), true);
            String str;

            // handle incoming transmissions
            while (!(str = inputStream.readLine()).equals("")) {
                System.out.println(str);
                switch (str) {
                    case CMD_UPLOAD:
                        receiveFromClient();
                        break;
                    case CMD_DOWNLOAD:
                        sendToClient();
                        break;
                    case CMD_DELETE:
                        delete();
                        break;
                    default:
                        break;
                }
            }
            // close connection
            incoming.close();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void receiveFromClient() {

    }

    private void sendToClient() {
        System.out.println("Sending to client");
        try {
            String str;
            String fileName = null;
            while (!(str = inputStream.readLine()).equals(CMD_END)) {
                // receive filename
                fileName = str;
                System.out.println("filename: " + fileName);
            }

            if (fileName != null) {
                // read file and send it
                try (BufferedReader reader = new BufferedReader(
                        new FileReader(OUTPUT_FOLDER + fileName))) {
                    // read file and send it to client
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputStream.println(line);
                    }
                } catch (IOException ioe) {
                    outputStream.println("File does not exist!");
                    System.out.println(ioe);
                    ioe.printStackTrace();
                }
            }
            System.out.println("ending, file sent");

            // notify client end of file transmission
            outputStream.println(CMD_END);
        } catch (Exception e) {
            outputStream.println("Something went wrong");
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void delete() {
        try {
            String str;
            String fileName = null;
            while (!(str = inputStream.readLine()).equals(CMD_END)) {
                fileName = str;
            }

            if (fileName != null) {
                try {
                    File file = new File(OUTPUT_FOLDER + fileName);
                    if (file.delete()) {
                        outputStream.println("deleted " + fileName);
                    }
                } catch (Exception e) {
                    outputStream.println("not deleted");
                }
            }

        } catch (Exception e) {
            outputStream.println("Something went wrong");
            System.out.println(e);
            e.printStackTrace();
        }
    }
}

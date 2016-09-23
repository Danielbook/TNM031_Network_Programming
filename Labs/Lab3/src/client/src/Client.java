import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.security.KeyStore;

public class Client {
    private InetAddress hostAddress;
    private int hostPort;

    private BufferedReader socketIn;
    private PrintWriter socketOut;

    static final int DEFAULT_PORT = 8189;
    static final String LABKEYSTORE = "src/server/resources/LIUkeystore.ks";
    static final String LABTRUSTSTORE = "src/client/resources/LIUkeystore.ks";
    static final String LABSTOREPASSWD = "123456";
    static final String LABALIASPASSWD = "123456";

    static final String CMD_DELETE = "cmd:delete";
    static final String CMD_UPLOAD = "cmd:upload";
    static final String CMD_DOWNLOAD = "cmd:download";
    static final String CMD_END = "cmd:end";
    static final String OUTPUT_FOLDER = "src/client/files/";

    public Client(InetAddress hostAddress) {
        this.hostAddress = hostAddress;
        this.hostPort = DEFAULT_PORT;
    }

    public void setup() {
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
            sslContext.init(kmf.getKeyManagers(),
                    tmf.getTrustManagers(),
                    null);
            SSLSocketFactory sslFact = sslContext.getSocketFactory();
            SSLSocket client = (SSLSocket) sslFact.createSocket(this.hostAddress, this.hostPort);
            client.setEnabledCipherSuites(client.getSupportedCipherSuites());

            // setup transmissions
            socketIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
            socketOut = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void upload(String fileName) {
        try {
            socketOut.println(CMD_UPLOAD);
            socketOut.println(fileName);

            try (BufferedReader reader = new BufferedReader(
                    new FileReader(OUTPUT_FOLDER + fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    socketOut.println(line);
                }
            } catch (IOException ioExeption) {
                System.out.println(ioExeption);
                ioExeption.printStackTrace();
            }

            socketOut.println(CMD_END);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void download(String fileName) {
        try {
            // Send download command and file name
            socketOut.println(CMD_DOWNLOAD);
            socketOut.println(fileName);
            socketOut.println(CMD_END);

            // Receives the file
            String str;
            StringBuilder fileContent = new StringBuilder();
            while (!(str = socketIn.readLine()).equals(CMD_END)) {
                fileContent.append(str + "\n");
            }

            // Writes to file
            String file = fileContent.toString();
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(OUTPUT_FOLDER + fileName))) {
                writer.write(file, 0, file.length());
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void delete(String fileName) {
        try {
            socketOut.println(CMD_DELETE);
            socketOut.println(fileName);
            socketOut.println(CMD_END);
            System.out.println(socketIn.readLine());
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void terminate() {
        try {
            socketOut.println("");
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}

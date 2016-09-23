import java.net.InetAddress;
import java.net.UnknownHostException;

public class mainClient {
    public static void main(String[] args) {
        try {
            InetAddress host = InetAddress.getLocalHost();
            Client client = new Client(host);

            client.setup();
            client.upload("text.txt");
            client.download("downloadable.md");
            client.delete("text.txt");
            client.terminate();

        } catch (UnknownHostException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}

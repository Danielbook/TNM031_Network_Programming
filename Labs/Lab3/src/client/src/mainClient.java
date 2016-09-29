import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class mainClient {
    public static void main(String[] args) {
        boolean running = true;
        try {
            InetAddress host = InetAddress.getLocalHost();
            Client client = new Client(host);


            client.setup();

            while(running){
                System.out.println("Connected to your secure server");
                System.out.println("1. Upload to server");
                System.out.println("2. Download from server");
                System.out.println("3. Delete from server");
                System.out.println("4. Exit application");

                String input = "";

                try {
                    input = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                } catch(IOException e) {
                    System.out.println("Something went wrong: " + e.toString());
                    return;
                }


                switch(input) {
                    case "1": {
                        System.out.println("File to upload?");
                        try {
                            input = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                        } catch(IOException e) {
                            System.out.println("Something went wrong: " + e.toString());
                            return;
                        }
                        client.upload(input);
                        break;
                    }
                    case "2": {
                        System.out.println("File to download?");
                        try {
                            input = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                        } catch(IOException e) {
                            System.out.println("Something went wrong: " + e.toString());
                            return;
                        }
                        client.download(input);
                        break;
                    }

                    case "3": {
                        System.out.println("File to delete on server?");
                        try {
                            input = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                        } catch(IOException e) {
                            System.out.println("Something went wrong: " + e.toString());
                            return;
                        }
                        client.delete(input);
                        break;
                    }

                    case "4": {
                        System.out.println("Goodbye");
                        client.terminate();
                        running = false;
                        break;
                    }
                    default: {
                        System.out.println("Wrong input");
                    }
                }
            }

//            client.upload("text.txt");
//            client.download("downloadable.md");
//            client.delete("text.txt");
//            client.terminate();

        } catch (UnknownHostException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}

package xyz.pitongku.fishinfo.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiRepository {
    String line = "";
    StringBuilder sb;
    String doRequest(String url){
        try {

            URL link = new URL(url);

            // read text returned by server
            BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream()));

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            line = sb.toString();
            in.close();


        }
        catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
        return line;

    }
}


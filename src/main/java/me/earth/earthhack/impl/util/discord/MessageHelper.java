package me.earth.earthhack.impl.util.discord;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class MessageHelper {



    public MessageHelper() {
    }

    public static void sendMessage(String message, String URLthing) throws IOException {
        URL url = new URL(URLthing);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        String json = "{\"content\":\"" + message + "\"}";

        OutputStream stream = connection.getOutputStream();
        stream.write(json.getBytes());
        stream.flush();
        stream.close();

        connection.getInputStream().close();
        connection.disconnect();
    }
}


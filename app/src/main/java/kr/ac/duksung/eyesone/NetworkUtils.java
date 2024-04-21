package kr.ac.duksung.eyesone;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private static final OkHttpClient client = new OkHttpClient();

    public static String getServerData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }
    }
}

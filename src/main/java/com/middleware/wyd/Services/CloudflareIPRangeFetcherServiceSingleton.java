package com.middleware.wyd.Services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CloudflareIPRangeFetcherServiceSingleton {

    private static CloudflareIPRangeFetcherServiceSingleton instance;
    private final List<String> ipRanges;
    private final List<String> urlIpRanges = Arrays.asList("https://www.cloudflare.com/ips-v4", "https://www.cloudflare.com/ips-v6");

    private CloudflareIPRangeFetcherServiceSingleton() {
        this.ipRanges = fetchIPRanges();
    }

    public static synchronized CloudflareIPRangeFetcherServiceSingleton getInstance() {
        if (instance == null) {
            instance = new CloudflareIPRangeFetcherServiceSingleton();
        }
        return instance;
    }

    public List<String> getIPRanges() {
        return ipRanges;
    }

    private List<String> fetchIPRanges() {
        OkHttpClient client = new OkHttpClient();
        List<String> proxiesIpv4AndIpv6 = new ArrayList<String>();
        urlIpRanges.forEach(url -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    proxiesIpv4AndIpv6.addAll(Collections.emptyList());
                }

                String responseBody = response.body().string();
                proxiesIpv4AndIpv6.addAll(Arrays.stream(responseBody.split("\n"))
                        .map(String::trim)
                        .filter(line -> !line.isEmpty())
                        .collect(Collectors.toList()));
            } catch (IOException e) {
                proxiesIpv4AndIpv6.addAll(Collections.emptyList());
            }
        });
       return proxiesIpv4AndIpv6;
    }
}
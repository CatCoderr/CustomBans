package me.catcoder.custombans.utility;

import com.google.gson.*;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author evilmidget38
 */

public class UUIDFetcher implements Callable<Map<String, UUID>> {

    private static final Map<String, UUID> UUID_CACHE = new HashMap<>();
    private static final double PROFILES_PER_REQUEST = 100;
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private static final Gson GSON = new Gson();
    private final List<String> names;
    private final boolean rateLimiting;

    public UUIDFetcher(List<String> names, boolean rateLimiting) {
        this.names = names;
        this.rateLimiting = rateLimiting;
    }

    public UUIDFetcher(List<String> names) {
        this(names, true);
    }

    public Map<String, UUID> call() throws Exception {
        Map<String, UUID> uuidMap = new HashMap<String, UUID>();
        names.stream().filter(name -> UUID_CACHE.containsKey(name.toLowerCase())).forEach(name -> {
            uuidMap.put(name, UUID_CACHE.get(name));
        });
        int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);
        for (int i = 0; i < requests; i++) {
            HttpURLConnection connection = createConnection();
            List<String> sublist = names.subList(i * 100, Math.min((i + 1) * 100, names.size()));
            JsonArray body = new JsonArray();
            sublist.stream()
                    .filter(string -> !UUID_CACHE.containsKey(string))
                    .forEach(string -> body.add(new JsonPrimitive(string)));
            writeBody(connection, body.toString());
            JsonArray array = GSON.fromJson(new InputStreamReader(connection.getInputStream()), JsonArray.class);
            for (JsonElement profile : array) {
                JsonObject jsonProfile = profile.getAsJsonObject();
                String id = jsonProfile.get("id").getAsString();
                String name = jsonProfile.get("name").getAsString();
                UUID uuid = UUIDFetcher.getUUID(id);
                uuidMap.put(name, uuid);
                UUID_CACHE.put(name.toLowerCase(), uuid);
            }
            if (rateLimiting && i != requests - 1) {
                Thread.sleep(100L);
            }
        }
        return uuidMap;
    }

    private static void writeBody(HttpURLConnection connection, String body) throws Exception {
        OutputStream stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();
        stream.close();
    }

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL(PROFILE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private static UUID getUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public static byte[] toBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static UUID fromBytes(byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Illegal byte array length: " + array.length);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        long mostSignificant = byteBuffer.getLong();
        long leastSignificant = byteBuffer.getLong();
        return new UUID(mostSignificant, leastSignificant);
    }

    public static UUID getUUIDOf(String name) throws Exception {
        return new UUIDFetcher(Collections.singletonList(name)).call().get(name);
    }
}

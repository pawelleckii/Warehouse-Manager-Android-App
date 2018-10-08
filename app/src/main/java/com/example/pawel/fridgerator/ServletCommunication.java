package com.example.pawel.fridgerator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.callback.UnsupportedCallbackException;

import static android.R.attr.data;
import static android.R.attr.path;
import static android.content.ContentValues.TAG;

/**
 * Created by Pawel on 11-Nov-17.
 */

public class ServletCommunication extends Activity {

    private static final String urlString = "http://10.0.2.2:8080/fridge";
    private static final String urlLogin = "http://10.0.2.2:8080/login";
    private static final int VERSION = 1;


    public static ItemList sendPost(User user, ItemList items, String deviceId, Integer communicationCounter) {

        URL url;
        try {
            url = new URL(urlString);

            HttpURLConnection conn = getHttpURLConnection(url, "POST");

            String json;
            if (items != null)
                json = createJson(user, items, deviceId, communicationCounter);
            else
                json = createJson(user);

            if (json != null) {
                sendRequest(conn, json);

                int responseCode = conn.getResponseCode();

                switch (responseCode) {
                    // Server and Application states are the same, no need to update
                    case HttpURLConnection.HTTP_NO_CONTENT:
                        return null;

                    // Server noticed a conflict and sent up-to-date list in request body
                    case HttpURLConnection.HTTP_RESET:
                    case HttpURLConnection.HTTP_OK:
                        String responseBody = getResponseBody(conn);
                        return new ItemList(responseBody);

                    //Connection was good but we received some strange code xd
                    default:
                        throw new IllegalStateException("Unhandled HTTP code received.");
                }
            } else {
                throw new IllegalStateException("Cannot create Json from data provided");
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int login(String username, String password) {
        URL url;
        try {
            url = new URL(urlLogin);
            HttpURLConnection conn = getHttpURLConnection(url, "POST");
            String json = createJsonLogin(username, password);
            if (json != null) {
                sendRequest(conn, json);
                return conn.getResponseCode();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @NonNull
    private static String getResponseBody(HttpURLConnection conn) throws IOException {
        String response = "";
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        while ((line = br.readLine()) != null) {
            response += line;
        }
        return response;
    }

    private static void sendRequest(HttpURLConnection conn, String json) throws IOException {
        byte[] outputBytes = json.getBytes("UTF-8");
        OutputStream os = conn.getOutputStream();
        os.write(outputBytes);
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(URL url, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(context.getResources().getInteger(
//                    R.integer.maximum_timeout_to_server));
//            conn.setConnectTimeout(context.getResources().getInteger(
//                    R.integer.maximum_timeout_to_server));
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestProperty("Content-Type", "application/json");
        return conn;
    }

    private static String createJson(User user, ItemList items, String deviceId, Integer communicationCounter) {
        JSONObject root = new JSONObject();


        try {
            root.put("version", VERSION);
            root.put("device_id", deviceId);
            root.put("comm_counter", communicationCounter);
            root.put("username", user.getUsername());
            root.put("password", user.getPassword());

            JSONArray itemList = new JSONArray();

            List<Item> items1 = items.getItems();
            for (Item item : items1) {
                JSONObject itemJson = item.createJson();
                itemList.put(itemJson);
            }

            root.put("items", itemList);

            return root.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static String createJson(User user) {
        JSONObject root = new JSONObject();

        try {
            root.put("version", VERSION);
            root.put("username", user.getUsername());
            root.put("password", user.getPassword());

            return root.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String createJsonLogin(String username, String password) {
        JSONObject root = new JSONObject();
        try {
            root.put("username", username);
            root.put("password", password);
            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

//    public static ItemList sendGet(User user) {
//        URL url;
//        try {
//            url = new URL(urlString);
//
//            HttpURLConnection conn = getHttpURLConnection(url, "GET");
//
//            JSONObject root = new JSONObject();
//            root.put("version", VERSION);
//            root.put("username", user.getUsername());
//            root.put("password", user.getPassword());
//
//            sendRequest(conn, root.toString());
//
//            int responseCode = conn.getResponseCode();
//
//            switch (responseCode) {
//                case HttpURLConnection.HTTP_OK:
//                    return new ItemList(getResponseBody(conn));
////                default:
////                    return new ItemList();
//            }
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}

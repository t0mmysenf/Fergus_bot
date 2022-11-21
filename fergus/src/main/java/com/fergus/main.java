package com.fergus;
import com.google.common.hash.Hashing;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;

public class main {
    public static void main(String[] args) throws InterruptedException {

        //Load Properties
        Properties prop = new Properties();
        JSONArray stations = new JSONArray();
        try {
            prop.load(ClassLoader.getSystemResourceAsStream("conf.properties"));
            InputStream stationStream = ClassLoader.getSystemResourceAsStream("stations.json");
            BufferedReader r = new BufferedReader(new InputStreamReader(stationStream));
            String line;
            String stationsJson ="";
            while((line = r.readLine()) != null) {
                stationsJson = stationsJson + line;
            }
            stationStream.close();
            stations = new JSONArray(stationsJson);
        } catch (Exception e) {
            System.out.println("Failed to get properties or stations");
            return;
        }
        String clientId = prop.getProperty("SPOTIFY_CLIENT_ID");
        String clientSecret = prop.getProperty("SPOTIFY_CLIENT_SECRET");
        String conUrl = prop.getProperty("DB_URL");
        Integer interval = Integer.parseInt(prop.getProperty("BOT_INTERVAL"));

        URL url;
        HttpURLConnection con;
        while(true) {

            Thread.sleep(interval * 1000);
            for (int i = 0; i < stations.length(); i ++){
                JSONObject station = stations.getJSONObject(i);
                String radioLink = station.getString("stationLink");
                String radioName = station.getString("stationName");

                //Get song data
                String response = "";
                try{
                    url = new URL(radioLink);
                    con = (HttpURLConnection)url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String inputLine;
                    StringBuffer responseBuffer = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        responseBuffer.append(inputLine);
                    }
                    in.close();
                    System.out.println(response);
                    response = responseBuffer.toString();
                }catch (Exception e){
                    System.out.println("Failed to get song data from radio");
                    System.out.println(e.getMessage());
                }
                if(response.isEmpty()) continue;

                //Parse Json
                JSONObject songJson = new JSONObject(response);
                if(!songJson.getBoolean("success")) continue;

                String songTitle = songJson.getJSONObject("data").getString("title");
                String songArtist = songJson.getJSONObject("data").getString("artist");
                String songRemix = songJson.getJSONObject("data").getString("remix");
                String hash = Hashing.sha256()
                        .hashString(songTitle + songArtist + songRemix, StandardCharsets.UTF_8)
                        .toString();

                try{
                    //Check if track exists already by hash
                    Connection conn = DriverManager.getConnection(conUrl, prop.getProperty("DB_USER"), prop.getProperty("DB_PASSWORD"));
                    PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS COUNT FROM SONG WHERE HASH = ?");
                    ps.setString(1, hash);
                    ResultSet rs = ps.executeQuery();
                    //Check count
                    boolean skip = false;
                    while (rs.next()) {
                        if(rs.getLong(1) > 0) skip = true;
                    }
                    if(skip) continue;

                    //Insert song
                    PreparedStatement psInsert = conn.prepareStatement("INSERT INTO SONG (TITLE, ARTIST, REMIX, HASH, RADIONAME) VALUES (?,?,?,?,?)");
                    psInsert.setString(1,songTitle);
                    psInsert.setString(2,songArtist);
                    psInsert.setString(3,songRemix);
                    psInsert.setString(4,hash);
                    psInsert.setString(5,radioName);
                    psInsert.executeUpdate();
                } catch (Exception e) {
                    System.out.println("Insert Failed for song: " + songTitle + "/" + songArtist);
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}

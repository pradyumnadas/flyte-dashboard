/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

/**
 *
 * @author pradyumnadas
 */
public class ParseDownloadDmps implements Runnable {
    
    final static String PARSE_APP_ID = "BJCxwAj9WNG0z0iumD80njBea9wG2cwKq1RbherY";
    final static String PARSE_REST_API_KEY = "5XwzdDtf0XDsLrHPKV4TH4QL9LI4OGYywa5RPiJQ";
    final static String API_VER = "1";
    final static String API_BASE_URL = "https://api.parse.com";
    
    String classname;
    URL downloadUrl;
    Thread th;
    ParseObject.ParseType type;

    public ParseDownloadDmps(ParseObject.ParseType type) {        
        classname = Settings.getInstance().getParseObjectClassName(type);
        
        try {
            downloadUrl = new URL(String.format("%s/%s/classes/%s", API_BASE_URL, API_VER, classname));
        } catch (MalformedURLException ex) {
            downloadUrl = null;
        }
        
        this.type = type;
        th = new Thread(this, "DownloadDumpsThread");
    }

    public void startDownload() {
        th.start();
    }

    @Override
    public void run() {
        try {
            if (downloadUrl != null) {
                HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Parse-Application-Id", PARSE_APP_ID);
                connection.setRequestProperty("X-Parse-REST-API-Key", PARSE_REST_API_KEY);
                
                String ts = Settings.getInstance().getLatestObjTimeStamp(type);
                
                if(ts != null) {
                    String constraint = URLEncoder.encode("where={\"createdAt\":{\"$gte\":{\"__type\":\"Date\",\"iso\":\"" + ts + "\"}}}");
                    
                    connection.setRequestProperty("Content-Length", Integer.toString(constraint.length()));
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    
                    OutputStream contentStream = connection.getOutputStream();
                    contentStream.write(constraint.getBytes());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line, json = "";
                while ((line = br.readLine()) != null) {
                    json += line;
                }
                ParseObject[] parseObjs = JsonParser.getParseObjectsFromJSON(json, type);
                for (ParseObject obj : parseObjs) {
                    downloadFile(obj);
                }
                
                updateLatestObjectTimestamp(parseObjs);
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }

    private void downloadFile(ParseObject obj) throws MalformedURLException {
        if (!(obj.getUrl().isEmpty() || obj.getUrl() == null)) {
            URL crashFileDownloadUrl = new URL(obj.getUrl());      
            FileOutputStream crashFileos;
            String destination = Settings.getInstance().getParseFileDownloadDirectory(type) + File.separator + 
                    obj.getName();
            try {
                crashFileos = new FileOutputStream(destination);
            } catch (FileNotFoundException ex) {
                return;
            }
            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection) crashFileDownloadUrl.openConnection();
                BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                byte[] bytesRead = new byte[2048];
                while (bis.read(bytesRead) != 0) {
                    crashFileos.write(bytesRead);
                }
            } catch (IOException ex) {
            }
        }
    }

    private void updateLatestObjectTimestamp(ParseObject[] parseObjs) {
        ParseObject latestObj = parseObjs[0];
        for(ParseObject tmpObj : parseObjs){
            if(tmpObj.getCreatedTime().compareTo(latestObj.getCreatedTime()) > 0){
                latestObj = tmpObj;
            }
        }
        
        String iso8601_date;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        iso8601_date = formatter.format(latestObj.getCreatedTime().getTime());
        Settings.getInstance().setLatestObjTimeStamp(type, iso8601_date);
    }
}

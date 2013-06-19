/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Observable;

/**
 *
 * @author pradyumnadas
 */
public class ParseDownloadDmps extends Observable implements Runnable {

    public enum DownloadState {

        DOWNLOADING,
        DOWNLOAD_COMPLETED,
        DOWNLOAD_FAILED
    }
    final static String PARSE_APP_ID = "E0s2fYU4BoISPxBd6qFbW2qy5HygtnB7y20mJjG7";
    final static String PARSE_REST_API_KEY = "3Sklxck6rsjXTSQbWns97EPlQXwvPnE1kHnErHVb";
    final static String API_VER = "1";
    final static String API_BASE_URL = "https://api.parse.com";
    String classname;
    URL downloadUrl;
    Thread th;
    ParseObject.ParseType type;
    DownloadState currentState;

    public void setCurrentState(DownloadState currentState) {
        if (this.currentState != currentState) {
            this.currentState = currentState;
            notifyObservers(currentState);
        }
    }

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
        setCurrentState(DownloadState.DOWNLOADING);
        try {
            if (downloadUrl != null) {
                HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Parse-Application-Id", PARSE_APP_ID);
                connection.setRequestProperty("X-Parse-REST-API-Key", PARSE_REST_API_KEY);

                String ts = Settings.getInstance().getLatestObjTimeStamp(type);

//                if(ts != null) {
//                    String constraint = URLEncoder.encode("where={\"createdAt\":{\"$gte\":{\"__type\":\"Date\",\"iso\":\"" + ts + "\"}}}");
//                    
//                    connection.setRequestProperty("Content-Length", Integer.toString(constraint.length()));
//                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                    
//                    OutputStream contentStream = connection.getOutputStream();
//                    contentStream.write(constraint.getBytes());
//                }

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line, json = "";
                while ((line = br.readLine()) != null) {
                    json += line;
                }
                LinkedList<ParseObject> parseObjs = JsonParser.getParseObjectsFromJSON(json, type);
                for (ParseObject obj : parseObjs) {
                    downloadFile(obj);
                }

                //updateLatestObjectTimestamp(parseObjs);
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        
        setCurrentState(DownloadState.DOWNLOAD_COMPLETED);
    }

    private void downloadFile(ParseObject obj) throws MalformedURLException {
        if (!(obj.getUrl().isEmpty() || obj.getUrl() == null)) {
            URL crashFileDownloadUrl = new URL(obj.getUrl());
            FileOutputStream crashFileos = null;
            File destinationFolder = new File(Settings.getInstance().getParseFileDownloadDirectory(type));
            File destinationFile = new File(destinationFolder.getAbsolutePath() + File.separator + obj.getName());

            if (!destinationFolder.exists()) {
                if (!destinationFolder.mkdirs()) {
                    return;
                }
            }

            try {
                crashFileos = new FileOutputStream(destinationFile, true);
            } catch (FileNotFoundException ex) {
                return;
            }
            try {
                BufferedInputStream bis = new BufferedInputStream(crashFileDownloadUrl.openStream());
                byte[] bytesRead = new byte[2048];
                while (bis.read(bytesRead) != -1) {
                    crashFileos.write(bytesRead);
                }
                crashFileos.close();
                bis.close();
            } catch (IOException ex) {
            }
        }
    }

    private void updateLatestObjectTimestamp(LinkedList<ParseObject> parseObjs) {
        ParseObject latestObj = parseObjs.getFirst();
        for (ParseObject tmpObj : parseObjs) {
            if (tmpObj.getCreatedTime().compareTo(latestObj.getCreatedTime()) > 0) {
                latestObj = tmpObj;
            }
        }

        String iso8601_date;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        iso8601_date = formatter.format(latestObj.getCreatedTime().getTime());
        Settings.getInstance().setLatestObjTimeStamp(type, iso8601_date);
    }
}

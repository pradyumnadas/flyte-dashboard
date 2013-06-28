/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author pradyumnadas
 */
public class Settings {
    final Properties prop;
    String propFilePath;
    
    private static Settings instance = new Settings();
    
    private Settings() {
        prop = new Properties();
        propFilePath = "/home/pradyumnadas/CrashDumpDashboard/src/java/resources/Settings.properties";
    }
    
    public static Settings getInstance() {
        return instance;
    }
    
    private void writeProperty(String propertyName, String propertyValue){
        try {
            prop.setProperty(propertyName, propertyValue);
            prop.store(new FileOutputStream(propFilePath), null);
        }
        catch (FileNotFoundException e) {
            
        }
        catch (IOException ex) {
            //Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String readProperty(String propertyName) {
        String value;
        
        try {
            prop.load(new FileInputStream(propFilePath));
            value = prop.getProperty(propertyName);
        } catch (FileNotFoundException ex) {
            value = null;
//            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            value = null;
        }
        
        return value;
    }
    
    public void setDBPath(String newValue) {
        writeProperty("DBPath", newValue);
    }
    
    public void setSymbolFileDirectory(String newValue) {
        writeProperty("SymbolFileDirectory", newValue);
    }
    
    public void setParseFileDownloadDirectory(ParseObject.ParseType type, String newValue) {
        if(type == ParseObject.ParseType.CRASHDUMP){
            writeProperty("DumpFileDirectory", newValue);
        }
        else if(type == ParseObject.ParseType.BUG){
            writeProperty("BugFileDirectory", newValue);
        }
        else if(type == ParseObject.ParseType.FEEDBACK){
            writeProperty("FeedbackFileDirectory", newValue);
        }
        else{
            writeProperty("QueryFileDirectory", newValue);            
        }
    }
    
    public void setParseObjectClassName(ParseObject.ParseType type, String newValue) {
        if(type == ParseObject.ParseType.CRASHDUMP){
            writeProperty("CrashdumpClassName", newValue);
        }
        else if(type == ParseObject.ParseType.BUG){
            writeProperty("BugsClassName", newValue);
        }
        else if(type == ParseObject.ParseType.FEEDBACK){
            writeProperty("FeedbackClassName", newValue);
        }
        else{
            writeProperty("QueryClassName", newValue);            
        }
    }
    
    public void setLatestObjTimeStamp(ParseObject.ParseType type, String newValue) {
        if(type == ParseObject.ParseType.CRASHDUMP){
            writeProperty("LatestCrashObjectTimeStamp", newValue);
        }
        else if(type == ParseObject.ParseType.BUG){
            writeProperty("LatestBugObjectTimeStamp", newValue);
        }
        else if(type == ParseObject.ParseType.FEEDBACK){
            writeProperty("LatestFeedbackObjectTimeStamp", newValue);
        }
        else{
            writeProperty("LatestQueryObjectTimeStamp", newValue);            
        }
    }
    
    public String getDBPath() {
        String val = readProperty("DBPath");
        return val;
    }
    
    public String getSymbolFileDirectory() {
        String val = readProperty("SymbolFileDirectory");
        return val;
    }
    
    public String getParseObjectClassName(ParseObject.ParseType type) {
        String val;
        if(type == ParseObject.ParseType.CRASHDUMP){
            val = readProperty("CrashdumpClassName");
        }
        else if(type == ParseObject.ParseType.BUG){
            val = readProperty("BugsClassName");
        }
        else if(type == ParseObject.ParseType.FEEDBACK){
            val = readProperty("FeedbackClassName");
        }
        else{
            val = readProperty("QueryClassName");
        }
        return val;
    }


    public String getParseFileDownloadDirectory(ParseObject.ParseType type) {
        String val;
        if(type == ParseObject.ParseType.CRASHDUMP){
            val = readProperty("DumpFileDirectory");
        }
        else if(type == ParseObject.ParseType.BUG){
            val = readProperty("BugFileDirectory");
        }
        else if(type == ParseObject.ParseType.FEEDBACK){
            val = readProperty("FeedbackFileDirectory");
        }
        else{
            val = readProperty("QueryFileDirectory");
        }
        return val;
    }
    
    public String getLatestObjTimeStamp(ParseObject.ParseType type) {
        String val;
        if(type == ParseObject.ParseType.CRASHDUMP){
            val = readProperty("LatestCrashObjectTimeStamp");
        }
        else if(type == ParseObject.ParseType.BUG){
            val = readProperty("LatestBugObjectTimeStamp");
        }
        else if(type == ParseObject.ParseType.FEEDBACK){
            val = readProperty("LatestFeedbackObjectTimeStamp");
        }
        else{
            val = readProperty("LatestQueryObjectTimeStamp");
        }
        return val;
    }
}

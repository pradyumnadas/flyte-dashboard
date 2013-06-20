/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.io.File;
import java.util.GregorianCalendar;

/**
 *
 * @author pradyumnadas
 */
public class ParseObject {

    public enum ParseType {

        CRASHDUMP, FEEDBACK, QUERY, BUG
    }
    String OS, objectId, userId, version, name, url;
    GregorianCalendar createdTime;
    File file;
    ParseType type;

    public ParseObject(ParseType type, String os, String objID, String usId, String ver, String name, String url, GregorianCalendar cal) {
        this.type = type;
        this.OS = os;
        this.objectId = objID;
        this.name = name;
        this.url = url;
        this.userId = usId;
        this.version = ver;
        this.createdTime = cal;
        file = new File(Settings.getInstance().getParseFileDownloadDirectory(type) + File.separator + name);
    }

    //GETTERS
    public String getFilePath() {
        return file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    public ParseType getType() {
        return type;
    }

    public String getOS() {
        return OS;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getUserId() {
        return userId;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public GregorianCalendar getCreatedTime() {
        return createdTime;
    }

    public String getCreationTimeAsISO8601String() {
        return Utility.convertGregorianCalToISO8601String(createdTime);
    }

    public void setCreationTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        createdTime = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    public void setCreationTime(GregorianCalendar cal) {
        createdTime = cal;
    }

    @Override
    public String toString() {
        String s = name + "(" + userId + ")";
        return s;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 *
 * @author pradyumnadas
 */
public class Utility {

    public static String convertGregorianCalToISO8601String(GregorianCalendar cal) {
        String iso8601_date;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH':'mm':'ss.SSS'Z'");
        iso8601_date = formatter.format(cal.getTime());
        return iso8601_date;
    }

    public static GregorianCalendar convertISO8601StringToGregorianCalendar(String iso8601_date) {
        int y = 0, m = 0, d = 0, h = 0, min = 0, s = 0;

        String[] datetime = iso8601_date.split("T");
        String[] date = datetime[0].split("-");
        y = new Integer(date[0]);
        m = new Integer(date[1]);
        d = new Integer(date[2]);
        String[] time = datetime[1].split(":");
        h = new Integer(time[0]);
        min = new Integer(time[1]);
        s = new Integer(time[2].substring(0, 2));

        GregorianCalendar createdTime = new GregorianCalendar(y, m, d, h, min, s);
        return createdTime;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

/**
 *
 * @author pradyumnadas
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import misc.ParseObject;
import misc.Utility;

public class DatabaseManager {

    static DatabaseManager dbm = null;
    Connection connection;
    Statement stmt;
    ResultSet rs;
    final String TABLENAME = "UserData";

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:FlipkartDashboard.db");
            stmt = connection.createStatement();
            initialise();
        } catch (SQLException ex) {
            System.out.println("SQLException");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException");
        }

    }

    public static DatabaseManager getInstance() {
        if (dbm == null) {
            dbm = new DatabaseManager();
        }
        return dbm;
    }

    private void initialise() throws SQLException {
        stmt.executeUpdate("create table if not exists " + TABLENAME
                + "("
                + "object_id   varchar(50) primary key,"
                + "os          varchar(50),"
                + "user_id     varchar(200),"
                + "version     varchar(10),"
                + "name        varchar(500),"
                + "url         varchar(500),"
                + "creation_time    varchar(40),"
                + "type        integer"
                + ")");
    }

    private int translateObjectTypeToInt(ParseObject.ParseType type) {
        int res;
        if (type == ParseObject.ParseType.BUG) {
            res = 0;
        } else if (type == ParseObject.ParseType.CRASHDUMP) {
            res = 1;
        } else if (type == ParseObject.ParseType.FEEDBACK) {
            res = 2;
        } else {
            res = 3;
        }
        return res;
    }

    private ParseObject.ParseType translateIntToObjectType(int i) {
        ParseObject.ParseType type;
        if (i == 0) {
            type = ParseObject.ParseType.BUG;
        } else if (i == 1) {
            type = ParseObject.ParseType.CRASHDUMP;
        } else if (i == 2) {
            type = ParseObject.ParseType.FEEDBACK;
        } else {
            type = ParseObject.ParseType.QUERY;
        }
        return type;
    }

    public int saveAllObjects(List<ParseObject> lstObj) {
        int num = 0;
        for (ParseObject obj : lstObj) {
            if (!saveObject(obj)) {
                ++num;
            }
        }
        return num;
    }

    public boolean saveObject(ParseObject obj) {
        boolean outcome;
        String query = String.format("insert into %s values('%s','%s','%s','%s','%s','%s','%s',%d)", TABLENAME, obj.getObjectId(),
                obj.getOS(), obj.getUserId(), obj.getVersion(), obj.getName(), obj.getUrl(),
                obj.getCreationTimeAsISO8601String(), translateObjectTypeToInt(obj.getType()));
        try {
            outcome = (stmt.executeUpdate(query) != 1) ? false : true;
        } catch (SQLException e) {
            outcome = false;
        }

        return outcome;
    }

    public LinkedList<ParseObject> readAllObjects() {
        LinkedList<ParseObject> objs = new LinkedList<ParseObject>();
        try {
            rs = stmt.executeQuery("select * from " + TABLENAME);
            String[] vals;
            while (rs.next()) {
                vals = new String[7];
                for (int i = 1; i <= vals.length; i++) {
                    vals[i] = rs.getString(i);
                }
                ParseObject.ParseType type = translateIntToObjectType(rs.getInt(8));
                objs.add(new ParseObject(type, vals[1], vals[0], vals[2], vals[3], vals[4], vals[5],
                        Utility.convertISO8601StringToGregorianCalendar(vals[6])));
            }
        } catch (SQLException ex) {
        }
        return objs;
    }

    public ParseObject readObject(String objID) {
        ParseObject obj = null;
        try {
            rs = stmt.executeQuery(String.format("select * from %s where object_id='%s'", TABLENAME, objID));
            String[] vals = new String[7];
            for (int i = 1; i <= vals.length; i++) {
                vals[i] = rs.getString(i);
            }
            ParseObject.ParseType type = translateIntToObjectType(rs.getInt(8));
            obj = new ParseObject(type, vals[1], vals[0], vals[2], vals[3], vals[4], vals[5],
                    Utility.convertISO8601StringToGregorianCalendar(vals[6]));
        } catch (SQLException ex) {
        }
        return obj;
    }
}

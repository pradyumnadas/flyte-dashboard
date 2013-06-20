/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author pradyumnadas
 */
public class JsonParser {

    static final JdomParser JDOM_PARSER = new JdomParser();

    public static LinkedList<ParseObject> getParseObjectsFromJSON(String json, ParseObject.ParseType type) {
        LinkedList<ParseObject> parseObjList = new LinkedList<ParseObject>();

        try {
            JsonRootNode jsonroot = JDOM_PARSER.parse(json);
            List<JsonNode> objList = jsonroot.getArrayNode("results");
            for (JsonNode node : objList) {
                parseObjList.add(readObject(node, type));
            }
        } catch (InvalidSyntaxException ex) {
            //Logger.getLogger(JsonParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return parseObjList;
    }

    private static ParseObject readObject(JsonNode node, ParseObject.ParseType type) {
        String OS = node.getStringValue("OS");
        String objectId = node.getStringValue("objectId");
        String userId = node.getStringValue("UserID");
        String version = node.getStringValue("Version");
        String name = node.getStringValue("name");
        String url = node.getStringValue("url");
        String createdAt = node.getStringValue("createdAt");
        GregorianCalendar calendar = Utility.convertISO8601StringToGregorianCalendar(createdAt);

        ParseObject prsobj = new ParseObject(type, OS, objectId, userId, version, name, url, calendar);
        return prsobj;
    }
}

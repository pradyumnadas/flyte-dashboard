/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

/**
 *
 * @author pradyumnadas
 */
public class JsonParser {
    public static ParseObject[] getParseObjectsFromJSON(String json, ParseObject.ParseType type){
        ParseObject[] prsObj;
        int startOfArray = json.indexOf('[');
        int endOfArray = json.lastIndexOf(']');
        json = json.substring(startOfArray + 1, endOfArray);
        String[] objs = json.split(",");
        prsObj = new ParseObject[objs.length];
        for(int i = 0; i < objs.length; i++){
            prsObj[i] = readObject(objs[i], type);
        }
        return prsObj;
    }
    
    private static ParseObject readObject(String js, ParseObject.ParseType type){
        String OS="", objectId="", userId="", version="", name="", url="";
        int y=0,m=0,d=0,h=0,min=0,s=0;
        
        js = js.substring(js.indexOf('{') + 1, js.lastIndexOf('}'));
        String[] vals = js.split(",");
        for(String val : vals){
            String[] pair = val.split(":");
            if(pair[0].contains("OS")){
                OS = pair[1].substring(pair[1].indexOf('"') + 1, pair[1].lastIndexOf('"'));
            }
            else if(pair[0].contains("UserID")){
                userId = pair[1].substring(pair[1].indexOf('"') + 1, pair[1].lastIndexOf('"'));
            }
            else if(pair[0].contains("objectId")){
                objectId = pair[1].substring(pair[1].indexOf('"') + 1, pair[1].lastIndexOf('"'));
            }
            else if(pair[0].contains("Version")){
                version = pair[1].substring(pair[1].indexOf('"') + 1, pair[1].lastIndexOf('"'));
            }
            else if(pair[0].contains("name")){
                name = pair[1].substring(pair[1].indexOf('"') + 1, pair[1].lastIndexOf('"'));
            }
            else if(pair[0].contains("url")){
                url = pair[1].substring(pair[1].indexOf('"') + 1, pair[1].lastIndexOf('"'));
            }
            else if(pair[0].contains("createdAt")){
                String[] datetime = (pair[1].substring(pair[1].indexOf('"') + 1, pair[1].lastIndexOf('"'))).split("T");                
                String[] date = datetime[0].split("-");
                y = new Integer(date[0]);
                m = new Integer(date[1]);
                d = new Integer(date[2]);
                String[] time = datetime[1].split(":");
                h = new Integer(time[0]);
                min = new Integer(time[1]);
                s = new Integer(time[2].split(".")[0]);
            }
        }
        ParseObject prsobj = new ParseObject(type, OS, objectId, userId, version, name, url, null);
        prsobj.setCreationTime(y, m, d, h, min, s);
        return prsobj;
    }
}

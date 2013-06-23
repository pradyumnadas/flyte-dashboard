<%-- 
    Document   : mainpage
    Created on : Jun 20, 2013, 2:04:29 PM
    Author     : pradyumnadas
--%>

<%@page import="misc.ParseObjectProcessor"%>
<%@page import="DB.DatabaseManager"%>
<%@page import="misc.ParseObject"%>
<%@page import="java.util.LinkedList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Flipkart Desktop Application Crash Reports</title>
    </head>
    <body>
        <script type="text/javascript">
            function getUrlParameter(name) {
                name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
                var regexS = "[\\?&]" + name + "=([^&#]*)";
                var regex = new RegExp(regexS);
                var results = regex.exec(window.location.href);
                if (results === null)
                    return null;
                else
                    return results[1];
            }
            function displayDetails(param) {
                var e, paramValue;
                if (param === "objID") {
                    e = document.getElementById("sel_objID");
                    paramValue = e.options[e.selectedIndex].value;
                    var selType = getUrlParameter('type');
                    window.location = "mainpage.jsp?type=" + selType + "&objID=" + paramValue;
                } else if (param === "type") {
                    e = document.getElementById("sel_Type");
                    paramValue = e.options[e.selectedIndex].value;
                    window.location = "mainpage.jsp?type=" + paramValue;
                }
            }
        </script>

        <div>
            <select id="sel_Type" onchange="displayDetails('type')">
                <option value="none">--Select Type--</option>
                <option value="feedback">Feedback</option>
                <option value="bug">Bug</option>
                <option value="query">Query</option>
                <option value="crash">Crash Dumps</option>
            </select>
        </div>
        <div>
            <%! LinkedList<ParseObject> parseObjs;%>
            <select id="sel_objID" size="10" onchange="displayDetails('objID')">
                <%
                    ParseObject.ParseType type;
                    String param = request.getParameter("type");
                    if (param != null) {
                        if (param.equals("feedback")) {
                            type = ParseObject.ParseType.FEEDBACK;
                        } else if (param.equals("bug")) {
                            type = ParseObject.ParseType.BUG;
                        } else if (param.equals("query")) {
                            type = ParseObject.ParseType.QUERY;
                        } else {
                            type = ParseObject.ParseType.CRASHDUMP;
                        }
                        parseObjs = DatabaseManager.getInstance().readAllObjects(type);
                        for (int i = 0; i < parseObjs.size(); i++) {
                            out.println(String.format("<option value='%s'>%s</option>", parseObjs.get(i).getObjectId(), parseObjs.get(i).toString()));
                        }
                    }
                %>
            </select>
        </div>
        <div>
            <textarea>
                <%
                    String s = request.getParameter("objID");
                    String description;
                    if (s != null) {
                        ParseObject selectedObj = DatabaseManager.getInstance().readObject(s);
                        if ((selectedObj.getType() == ParseObject.ParseType.FEEDBACK) || (selectedObj.getType() == ParseObject.ParseType.QUERY)) {
                            description = (String) (ParseObjectProcessor.processObject(selectedObj).get(0));
                        } else if (selectedObj.getType() == ParseObject.ParseType.CRASHDUMP) {
                            description = (String) (ParseObjectProcessor.processObject(selectedObj).get(0));
                        } else {
                            description = "";
                        }
                        description = description.trim();
                        out.println(description);
                    }
                %>
            </textarea>
        </div>
    </body>
</html>

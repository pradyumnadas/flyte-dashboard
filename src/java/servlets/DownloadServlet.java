/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import misc.ParseDownloadDmps;
import misc.ParseObject;

/**
 *
 * @author pradyumnadas
 */
public class DownloadServlet extends HttpServlet {

    boolean downloadCompleted = false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ParseDownloadDmps downloadThread = new ParseDownloadDmps(ParseObject.ParseType.CRASHDUMP);
        downloadThread.startDownload();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ParseDownloadDmps downloadThread = new ParseDownloadDmps(ParseObject.ParseType.FEEDBACK);
        downloadThread.startDownload();
        downloadThread.waitForDownloadCompletion();
        response.sendRedirect("mainpage.jsp");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

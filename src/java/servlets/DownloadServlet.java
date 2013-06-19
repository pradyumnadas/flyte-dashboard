/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import misc.ParseDownloadDmps;
import misc.ParseObject;

/**
 *
 * @author pradyumnadas
 */
public class DownloadServlet extends HttpServlet implements Observer {

    boolean downloadCompleted = false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ParseDownloadDmps downloadThread = new ParseDownloadDmps(ParseObject.ParseType.CRASHDUMP);
        downloadThread.addObserver(this);
        downloadThread.startDownload();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        synchronized (this) {
            try {
                ParseDownloadDmps downloadThread = new ParseDownloadDmps(ParseObject.ParseType.CRASHDUMP);
                downloadThread.addObserver(this);
                downloadThread.startDownload();
                wait();
                response.sendRedirect("donedownloading.html");
            } catch (InterruptedException ex) {
                Logger.getLogger(DownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void update(Observable o, Object arg) {
        ParseDownloadDmps.DownloadState state = (ParseDownloadDmps.DownloadState) arg;
        if (state == ParseDownloadDmps.DownloadState.DOWNLOAD_COMPLETED) {
            synchronized (this) {
                notify();
            }
        }
    }
}

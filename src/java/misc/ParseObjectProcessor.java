/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author pradyumnadas
 */
public class ParseObjectProcessor {

    public static void processObject(ParseObject targetObj) {
        String[] processedOutput = new String[3];
        if (targetObj.getType() == ParseObject.ParseType.CRASHDUMP) {
            String stacktrace = generateStacktrace(targetObj);
        } else if (targetObj.getType() == ParseObject.ParseType.BUG) {
            String[] info = extractBugInfo(targetObj);
        } else if (targetObj.getType() == ParseObject.ParseType.FEEDBACK) {
            String feedbackDesc = getDescription(targetObj);
        } else {
            String queryDesc = getDescription(targetObj);
        }
    }

    private static String generateStacktrace(ParseObject targetObj) {
        String stacktrace = "";
        Process minidump_stacktwalk;
        String[] cmdArray = new String[3];
        BufferedReader stacktraceReader;

        cmdArray[0] = "/home/pradyumnadas/google-breakpad/src/processor/minidump_stackwalk";
        cmdArray[1] = Settings.getInstance().getParseFileDownloadDirectory(targetObj.getType()) + File.separator
                + targetObj.getName();
        cmdArray[2] = Settings.getInstance().getSymbolFileDirectory() + File.separator
                + targetObj.getVersion() + File.separator + targetObj.getOS() + File.separator + "symbols";
        try {
            minidump_stacktwalk = Runtime.getRuntime().exec(cmdArray);

            if (minidump_stacktwalk.exitValue() == 0) {
                stacktraceReader = new BufferedReader(new InputStreamReader(minidump_stacktwalk.getInputStream()));

                String temp;

                do {
                    temp = stacktraceReader.readLine();
                    if (temp != null) {
                        stacktrace += temp;
                    }
                } while (temp != null);

                stacktraceReader.close();
            }
        } catch (IOException ex) {
            stacktrace = null;
        }
        return stacktrace;
    }

    private static String getDescription(ParseObject targetObj) {
        String description = "";
        String filePath = Settings.getInstance().getParseFileDownloadDirectory(targetObj.getType()) + File.separator +
                targetObj.getName();
        
        ZipInputStream fileZIS;
        ZipEntry entry;
        try{
            fileZIS = new ZipInputStream(new FileInputStream(filePath));
            while((entry = fileZIS.getNextEntry()) != null) {
                if(entry.getName().equals("description.txt")){
                    BufferedReader br = new BufferedReader(new InputStreamReader(fileZIS));
                    String temp;
                    while((temp = br.readLine()) != null) {
                        description += temp;
                    }
                }
            }
        }catch(FileNotFoundException e) {
            description = null;
        }catch(IOException e) {
            description = null;
        }
        
        return description;
    }

    private static String[] extractBugInfo(ParseObject targetObj) {
    }
}

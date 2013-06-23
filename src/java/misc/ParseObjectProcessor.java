/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author pradyumnadas
 */
public class ParseObjectProcessor {

    public static ArrayList<Object> processObject(ParseObject targetObj) {
        ArrayList<Object> processedOutput = new ArrayList<Object>();
        if (targetObj.getType() == ParseObject.ParseType.CRASHDUMP) {
            processedOutput.add(generateStacktrace(targetObj));
        } else if (targetObj.getType() == ParseObject.ParseType.BUG) {
            for (File f : extractBugInfo(targetObj)) {
                processedOutput.add(f);
            }
        } else if (targetObj.getType() == ParseObject.ParseType.FEEDBACK) {
            processedOutput.add(getDescription(targetObj));
        } else {
            processedOutput.add(getDescription(targetObj));
        }
        return processedOutput;
    }

    private static String generateStacktrace(ParseObject targetObj) {
        Process minidump_stacktwalk;
        String[] cmdArray = new String[3];
        BufferedReader stacktraceReader;
        StringBuilder stacktrace = new StringBuilder();

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
                        stacktrace.append(temp);
                        stacktrace.append("\n");
                    }
                } while (temp != null);

                stacktraceReader.close();
            }
        } catch (IOException ex) {
        }
        return stacktrace.toString();
    }

    private static String getDescription(ParseObject targetObj) {
        StringBuilder description = new StringBuilder();
        String filePath = Settings.getInstance().getParseFileDownloadDirectory(targetObj.getType()) + File.separator
                + targetObj.getName();

        ZipInputStream fileZIS;
        ZipEntry entry;
        try {
            fileZIS = new ZipInputStream(new FileInputStream(filePath));
            while ((entry = fileZIS.getNextEntry()) != null) {
                if (entry.getName().equals("description.txt")) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(fileZIS));
                    String temp;
                    while ((temp = br.readLine()) != null) {
                        description.append(temp);
                        description.append("\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return description.toString();
    }

    private static File[] extractBugInfo(ParseObject targetObj) {
        File[] outputFiles = new File[3];

        String filePath = Settings.getInstance().getParseFileDownloadDirectory(targetObj.getType()) + File.separator
                + targetObj.getName();

        ZipInputStream fileZIS;
        BufferedWriter descriptionWriter;
        ZipEntry entry;
        try {
            fileZIS = new ZipInputStream(new FileInputStream(filePath));
            int i = 0;
            while ((entry = fileZIS.getNextEntry()) != null) {
                outputFiles[i] = File.createTempFile("flyte", "");
                BufferedReader br = new BufferedReader(new InputStreamReader(fileZIS));
                descriptionWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFiles[i])));
                String temp;
                while ((temp = br.readLine()) != null) {
                    descriptionWriter.write(temp);
                }
                ++i;
            }
        } catch (FileNotFoundException e) {
            outputFiles = null;
        } catch (IOException e) {
            outputFiles = null;
        }

        return outputFiles;
    }
}

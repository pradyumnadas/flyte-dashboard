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

    public static ArrayList<File> processObject(ParseObject targetObj) {
        ArrayList<File> processedOutput = new ArrayList<File>();
        if (targetObj.getType() == ParseObject.ParseType.CRASHDUMP) {
            processedOutput.add(generateStacktrace(targetObj));
        } else if (targetObj.getType() == ParseObject.ParseType.BUG) {
            for(File f : extractBugInfo(targetObj)) {
                processedOutput.add(f);
            }
        } else if (targetObj.getType() == ParseObject.ParseType.FEEDBACK) {
            processedOutput.add(getDescription(targetObj));
        } else {
            processedOutput.add(getDescription(targetObj));
        }
        return processedOutput;
    }

    private static File generateStacktrace(ParseObject targetObj) {
        File stacktraceFile;
        Process minidump_stacktwalk;
        String[] cmdArray = new String[3];
        BufferedReader stacktraceReader;
        BufferedWriter stacktraceWriter;

        cmdArray[0] = "/home/pradyumnadas/google-breakpad/src/processor/minidump_stackwalk";
        cmdArray[1] = Settings.getInstance().getParseFileDownloadDirectory(targetObj.getType()) + File.separator
                + targetObj.getName();
        cmdArray[2] = Settings.getInstance().getSymbolFileDirectory() + File.separator
                + targetObj.getVersion() + File.separator + targetObj.getOS() + File.separator + "symbols";
        try {
            stacktraceFile = File.createTempFile("flyte", "stacktrace");
            minidump_stacktwalk = Runtime.getRuntime().exec(cmdArray);

            if (minidump_stacktwalk.exitValue() == 0) {
                stacktraceReader = new BufferedReader(new InputStreamReader(minidump_stacktwalk.getInputStream()));
                stacktraceWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stacktraceFile)));
                
                String temp;

                do {
                    temp = stacktraceReader.readLine();
                    if (temp != null) {
                        stacktraceWriter.write(temp);
                    }
                } while (temp != null);

                stacktraceReader.close();
                stacktraceWriter.close();
            }
        } catch (IOException ex) {
            stacktraceFile = null;
        }
        return stacktraceFile;
    }

    private static File getDescription(ParseObject targetObj) {
        File description;
        String filePath = Settings.getInstance().getParseFileDownloadDirectory(targetObj.getType()) + File.separator +
                targetObj.getName();
        
        ZipInputStream fileZIS;
        BufferedWriter descriptionWriter;
        ZipEntry entry;
        try{
            description = File.createTempFile("flyte", "desc");
            descriptionWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(description)));
            fileZIS = new ZipInputStream(new FileInputStream(filePath));
            while((entry = fileZIS.getNextEntry()) != null) {
                if(entry.getName().equals("description.txt")){
                    BufferedReader br = new BufferedReader(new InputStreamReader(fileZIS));
                    String temp;
                    while((temp = br.readLine()) != null) {
                        descriptionWriter.write(temp);
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

    private static File[] extractBugInfo(ParseObject targetObj) {
        File[] outputFiles = new File[3];
        return outputFiles;
    }
}

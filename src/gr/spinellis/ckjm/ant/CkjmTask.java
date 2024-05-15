package gr.spinellis.ckjm.ant;

import gr.spinellis.ckjm.MetricsFilter;
import gr.spinellis.ckjm.PrintPlainResults;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;

public class CkjmTask extends MatchingTask {
    private File outputFile;
    private File classDir;
    private Path extdirs;
    private String format;

    public CkjmTask() {
        this.format = "plain";
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setOutputfile(File outputfile) {
        this.outputFile = outputfile;
    }

    public void setClassdir(File classDir) {
        this.classDir = classDir;
    }

    public void setExtdirs(Path e) {
        if (extdirs == null) {
            extdirs = e;
        } else {
            extdirs.append(e);
        }
    }

    public Path getExtdirs() {
        return extdirs;
    }

    public Path createExtdirs() {
        if (extdirs == null) {
            extdirs = new Path(getProject());
        }
        return extdirs.createPath();
    }

    public void execute() throws BuildException {
        validateClassDir();

        DirectoryScanner ds = super.getDirectoryScanner(classDir);
        String files[] = ds.getIncludedFiles();

        if (files.length == 0) {
            log("No class files in specified directory " + classDir);
        } else {
            for (int i = 0; i < files.length; i++) {
                files[i] = classDir.getPath() + File.separatorChar + files[i];
            }

            try {
                OutputStream outputStream = new FileOutputStream(outputFile);
                PrintStream printStream = new PrintStream(outputStream);

                if (format.equals("xml")) {
                    printXmlMetrics(files, printStream);
                } else {
                    printPlainMetrics(files, printStream);
                }

                outputStream.close();

            } catch (IOException ioe) {
                throw new BuildException("Error file handling: " + ioe.getMessage());
            }
        }
    }

    private void validateClassDir() {
        if (classDir == null || !classDir.exists() || !classDir.isDirectory()) {
            throw new BuildException("Invalid class directory: " + classDir);
        }
    }

    private void printXmlMetrics(String[] files, PrintStream printStream) {
        PrintXmlResults outputXml = new PrintXmlResults(printStream);
        outputXml.printHeader();
        MetricsFilter.runMetrics(files, outputXml);
        outputXml.printFooter();
    }

    private void printPlainMetrics(String[] files, PrintStream printStream) {
        PrintPlainResults outputPlain = new PrintPlainResults(printStream);
        MetricsFilter.runMetrics(files, outputPlain);
    }
}

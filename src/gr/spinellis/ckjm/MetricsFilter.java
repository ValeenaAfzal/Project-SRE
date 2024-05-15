package gr.spinellis.ckjm;

import org.apache.bcel.classfile.*;
import java.io.*;
import java.util.logging.Logger;

public class MetricsFilter {
    private static final Logger logger = Logger.getLogger(MetricsFilter.class.getName());

    private static boolean includeJdk = false;
    private static boolean onlyPublic = false;

    public static boolean isJdkIncluded() {
        return includeJdk;
    }

    public static boolean includeAll() {
        return !onlyPublic;
    }

    static void processClass(ClassMetricsContainer cm, String clspec) {
        JavaClass jc = loadClass(clspec);
        if (jc != null) {
            analyzeClass(jc, cm);
        }
    }

    private static JavaClass loadClass(String clspec) {
        int spc = clspec.indexOf(' ');
        if (spc != -1) {
            String jar = clspec.substring(0, spc);
            clspec = clspec.substring(spc + 1);
            try {
                return new ClassParser(jar, clspec).parse();
            } catch (IOException e) {
                logger.severe("Error loading " + clspec + " from " + jar + ": " + e);
            }
        } else {
            try {
                return new ClassParser(clspec).parse();
            } catch (IOException e) {
                logger.severe("Error loading " + clspec + ": " + e);
            }
        }
        return null;
    }

    private static void analyzeClass(JavaClass jc, ClassMetricsContainer cm) {
        ClassVisitor visitor = new ClassVisitor(jc, cm);
        visitor.start();
        visitor.end();
    }

    public static void runMetrics(String[] files, CkjmOutputHandler outputHandler) {
        ClassMetricsContainer cm = new ClassMetricsContainer();
        for (String file : files) {
            processClass(cm, file);
        }
        cm.printMetrics(outputHandler);
    }

    public static void main(String[] argv) {
        parseCommandLineArgs(argv);
        ClassMetricsContainer cm = new ClassMetricsContainer();
        processInputClasses(argv, cm);
        printMetrics(cm);
    }

    private static void parseCommandLineArgs(String[] argv) {
        for (int i = 0; i < argv.length; i++) {
            if ("-s".equals(argv[i])) {
                includeJdk = true;
            } else if ("-p".equals(argv[i])) {
                onlyPublic = true;
            }
        }
    }

    private static void processInputClasses(String[] argv, ClassMetricsContainer cm) {
        int argp = 0;
        if (argv.length == argp) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    processClass(cm, s);
                }
            } catch (IOException e) {
                System.err.println("Error reading line: " + e);
                System.exit(1);
            }
        }

        for (int i = argp; i < argv.length; i++) {
            processClass(cm, argv[i]);
        }
    }

    private static void printMetrics(ClassMetricsContainer cm) {
        CkjmOutputHandler handler = new PrintPlainResults(System.out);
        cm.printMetrics(handler);
    }
}

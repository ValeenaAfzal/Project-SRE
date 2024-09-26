package gr.spinellis.ckjm.ant;

import gr.spinellis.ckjm.CkjmOutputHandler;
import gr.spinellis.ckjm.ClassMetrics;

import java.io.PrintStream;

public class PrintXmlResults implements CkjmOutputHandler {
    private final PrintStream printStream;

    public PrintXmlResults(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void printHeader() {
        printStream.println("<?xml version=\"1.0\"?>");
        printStream.println("<ckjm>");
    }

    public void handleClass(String name, ClassMetrics classMetrics) {
        printStream.print(formatClassXml(name, classMetrics));
    }

    private String formatClassXml(String name, ClassMetrics classMetrics) {
        return "<class>\n" +
                "<name>" + name + "</name>\n" +
                "<wmc>" + classMetrics.getWmc() + "</wmc>\n" +
                "<dit>" + classMetrics.getDit() + "</dit>\n" +
                "<noc>" + classMetrics.getNoc() + "</noc>\n" +
                "<cbo>" + classMetrics.getCbo() + "</cbo>\n" +
                "<rfc>" + classMetrics.getRfc() + "</rfc>\n" +
                "<lcom>" + classMetrics.getLcom() + "</lcom>\n" +
                "<ca>" + classMetrics.getCa() + "</ca>\n" +
                "<npm>" + classMetrics.getNpm() + "</npm>\n" +
                "</class>\n";
    }

    public void printFooter () {
        printStream.println("</ckjm>");
    }
}

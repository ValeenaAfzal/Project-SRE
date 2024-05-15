/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gr.spinellis.ckjm;

/**
 *
 * @author enxsys
 */
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.IOException;

public class ClassProcessor {

    static void processClass(ClassMetricsContainer cm, String clspec) {
        try {
            JavaClass jc = loadClass(clspec);
            if (jc != null) {
                ClassVisitor visitor = new ClassVisitor(jc, cm);
                visitor.start();
                visitor.end();
            }
        } catch (IOException e) {
            System.err.println("Error loading class: " + e.getMessage());
        }
    }

    private static JavaClass loadClass(String clspec) throws IOException {
        int spc = clspec.indexOf(' ');
        if (spc != -1) {
            String jar = clspec.substring(0, spc);
            clspec = clspec.substring(spc + 1);
            return new ClassParser(jar, clspec).parse();
        } else {
            return new ClassParser(clspec).parse();
        }
    }
}

/*
 * (C) Copyright 2005 Diomidis Spinellis
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.spinellis.ckjm;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Repository;
import org.apache.bcel.Constants;
import org.apache.bcel.util.*;
import java.io.*;
import java.util.*;

/**
 * Convert a list of classes into their metrics.
 * Process standard input lines or command line arguments
 * containing a class file name or a jar file name,
 * followed by a space and a class file name.
 * Display on the standard output the name of each class, followed by its
 * six Chidamber Kemerer metrics:
 * WMC, DIT, NOC, CBO, RFC, LCOM
 *
 * @see ClassMetrics
 * @version $Revision: 1.9 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MetricsFilter {

    private static boolean includeJdk = false;
    private static boolean onlyPublic = false;

    public static boolean isJdkIncluded() {
        return includeJdk;
    }

    public static boolean includeAll() {
        return !onlyPublic;
    }
    
    public static void runMetrics(String[] files, CkjmOutputHandler outputHandler) {
        ClassMetricsContainer cm = new ClassMetricsContainer();

        for (int i = 0; i < files.length; i++)
            ClassProcessor.processClass(cm, files[i]);
        cm.printMetrics(outputHandler);
    }

    public static void main(String[] argv) {
        parseArguments(argv);
        ClassMetricsContainer cm = new ClassMetricsContainer();

        if (argv.length == 0) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = in.readLine()) != null)
                    ClassProcessor.processClass(cm, line);
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                System.exit(1);
            }
        }

        for (String arg : argv)
            ClassProcessor.processClass(cm, arg);

        CkjmOutputHandler handler = new PrintPlainResults(System.out);
        cm.printMetrics(handler);
    }

    private static void parseArguments(String[] argv) {
        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals("-s")) {
                includeJdk = true;
            } else if (argv[i].equals("-p")) {
                onlyPublic = true;
            }
        }
    }
}


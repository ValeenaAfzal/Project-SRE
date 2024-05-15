/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gr.spinellis.ckjm;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants; // Added import statement

public class MetricsCalculator {
    private String myClassName;
    private ClassMetricsContainer cmap;

    public MetricsCalculator(String className, ClassMetricsContainer classMap) {
        myClassName = className;
        cmap = classMap;
    }

    public ClassMetrics calculateMetrics() {
        ClassMetrics cm = new ClassMetrics();
        // Calculate metrics here...
        return cm;
    }


    public static String className(Type type) {
                if (type.getType() <= Constants.T_VOID) {
            return "java.PRIMITIVE";
        } else if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            return className(arrayType.getBasicType());
        } else {
            return type.toString();
        }
    }
}

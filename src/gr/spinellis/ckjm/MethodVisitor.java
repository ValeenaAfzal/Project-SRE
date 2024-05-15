
package gr.spinellis.ckjm;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;

class MethodVisitor extends EmptyVisitor {
    private MethodGen mg;
    private ConstantPoolGen cp;
    private ClassVisitor cv;
    private ClassMetrics cm;

    MethodVisitor(MethodGen m, ClassVisitor c) {
        mg = m;
        cv = c;
        cp = mg.getConstantPool();
        cm = cv.getMetrics();
    }

    public void start() {
        if (!mg.isAbstract() && !mg.isNative()) {
            for (InstructionHandle ih = mg.getInstructionList().getStart();
                 ih != null; ih = ih.getNext()) {
                Instruction instruction = ih.getInstruction();
                if (!visitInstruction(instruction))
                    instruction.accept(this);
            }
            updateExceptionHandlers();
        }
    }

    private boolean visitInstruction(Instruction instruction) {
        short opcode = instruction.getOpcode();
        return InstructionConstants.INSTRUCTIONS[opcode] != null &&
                !(instruction instanceof ConstantPushInstruction) &&
                !(instruction instanceof ReturnInstruction);
    }

    @Override
    public void visitLocalVariableInstruction(LocalVariableInstruction instruction) {
        if (instruction.getOpcode() != Constants.IINC)
            cv.registerCoupling(instruction.getType(cp));
    }

    @Override
    public void visitArrayInstruction(ArrayInstruction instruction) {
        cv.registerCoupling(instruction.getType(cp));
    }

    @Override
    public void visitFieldInstruction(FieldInstruction instruction) {
        cv.registerFieldAccess(instruction.getClassName(cp), instruction.getFieldName(cp));
        cv.registerCoupling(instruction.getFieldType(cp));
    }

    @Override
    public void visitInvokeInstruction(InvokeInstruction instruction) {
        Type[] argTypes = instruction.getArgumentTypes(cp);
        for (Type argType : argTypes)
            cv.registerCoupling(argType);
        cv.registerCoupling(instruction.getReturnType(cp));
        cv.registerMethodInvocation(instruction.getClassName(cp), instruction.getMethodName(cp), argTypes);
    }

    @Override
    public void visitINSTANCEOF(INSTANCEOF instruction) {
        cv.registerCoupling(instruction.getType(cp));
    }

    @Override
    public void visitCHECKCAST(CHECKCAST instruction) {
        cv.registerCoupling(instruction.getType(cp));
    }

    @Override
    public void visitReturnInstruction(ReturnInstruction instruction) {
        cv.registerCoupling(instruction.getType(cp));
    }

    private void updateExceptionHandlers() {
        CodeExceptionGen[] handlers = mg.getExceptionHandlers();
        for (CodeExceptionGen handler : handlers) {
            Type catchType = handler.getCatchType();
            if (catchType != null)
                cv.registerCoupling(catchType);
        }
    }
}

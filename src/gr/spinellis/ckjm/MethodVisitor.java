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

import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;
import org.apache.bcel.util.*;
import java.util.*;

class MethodVisitor extends EmptyVisitor {
    private MethodGen mg;
    private ConstantPoolGen cp;
    private ClassVisitor cv;
    private ClassMetrics cm;

    /** Constructor. */
    MethodVisitor(MethodGen m, ClassVisitor c) {
	mg  = m;
	cv = c;
	cp  = mg.getConstantPool();
	cm = cv.getMetrics();
    }

    public void start() {
        if (!isMethodAbstractOrNative()) {
            visitMethodInstructions();
            updateExceptionHandlers();
        }
    }

    private boolean isMethodAbstractOrNative() {
        return mg.isAbstract() || mg.isNative();
    }

    private void visitMethodInstructions() {
        for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            if (!visitInstruction(i))
                i.accept(this);
        }
    }

    /** Visit a single instruction. */
    private boolean visitInstruction(Instruction i) {
        try {
            short opcode = i.getOpcode();
            return InstructionConstants.INSTRUCTIONS[opcode] != null &&
                   !(i instanceof ConstantPushInstruction) &&
                   !(i instanceof ReturnInstruction);
        } catch (Exception e) {
            // Handle any potential exceptions here, or log them.
            return false; // or handle differently based on requirements
        }
    }

    /** Local variable use. */
    @Override
    public void visitLocalVariableInstruction(LocalVariableInstruction i) {
	if(i.getOpcode() != Constants.IINC)
	    cv.registerCoupling(i.getType(cp));
    }

    /** Array use. */
    @Override
    public void visitArrayInstruction(ArrayInstruction i) {
	cv.registerCoupling(i.getType(cp));
    }

    /** Field access. */
    @Override
    public void visitFieldInstruction(FieldInstruction i) {
	cv.registerFieldAccess(i.getClassName(cp), i.getFieldName(cp));
	cv.registerCoupling(i.getFieldType(cp));
    }

    /** Method invocation. */
    @Override
    public void visitInvokeInstruction(InvokeInstruction instruction) {
	Type[] argTypes   = instruction.getArgumentTypes(cp);
        for (Type argType : argTypes)
            cv.registerCoupling(argType);
        cv.registerCoupling(instruction.getReturnType(cp));
        cv.registerMethodInvocation(instruction.getClassName(cp), instruction.getMethodName(cp), argTypes);
    }

    /** Visit an instanceof instruction. */
    @Override
    public void visitINSTANCEOF(INSTANCEOF i) {
	cv.registerCoupling(i.getType(cp));
    }

    /** Visit checklast instruction. */
    @Override
    public void visitCHECKCAST(CHECKCAST i) {
	cv.registerCoupling(i.getType(cp));
    }

    /** Visit return instruction. */
    @Override
    public void visitReturnInstruction(ReturnInstruction i) {
	cv.registerCoupling(i.getType(cp));
    }

    /** Visit the method's exception handlers. */
    private void updateExceptionHandlers() {
	CodeExceptionGen[] handlers = mg.getExceptionHandlers();

	/* Measuring decision: couple exceptions */
        for (CodeExceptionGen handler : handlers) {
            Type catchType = handler.getCatchType();
            if (catchType != null)
                cv.registerCoupling(catchType);
        }
    }
}

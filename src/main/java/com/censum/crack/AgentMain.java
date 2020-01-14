package com.censum.crack;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author xiaobai
 */
public class AgentMain {

    /**
     * AdviceAdapter
     */
    public static class MyMethodVisitor extends AdviceAdapter {

        MyMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
            super(Opcodes.ASM7, mv, access, name, desc);
        }

        @Override
        protected void onMethodEnter() {
            // 强行插入 return CanLoadState.SUCCESS;
            mv.visitFieldInsn(GETSTATIC, "com/jclarity/censum/CanLoadState",
                    "SUCCESS",
                    "Lcom/jclarity/censum/CanLoadState;");
            mv.visitInsn(ARETURN);
        }
    }

    /**
     * ClassVisitor
     */
    public static class MyClassVisitor extends ClassVisitor {

        MyClassVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM7, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            // 只注入 canLoadCensum 方法
            String canLoadMethodName = "canLoadCensum";
            if (name.equals(canLoadMethodName)) {
                return new MyMethodVisitor(mv, access, name, desc);
            }
            return mv;
        }
    }

    /**
     * Transformer
     */
    public static class MyClassFileTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classBytes) throws IllegalClassFormatException {
            // 只注入 CensumStartupChecks 类
            String canLoadClassName = "com/jclarity/censum/CensumStartupChecks";
            if (className.equals(canLoadClassName)) {
                ClassReader cr = new ClassReader(classBytes);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
                ClassVisitor cv = new MyClassVisitor(cw);
                cr.accept(cv, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                return cw.toByteArray();
            }
            return classBytes;
        }

    }

    /**
     * Premain
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new MyClassFileTransformer(), true);
    }
}

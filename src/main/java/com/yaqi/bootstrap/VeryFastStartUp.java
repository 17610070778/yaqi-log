package com.yaqi.bootstrap;

import com.yaqi.config.Config;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Date;
import java.util.UUID;

import static jdk.internal.org.objectweb.asm.Opcodes.*;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * @author wangyq26022@yunrong.cn
 * @version V3.0.1
 * @since 2022/4/21 23:21
 */
public class VeryFastStartUp implements ClassFileTransformer {


    public static void premain(String options, Instrumentation ins) {
        ins.addTransformer(new VeryFastStartUp());
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (isBeanDefinition(className)) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
            classReader.accept(new ClassVisitor(ASM5, classWriter) {
                @Override
                public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
                    MethodVisitor mv = super.visitMethod(i, s, s1, s2, strings);
                    AdviceAdapter adviceAdapter = new AdviceAdapter(ASM5, mv, i, s, s1) {
                        @Override
                        protected void onMethodExit(int i) {
                            Label IF = new Label();
                            Label ELSE = new Label();
                            this.mv.visitVarInsn(ALOAD, 0);
                            this.mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Config.class), "isNotLazy", "(Lorg/springframework/beans/factory/config/BeanDefinition;)Z", false);
                            this.mv.visitVarInsn(ISTORE, 2);
                            this.mv.visitVarInsn(ILOAD, 2);
                            this.mv.visitJumpInsn(IFNE, IF);
                            this.mv.visitJumpInsn(GOTO, ELSE);
                            this.mv.visitLabel(IF);
                            this.mv.visitInsn(ICONST_0);
                            this.mv.visitInsn(IRETURN);
                            this.mv.visitLabel(ELSE);
                            this.mv.visitInsn(ICONST_1);
                            this.mv.visitInsn(IRETURN);
                            super.onMethodExit(i);
                        }
                    };
                    return s.equals("isLazyInit") ? adviceAdapter : mv;
                }
            }, ClassReader.EXPAND_FRAMES);
            byte[] data = classWriter.toByteArray();
            return data;
        } else if (Config.isIntercept(className)) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
            classReader.accept(new ClassVisitor(ASM5, classWriter) {
                @Override
                public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
                    MethodVisitor mv = super.visitMethod(i, s, s1, s2, strings);

                    if (s.equals("nextId")) {
                        return new MethodVisitor(ASM5, mv) {
                            @Override
                            public void visitMethodInsn(int i, String s, String s1, String s2, boolean b) {
                                // ????????????UUID???-  ?????????""
                                this.mv.visitLdcInsn("-");
                                this.mv.visitVarInsn(ASTORE, 100);
                                this.mv.visitLdcInsn("");
                                this.mv.visitVarInsn(ASTORE, 101);
                                this.mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(UUID.class), "randomUUID", "()Ljava/util/UUID;", false);
                                this.mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(UUID.class), "toString", "()Ljava/lang/String;", false);

                                this.mv.visitVarInsn(ALOAD, 100);
                                this.mv.visitVarInsn(ALOAD, 101);
                                this.mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(String.class), "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;", false);
                            }
                        };
                    }
                    return mv;
                }
            }, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        } else {
            return classfileBuffer;
        }
    }

    public static boolean isBeanDefinition(String className) {
        return className.equals("org/springframework/beans/factory/support/AbstractBeanDefinition");
    }
}

package com.yaqi.mv;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

/**
 * @author wangyq26022@yunrong.cn
 * @version V3.0.1
 * @since 2022/4/22 9:14
 */
public interface MvChain {
    public AdviceAdapter doFilter(AdviceAdapter adviceAdapter);
}

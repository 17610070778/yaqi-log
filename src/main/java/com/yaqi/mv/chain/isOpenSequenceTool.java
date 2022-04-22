package com.yaqi.mv.chain;

import com.yaqi.mv.MvChain;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

/**
 * 拦截com.hsjry.lang.sequence.SequenceTool的next()方法，不让使用redis、zk、baidu Id等；
 * @author wangyq26022@yunrong.cn
 * @version V3.0.1
 * @since 2022/4/22 9:11
 */
public class isOpenSequenceTool implements MvChain {

    @Override
    public AdviceAdapter doFilter(AdviceAdapter adviceAdapter) {
        return null;
    }
}

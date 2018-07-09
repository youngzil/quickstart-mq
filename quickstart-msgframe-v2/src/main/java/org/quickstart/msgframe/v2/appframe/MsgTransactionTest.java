package org.quickstart.msgframe.v2.appframe;

import org.quickstart.msgframe.v2.appframe.service.interfaces.ICustSV;

import com.ai.appframe2.service.ServiceFactory;

public class MsgTransactionTest {
    /*
     在defaults.xml中设置
    <!-- 消息的拦截器 -->
    <interceptor>
        <clazz name="com.ai.aif.msgframe.auto.commit.MsgTransactionInterceptorImpl" />
    </interceptor>
    */

    public static void main(String[] args) throws Exception {
        ICustSV sv = (ICustSV) ServiceFactory.getService(ICustSV.class);
        sv.doService();

    }
}

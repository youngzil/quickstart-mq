package org.quickstart.msgframe.v1.consumer;

import com.ai.aif.msgframe.common.exception.MsgFrameClientException;
import com.ai.aif.msgframe.common.interfaces.IWarmUpper;

public class WarmUpperImplTest implements IWarmUpper {

    public void warmup() throws MsgFrameClientException {
        System.out.println("预加载打印信息");
    }

}

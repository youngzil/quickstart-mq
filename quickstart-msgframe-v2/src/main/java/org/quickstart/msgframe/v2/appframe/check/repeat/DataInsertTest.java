/**
 * 项目名称：msgframe-extend-appframe 
 * 文件名：DataInsertTest.java
 * 版本信息：
 * 日期：2018年5月7日
 * Copyright youngzil Corporation 2018
 * 版权所有 *
 */
package org.quickstart.msgframe.v2.appframe.check.repeat;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.aif.msgframe.extend.appframe.db.bo.MsgExamineConsumerBean;
import com.ai.aif.msgframe.extend.appframe.db.service.interfaces.IMsgExamineConsumerSV;
import com.ai.appframe2.common.AIException;
import com.ai.appframe2.service.ServiceFactory;

/**
 * DataInsertTest
 * 
 * @author：youngzil@163.com
 * @2018年5月7日 上午10:32:12
 * @since 1.0
 */
public class DataInsertTest {

    private static final Logger logger = LoggerFactory.getLogger(DataInsertTest.class);

    public static void main(String[] args) {

        logger.info("============插入数据===========");
        try {
            IMsgExamineConsumerSV sv = (IMsgExamineConsumerSV) ServiceFactory.getService(IMsgExamineConsumerSV.class);
            MsgExamineConsumerBean msgCheckRepeatBean = new MsgExamineConsumerBean();
            msgCheckRepeatBean.setMsgId("11111111111");
            msgCheckRepeatBean.setDestinationCode("test");
            sv.insert(msgCheckRepeatBean);
        } catch (AIException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            boolean dd = null != e.getMessage() && e.getMessage().contains("ORA-00001: 违反唯一约束条件");
            boolean dd2 = null != e.getMessage() && e.getMessage().contains("for key 'PRIMARY'");
            logger.error(dd + "," + dd2);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        logger.info("============插入数据end===========");
    }

}

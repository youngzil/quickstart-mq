/**
 * 项目名称：msgframe-console2 
 * 文件名：MsgframeConfigXmlUtil.java
 * 版本信息：
 * 日期：2017年11月9日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package com.ai.aif.msgframe.common.util;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * MsgframeConfigXmlUtil 
 *  
 * @author：youngzil@163.com
 * @2017年11月9日 上午9:23:06 
 * @since 1.0
 */
public class MsgframeConfigXmlUtil {
    
    
    public static Document getDocument(Map<String, Object> map) throws Exception {
        // 创建一个空文档
        Document doc = DOMUtils.newXMLDocument();
        
         //创建根节点
        Element root = doc.createElement("msgframeCfg");
        root.setAttribute("xmlns", "http://www.youngzil.com/msgframe");
       //将根节点添加到Document对象中
        doc.appendChild(root);
        
        return doc;
    }
    
    public static Document addDestinations(Map<String, Object> map) throws Exception {
        // 创建一个空文档
        Document doc = DOMUtils.newXMLDocument();
        
         //创建根节点
        Element root = doc.createElement("msgframeCfg");
        root.setAttribute("xmlns", "http://www.youngzil.com/msgframe");
       //将根节点添加到Document对象中
        doc.appendChild(root);
        
        return doc;
    }
    
    public static Document addSubscribes(Map<String, Object> map) throws Exception {
        // 创建一个空文档
        Document doc = DOMUtils.newXMLDocument();
        
         //创建根节点
        Element root = doc.createElement("msgframeCfg");
        root.setAttribute("xmlns", "http://www.youngzil.com/msgframe");
       //将根节点添加到Document对象中
        doc.appendChild(root);
        
        return doc;
    }
    
    public static Document addClusters(Map<String, Object> map) throws Exception {
        // 创建一个空文档
        Document doc = DOMUtils.newXMLDocument();
        
         //创建根节点
        Element root = doc.createElement("msgframeCfg");
        root.setAttribute("xmlns", "http://www.youngzil.com/msgframe");
       //将根节点添加到Document对象中
        doc.appendChild(root);
        
        return doc;
    }
    
    public static Document addPersistence(Map<String, Object> map) throws Exception {
        // 创建一个空文档
        Document doc = DOMUtils.newXMLDocument();
        
         //创建根节点
        Element root = doc.createElement("msgframeCfg");
        root.setAttribute("xmlns", "http://www.youngzil.com/msgframe");
       //将根节点添加到Document对象中
        doc.appendChild(root);
        
        return doc;
    }

}

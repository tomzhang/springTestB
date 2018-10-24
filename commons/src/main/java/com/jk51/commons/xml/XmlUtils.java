package com.jk51.commons.xml;

import org.dom4j.*;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
public class XmlUtils {

    public static Map xml2map(String xml) throws DocumentException {
        Document doc = DocumentHelper.parseText(xml);
        Element element = doc.getRootElement();
        Object result = xml2map(element);
        if(result != null && result instanceof Map)
            return (Map)result;
        return null;
    }

    public static Object xml2map(Element element) throws DocumentException {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Element> elements = element.elements();
        if (elements.size() == 0) {
            map.put(element.getName(), element.getText());
            if (!element.isRootElement()) {
                return element.getText();
            }
        } else if (elements.size() == 1) {
            map.put(elements.get(0).getName(), xml2map(elements.get(0)));
        } else if (elements.size() > 1) {
            // 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的
            // 构造一个map用来去重
            Map<String, Element> tempMap = new HashMap<String, Element>();
            for (Element ele : elements) {
                tempMap.put(ele.getName(), ele);
            }
            Set<String> keySet = tempMap.keySet();
            for (String string : keySet) {
                Namespace namespace = tempMap.get(string).getNamespace();
                List<Element> elements2 = element.elements(new QName(string, namespace));
                // 如果同名的数目大于1则表示要构建list
                if (elements2.size() > 1) {
                    List<Object> list = new ArrayList<Object>();
                    for (Element ele : elements2) {
                        list.add(xml2map(ele));
                    }
                    map.put(string, list);
                } else {
                    // 同名的数量不大于1则直接递归去
                    map.put(string, xml2map(elements2.get(0)));
                }
            }
        }

        return map;
    }
}

package com.fangjun.xmltojava;

import lombok.Data;

/**
 * TODO
 * @author: fangjun
 * @date: 2021/09/30
 */
@Data
public class XmlToJavaReq {
    private String className;
    private String packageName;
    private String xml;
    private String download;
}

package com.fangjun.xmltojava;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO
 * 
 * @author: fangjun
 * @date: 2021/09/29
 */
@Setter
@Getter
public class XmlMetadata {

    private String rootName;

    private List<FieldInfo> fieldList;

    private List<ChildClass> childClass;

    @Data
    @AllArgsConstructor
    public static class FieldInfo {
        private String nodeName;
        private String className;
        private String fieldName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChildClass {
        private String className;
        private List<FieldInfo> fieldList;
        private boolean needBuild;
    }
}

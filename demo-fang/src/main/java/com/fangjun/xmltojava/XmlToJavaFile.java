package com.fangjun.xmltojava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.fangjun.util.FreemarkerUtil;
import com.fangjun.util.HumpUtils;
import com.fangjun.util.TxtFileUtils;
import com.fangjun.xmltojava.XmlMetadata.ChildClass;
import com.fangjun.xmltojava.XmlMetadata.FieldInfo;
import com.google.gson.Gson;

import lombok.NoArgsConstructor;

/**
 * xml文件需要格式化，避免空数据的子节点被识别为value
 * @author: fangjun
 * @date: 2021/09/29
 */
@NoArgsConstructor
public class XmlToJavaFile {
    
    public static List<String> importClassList = Arrays.asList("javax.xml.bind.annotation.XmlAccessType;",
            "javax.xml.bind.annotation.XmlAccessorType;",
            "javax.xml.bind.annotation.XmlElement;",
            "javax.xml.bind.annotation.XmlRootElement;",
            "lombok.AllArgsConstructor;",
            "lombok.Builder;",
            "lombok.Data;",
            "lombok.NoArgsConstructor;");
    
    public static final String FILE_ENCONDING = "UTF-8";
    
    public static final Gson GSON = new Gson();
    
    private static boolean hasList = false;

    private String className = "ClassName";
    private String packageName = "com.newland.crm.common;";
    
    public XmlToJavaFile(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
    }
    

    public String generateJavaContent(XmlMetadata xmlMetadata) throws Exception {
        Map<String, Object> map = new HashMap<>();

        List<String> importClasses = new ArrayList<String>(importClassList);
        if (hasList) {
            importClasses.add("java.util.List;");
        }
        if (packageName != null && !packageName.equals("")) {
            map.put("package", packageName);
        }
        map.put("importClasses", importClasses);
        map.put("rootName", xmlMetadata.getRootName());
        map.put("className", className);
        map.put("fieldList", xmlMetadata.getFieldList());
        map.put("childClassList", xmlMetadata.getChildClass());

        String templateStr = FreemarkerUtil.loadTemplate("/xmlToJava.ftl");
        String content = FreemarkerUtil.doStringTemplate(templateStr, map);
        return content;
    }
    public void generateJava(XmlMetadata xmlMetadata) throws Exception {
        String content = generateJavaContent(xmlMetadata);
        String outWritePath = className+".java";
        FreemarkerUtil.writeTxt(outWritePath, content);
    }
    
    @SuppressWarnings("unchecked")
    public static XmlMetadata getXmlMetadataByFile(String filePath) throws DocumentException {
        XmlMetadata xmlMetadata = new XmlMetadata();
        String xml = TxtFileUtils.loadTxtStr(filePath, FILE_ENCONDING);
        Document doc = DocumentHelper.parseText(xml);
        return getXmlMetadata(doc);
    }
    @SuppressWarnings("unchecked")
    public static XmlMetadata getXmlMetadata(Document doc) {
        XmlMetadata xmlMetadata = new XmlMetadata();
        
        Element rootElement = doc.getRootElement();
        xmlMetadata.setRootName(rootElement.getName());
        List<FieldInfo> fieldList = new ArrayList<>();
        List<ChildClass> childClassList = new ArrayList<>();

        List<Element> elements = rootElement.elements();
        List<String> collect = elements.stream().map(Element::getName).distinct().collect(Collectors.toList());

        for (String nodeName : collect) {
            List<Element> curElement = rootElement.elements(nodeName);
            boolean isList = curElement.size() > 1;
            if(Boolean.FALSE.equals(hasList) && isList) {
                hasList = true;
            }
            Element element = curElement.get(0);
            if (element.hasMixedContent()) {
                buildChildClass(element, childClassList);
                fieldList.add(new FieldInfo(element.getName(), convertJavaClassType(element.getName(), isList),
                        convertJavaFieldName(element.getName())));
            } else {
                fieldList.add(new FieldInfo(element.getName(), convertJavaClassType("String", isList),
                        convertJavaFieldName(element.getName())));
            }
        }
        
        xmlMetadata.setFieldList(fieldList);
        xmlMetadata.setChildClass(childClassList);
        System.out.println(GSON.toJson(xmlMetadata));
        return xmlMetadata;
    }

    @SuppressWarnings("unchecked")
    public static void buildChildClass(Element rootElement, List<ChildClass> childClassList) {
        ChildClass childClass = new ChildClass();
        childClass.setClassName(convertJavaClassType(rootElement.getName(), false));
        List<FieldInfo> fieldList = new ArrayList<>(); 

        List<Element> elements = rootElement.elements();
        List<String> collect = elements.stream().map(Element::getName).distinct().collect(Collectors.toList());
        
        collect.forEach(e -> {
            List<Element> curElement = rootElement.elements(e);
            boolean isList = curElement.size() > 1;
            if(Boolean.FALSE.equals(hasList) && isList) {
                hasList = true;
            }
            Element element = curElement.get(0);
            if(element.hasMixedContent()) {
//                buildChildClass(element, childClassList);
                fieldList.add(new FieldInfo(element.getName(), convertJavaClassType(element.getName(), isList), convertJavaFieldName(element.getName())));
            } else {
                fieldList.add(new FieldInfo(element.getName(), convertJavaClassType("String", isList), convertJavaFieldName(element.getName())));
            }
        });
        childClass.setFieldList(fieldList);
        // 内部类成员变量超过3个，则增加@Builder注解
        childClass.setNeedBuild(fieldList.size() > 3);
        childClassList.add(childClass);

        collect.forEach(e -> {
            List<Element> curElement = rootElement.elements(e);
            Element element = curElement.get(0);
            if(element.hasMixedContent()) {
                buildChildClass(element, childClassList);
            }
        });
    }
    public static String convertJavaFieldName(String name) {
        return HumpUtils.lineToHump(name);
    }
    /**
     * 返回字段的类型
     * @param name
     * @param isList    是否List
     * @return
     */
    public static String convertJavaClassType(String name, boolean isList) {
        name = HumpUtils.lineToHump(name);
        String type = String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
        if(isList) {
            return String.format("List<%s>", type);
        }
        return type;
    }
}

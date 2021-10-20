package com.fangjun.util;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import javax.servlet.ServletContext;

import com.google.common.io.ByteStreams;
/**
 * @Description: TODO
 * @Author: fangjun
 * @CreateDate: 2020年9月9日 下午4:25:39
 * @Version: 1.0
 */
public class FreemarkerUtil {

    public static String replaceFtl(ServletContext servletContext, String path, String fileName, Map<String, ?> model)
            throws Exception {
        Configuration cfg = new Configuration();
        StringWriter result = new StringWriter();
        cfg.setServletContextForTemplateLoading(servletContext, path);
        Template t = cfg.getTemplate(fileName);
        t.setEncoding("utf-8");
        t.process(model, result);
        return result.toString();
    }

    public static String replaceFtl(String path, String fileName, Map<String, ?> model) throws Exception {
        Configuration cfg = new Configuration();
        StringWriter result = new StringWriter();
        cfg.setClassForTemplateLoading(FreemarkerUtil.class, path);
        cfg.setDefaultEncoding("UTF-8");
        Template t = cfg.getTemplate(fileName);
        t.setEncoding("UTF-8");
        t.setOutputEncoding("UTF-8");
        t.process(model, result);
        return result.toString();
    }

    public static String renderString(String templateString, Map<String, ?> model) throws Exception {
        try {
            StringWriter result = new StringWriter();
            Template t = new Template("name", new StringReader(templateString), new Configuration());
            t.process(model, result);
            System.out.println(result.toString());
            return result.toString();
        } catch (Exception var4) {
            throw new Exception("");
        }
    }

    public static String doStringTemplate(String mb, Map<String, Object> datas) throws Exception {
        Configuration cfg = new Configuration();
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("templateTemp", mb);
        cfg.setTemplateLoader(stringLoader);
        Template template = cfg.getTemplate("templateTemp", "utf-8");
        StringWriter writer = new StringWriter();

        try {
            template.process(datas, writer);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return writer.toString();
    }

    public static void creatExcel(String mb, Map<String, Object> datas, String path, String fileName) throws Exception {
        Configuration cfg = new Configuration();
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("templateTemp", mb);
        cfg.setTemplateLoader(stringLoader);
        Template template = cfg.getTemplate("templateTemp", "utf-8");
        File saveDir = new File(path);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        File outFile = new File(path + fileName);
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
            template.process(datas, out);
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            out.close();
        }

    }

    public static void creatWord(String mb, Map<String, Object> datas, String path, String fileName)
            throws IOException, Exception {
        Configuration cfg = new Configuration();
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("templateTemp", mb);
        cfg.setTemplateLoader(stringLoader);
        Template template = cfg.getTemplate("templateTemp", "utf-8");
        File saveDir = new File(path);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        File outFile = new File(path + fileName);
        Writer out = null;
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
        new StringWriter();
        if (datas != null) {
            for (int i = 0; i < datas.size(); ++i) {
                template.process(datas.get("data" + i), out);
            }
        }

        out.flush();
        out.close();
    }

    public static String loadTemplate(String fileName) throws Exception {
        String str = null;
        try (InputStream inputStream = FreemarkerUtil.class.getResourceAsStream(fileName);) {
            str = new String(ByteStreams.toByteArray(inputStream), "UTF-8");
        }
        return str;
    }
    public static void writeTxt(String filePath, String content) throws Exception {
        File file = new File(filePath);
        file.createNewFile();
        try(FileOutputStream outputStream =  new FileOutputStream(file)) {
            outputStream.write(content.getBytes());
            outputStream.flush();
        }
    }
}

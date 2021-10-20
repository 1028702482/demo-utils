package com.fangjun.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.io.ByteStreams;

/**
 * @Description: TODO
 * @Author: fangjun
 * @CreateDate: 2020年9月16日 上午9:59:06
 * @Version: 1.0
 */
public class TxtFileUtils {

    
    public static void printFileNameByAndStr(List<File> files, String[] rexArr, String fileEnconding) {
        files.stream().forEach(e -> printFileNameByAndStr(e, rexArr, fileEnconding));
    }
    /**
     * 文本文件中匹配关键字则打印该文件名
     * @param file
     * @param rexArr    同时匹配
     */
    public static void printFileNameByAndStr(File file, String[] rexArr, String fileEnconding)  {
        try (FileInputStream reader = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(reader, fileEnconding));) {
            String line = null;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                if(checkStrByAndArr(line, rexArr)) {
                    System.out.println(file.getName()+"\tline:"+lineNum);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * rexArr 全部匹配才返回true
     * @param str
     * @param rexArr
     * @return
     */
    private static boolean checkStrByAndArr(String str, String[] rexArr) {
        for(String rex:rexArr) {
            if(str.indexOf(rex)==-1) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 读取文本文件
     * @param path  文件路径
     * @param fileEnconding 编码
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public static String loadTxtStr(String path,String fileEnconding)  {
        String str = "";
        try (InputStream inputStream = new FileInputStream(path);) {
            if (inputStream == null) {
                System.err.println("文件不存在！");
            }
            str = new String(ByteStreams.toByteArray(inputStream), fileEnconding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    
    /**
     * 读取文本文件
     * @param path  文件路径
     * @param fileEnconding 编码
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public static String loadTxtStr(File file,String fileEnconding)  {
        String str = "";
        try (InputStream inputStream = new FileInputStream(file);) {
            if (inputStream == null) {
                System.err.println("文件不存在！");
            }
            str = new String(ByteStreams.toByteArray(inputStream), fileEnconding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    
    public static String getByIntercept(String path, String index1, String index2, String fileEnconding) {
        String str = "";
        try {
        str = loadTxtStr(path, fileEnconding);
        int start = str.indexOf(index1) + index1.length();
        int end = str.indexOf(index2);
        str = str.substring(start, end);
        }catch (Exception e) {
            System.err.println("文件截取失败："+path);
            str = "";
        }
        return str.trim();
    }
    
    public static void writeFile(String fileName, String content) {
        try {
            File writeName = new File(fileName); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName); BufferedWriter out = new BufferedWriter(writer)) {
                out.write(content); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

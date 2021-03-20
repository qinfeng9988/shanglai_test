package com.qjfcc.test20210308.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * @Auther: qinjiangfeng
 * @Date: 2021/3/2 13:56
 * @Description:
 */
public class CrawTextThread extends Thread {
    List<String> UrlList;

    public CrawTextThread(List<String> urlList) {
        this.UrlList = urlList;
    }

    String rule = "";
    String rule_title = "h1";
    String rule_content = "content";

    public static String PATH = "D:\\JSOUP\\";

    /**
     * 创建文件
     *
     * @param fileName
     * @return
     */
    public static void createFile(File fileName) throws Exception {
        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void writeTxtFile(String content, File fileName) throws Exception {
        RandomAccessFile mm = null;
        FileOutputStream o = null;
        try {
            o = new FileOutputStream(fileName);
            o.write(content.getBytes("UTF-8"));
            o.close();
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (mm != null) {
                mm.close();
            }
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("一个都别跑:");

        String title;

        String content;

        for (String url : UrlList) {
            try {

                Document document = Jsoup.connect(url).timeout(6000).get();
                title = document.select("h1").toString();
                content = document.select("#content").html();

                System.out.println("线程:"+Thread.currentThread().getName()+"爬取URL—>"+url);
                File file = new File(PATH+title.replaceAll("<h1>", "").replaceAll("</h1>", "")+".txt");
                createFile(file);
                System.out.println("创建文件:"+file.getPath());
                writeTxtFile(FileterHtml(content), file);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static String FileterHtml(String str) {
        return str.replaceAll(" ", "").replaceAll("<br>", "\r\n");

    }
}

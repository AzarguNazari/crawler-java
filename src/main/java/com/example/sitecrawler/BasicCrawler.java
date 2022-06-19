package com.example.sitecrawler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import static com.example.sitecrawler.SiteCrawlerApplication.DirectoryToStorePages;

public class BasicCrawler extends WebCrawler {
    
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return href.startsWith("http");
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            Set<WebURL> links = htmlParseData.getOutgoingUrls().stream().filter(webURL -> {
                return !(webURL.getURL().endsWith("png") ||
                        webURL.getURL().endsWith("jpg") ||webURL.getURL().endsWith("bmp") ||
                        webURL.getURL().endsWith("js") ||
                        webURL.getURL().endsWith("css") ||
                        webURL.getURL().endsWith("bmp"));
            }).collect(Collectors.toSet());
            for (WebURL link : links) {
                System.out.println(link.getURL());
            }
            System.out.println(links.size());
            links.forEach(this::downloadPage);
        }
    }

    public void downloadPage(WebURL webUrl){
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        try {
            url = new URL(webUrl.getURL());
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            StringBuilder str = new StringBuilder();
            while ((line = br.readLine()) != null) {
                str.append(line);
            }

            File file = new File(DirectoryToStorePages, webUrl.getDomain() + webUrl.getSubDomain() + ".html");
            if(!file.exists()) file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(str.toString());
            fileWriter.close();
        } catch (IOException mue) {
            mue.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
    }
}
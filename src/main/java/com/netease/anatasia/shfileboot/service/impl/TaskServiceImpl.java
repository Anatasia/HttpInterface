package com.netease.anatasia.shfileboot.service.impl;

import com.netease.anatasia.shfileboot.service.TaskService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Created by xujuan1 on 2017/7/3.
 */
@Service
public class TaskServiceImpl implements TaskService{
    private static Logger logger = Logger.getLogger(TaskServiceImpl.class);
    public String getTaskInfo(String proId){
        String taskInfo = "";
        Document document = parse("classpath:config.xml");
        if(document==null) return "Analyze config file failed";
        NodeList nodes = document.getElementsByTagName("project");
        for(int i=0;i<nodes.getLength();i++){
            Node node = nodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element projectElement = (Element) node;
                String id = projectElement.getAttribute("id");
                if(id.equals(proId)){
                    NodeList scriptList = projectElement.getChildNodes();
                    for(int j=0;j<scriptList.getLength();j++){
                        Node scriptNode = scriptList.item(j);
                        String scriptPath = scriptNode.getTextContent();
                        if(scriptPath.endsWith(".sh")){
                            taskInfo += executeFile(scriptPath);
                            logger.info("scriptPath = " +scriptPath);
                        }

                    }
                    break;
                }
            }

        }
        return taskInfo;
    }


    public Document parse(String filePath){
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;
        if(StringUtils.isEmpty(filePath)) return document;
        File file = null;
        try {
            file = ResourceUtils.getFile(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(!file.exists())
        {
            logger.info("can not find config file");
            return document;
        }
        logger.info("file exist");
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(file);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            logger.info(e.getStackTrace());
        } catch (SAXException e) {
            e.printStackTrace();
            logger.info(e.getStackTrace());
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getStackTrace());
        }
        return document;
    }

    private String executeFile(String filePath){
        String resultInfo = "";
        if(!filePath.endsWith("sh")){
            return "This is not a sh file!";
        }
        try {
            Process ps = Runtime.getRuntime().exec("sh "+filePath);
            int ev = ps.waitFor();
            logger.info("ev="+ev);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(";");
            }
            resultInfo = sb.toString();
            if(resultInfo.equals("")||resultInfo==null||resultInfo.isEmpty()){
                resultInfo="Fail to execute shell file! The file path is : " + filePath;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return resultInfo;
    }
}

package com.example.stylemigration.utils;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.net.NetManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.net.MalformedURLException;



public class myHTTP {
    private static final HiLogLabel LABEL1 = new HiLogLabel(HiLog.LOG_APP, 0x00202, "Upload");
    private static final HiLogLabel LABEL2 = new HiLogLabel(HiLog.LOG_APP, 0x00204, "Upload");
    public static void formUpload(String urlStr, Map textMap, Map fileMap){

        String res = "";
        HttpURLConnection conn = null;
        //boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "---------------------------123821742118716";

        try{
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream((conn.getOutputStream()));

            //text
            if (textMap != null){
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()){
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if(inputValue == null){
                        continue;
                    }
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                    System.out.println(inputName+","+inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }
            //file

            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();

//                    //没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
//                    contentType = new MimetypesFileTypeMap().getContentType(file);
//                    //contentType非空采用filename匹配默认的图片类型
//                    if(!"".equals(contentType)){
//                        if (filename.endsWith(".png")) {
//                            contentType = "image/png";
//                        }else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
//                            contentType = "image/jpeg";
//                        }else if (filename.endsWith(".gif")) {
//                            contentType = "image/gif";
//                        }else if (filename.endsWith(".ico")) {
//                            contentType = "image/image/x-icon";
//                        }
//                    }
//                    if (contentType == null || "".equals(contentType)) {
//                        contentType = "application/octet-stream";
//                    }
                    StringBuffer strBuf = new StringBuffer();
//                    strBuf.append("\r\n").append("--").append(BOUNDARY)
//                            .append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\""
//                            + inputName + "\"; filename=\"" + filename
//                            + "\"\r\n");
                    System.out.println(inputName + "," + filename);

//                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes());
                    DataInputStream in = new DataInputStream(
                            new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();

                }
//                byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
//                out.write(endData);
                out.flush();
                out.close();

                int responseCode = conn.getResponseCode(); // 获取返回码
                String s = Integer.toString(responseCode); //转换为string
                HiLog.info(LABEL2, "HTTP Response Status: " + s);
                // 读取返回数据
                StringBuffer strBuf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strBuf.append(line).append("\n");
                }
                res = strBuf.toString();
                HiLog.info(LABEL1, res);
                reader.close();
                reader = null;

            }
        }catch (Exception e){
            System.out.println("发送POST请求出错。"+urlStr);
            e.printStackTrace();
        }finally {
            if (conn != null){
                conn.disconnect();
//                conn = null;

            }
        }
    }
    // 从服务器中获取图片并保存至本地
    public static void saveImageToDisk(String urlStrDld){
        InputStream inputStream = null;
        inputStream = getInputStream(urlStrDld);
        byte[] data = new byte[1024];
        int len = 0;
        FileOutputStream fileOutputStream = null;
        try{
            String uri = "D:\\Study\\DevEcoStudioProjects\\StyleMigration\\entry\\src\\main\\resources\\base\\media\\test_fake.png";
            fileOutputStream = new FileOutputStream(uri);
            //循环读取inputStream流中的数据，存入文件流
            while((len = inputStream.read(data))!=-1){
                fileOutputStream.write(data,0,len);

            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }finally{
            //finally函数，不管有没有异常发生，都要调用这个函数下的代码
            if(fileOutputStream!=null){
                try{
                    fileOutputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(inputStream!=null){
                try{
                    inputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static InputStream getInputStream(String url_path){
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(url_path);
            if (url!=null){
                httpURLConnection = (HttpURLConnection) url.openConnection(); // 打开链接
                httpURLConnection.setConnectTimeout(3000); // 超时重新链接，3s
                httpURLConnection.setDoInput(true);  // 打开输入流
                httpURLConnection.setRequestMethod("GET"); // 以GET方式请求
                int responseCode = httpURLConnection.getResponseCode(); // 获取返回码
                if (responseCode == 200){
                    inputStream = httpURLConnection.getInputStream();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if(null!=httpURLConnection){
                httpURLConnection.disconnect();
            }
        }
        return inputStream;
    }


}

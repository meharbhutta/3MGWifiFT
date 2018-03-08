package com.bhutta.mmmgwifift;

/**
 * Created by Muhammad Mehar on 9/26/2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class HTTPPOSTServer extends Thread {
    private static final String HTML_START = "<html>" +
            "<title>3MG WifiFT</title>" +
            "<body>";
    private static final String HTML_END = "</body>" + "</html>";
    private Socket connectedClient = null;
    private DataOutputStream outToClient = null;
    private String addressport;
    private int port;
    private Context context;


    public HTTPPOSTServer(Socket client, String address, int port, Context context) {
        this.connectedClient = client;
        this.addressport = address;
        this.port = port;
        this.context = context;
    }

    public void run() {
        String currentLine;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-hh:mm:ss z", Locale.getDefault());
        try {
            String targetPath = "http://" + addressport.trim() + ":" + port;
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
            outToClient = new DataOutputStream(connectedClient.getOutputStream());
            currentLine = inFromClient.readLine();
            String headerLine = currentLine;
            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            String httpQueryString = tokenizer.nextToken();
            if (httpMethod.equals("GET")) {
                if (httpQueryString.equals("/favicon.ico")){
                    File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bhutta.mmmgwifift/favicon.ico");
                    if (dirFile.exists()) {
                        String responseString = dirFile.getAbsolutePath();
                        sendResponse(200, responseString, true, false);
                    }
                } else if (httpQueryString.equals("/video.png")){
                    File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bhutta.mmmgwifift/video.png");
                    if (dirFile.exists()) {
                        String responseString = dirFile.getAbsolutePath();
                        sendResponse(200, responseString, true, false);
                    }
                } else if (httpQueryString.equals("/music.png")){
                    File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bhutta.mmmgwifift/music.png");
                    if (dirFile.exists()) {
                        String responseString = dirFile.getAbsolutePath();
                        sendResponse(200, responseString, true, false);
                    }
                } else if (httpQueryString.equals("/pa_folder.png")){
                    File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bhutta.mmmgwifift/pa_folder.png");
                    if (dirFile.exists()) {
                        String responseString = dirFile.getAbsolutePath();
                        sendResponse(200, responseString, true, false);
                    }
                } else if (httpQueryString.equals("/folder.png")){
                    File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bhutta.mmmgwifift/folder.png");
                    if (dirFile.exists()) {
                        String responseString = dirFile.getAbsolutePath();
                        sendResponse(200, responseString, true, false);
                    }
                } else if (httpQueryString.equals("/document_file.png")){
                    File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bhutta.mmmgwifift/document_file.png");
                    if (dirFile.exists()) {
                        String responseString = dirFile.getAbsolutePath();
                        sendResponse(200, responseString, true, false);
                    }
                } else if (httpQueryString.equals("/compressed.png")){
                    File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bhutta.mmmgwifift/compressed.png");
                    if (dirFile.exists()) {
                        String responseString = dirFile.getAbsolutePath();
                        sendResponse(200, responseString, true, false);
                    }
                } else if (httpQueryString.equals("/unknown_file.png")){
                    File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bhutta.mmmgwifift/unknown_file.png");
                    if (dirFile.exists()) {
                        String responseString = dirFile.getAbsolutePath();
                        sendResponse(200, responseString, true, false);
                    }
                } else {
                    ArrayList<Uri> shareDataUri = null;
                    File dirFile = null;
                    httpQueryString = decodeFromFirebaseData(httpQueryString.replace("/storage/", "/"));
                    if (context instanceof ShareActivity) {
                        if (httpQueryString.equals("/")) shareDataUri = ShareActivity.getShareDataUri();
                        else {
                            httpQueryString = httpQueryString.substring(1);
                            sendResponse( 200, httpQueryString, false, true);
                            return;
                        }
                    } else if (httpQueryString.equals("/0/") || httpQueryString.equals("/emulated/")) {
                        dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                    } else if (httpQueryString.equals("/") || httpQueryString.equals("/storage/")) {
                        dirFile = new File(Environment.getExternalStorageDirectory().getParent()).getParentFile();
                    } else {
                        dirFile = new File(Environment.getExternalStorageDirectory().getParentFile().getParentFile().getAbsolutePath() + httpQueryString);
                    }
                    String responseString;
                    if (shareDataUri != null && shareDataUri.size() > 1) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("\n<html>");
                        sb.append("\n<head>");
                        sb.append("\n<style>");
                        sb.append("\n.datagrid table { border-collapse: collapse; text-align: left; width: 100%; } .datagrid {font: normal 12px/150% Arial, Helvetica, sans-serif; background: #fff; overflow: hidden; border: 1px solid #006699; -webkit-border-radius: 3px; -moz-border-radius: 3px; border-radius: 3px; }.datagrid table td, .datagrid table th { padding: 3px 10px; }.datagrid table thead th {background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 70% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; color:#ffffff; font-size: 15px; font-weight: bold; border-left: 1px solid #0070A8; } .datagrid table thead th:first-child { border: none; }.datagrid table tbody td { color: #00496B; border-left: 1px solid #E1EEF4;font-size: 12px;font-weight: normal; }.datagrid table tbody .alt td { background: #E1EEF4; color: #00496B; }.datagrid table tbody td:first-child { border-left: none; }.datagrid table tbody tr:last-child td { border-bottom: none; }.datagrid table tfoot td div { border-top: 1px solid #006699;background: #E1EEF4;} .datagrid table tfoot td { padding: 0; font-size: 12px } .datagrid table tfoot td div{ padding: 2px; }.datagrid table tfoot td ul { margin: 0; padding:0; list-style: none; text-align: right; }.datagrid table tfoot  li { display: inline; }.datagrid table tfoot li a { text-decoration: none; display: inline-block;  padding: 2px 8px; margin: 1px;color: #FFFFFF;border: 1px solid #006699;-webkit-border-radius: 3px; -moz-border-radius: 3px; border-radius: 3px; background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 70% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; }.datagrid table tfoot ul.active, .datagrid table tfoot ul a:hover { text-decoration: none;border-color: #006699; color: #FFFFFF; background: none; background-color:#00557F;}div.dhtmlx_window_active, div.dhx_modal_cover_dv { position: fixed !important; }");
                        sb.append("\n</style>");
                        sb.append("\n<title>List of files need to be share</title>");
                        sb.append("\n</head>");
                        sb.append("\n<body>");
                        sb.append("\n<div class=\"datagrid\">");
                        sb.append("\n<table>");
                        sb.append("\n<caption>Share Files Listing</caption>");
                        sb.append("\n<thead>");
                        sb.append("\n<tr>");
                        sb.append("\n<th>File</th>");
                        sb.append("\n<th>Type</th>");
                        sb.append("\n<th>Size</th>");
                        sb.append("\n</tr>");
                        sb.append("\n</thead>");
                        sb.append("\n<tfoot>");
                        sb.append("\n<tr>");
                        sb.append("\n<th>File</th>");
                        sb.append("\n<th>Type</th>");
                        sb.append("\n<th>Size</th>");
                        sb.append("\n</tr>");
                        sb.append("\n</tfoot>");
                        sb.append("\n<tbody>");
                        for(Uri uri : shareDataUri) {
                            String srcImg = null;
                            String fileName = null;
                            String type = null;
                            double size = 0;
                            String pathSet = null;
                            if (!uri.toString().contains("file:///")) {
                                Cursor cursor = context.getContentResolver().query(
                                        uri,
                                        null,
                                        null,
                                        null,
                                        null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    switch (fileType(fileName)) {
                                        case 1:
                                            srcImg = "/document_file.png";
                                            break;
                                        case 2:
                                            srcImg = httpQueryString + encodeAsFirebaseData(uri.toString());
                                            break;
                                        case 3:
                                            srcImg = "/video.png";
                                            break;
                                        case 4:
                                            srcImg = "/music.png";
                                            break;
                                        case 5:
                                            srcImg = "/compressed.png";
                                            break;
                                        default:
                                            srcImg = "/unknown_file.png";
                                    }
                                    type = context.getContentResolver().getType(uri);
                                    size = cursor.getDouble(cursor.getColumnIndex(OpenableColumns.SIZE));
                                    pathSet = encodeAsFirebaseData(uri.toString());
                                    cursor.close();
                                }
                            } else {
                                File file = new File(uri.getPath());
                                if (file.exists()) {
                                    fileName = file.getName();
                                    switch (fileType(fileName)) {
                                        case 1:
                                            srcImg = "/document_file.png";
                                            break;
                                        case 2:
                                            srcImg = httpQueryString + encodeAsFirebaseData(uri.toString());
                                            break;
                                        case 3:
                                            srcImg = "/video.png";
                                            break;
                                        case 4:
                                            srcImg = "/music.png";
                                            break;
                                        case 5:
                                            srcImg = "/compressed.png";
                                            break;
                                        default:
                                            srcImg = "/unknown_file.png";
                                    }
                                    type = fileName.substring(fileName.lastIndexOf(".")+1);
                                    size = file.length();
                                    pathSet = encodeAsFirebaseData(uri.toString());
                                }
                            }
                            if (fileName != null && size > 0) {
                                sb.append("\n\t\t<td><a href='" + targetPath + httpQueryString + pathSet + "'><img src='" + targetPath + srcImg + "' style='padding-right: 10;padding-top: 5;padding-bottom: 5px;' align='middle' height='50' width='50' />" + fileName + "</a></td>" +
                                        "<td>" + type + "</td>" + "<td>" + getFormatSize(size) +
                                        "</td>" + "\n\t</tr>");
                            }
                        }
                        sb.append("\n</tbody>");
                        sb.append("\n</table>");
                        sb.append("\n</div>");
                        sb.append("\n</body>");
                        sb.append("\n</html>");
                        responseString = sb.toString();
                        sendResponse(200, responseString, false, false);
                    } else if (shareDataUri != null && !shareDataUri.isEmpty() && shareDataUri.size() == 1) {
                        responseString = shareDataUri.get(0).toString();
                        sendResponse( 200, responseString, false, true);
                    } else if (dirFile != null && dirFile.exists() && dirFile.isDirectory()) {
                        if (dirFile.canRead()) {
                            File checkFiles[] = dirFile.listFiles();
                            List<File> lsFiles = new ArrayList<>();
                            for (File file : checkFiles){
                                if (file.canRead()){
                                    lsFiles.add(file);
                                } else if (file.getName().equals("emulated")){
                                    lsFiles.add(file);
                                }
                            }
                            StringBuilder sb = new StringBuilder();
                            sb.append("\n<html>");
                            sb.append("\n<head>");
                            sb.append("\n<style>");
                            sb.append("\n.uploadFile{text-align: left;margin-top: 8%;} .datagrid table { border-collapse: collapse; text-align: left; width: 100%; } .datagrid {font: normal 12px/150% Arial, Helvetica, sans-serif; background: #fff; overflow: hidden; border: 1px solid #006699; -webkit-border-radius: 3px; -moz-border-radius: 3px; border-radius: 3px; }.datagrid table td, .datagrid table th { padding: 3px 10px; }.datagrid table thead th {background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 70% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; color:#ffffff; font-size: 15px; font-weight: bold; border-left: 1px solid #0070A8; } .datagrid table thead th:first-child { border: none; }.datagrid table tbody td { color: #00496B; border-left: 1px solid #E1EEF4;font-size: 12px;font-weight: normal; }.datagrid table tbody .alt td { background: #E1EEF4; color: #00496B; }.datagrid table tbody td:first-child { border-left: none; }.datagrid table tbody tr:last-child td { border-bottom: none; }.datagrid table tfoot td div { border-top: 1px solid #006699;background: #E1EEF4;} .datagrid table tfoot td { padding: 0; font-size: 12px } .datagrid table tfoot td div{ padding: 2px; }.datagrid table tfoot td ul { margin: 0; padding:0; list-style: none; text-align: right; }.datagrid table tfoot  li { display: inline; }.datagrid table tfoot li a { text-decoration: none; display: inline-block;  padding: 2px 8px; margin: 1px;color: #FFFFFF;border: 1px solid #006699;-webkit-border-radius: 3px; -moz-border-radius: 3px; border-radius: 3px; background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 70% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; }.datagrid table tfoot ul.active, .datagrid table tfoot ul a:hover { text-decoration: none;border-color: #006699; color: #FFFFFF; background: none; background-color:#00557F;}div.dhtmlx_window_active, div.dhx_modal_cover_dv { position: fixed !important; }");
                            sb.append("\n</style>");
                            sb.append("\n<title>List of files/dirs under /sdcard0/</title>");
                            sb.append("\n</head>");
                            sb.append("\n<body>");
                            sb.append("\n<div class=\"uploadFile\">");
                            sb.append("\n</div>");
                            sb.append("\n<div class=\"datagrid\">");
                            sb.append("\n<table>");
                            sb.append("\n<caption>Directory Listing</caption>");
                            sb.append("\n<thead>");
                            sb.append("\n<tr>");
                            sb.append("\n<th>File</th>");
                            sb.append("\n<th>Dir ?</th>");
                            sb.append("\n<th>Size</th>");
                            sb.append("\n<th>Date</th>");
                            sb.append("\n</tr>");
                            sb.append("\n</thead>");
                            sb.append("\n<tfoot>");
                            sb.append("\n<tr>");
                            sb.append("\n<th>File</th>");
                            sb.append("\n<th>Dir ?</th>");
                            sb.append("\n<th>Size</th>");
                            sb.append("\n<th>Date</th>");
                            sb.append("\n</tr>");
                            sb.append("\n</tfoot>");
                            sb.append("\n<tbody>");
                            if (!httpQueryString.equals("/") && !httpQueryString.equals("/storage/") && dirFile.getParent() != null) {
                                File parent;
                                if (httpQueryString.equals("/0/") || httpQueryString.equals("/emulated/"))
                                    parent = new File(dirFile.getParent()).getParentFile();
                                else parent = new File(dirFile.getParent());
                                sb.append("\n\t<tr>");
                                if (parent.getName().equals("0")) parent = parent.getParentFile();
                                sb.append("\n\t\t<td><a href='" + targetPath + parent + "/'><img src='"+ targetPath + "/pa_folder.png' align='middle' style='padding-right: 10;padding-top: 5;padding-bottom: 5px;' height='50' width='50'/>" + "GoTo Parent" + "</a></td>" +
                                        "<td>Yes</td>" + "<td>" + getFormatSize(getDirSize(parent)) +
                                        "</td>" + "<td>" + formatter.format(new Date(parent.lastModified())) + "</td>\n\t</tr>");
                            }
                            if (lsFiles != null) {
                                if (httpQueryString.equals("/emulated/")) httpQueryString += "0/";
                                else if (httpQueryString.equals("/0/"))
                                    httpQueryString = "/emulated" + httpQueryString;
                                int i = 0;
                                for (File file : lsFiles) {
                                    if (i % 2 == 0)
                                        sb.append("\n\t<tr class='alt'>");
                                    else
                                        sb.append("\n\t<tr>");
                                    if (file.isDirectory())
                                        sb.append("\n\t\t<td><a href='" + targetPath + httpQueryString + file.getName() + "/'><img src='" + targetPath + "/folder.png' style='padding-right: 10;padding-top: 5;padding-bottom: 5px;' align='middle' height='50' width='50'/>" + file.getName() + "</a></td>" +
                                                "<td>Yes</td>" + "<td>" + getFormatSize(getDirSize(file)) +
                                                "</td>" + "<td>" + formatter.format(new Date(file.lastModified())) + "</td>\n\t</tr>");
                                    else {
                                        String srcImg;
                                        switch (fileType(file.getName())){
                                            case 1:
                                                srcImg = "/document_file.png";
                                                break;
                                            case 2:
                                                srcImg = httpQueryString + file.getName();
                                                break;
                                            case 3:
                                                srcImg = "/video.png";
                                                break;
                                            case 4:
                                                srcImg = "/music.png";
                                                break;
                                            case 5:
                                                srcImg = "/compressed.png";
                                                break;
                                            default:
                                                srcImg = "/unknown_file.png";
                                        }
                                        sb.append("\n\t\t<td><a href='" + targetPath + httpQueryString + file.getName() + "'><img src='" + targetPath + srcImg + "' style='padding-right: 10;padding-top: 5;padding-bottom: 5px;' align='middle' height='50' width='50' />" + file.getName() + "</a></td>" +
                                                "<td> </td>" + "<td>" + getFormatSize(file.length()) +
                                                "</td>" + "<td>" + formatter.format(new Date(file.lastModified())) + "</td>\n\t</tr>");
                                    }
                                    i++;
                                }
                            }
                            sb.append("\n</tbody>");
                            sb.append("\n</table>");
                            sb.append("\n</div>");
                            sb.append("\n</body>");
                            sb.append("\n</html>");
                            responseString = sb.toString();
                            sendResponse(200, responseString, false, false);
                        }
                    } else if (dirFile != null && dirFile.exists() && dirFile.isFile()){
                        responseString = dirFile.getAbsolutePath();
                        sendResponse(200, responseString, true, false);
                    } else {
                        sendResponse(404, "<b>The Requested resource not found ...." +
                                "Usage: http://" + addressport + ":" + port + "</b>", false, false);
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private byte fileType(String filename){
        String[] temp = filename.split("\\.");
        switch (temp[temp.length - 1].toLowerCase()){
            case "txt":
            case "xlsx":
            case "xls":
            case "gslides":
            case "ppt":
            case "pptx":
            case "pdf":
            case "doc":
            case "docx":
            case "docm":
                return 1;
            case "gif":
            case "tif":
            case "ico":
            case "jpg":
            case "jpeg":
            case "png":
            case "tiff":
                return 2;
            case "3gp":
            case "avi":
            case "flv":
            case "m4v":
            case "mkv":
            case "mov":
            case "mng":
            case "mpeg":
            case "mpg":
            case "mpe":
            case "mp4":
            case "wmv":
            case "webm":
                return 3;
            case "mp1":
            case "mp2":
            case "mp3":
            case "aac":
            case "wma":
            case "amr":
            case "wav":
                return 4;
            case "zip":
            case "7z":
            case "cab":
            case "gzip":
            case "bin":
            case "rar":
            case "tar":
            case "iso":
                return 5;
            default:
                return 0;
        }
    }

    private void sendResponse(int statusCode, String responseString, boolean isFile, boolean isUri)
            throws Exception {
        String statusLine;
        String serverdetails = "Server: Java HTTPServer";
        String contentLengthLine;
        String fileName;
        String contentTypeLine = "Content type: text/html" + "\r\n";
        FileInputStream fin;
        if (statusCode == 200)
            statusLine = "HTTP/1.1 200 OK" + "\r\n";
        else
            statusLine = "HTTP/1.1 404 Not Found" + "\r\n";
        if (isUri) {
            Uri uri = Uri.parse(responseString);
            if (!responseString.contains("file:///")) {
                if (context.checkUriPermission(
                        uri,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.os.Process.myPid(),
                        android.os.Process.myUid(),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        == PackageManager.PERMISSION_GRANTED) {
                    InputStream in = context.getContentResolver().openInputStream(uri);
                    if (in != null) {
                        contentLengthLine = "Content length: " + Integer.toString(in.available()) + "\r\n";
                        outToClient.writeBytes(statusLine);
                        outToClient.writeBytes(serverdetails);
                        outToClient.writeBytes(contentTypeLine);
                        outToClient.writeBytes(contentLengthLine);
                        outToClient.writeBytes("\r\n");
                        shareFile(in, outToClient);
                        outToClient.close();
                    }
                } else {
                    responseString = HTTPPOSTServer.HTML_START + "<b>Internal Server Error ...." +
                            "Usage: http://" + addressport + ":" + port + "</b>" + HTTPPOSTServer.HTML_END;
                    contentLengthLine = "Content length: " + responseString.length() + "\r\n";
                    PrintWriter pout = new PrintWriter(outToClient);
                    pout.print("HTTP/1.1 404 Not Found" + "\r\n");
                    pout.print(serverdetails);
                    pout.print(contentTypeLine);
                    pout.print(contentLengthLine);
                    pout.print("\r\n");
                    pout.print(responseString + "\r\n");
                    pout.close();
                    outToClient.close();
                }
            } else {
                fin = new FileInputStream(new File(uri.getPath()));
                contentLengthLine = "Content length: " + Integer.toString(fin.available()) + "\r\n";
                outToClient.writeBytes(statusLine);
                outToClient.writeBytes(serverdetails);
                outToClient.writeBytes(contentTypeLine);
                outToClient.writeBytes(contentLengthLine);
                outToClient.writeBytes("\r\n");
                sendFile(fin, outToClient);
                outToClient.close();
            }
        } else if (isFile) {
            fileName = responseString;
            fin = new FileInputStream(fileName);
            contentLengthLine = "Content length: " + Integer.toString(fin.available()) + "\r\n";
            if (!fileName.endsWith(".htm") && !fileName.endsWith(".html"))
                contentTypeLine = "Content type: \r\n";
            outToClient.writeBytes(statusLine);
            outToClient.writeBytes(serverdetails);
            outToClient.writeBytes(contentTypeLine);
            outToClient.writeBytes(contentLengthLine);
            outToClient.writeBytes("\r\n");
            sendFile(fin, outToClient);
            outToClient.close();
        } else {
            responseString = HTTPPOSTServer.HTML_START + responseString + HTTPPOSTServer.HTML_END;
            contentLengthLine = "Content length: " + responseString.length() + "\r\n";
            PrintWriter pout = new PrintWriter(outToClient);
            pout.print(statusLine);
            pout.print(serverdetails);
            pout.print(contentTypeLine);
            pout.print(contentLengthLine);
            pout.print("\r\n");
            pout.print(responseString + "\r\n");
            pout.close();
            outToClient.close();
        }

    }

    private void sendFile(FileInputStream fin, DataOutputStream out)
            throws Exception {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fin.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        fin.close();
    }

    private void shareFile(InputStream fin, DataOutputStream out)
            throws Exception {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fin.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        fin.close();
    }

    private String getFormatSize(double size){
        final double KB = 1024.00;
        final double MB = 1024.00 * KB;
        final double GB = 1024.00 * MB;
        if (size/KB < 0.90) {
            return String.format( Locale.getDefault(),"%.2f",size) + " B";
        } else if (size/MB < 0.90) {
            return String.format( Locale.getDefault(),"%.2f",size/KB) + " KB";
        } else if (size/GB  < 0.90) {
            return String.format( Locale.getDefault(),"%.2f", size/MB) + " MB";
        } else {
            return String.format( Locale.getDefault(),"%.2f",size/GB) + " GB";
        }
    }

    private long getDirSize(File dir){
        long size = 0;
        if (dir.isDirectory() && dir.canRead()){
            for (File file : dir.listFiles()){
                if (file.isFile() && file.canRead()){
                    size += file.length();
                } else {
                    size += getDirSize(file);
                }
            }
        } else if (dir.isFile() && dir.canRead()){
            size += dir.length();
        } else if (dir.getName().equals("emulated")){
            size += getDirSize(new File(dir.getAbsolutePath() + "/0"));
        }
        return size;
    }

    private String decodeFromFirebaseData(String string) {
        return string
                .replace("%2E", ".")
                .replace("%23", "#")
                .replace("%24", "$")
                .replace("%2F", "/")
                .replace("%5B", "[")
                .replace("%20", " ")
                .replace("%5D", "]")
                .replace("%5C", "\\")
                .replace("%21", "!")
                .replace("%22", "\"")
                .replace("%26", "&")
                .replace("%27", "\'")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%2A", "*")
                .replace("%2C", ",")
                .replace("%3A", ":")
                .replace("%3B", ";")
                .replace("%3C", "<")
                .replace("%3D", "=")
                .replace("%3E", ">")
                .replace("%3F", "?")
                .replace("%40", "@")
                .replace("%5F", "^")
                .replace("%60", "`")
                .replace("%7B", "{")
                .replace("%7C", "|")
                .replace("%7D", "}")
                .replace("%7E", "~")
                .replace("%25", "%");
    }

    private String encodeAsFirebaseData(String string) {
        return string
                .replace("%", "%25")
                .replace(".", "%2E")
                .replace("#", "%23")
                .replace("$", "%24")
                .replace("/", "%2F")
                .replace("[", "%5B")
                .replace(" ", "%20")
                .replace("]", "%5D")
                .replace("\\", "%5C")
                .replace("!", "%21")
                . replace("\"", "%22")
                . replace("&", "%26")
                . replace("\'", "%27")
                . replace("(", "%28")
                . replace(")", "%29")
                . replace("*", "%2A")
                . replace(",", "%2C")
                . replace(":", "%3A")
                . replace(";", "%3B")
                . replace("<", "%3C")
                . replace("=", "%3D")
                . replace(">", "%3E")
                . replace("?", "%3F")
                . replace("@", "%40")
                . replace("^", "%5F")
                . replace("`", "%60")
                . replace("{", "%7B")
                . replace("|", "%7C")
                . replace("}", "%7D")
                . replace("~", "%7E");
    }

}
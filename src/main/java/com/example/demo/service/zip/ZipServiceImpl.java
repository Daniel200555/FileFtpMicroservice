package com.example.demo.service.zip;

import com.example.demo.dto.FileInfoDTO;
import com.example.demo.service.ftpservice.FtpService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public non-sealed class ZipServiceImpl implements ZipService{

    @Autowired
    private FtpService ftpService;

    @Override
    public String zipFolder(String dir, String user, String name, String path, FTPClient ftpClient) {
        try {
            String result = name + ".zip";
            List<FileInfoDTO> files = ftpService.frontInfo(path, dir, ftpClient);
            File dirUser = new File("C:\\Users\\serda\\temp\\" + user);
            if(!dirUser.exists()) {
                dirUser.mkdir();
            }
            File dirFolder = new File("C:\\Users\\serda\\temp\\" + user + "\\" + name);
            if(!dirFolder.exists()) {
                dirFolder.mkdir();
            }
            File d = new File("C:\\Users\\serda\\temp\\" + user + "\\" + result);
            System.out.println(d.getPath());
            FileOutputStream fos = new FileOutputStream(d);
            ZipOutputStream zos = new ZipOutputStream(fos);
            List<File> fileList = new ArrayList<File>();
            List<FileInfoDTO> fi = new ArrayList<>();
            for (FileInfoDTO f: files) {
                if (!f.getIsDirectory()) {
                    System.out.println("Success folder " + dirFolder.getPath());
                    download(f, dirFolder, ftpClient);
                } else {
                    fi.add(f);
                }
            }
            if (fi.size() != 0) {
                for (FileInfoDTO file: fi) {
                    downloadDir(dirFolder.getPath(), file, ftpClient);
                }
            }

            d.getParentFile().mkdirs();
            List<File> listFiles = new ArrayList();
            listFiles(listFiles, dirFolder);
            createZipFile(listFiles, dirFolder, zos);
            FileUtils.deleteDirectory(dirFolder);
            return d.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void download(FileInfoDTO f, File dirFolder, FTPClient ftpClient) throws Exception {
        File file = new File(dirFolder.getPath() + "\\" + f.getName());
        System.out.println(file.getPath());
        String temp = "/home/danya/ftp/files" + f.getPath();
        String resultTemp = new String(temp.getBytes("UTF-8"), "ISO-8859-1");
        byte[] buffer = ftpService.downloadFile(resultTemp, ftpClient);

        OutputStream outStream = new FileOutputStream(file);
        outStream.write(buffer);
        outStream.close();
    }

    public void downloadDir(String path, FileInfoDTO file, FTPClient ftpClient) throws Exception {
        List<FileInfoDTO> fi = new ArrayList<>();
        File dir = new File(path + "\\" + file.getName() + "\\");
        if (!dir.exists())
            dir.mkdir();
        String temp = "/home/danya/ftp/files/" + file.getPath();
        String result = new String(temp.getBytes("UTF-8"), "ISO-8859-1");
        List<FileInfoDTO> files = ftpService.frontInfo(result, temp, ftpClient);
        for (FileInfoDTO f: files) {
            if (!f.getIsDirectory())
                download(f, dir, ftpClient);
            else {
                fi.add(f);
            }
        }
        if (fi.size() != 0) {
            for (FileInfoDTO f: fi) {
                downloadDir(dir.getPath(), f, ftpClient);
            }
        }
    }

    private static void createZipFile(List<File> listFiles, File inputDirectory,
                                      ZipOutputStream zipOutputStream) throws IOException {

        for (File file : listFiles) {
            String filePath = file.getCanonicalPath();
            int lengthDirectoryPath = inputDirectory.getCanonicalPath().length();
            int lengthFilePath = file.getCanonicalPath().length();
            String zipFilePath = filePath.substring(lengthDirectoryPath + 1, lengthFilePath);
            ZipEntry zipEntry = new ZipEntry(zipFilePath);
            zipOutputStream.putNextEntry(zipEntry);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }
            zipOutputStream.closeEntry();
            System.out.println("Zipped file:"+filePath);
        }
        zipOutputStream.close();
    }

    private static List listFiles(List listFiles, File inputDirectory)
            throws IOException {

        File[] allFiles = inputDirectory.listFiles();
        for (File file : allFiles) {
            if (file.isDirectory()) {
                listFiles(listFiles, file);
            } else {
                System.out.println(file.getCanonicalPath());
                listFiles.add(file);
            }
        }
        return listFiles;
    }

}

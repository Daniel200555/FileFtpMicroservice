package com.example.demo.service.ftpservice;

import com.example.demo.dto.FileInfoDTO;
import com.example.demo.format.GetFormat;
import com.example.demo.message.LogMessage;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public non-sealed class FtpServiceImpl implements FtpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpServiceImpl.class);

    public FTPClient loginFTP(String hostname, int port, String username, String password) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new ProtocolCommandListener() {
            @Override
            public void protocolCommandSent(ProtocolCommandEvent protocolCommandEvent) {
                System.out.printf("[%s][%d] Command sent: [%s]-%s", Thread.currentThread().getName(),
                        System.currentTimeMillis(),
                        protocolCommandEvent.getCommand(),
                        protocolCommandEvent.getMessage());
            }

            @Override
            public void protocolReplyReceived(ProtocolCommandEvent protocolCommandEvent) {
                System.out.printf("[%s][%d] Reply received : %s", Thread.currentThread().getName(),
                        System.currentTimeMillis(),
                        protocolCommandEvent.getMessage());
            }
        });
        ftpClient.connect(hostname, port);
        System.out.printf("connect");
        ftpClient.login(username, password);
        System.out.printf("login");
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    public void createDirectory(String path, FTPClient ftpClient) throws IOException {
        System.out.println();
        System.out.printf("[createDirectory][%d] Is success to create directory : %s -> %b",
                System.currentTimeMillis(),
                path,
                ftpClient.makeDirectory(path));
        System.out.println();
    }

    @Override
    public byte[] downloadFile(String path, FTPClient ftpClient) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.out.println();
        System.out.printf("[downloadFile][%d] Is success to download file : %s -> %b",
                System.currentTimeMillis(), path, ftpClient.retrieveFile(path, byteArrayOutputStream));
        System.out.println();
        return byteArrayOutputStream.toByteArray();
    }

    public void deleteDirectory(String path, FTPClient ftpClient) throws IOException {
        System.out.println();
        FTPFile[] files=ftpClient.listFiles(path);
        if(files.length>0) {
            for (FTPFile ftpFile : files) {
                if(ftpFile.isDirectory()){
                    System.out.printf("[deleteDirectory][%d] Is success to delete directory : %s",
                            System.currentTimeMillis(),
                            ftpFile.getName());
                    System.out.println();
                    deleteDirectory(path + "/" + ftpFile.getName(), ftpClient);
                }
                else {
                    String deleteFilePath = path + "/" + ftpFile.getName();
                    System.out.printf("[deleteFile][%d] Is success to delete file : %s -> %b",
                            System.currentTimeMillis(),
                            deleteFilePath,
                            ftpClient.deleteFile(deleteFilePath));
                    System.out.println();
                }

            }
        }
        System.out.printf("[deleteDirectory][%d] Is success to delete directory : %s -> %b",
                System.currentTimeMillis(),
                path,
                ftpClient.removeDirectory(path));
        System.out.println();
    }



    public void deleteFile(String path, FTPClient ftpClient) throws IOException {
        System.out.println();
        System.out.printf("[deleteFile][%d] Is success to delete file : %s -> %b",
                System.currentTimeMillis(),
                path,
                ftpClient.deleteFile(path));
        System.out.println();
    }

    @Override
    public FileInfoDTO getInfo(String dir, String path, FTPFile ftpFile) throws IOException {
//        return new FileInfoDTO(path + ftpFile.getName(), ftpFile.getName(), String.valueOf(ftpFile.getType()), ftpFile.isFile(), ftpFile.isDirectory());
        String temp = ftpFile.getName();
        String pathTemp = null;
        String result = new String(temp.getBytes("ISO-8859-1"), "UTF-8");
        var message = new LogMessage("OK");
        FileInfoDTO infoDTO = new FileInfoDTO();
        infoDTO.setName(ftpFile.getName());
        System.out.println(infoDTO.getName());
        if (ftpFile.isFile()) {
            pathTemp = new GetFormat().getPath(path + ftpFile.getName());
            infoDTO.setPath(pathTemp);
            infoDTO.setType(new GetFormat().getType(ftpFile.getName(), '.'));
            infoDTO.setFormat(new GetFormat().formatFile(infoDTO.getType()));
            infoDTO.setJustname(new GetFormat().getJName(ftpFile.getName()));
        }
        else {
            pathTemp = new GetFormat().getPath(path + ftpFile.getName() + "/");
            infoDTO.setPath(pathTemp);
            infoDTO.setType(null);
            infoDTO.setFormat(null);
            infoDTO.setJustname(null);
        }
        infoDTO.setLink(new GetFormat().getWatch(infoDTO.getPath()));
        infoDTO.setIsFile(ftpFile.isFile());
        infoDTO.setIsDirectory(ftpFile.isDirectory());
        System.out.println(message.message());
        return infoDTO;
    }

    @Override
    public void uploadFile(InputStream input, String remotePath, FTPClient ftpClient) throws IOException {
//        FileInputStream inputStream = new FileInputStream(path);
        System.out.println();
        System.out.printf("[uploadFile][%d] Is success to upload file : %s -> %b",
                System.currentTimeMillis(), remotePath, ftpClient.storeFile(remotePath, input));
        System.out.println();
    }

    @Override
    public List<FileInfoDTO> frontInfo(String dir,String path, FTPClient ftpClient) throws IOException {
        List<FileInfoDTO> list = new ArrayList<>();
        FileInfoDTO temp;
        for (FTPFile ftpFile : ftpClient.listFiles(dir)) {
            temp = getInfo(dir,path, ftpFile);
            list.add(temp);
            System.out.println(new LogMessage("ADD"));
        }
        return list;
    }
}

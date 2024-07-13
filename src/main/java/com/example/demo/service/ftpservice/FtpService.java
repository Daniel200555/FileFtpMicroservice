package com.example.demo.service.ftpservice;

import com.example.demo.dto.FileInfoDTO;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public sealed interface FtpService permits FtpServiceImpl {

    FTPClient loginFTP(String hostname, int port, String username, String password) throws IOException;

    void createDirectory(String path, FTPClient ftpClient) throws IOException;

    byte[] downloadFile(String path, FTPClient ftpClient) throws Exception;

    void deleteDirectory(String path, FTPClient ftpClient) throws IOException;

    void deleteFile(String path, FTPClient ftpClient) throws IOException;

    FileInfoDTO getInfo(String dir, String path, FTPFile ftpFile) throws IOException;

    void uploadFile(InputStream input, String remotePath, FTPClient ftpClient) throws IOException;

    List<FileInfoDTO> frontInfo(String dir,String path, FTPClient ftpClient) throws IOException;

}

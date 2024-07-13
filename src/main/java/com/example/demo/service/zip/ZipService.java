package com.example.demo.service.zip;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.core.io.ByteArrayResource;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public sealed interface ZipService permits ZipServiceImpl {

    String zipFolder(String dir, String user, String name, String path, FTPClient ftpClient);

}

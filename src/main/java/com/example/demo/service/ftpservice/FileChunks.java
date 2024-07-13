package com.example.demo.service.ftpservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
@Setter
public class FileChunks {

    @Autowired
    private FtpService ftpService;

    @Autowired
    private Environment environment;

    private String host;
    private String port;
    private String user;
    private String password;
    private String pathStandard;

    private final Map<String, Map<Integer, byte[]>> fileChunks = new ConcurrentHashMap<>();
    private final Map<String, Integer> chunkCounts = new ConcurrentHashMap<>();

    public FileChunks(Environment environment) {
        this.environment = environment;
        this.host = environment.getProperty("ftp.host");
        this.port = environment.getProperty("ftp.port");
        this.user = environment.getProperty("ftp.user");
        this.password = environment.getProperty("ftp.password");
        this.pathStandard = environment.getProperty("ftp.pathStandard");
    }

    public synchronized void addChunk(String fileId, int chunkIndex, int totalChunks, String dir, byte[] chunk) throws IOException {
        System.out.println(chunkIndex);
        fileChunks.computeIfAbsent(fileId, k -> new ConcurrentHashMap<>()).put(chunkIndex, chunk);
        chunkCounts.put(fileId, totalChunks);

        if (fileChunks.get(fileId).size() == totalChunks) {
            saveFile(dir, assembleFile(fileId));
        }
    }

    private InputStream assembleFile(String fileId) {
        Map<Integer, byte[]> chunks = fileChunks.get(fileId);
        int totalChunks = chunkCounts.get(fileId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < totalChunks; i++) {
            try {
                outputStream.write(chunks.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        fileChunks.remove(fileId);
        chunkCounts.remove(fileId);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private void saveFile(String dir, InputStream inputStream) throws IOException {
        ftpService.uploadFile(inputStream,this.pathStandard + dir, ftpService.loginFTP(getHost(), getPortI(), getUser(), getPassword()));
    }

    public int getPortI() {
        return Integer.parseInt(this.port);
    }

}

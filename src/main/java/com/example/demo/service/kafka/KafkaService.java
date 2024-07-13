package com.example.demo.service.kafka;

import com.example.demo.service.ftpservice.FileChunks;
import com.example.demo.service.ftpservice.FtpService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KafkaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private FtpService ftpService;

    @Autowired
    private Environment environment;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private FileChunks fileChunks;

    private String host;
    private String port;
    private String user;
    private String password;
    private String pathStandard;

    private Map<String, ByteArrayOutputStream> file = new HashMap<>();

    public KafkaService(Environment environment) {
        this.environment = environment;
        this.host = environment.getProperty("ftp.host");
        this.port = environment.getProperty("ftp.port");
        this.user = environment.getProperty("ftp.user");
        this.password = environment.getProperty("ftp.password");
        this.pathStandard = environment.getProperty("ftp.pathStandard");
    }

    @KafkaListener(topics = "createuser", groupId = "group-id")
    public void consume(String messages) throws IOException {
        LOGGER.info(String.format("Message received -> %s", messages));
        createDirectory(messages);
    }

    @KafkaListener(topics = "createfile", groupId = "group-id")
    public void createFile(@Payload Map<String, Object> chunk) throws Exception {
        String UID = (String) chunk.get("uid");
        int chunkIndex = (int) chunk.get("index");
        int totalChunks = (int) chunk.get("totalChunk");
        String dir = (String) chunk.get("dir");
        byte[] temp = Base64.getDecoder().decode((String) chunk.get("temp"));
        fileChunks.addChunk(UID, chunkIndex, totalChunks, dir, temp);
    }

    private void saveCompleteFile(String dir) throws IOException {
        ByteArrayOutputStream outputStream = file.get(dir);
        byte[] result = outputStream.toByteArray();
        outputStream.close();

        saveFile(new ByteArrayInputStream(result), dir);
    }

    @KafkaListener(topics = "createdir", groupId = "group-id")
    public void createDir(@Payload  String dir) {
        LOGGER.info(String.format("Sent message to create folder -> %s", dir));
        try {
            String result = new String(dir.getBytes("UTF-8"), "ISO-8859-1");
            ftpService.createDirectory(this.pathStandard + result, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
        } catch (IOException e) {
            LOGGER.info("ERROR: cannot create directory in ftp server!!!");
        }
    }

    @KafkaListener(topics = "deletefile", groupId = "group-id")
    public void deleteFile(String dir) throws IOException {
        ftpService.deleteFile(this.pathStandard + dir, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
    }

    public void saveFile(InputStream input, String dir) throws IOException {
        ftpService.uploadFile(input,this.pathStandard + dir, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
        LOGGER.info(String.format("File createed with path -> %s", dir));
    }

    public void createDirectory(String messages) throws IOException {
        ftpService.createDirectory(messages, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
        LOGGER.info(String.format("File created with name -> %s", messages));
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return Integer.parseInt(this.port);
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

}

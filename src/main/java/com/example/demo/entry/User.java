package com.example.demo.entry;


import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "users")
public class User {

    public User() {
    }

    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    @MongoId
    private int id;

    private String nickname;

    private List<FileData> files;

    private List<FileData> sharedFiles;

    public User(String nickname, List<FileData> files, List<FileData> sharedFiles) {
        this.nickname = nickname;
        this.files = files;
        this.sharedFiles = sharedFiles;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void setFiles(List<FileData> files) {
        this.files = files;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<FileData> getSharedFiles() {
        return sharedFiles;
    }

    public void setSharedFiles(List<FileData> sharedFiles) {
        this.sharedFiles = sharedFiles;
    }
}

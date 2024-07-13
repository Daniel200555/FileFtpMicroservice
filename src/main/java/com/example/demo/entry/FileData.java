package com.example.demo.entry;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "files")
public class FileData {

    public FileData() {
    }

    @Id
    private long file_id;

    private String filename;

    private boolean isFile;

    private String format;

    private String type;

    private String dir;

    private List<FileData> files;

    public FileData(Builder build) {
        this.file_id = build.file_id;
        this.filename = build.filename;
        this.format = build.format;
        this.type = build.type;
        this.dir = build.dir;
        this.files = build.files;
        this.isFile = build.isFile;
    }

    public long getFile_id() {
        return file_id;
    }

    public String getFilename() {
        return filename;
    }

    public String getFormat() {
        return format;
    }

    public String getType() {
        return type;
    }

    public String getDir() {return dir;}

    public List<FileData> getFiles() {
        return files;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public void setFile_id(long file_id) {
        this.file_id = file_id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setFiles(List<FileData> files) {
        this.files = files;
    }

    public static class Builder {
        private long file_id;

        private String filename;
        private boolean isFile;
        private String format;
        private String type;

        private String dir;

        private List<FileData> files;

        public Builder setId(long id) {
            this.file_id = id;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.filename = fileName;
            return this;
        }

        public Builder setFormat(String format) {
            this.format = format;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setDir(String dir) {
            this.dir = dir;
            return this;
        }

        public Builder setFiles(List<FileData> files) {
            this.files = files;
            return this;
        }

        public Builder isFile(boolean file) {
            this.isFile = file;
            return this;
        }

        public Builder updateFileData(FileData file) {
            this.file_id = file.file_id;
            this.type = file.type;
            this.format = file.format;
            this.dir = file.dir;
            this.files = file.files;
            this.isFile = file.isFile;
            return this;
        }

        public FileData build() {
            return new FileData(this);
        }

    }

}

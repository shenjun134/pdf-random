package com.poc.pdf.model;

public class FileInfo {
    /**
     * name without type and without parent path
     */
    private String fileName;

    private String parentPath;

    private String type;

    private String fullpath;

    public FileInfo(String fileName, String parentPath, String type) {
        this.fileName = fileName;
        this.parentPath = parentPath;
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullpath() {
        return fullpath;
    }

    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (fileName != null ? !fileName.equals(fileInfo.fileName) : fileInfo.fileName != null) return false;
        if (parentPath != null ? !parentPath.equals(fileInfo.parentPath) : fileInfo.parentPath != null) return false;
        if (type != null ? !type.equals(fileInfo.type) : fileInfo.type != null) return false;
        return fullpath != null ? fullpath.equals(fileInfo.fullpath) : fileInfo.fullpath == null;
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (parentPath != null ? parentPath.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (fullpath != null ? fullpath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", parentPath='" + parentPath + '\'' +
                ", type='" + type + '\'' +
                ", fullpath='" + fullpath + '\'' +
                '}';
    }
}

package dao;

public class Image {
    private int imageId;
    private String imageName;
    private int size;
    private String uploadTime;
    private String contentType;
    private String path;//表示当前这张图片的具体内容存在磁盘的哪个路径上
    // （数据库只保存图片的属性，图片具体的内容要保存在磁盘具体的一个文件上）
    private String md5;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + imageId +
                ", imageName='" + imageName + '\'' +
                ", size=" + size +
                ", uploadTime='" + uploadTime + '\'' +
                ", contentType='" + contentType + '\'' +
                ", path='" + path + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}

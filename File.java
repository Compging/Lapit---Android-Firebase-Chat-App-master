package in.tvac.akshaye.lapitchat;

/**
 * Created by Win10 on 29/4/2561.
 */

public class File {
    public String filename;
    public String filepath;
    public String fileid;

    public File(){
    }

    public File(String filename, String filepath, String fileid) {
        this.filename = filename;
        this.filepath = filepath;
        this.fileid = fileid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }
}

package fun.qianxiao.yunchu.net.easyhttp;

import com.hjq.http.config.IRequestApi;

import java.io.File;

public class FileUploadApi implements IRequestApi {
    private File file;
    private String coll;
    private String cojs;
    private String cowjs;

    @Override
    public String getApi() {
        return "/filesfx.php";
    }

    public FileUploadApi setImage(File image) {
        this.file = image;
        return this;
    }

    public FileUploadApi setCollection_id(String collection_id) {
        this.coll = collection_id;
        return this;
    }

    public FileUploadApi setCollection_des(String collection_des) {
        this.cojs = collection_des;
        return this;
    }

    public FileUploadApi setFile_des(String file_des) {
        this.cowjs = file_des;
        return this;
    }
}

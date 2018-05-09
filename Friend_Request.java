package in.tvac.akshaye.lapitchat;

/**
 * Created by Win10 on 9/5/2561.
 */

public class Friend_Request {

    String request_type;

    public Friend_Request() {
    }

    public Friend_Request(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}

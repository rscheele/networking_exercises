package rodischeele.practicumfinal.enities;

/**
 * Created by steven on 7-6-2015.
 */
public class StreamEntity {

    private int id;
    private String ip;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return description;
    }
}

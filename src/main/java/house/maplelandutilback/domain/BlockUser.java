package house.maplelandutilback.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "blocks")
public class BlockUser {
    private String userName;
    private String globalName;

    public String getGlobalName() {
        return globalName;
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

package house.maplelandutilback.domain;

public class BlockRequest {
    private String keyword;

    public BlockRequest(String keyword) {
        this.keyword = keyword;
    }

    public BlockRequest() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

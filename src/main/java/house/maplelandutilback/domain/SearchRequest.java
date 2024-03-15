package house.maplelandutilback.domain;

public class SearchRequest {
    private String content;
    private String userName;
    private String optionSearch;
    private Boolean macro;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOptionSearch() {
        return optionSearch;
    }

    public void setOptionSearch(String optionSearch) {
        this.optionSearch = optionSearch;
    }

    public Boolean getMacro() {
        return macro;
    }

    public void setMacro(Boolean macro) {
        this.macro = macro;
    }
}

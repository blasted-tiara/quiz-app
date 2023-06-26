package ba.fet.rwa.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

public class PagesInfo {
    public PagesInfo(Integer pagesCount) {
        this.pagesCount = pagesCount;
    }

    private @Valid Integer pagesCount;

    @JsonProperty("pagesCount")
    public Integer getPagesCount() {
        return pagesCount;
    }

    public void setPagesCount(Integer pagesCount) {
        this.pagesCount = pagesCount;
    }
}

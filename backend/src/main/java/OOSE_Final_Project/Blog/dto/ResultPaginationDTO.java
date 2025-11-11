package OOSE_Final_Project.Blog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    @Builder
    public static class Meta {
        private long total;
        private int page;
        private int pageSize;
        private int pages;
    }
}

package project.xunolan.web.amisRespVo;

import com.sun.istack.NotNull;
import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScriptFilterParam {

    //当前页码
    @NotNull
    private Integer page = 1;

    //当前页大小
    @NotNull
    private Integer perPage = 10;

    private String keyword;

    @NotNull
    private Long usecaseId;
}

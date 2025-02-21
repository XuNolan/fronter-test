package project.xunolan.web.amisEntity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/*
 所有amis增删改查组件返回需要的VO。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmisCrudListVo {
    private List<Map<String, Object>> items;
    private int total;
    private int page;
}

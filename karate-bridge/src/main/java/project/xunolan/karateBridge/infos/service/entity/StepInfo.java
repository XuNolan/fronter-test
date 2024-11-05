package project.xunolan.karateBridge.infos.service.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@Accessors(chain = true)
public class StepInfo implements Serializable {
    String StepId;
    String prefix;
    String stepText;
}

package project.xunolan.karateBridge.infos.entity.impl;

import com.intuit.karate.core.Feature;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import project.xunolan.karateBridge.infos.entity.SendMsgBase;

import java.beans.Transient;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class FeatureInfo extends SendMsgBase implements Serializable {
    String featureId;
    String featureName;
    String fileName;
    List<ScenarioInfo> scenarios;

    transient Feature feature;
}

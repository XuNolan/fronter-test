package project.xunolan.karateBridge.actions.service.hooks.hookImpl;

import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.FeatureRuntime;

public class FeatureRecordRuntimeHook implements RuntimeHook {
    @Override
    public boolean beforeFeature(FeatureRuntime fr) {

        return true;
    }

    @Override
    public void afterFeature(FeatureRuntime fr) {
    }
}

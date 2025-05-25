package com.fxz.fuled.name.starter.selector;

import com.fxz.fuled.common.chain.PropertiesFilter;

public abstract class AbsInstanceSelector extends PropertiesFilter implements InstanceSelector {
    @Override
    public String filterGroup() {
        return InstanceSelector.INSTANCE_FILTER_GROUP;
    }
}

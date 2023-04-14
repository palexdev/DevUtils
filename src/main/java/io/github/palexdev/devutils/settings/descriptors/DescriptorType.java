package io.github.palexdev.devutils.settings.descriptors;

import io.github.palexdev.devutils.components.SettingComponent;

public enum DescriptorType {
    BOOLEAN {
        @Override
        public SettingComponent toComponent(SettingDescriptor sd) {
            return SettingComponent.bool(sd);
        }
    },
    STRING {
        @Override
        public SettingComponent toComponent(SettingDescriptor sd) {
            return SettingComponent.string(sd);
        }
    },
    NUMBER {
        @Override
        public SettingComponent toComponent(SettingDescriptor sd) {
            return SettingComponent.number(sd);
        }
    },
    ;

    public abstract SettingComponent toComponent(SettingDescriptor sd);
}

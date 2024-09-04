package com.matzua.dagger.component;

import com.matzua.App;
import dagger.Component;

@Component
public interface AppComponent {
    App getApp();
}

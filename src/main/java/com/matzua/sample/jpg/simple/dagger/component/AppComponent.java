package com.matzua.sample.jpg.simple.dagger.component;

//import com.matzua.engine.app.App;
import com.matzua.jpg.core.sys.AbstractApp;
import com.matzua.jpg.user.simple.SimpleApp;
import com.matzua.sample.jpg.simple.dagger.module.CoreModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component (modules = {CoreModule.class})//, ConfigModule.class})
public interface AppComponent {
    SimpleApp getSimpleApp();
}

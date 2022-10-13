package pl.jordii.mcauth.bungee.listener;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;
import pl.jordii.mcauth.bungee.McAuthBungee;
import pl.jordii.mcauth.common.reflect.TypeCriterion;
import pl.jordii.mcauth.common.reflect.TypeFinder;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.inject.Singleton;
import java.lang.reflect.Method;

@Singleton
public class EventHandlerRegistration {

    private final TypeFinder typeFinder;
    private final McAuthBungee plugin;
    private final Injector injector;

    @Inject
    public EventHandlerRegistration(TypeFinder typeFinder,
                                    McAuthBungee plugin,
                                    Injector injector) {
        this.typeFinder = typeFinder;
        this.plugin = plugin;
        this.injector = injector;
    }

    public void initialize(String... packageNames) {
        Preconditions.checkNotNull(packageNames);
        this.typeFinder
                .filter(packageNames, TypeCriterion.subclassOf(Listener.class))
                .forEach(listenerClass -> {
                    for (Method method : listenerClass.getMethods()) {
                        if (!method.isAnnotationPresent(EventHandler.class)) {
                            continue;
                        }

                        McAuthBungee.getProxyServer().getPluginManager().registerListener(
                                this.plugin,
                                (Listener) this.injector.getInstance(listenerClass)
                        );

                        break;
                    }
                });
    }

}

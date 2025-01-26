package com.matzua.engine.app;

import com.matzua.engine.app.config.Config;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.core.LayerManager;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.renderer.Renderer;
import com.matzua.engine.util.Fun;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.opengl.PGraphicsOpenGL;

import javax.inject.Inject;

import static com.matzua.engine.app.ConfigManager.accessors;

public class App extends PApplet {
    //// Ad-Hoc Testing... \\ -------------------------------------------------------------------------------------- \\
    private final EntityManager entityManager;
    private final Renderer renderer;
    // -------------------------------------------------------------------------------------- // ...Ad-Hoc Testing \\\\
    private final ConfigManager<Config> configManager;
    private final EventManager eventManager;
    private final LayerManager layerManager;

    // Mutable References
    private PGraphics canvas;
    @Inject
    public App(
        EntityManager entityManager,
        Renderer renderer,
        ConfigManager<Config> configManager,
        EventManager eventManager,
        LayerManager layerManager
    ) {
        this.entityManager = entityManager;
        this.renderer = renderer;
        this.configManager = configManager;
        this.eventManager = eventManager;
        this.layerManager = layerManager;
    }

    /**
     * "The settings() function is new with Processing 3.0. It's not needed in most sketches. It's only useful when it's
     * absolutely necessary to define the parameters to size() with a variable. Alternately, the settings() function is
     * necessary when using Processing code outside the Processing Development Environment (PDE). For example, when
     * using the Eclipse code editor, it's necessary to use settings() to define the size() and smooth() values for a
     * sketch.
     * <p>
     * The settings() method runs before the sketch has been set up, so other Processing functions cannot be used at
     * that point. For instance, do not use loadImage() inside settings(). The settings() method runs "passively" to set
     * a few variables, compared to the setup() command that call commands in the Processing API."
     */
    public void settings() {
        configManager.register(Config::getWindowInfoTitle, Config::setWindowInfoTitle, App::windowTitle);
        configManager.register(Fun.SerializableQuadConsumer.<App, Integer, Integer, String>of(App::size),
            accessors(Config::getWindowSizeWidth, Config::setWindowSizeWidth),
            accessors(Config::getWindowSizeHeight, Config::setWindowSizeHeight),
            accessors(Config::getWindowRenderer, Config::setWindowRenderer)
        );
        size (configManager.get(Config::getWindowSizeWidth), configManager.get(Config::getWindowSizeHeight), P2D);
        System.out.printf("window(%d, %d)%n",
            configManager.get(Config::getWindowSizeWidth),
            configManager.get(Config::getWindowSizeHeight));
        
    }

    /**
     * "The setup() function is run once, when the program starts. It's used to define initial environment properties
     * such as screen size and to load media such as images and fonts as the program starts. There can only be one
     * setup() function for each program, and it shouldn't be called again after its initial execution.
     * <p>
     * If the sketch is a different dimension than the default, the size() function or fullScreen() function must be the
     * first line in setup().
     * <p>
     * Note: Variables declared within setup() are not accessible within other functions, including draw()."
     */
    public void setup() {
        // hint(ENABLE_KEY_REPEAT);

        if (g.isGL()) {
            ((PGraphicsOpenGL) g).textureSampling(2);
        }

        canvas = createGraphics(
            configManager.get(Config::getCanvasSizeWidth),
            configManager.get(Config::getCanvasSizeHeight),
            P3D
        );
    }

    public void draw() {
        push();
        renderer.render(canvas);
        if (canvas != null) {
            image(canvas, 0, 0, width, height);
        }
        pop();

        entityManager.tick();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        eventManager.dispatch(keyEvent); // new Event.Input.Text(keyEvent.getKey()));
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        eventManager.dispatch(keyEvent); // new Event.Input.Device(keyEvent.getKeyCode(), 1.0));
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        eventManager.dispatch(keyEvent); // new Event.Input.Device(keyEvent.getKeyCode(), 0.0));
    }
}

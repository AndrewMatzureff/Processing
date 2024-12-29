package com.matzua.engine.app;

import com.matzua.engine.app.config.Config;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.core.LayerManager;
import com.matzua.engine.event.Event;
import com.matzua.engine.util.Fun;
import lombok.RequiredArgsConstructor;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.opengl.PGraphicsOpenGL;

import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import static com.matzua.engine.app.ConfigManager.accessors;

@RequiredArgsConstructor (onConstructor_ = {@Inject})
public class App extends PApplet {
    //// Ad-Hoc Testing... \\ -------------------------------------------------------------------------------------- \\
    private record Point(float x, float y) {}
    private static Point point = new Point(0, 0);
    final Map<Integer, Double> states = new HashMap<>();
    // -------------------------------------------------------------------------------------- // ...Ad-Hoc Testing \\\\
    private final ConfigManager<Config> configManager;
    private final EventManager eventManager;
    private final LayerManager layerManager;

    // Mutable References
    private PGraphics canvas;

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

        configManager.register(Fun.SerializableTriConsumer.of(PGraphics::resize),
            accessors(Config::getCanvasSizeWidth, Config::setCanvasSizeWidth),
            accessors(Config::getCanvasSizeHeight, Config::setCanvasSizeHeight)
        );

        canvas = createGraphics(
            configManager.get(Config::getCanvasSizeWidth),
            configManager.get(Config::getCanvasSizeHeight),
            P3D
        );

        // Adapt raw Processing KeyEvent into internal domain Event type...
        eventManager.adapt(KeyEvent.class, e -> switch (e.getAction()) {
            case KeyEvent.PRESS -> new Event.Input.Device(e.getKeyCode(), 1.0);
            case KeyEvent.RELEASE -> new Event.Input.Device(e.getKeyCode(), 0.0);
            case KeyEvent.TYPE -> new Event.Input.Text(e.getKey());
            default -> throw new IllegalStateException("Unexpected value: " + e.getAction());
        });

        // Create subscription to update global input states based on device events.
        eventManager.subscribe(Event.Input.Device.class, e -> states.put(e.axis(), e.state()));
    }

    public void draw() {
        if (canvas == null) {
            return;
        }

        // Process input-dependent state changes...
        point = Map.of(
            'W', new double[] { 0.0,-1.0},
            'A', new double[] {-1.0, 0.0},
            'S', new double[] { 0.0, 1.0},
            'D', new double[] { 1.0, 0.0})
            .entrySet()
            .stream()
            .map(e -> {
                e.getValue()[0] *= states.getOrDefault((int) e.getKey(), 0.0);
                e.getValue()[1] *= states.getOrDefault((int) e.getKey(), 0.0);
                return e.getValue();
            })
            .reduce((e1, e2) -> new double[]{e1[0] + e2[0], e1[1] + e2[1]})
            .map(d -> new Point(point.x + (float) d[0], point.y + (float) d[1]))
            .orElse(point);
        // ...

        push();
        canvas.beginDraw();
        canvas.push();
        canvas.fill(0f);
        canvas.translate(point.x, point.y);
        canvas.box(25);
        canvas.pop();
        canvas.endDraw();
        image(canvas, 0, 0, width, height);
        pop();
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

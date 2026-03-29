package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.common.AbstractResourceStore;
import com.matzua.jpg.core.app.resource.common.IAppStore;
import com.matzua.jpg.core.app.resource.common.ResourceFactory;
import com.matzua.jpg.core.app.resource.common.TrustedResourceFactory;
import com.matzua.jpg.core.sys.AbstractApp;
import com.matzua.jpg.user.state.Type;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//@RequiredArgsConstructor (onConstructor = @__({@Inject}))
public class PGraphicsCanvasStore extends AbstractResourceStore {
    @Inject
    public PGraphicsCanvasStore(String masterKey, Map<String, ICanvas> canvasesById) {super(masterKey, canvasesById);}
    // ↓ IAppStore ↓ \.................................................................................................:
    // ↓ Misc. ↓ \.....................................................................................................:
    // ↓ Inner Classes ↓ \.............................................................................................:
}

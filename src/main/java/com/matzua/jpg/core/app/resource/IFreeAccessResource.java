package com.matzua.jpg.core.app.resource;

import com.matzua.jpg.core.app.IControlledAccessResource;

public interface IFreeAccessResource extends IControlledAccessResource {
    default void open() {}
    default void close() {}
}

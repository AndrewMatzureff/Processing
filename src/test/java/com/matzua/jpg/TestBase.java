package com.matzua.jpg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
public class TestBase {
    private AutoCloseable mocks;
    protected void open() {mocks = openMocks(this);}
    protected void close() throws Exception {mocks.close();}
    public void setup() {
        open();
    }
    public void teardown() throws Exception {
        close();
    }
}

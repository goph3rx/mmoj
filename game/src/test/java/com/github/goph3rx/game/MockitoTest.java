package com.github.goph3rx.game;

import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

/** Base class for test cases that use mocks. */
public abstract class MockitoTest {
  private AutoCloseable closeable;

  @Before
  public void openMocks() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @After
  public void closeMocks() throws Exception {
    closeable.close();
  }
}

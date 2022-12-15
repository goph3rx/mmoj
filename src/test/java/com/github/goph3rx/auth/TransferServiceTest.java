package com.github.goph3rx.auth;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import com.github.goph3rx.transfer.ITransferDatabase;
import com.github.goph3rx.transfer.Transfer;
import com.github.goph3rx.transfer.TransferService;
import java.time.LocalDateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class TransferServiceTest extends MockitoTest {

  @Mock private ITransferDatabase database;
  @InjectMocks private TransferService service;

  @Captor private ArgumentCaptor<Transfer> transfer;

  @Test
  public void generate() {
    // When
    var result = service.generate("hello");

    // Then
    verify(database).create(transfer.capture());
    assertSame(result, transfer.getValue());
    assertEquals("hello", result.account());
    assertTrue(LocalDateTime.now().isBefore(result.expiry()));
  }
}

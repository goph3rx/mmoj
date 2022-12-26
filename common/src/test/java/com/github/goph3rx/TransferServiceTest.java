package com.github.goph3rx;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.github.goph3rx.transfer.ITransferDatabase;
import com.github.goph3rx.transfer.Transfer;
import com.github.goph3rx.transfer.TransferService;
import java.time.LocalDateTime;
import java.util.Optional;
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

  @Test
  public void completeNotFound() {
    // Given
    when(database.fetch("hello")).thenReturn(Optional.empty());

    // When
    var result = service.complete("hello", 123, 456);

    // Then
    assertFalse(result);
  }

  @Test
  public void completeAuthMismatch() {
    // Given
    when(database.fetch("hello"))
        .thenReturn(Optional.of(new Transfer("hello", 999, 456, LocalDateTime.now())));

    // When
    var result = service.complete("hello", 123, 456);

    // Then
    assertFalse(result);
  }

  @Test
  public void completePlayMismatch() {
    // Given
    when(database.fetch("hello"))
        .thenReturn(Optional.of(new Transfer("hello", 123, 999, LocalDateTime.now())));

    // When
    var result = service.complete("hello", 123, 456);

    // Then
    assertFalse(result);
  }

  @Test
  public void completeSuccess() {
    // Given
    when(database.fetch("hello"))
        .thenReturn(Optional.of(new Transfer("hello", 123, 456, LocalDateTime.now())));

    // When
    var result = service.complete("hello", 123, 456);

    // Then
    assertTrue(result);
  }

  @Test
  public void completeSuccessDeleteFailed() {
    // Given
    when(database.fetch("hello"))
        .thenReturn(Optional.of(new Transfer("hello", 123, 456, LocalDateTime.now())));
    doThrow(new RuntimeException("DB Fail")).when(database).remove("hello");

    // When
    var result = service.complete("hello", 123, 456);

    // Then
    assertTrue(result);
  }
}

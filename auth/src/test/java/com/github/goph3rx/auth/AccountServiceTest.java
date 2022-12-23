package com.github.goph3rx.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.goph3rx.account.Account;
import com.github.goph3rx.account.AccountService;
import com.github.goph3rx.account.IAccountDatabase;
import java.util.Optional;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class AccountServiceTest extends MockitoTest {
  @Mock private IAccountDatabase database;
  @InjectMocks private AccountService service;

  @Test
  public void findNotFound() {
    // Given
    when(database.fetch("hello")).thenReturn(Optional.empty());

    // When
    var account = service.find("hello", "world");

    // Then
    assertTrue(account.isEmpty());
  }

  @Test
  public void findPasswordMismatch() {
    // Given
    when(database.fetch("hello"))
        .thenReturn(
            Optional.of(
                new Account(
                    "hello",
                    "1lW97hYhtDwM1QE0y25vJA==",
                    "zdzywoYQaRFgkClsIo6FWQ7x2U0NHtv2u0dhKIFavFw=",
                    -1,
                    Optional.empty())));

    // When
    var account = service.find("hello", "wrong");

    // Then
    assertTrue(account.isEmpty());
  }

  @Test
  public void findSuccess() {
    // Given
    var expected =
        Optional.of(
            new Account(
                "hello",
                "1lW97hYhtDwM1QE0y25vJA==",
                "zdzywoYQaRFgkClsIo6FWQ7x2U0NHtv2u0dhKIFavFw=",
                -1,
                Optional.empty()));
    when(database.fetch("hello")).thenReturn(expected);

    // When
    var actual = service.find("hello", "world");

    // Then
    assertEquals(expected, actual);
  }

  @Test
  public void setLastWorld() {
    // When
    service.setLastWorld("hello", 3);

    // Then
    verify(database).setLastWorld("hello", 3);
  }
}

package com.github.goph3rx.character;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for managing characters. */
public class CharacterService implements ICharacterService {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);
  /** Maximum number of characters that can be created on the same account. */
  private static final int MAXIMUM_CHARACTERS = 7;
  /** How often to clean up the deleted characters. */
  private static final int CHARACTER_CLEANUP_SECONDS = 10;
  /** Executor for scheduled tasks originating from this class. */
  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());

  /** List of templates for character creation. */
  private List<CharacterTemplate> creationTemplates = List.of();
  /** Regular expression for checking character names. */
  private final Pattern namePattern;
  /** Adapter for the database. */
  @Inject public ICharacterDatabase database;
  /** How long to wait for (in days) before deleting the character. */
  @Named("character.deleteDays")
  @Inject
  public int deleteDays;

  /**
   * Create a new service.
   *
   * @param namePattern Regular expression for validating character names.
   */
  @Inject
  public CharacterService(@Named("character.namePattern") String namePattern) {
    this.namePattern = Pattern.compile(namePattern);
  }

  @Override
  public List<CharacterTemplate> templates() {
    logger.debug("Templates are {}", creationTemplates);
    return creationTemplates;
  }

  /**
   * Set the templates used for character creation. Must only be used in tests.
   *
   * @param templates List of templates.
   */
  public void setTemplates(List<CharacterTemplate> templates) {
    creationTemplates = templates;
  }

  @Override
  public void create(
      String account,
      String name,
      CharacterRace race,
      CharacterGender gender,
      int clazz,
      byte[] appearance)
      throws CharacterCreateException, CharacterNameException, CharacterLimitException,
          CharacterDuplicateException {
    logger.debug(
        "Create character account='{}' name='{}' race={} gender={} class={} appearance={}",
        account,
        name,
        race,
        gender,
        clazz,
        HexFormat.of().formatHex(appearance));

    // Check the template
    var hasTemplate =
        creationTemplates.stream()
            .anyMatch(
                t ->
                    t.race() == race
                        && t.clazz() == clazz
                        && t.gender().map(g -> g == gender).orElse(true));
    if (!hasTemplate) {
      throw new CharacterCreateException(
          "Invalid input for new character race=%s gender=%s class=%d"
              .formatted(race, gender, clazz));
    }

    // Check the name
    if (!namePattern.matcher(name).find()) {
      throw new CharacterNameException(
          "Invalid input for new character name='%s' pattern='%s'"
              .formatted(name, namePattern.pattern()));
    }

    // Check the number of characters on the account
    var totalCharacters = list(account).size();
    if (totalCharacters >= MAXIMUM_CHARACTERS) {
      throw new CharacterLimitException(
          "Character limit exceeded for new character account='%s' total=%d maximum=%d"
              .formatted(account, totalCharacters, MAXIMUM_CHARACTERS));
    }

    // Create the character
    var id = Character.PREFIX + UUID.randomUUID();
    var character =
        new Character(
            id,
            name,
            account,
            race,
            clazz,
            gender,
            appearance,
            LocalDateTime.now(),
            Optional.empty(),
            Optional.empty());
    database.create(character);
    logger.info("Created new character {}", character);
  }

  @Override
  public List<Character> list(String account) {
    logger.debug("Fetching list of characters account='{}'", account);
    var result = database.list(account);
    logger.debug("Characters are {}", result);
    return result;
  }

  /**
   * Find the character or fail.
   *
   * @param account Account name.
   * @param index Index of the character.
   * @return Character information.
   */
  private Character find(String account, int index) {
    var characters = database.list(account);
    return characters.get(index);
  }

  @Override
  public void remove(String account, int index) {
    logger.debug("Removing character account='{}' index={}", account, index);
    var character = find(account, index);
    logger.debug("Found {}", character);

    // Remove later
    if (deleteDays > 0) {
      var deleteOn = LocalDateTime.now().plusDays(deleteDays);
      database.setDeleteOn(character.id(), Optional.of(deleteOn));
      logger.info(
          "Queued character for removal on id='{}' name='{}' deleteOn={}",
          character.id(),
          character.name(),
          deleteOn);
      return;
    }

    // Remove now
    database.delete(character.id());
    logger.info("Instantly removed character id='{}' name='{}'", character.id(), character.name());
  }

  @Override
  public void restore(String account, int index) {
    logger.debug("Restoring character account='{}' index={}", account, index);
    var character = find(account, index);
    logger.debug("Found {}", character);
    database.setDeleteOn(character.id(), Optional.empty());
    logger.info("Restored character id='{}' name='{}'", character.id(), character.name());
  }

  @Override
  public void start() {
    // Load data
    var templatePath = Paths.get(".", "data", "character-templates.jsonc");
    try (var reader = new FileReader(new File(templatePath.toUri()))) {
      creationTemplates = CharacterTemplate.load(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    logger.info("Loaded {} character templates", creationTemplates.size());

    // Start scheduled tasks
    executor.scheduleAtFixedRate(
        () -> {
          try {
            var total = database.deleteNow();
            if (total > 0) {
              logger.info("Cleaned up {} deleted character(s)", total);
            }
          } catch (Exception e) {
            logger.warn("Failed to clean up deleted characters", e);
          }
        },
        0,
        CHARACTER_CLEANUP_SECONDS,
        TimeUnit.SECONDS);
  }
}

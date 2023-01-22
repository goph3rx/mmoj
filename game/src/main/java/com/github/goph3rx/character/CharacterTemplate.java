package com.github.goph3rx.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

/**
 * Template for character creation.
 *
 * @param race Race.
 * @param clazz Base class.
 * @param gender Expected gender (if gender specific).
 */
public record CharacterTemplate(
    CharacterRace race, @JsonProperty("class") int clazz, Optional<CharacterGender> gender) {
  /**
   * Load a list of templates from JSON.
   *
   * @param source Source of JSON.
   * @return Templates.
   * @throws IOException JSON parser cannot be created.
   */
  public static List<CharacterTemplate> load(Reader source) throws IOException {
    var mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
    return mapper.readValue(source, new TypeReference<>() {});
  }
}

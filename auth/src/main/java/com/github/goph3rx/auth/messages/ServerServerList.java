package com.github.goph3rx.auth.messages;

import com.github.goph3rx.world.World;
import java.util.List;

/**
 * Response used to display the list of available worlds.
 *
 * @param lastWorld Last world entered.
 * @param worlds List of worlds.
 */
public record ServerServerList(int lastWorld, List<World> worlds) {}

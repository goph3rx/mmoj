package com.github.goph3rx.world;

import java.net.InetAddress;

/**
 * Registered world record.
 *
 * @param id Unique identifier for the world.
 * @param ip IP of the game server.
 * @param port Port of the game server.
 * @param currentPlayers Number of players in the world right now.
 * @param maximumPlayers Maximum number of players that can enter the world.
 * @param isOnline World status.
 */
public record World(
    int id, InetAddress ip, int port, int currentPlayers, int maximumPlayers, boolean isOnline) {}

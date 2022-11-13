package me.ammardev.serverassistant.home;

import java.util.UUID;

public class Home {
    private double x,y,z;
    private UUID playerUUID;

    public Home(double x, double y, double z, UUID playerUUID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.playerUUID = playerUUID;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
}

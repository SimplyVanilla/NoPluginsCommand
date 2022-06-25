package net.simplyvanilla.nopluginscommand.opdata;

import java.util.Objects;
import java.util.UUID;

public class OpData {

    private final UUID uuid;
    private final String name;
    private final int level;
    private final boolean bypassesPlayerLimit;

    public OpData(UUID uuid, String name, int level, boolean bypassesPlayerLimit) {
        this.uuid = uuid;
        this.name = name;
        this.level = level;
        this.bypassesPlayerLimit = bypassesPlayerLimit;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public boolean isBypassesPlayerLimit() {
        return bypassesPlayerLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpData opData = (OpData) o;
        return level == opData.level && bypassesPlayerLimit == opData.bypassesPlayerLimit && Objects.equals(uuid, opData.uuid) && Objects.equals(name, opData.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, level, bypassesPlayerLimit);
    }

}

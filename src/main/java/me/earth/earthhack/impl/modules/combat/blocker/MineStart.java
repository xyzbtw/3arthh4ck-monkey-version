package me.earth.earthhack.impl.modules.combat.blocker;

import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class MineStart {
    private final BlockPos pos;
    private final int id;
    private final long time;

    public MineStart(BlockPos pos, int id, long time) {
        this.pos = pos;
        this.id = id;
        this.time = time;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MineStart mineStart = (MineStart) o;
        return id == mineStart.id && time == mineStart.time && pos.equals(mineStart.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, id, time);
    }

    @Override
    public String toString() {
        return "MineStart{" +
                "pos=" + pos +
                ", id=" + id +
                ", time=" + time +
                '}';
    }
}


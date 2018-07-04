package com.md.block.beans;

public class BlockHistory {

    private String name;
    private String number;
    private String location;
    private long blockTime;

    public BlockHistory() {
    }

    public BlockHistory(String name, String number, String location, long blockTime) {
        this.name = name;
        this.number = number;
        this.location = location;
        this.blockTime = blockTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(long blockTime) {
        this.blockTime = blockTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockHistory that = (BlockHistory) o;

        if (blockTime != that.blockTime) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;
        return location != null ? location.equals(that.location) : that.location == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (int) (blockTime ^ (blockTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "BlockHistory{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", location='" + location + '\'' +
                ", blockTime=" + blockTime +
                '}';
    }
}

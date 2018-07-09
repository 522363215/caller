package com.md.block.beans;

public class BlockInfo {


    private String name;
    private String number;
    private String photoID;
    private long blockTime;

    public BlockInfo() {
    }

    public BlockInfo(String name, String number, String photoID, long blockTime) {
        this.name = name;
        this.number = number;
        this.photoID = photoID;
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

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
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

        BlockInfo that = (BlockInfo) o;
        return number != null ? !number.equals(that.number) : that.number != null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (photoID != null ? photoID.hashCode() : 0);
        result = 31 * result + (int) (blockTime ^ (blockTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "BlockInfo{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", photoID='" + photoID + '\'' +
                ", blockTime=" + blockTime +
                '}';
    }
}

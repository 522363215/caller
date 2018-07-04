package blocker.call.wallpaper.screen.caller.ringtones.callercolor.update;

public class UpdateInfo {
    private boolean updatable;
    private boolean forceUpdate;
    private String title;
    private String description;
    private boolean isGooglePlay;
    private String url;
    private int newestVersion;

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGooglePlay() {
        return isGooglePlay;
    }

    public void setIsGooglePlay(boolean isGooglePlay) {
        this.isGooglePlay = isGooglePlay;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNewestVersion() {
		return newestVersion;
	}

	public void setNewestVersion(int newestVersion) {
		this.newestVersion = newestVersion;
	}

	public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append("UpdateInfo: [");
        sb.append("updatable = ");
        sb.append(updatable);
        sb.append(", forceUpdate  = ");
        sb.append(forceUpdate);
        sb.append(", title = ");
        sb.append(title);
        sb.append(", description = ");
        sb.append(description);
        sb.append(", isGooglePlay = ");
        sb.append(isGooglePlay);
        sb.append(", url = ");
        sb.append(url);
        sb.append(", newestVersion = ");
        sb.append(newestVersion);
        sb.append("]");
        return sb.toString();
    }
}
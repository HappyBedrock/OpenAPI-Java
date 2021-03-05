package eu.happybe.openapi.ranks;

import lombok.Getter;

public class Rank {

    @Getter
    private final String rankName;
    @Getter
    private final String displayFormat;
    @Getter
    private final String[] permissions;
    @Getter
    private final boolean isVisible;

    public Rank(String rankName, String displayFormat, String[] permissions) {
        this(rankName, displayFormat, permissions, true);
    }

    public Rank(String rankName, String displayFormat, String[] permissions, boolean isVisible) {
        this.rankName = rankName;
        this.displayFormat = displayFormat;
        this.permissions = permissions;
        this.isVisible = isVisible;
    }

    @Deprecated
    public String getFormatForChat() {
        return this.getDisplayFormat();
    }

    @Deprecated
    public String getFormatForDisplay() {
        return this.getDisplayFormat();
    }

    @Deprecated
    public String getFormatForNameTag() {
        return this.getDisplayFormat();
    }
}

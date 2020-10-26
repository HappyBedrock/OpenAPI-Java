package eu.happybe.openapi.ranks;

import lombok.Getter;

public class Rank {

    @Getter
    private final String rankName;
    @Getter
    private final String formatting;
    @Getter
    private final String[] permissions;
    @Getter
    private final boolean isVisible;

    public Rank(String rankName, String formatting, String[] permissions) {
        this(rankName, formatting, permissions, true);
    }

    public Rank(String rankName, String formatting, String[] permissions, boolean isVisible) {
        this.rankName = rankName;
        this.formatting = formatting;
        this.permissions = permissions;
        this.isVisible = isVisible;
    }

    public String getFormatForChat() {
        return this.isVisible ? this.getFormatting() + this.getRankName().toUpperCase() + "§r " : "";
    }

    public String getFormatForDisplay() {
        return this.getRankName();
    }

    public String getFormatForNameTag() {
        return this.isVisible ? this.getFormatting() + this.getRankName().toUpperCase() + "§r " : "";
    }
}

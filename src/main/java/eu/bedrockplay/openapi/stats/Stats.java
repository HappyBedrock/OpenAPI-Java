package eu.bedrockplay.openapi.stats;

import cn.nukkit.Player;
import eu.bedrockplay.openapi.mysql.QueryQueue;
import eu.bedrockplay.openapi.mysql.query.AddPointQuery;
import lombok.Getter;

public enum Stats {

    POINT_CRYSTAL("Crystals", "Values"),
    POINT_UHCRUN_WIN("UHCRunWins", "Stats"),
    POINT_UHCRUN_KILL("UHCRunKills", "Stats");

    @Getter
    private final String columnName;
    @Getter
    private final String table;

    Stats(String columnName, String table) {
        this.columnName = columnName;
        this.table = table;
    }

    public static void addPoint(Player player, Stats stat) {
        QueryQueue.submitQuery(new AddPointQuery(player.getName(), stat.getColumnName(), stat.getTable()));
    }
}

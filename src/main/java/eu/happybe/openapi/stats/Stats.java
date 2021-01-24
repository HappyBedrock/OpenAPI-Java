package eu.happybe.openapi.stats;

import cn.nukkit.Player;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.AddExperienceQuery;

public class Stats {

    public static void addExperience(Player player, int experience) {
        QueryQueue.submitQuery(new AddExperienceQuery(player.getName(), experience), query -> {
            if(!(query instanceof AddExperienceQuery)) {
                return;
            }

            if(((AddExperienceQuery) query).levelUp && player.isOnline()) {
                player.sendMessage("§9HappyBedrock> §a§lLEVEL UP! §r§aCurrent level: {$query->newLevel}!");
                player.namedTag.putInt("HappyBedrockLevel", ((AddExperienceQuery) query).newLevel);
            }
        });
    }
}

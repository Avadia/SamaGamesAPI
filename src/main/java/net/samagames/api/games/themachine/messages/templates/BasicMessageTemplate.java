package net.samagames.api.games.themachine.messages.templates;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/*
 * This file is part of SamaGamesAPI.
 *
 * SamaGamesAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
public class BasicMessageTemplate {
    /**
     * Prepare a empty message with spacers and given
     * content
     *
     * @param lines Content of the message
     * @return Formatted lines
     */
    public List<String> prepare(List<String> lines) {
        List<String> finalLines = new ArrayList<>();
        finalLines.add(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        finalLines.addAll(lines);
        finalLines.add(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        return finalLines;
    }

    /**
     * Send a empty message with spacers and given
     * content
     *
     * @param lines Content of the message
     */
    public void execute(List<String> lines) {
        this.prepare(lines).forEach(Bukkit::broadcastMessage);
    }
}

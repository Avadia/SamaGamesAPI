package net.samagames.tools.powerups;

import fr.farmvivi.api.commons.Messages;
import net.samagames.api.SamaGamesAPI;
import net.samagames.tools.Area;
import net.samagames.tools.Reflection;
import net.samagames.tools.Titles;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
public class PowerupManager {
    private final JavaPlugin plugin;
    private final Random random;
    private final List<Powerup> powerups;
    private final List<Location> locations;
    private final List<Location> removedLocations;
    private final List<Area> areas;

    private BukkitTask spawnTask;
    private int inverseFrequency;
    private int despawnTime;
    private double totalWeight;

    public PowerupManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();

        this.powerups = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.removedLocations = new ArrayList<>();
        this.areas = new ArrayList<>();

        this.inverseFrequency = 750;
        this.despawnTime = 20;
    }

    public void start() {
        this.spawnTask = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () ->
        {
            if (this.random.nextInt(this.inverseFrequency) == 0)
                this.spawnRandomPowerup();
        }, 1L, 1L);
    }

    public void stop() {
        this.spawnTask.cancel();
    }

    public void registerPowerup(Powerup powerup) {
        this.powerups.add(powerup);
        this.totalWeight += powerup.getWeight();
    }

    public void registerLocation(Location location) {
        this.locations.add(location);
    }

    public void registerArea(Area area) {
        this.areas.add(area);
    }

    public void setInverseFrequency(int inverseFrequency) {
        this.inverseFrequency = inverseFrequency;
    }

    public void setDespawnTime(int despawnTime) {
        this.despawnTime = despawnTime;
    }

    private void spawnRandomPowerup() {
        if (this.locations.isEmpty() && this.areas.isEmpty())
            return;

        List<Location> differentsLocations = new ArrayList<>();
        if (!this.locations.isEmpty())
            differentsLocations.addAll(this.locations);
        if (!this.areas.isEmpty())
            for (Area area : areas) {
                Location location = area.getRandomLocationOnTopOfABlock();
                if (location != null)
                    differentsLocations.add(location.clone().add(0.5, 1, 0.5));
            }

        Location location = differentsLocations.get(this.random.nextInt(differentsLocations.size()));

        Powerup powerup = null;
        double randomIndex = this.random.nextDouble() * this.totalWeight;

        for (Powerup testedPowerup : this.powerups) {
            randomIndex -= testedPowerup.getWeight();

            if (randomIndex <= 0.0D) {
                powerup = testedPowerup;
                break;
            }
        }

        if (powerup == null)
            throw new RuntimeException("Cannot find a powerup to spawn");

        if (this.locations.remove(location))
            this.removedLocations.add(location);

        ActivePowerup activePowerup = powerup.spawn(location);

        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            Reflection.playSound(player, player.getLocation(), Reflection.PackageType.getServerVersion().equals("v1_8_R3") ? "ARROW_HIT" : "ENTITY_ARROW_HIT_PLAYER", 1L, 1L);
            Titles.sendTitle(player, 5, 30, 5, ChatColor.GOLD + "✯", "");
        }

        SamaGamesAPI.get().getGameManager().getCoherenceMachine().getMessageManager().writeCustomMessage(ChatColor.YELLOW + "Un " + Messages.POWERUP.getMessage() + " vient de faire son apparition !", true);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
        {
            if (activePowerup.isAlive()) {
                activePowerup.remove(false);
                if (this.removedLocations.remove(location))
                    this.locations.add(location);
            }
        }, this.despawnTime * 20L);
    }
}

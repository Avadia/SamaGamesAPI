package net.samagames.tools.discord;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.pubsub.IPacketsReceiver;
import org.apache.commons.lang3.tuple.MutablePair;
import redis.clients.jedis.Jedis;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.logging.Level;

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
public class DiscordAPI {
    private static final int TIMEOUT = 20000;
    private static int generator = 0;
    private static final Map<Integer, MutablePair<ResultType, Object>> results = new HashMap<>();

    static {
        SamaGamesAPI.get().getPubSub().subscribe("discordbot.response", new TeamSpeakConsumer());
    }

    private static void publish(int id, String string) {
        Jedis jedis = null;
        try {
            jedis = SamaGamesAPI.get().getBungeeResource();
            if (jedis != null)
                jedis.publish("discordbot", SamaGamesAPI.get().getServerName() + "/" + id + "/" + string);
        } catch (Exception exception) {
            SamaGamesAPI.get().getPlugin().getLogger().log(Level.SEVERE, "Jedis error", exception);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public static int createChannel(@Nonnull String name) {
        MutablePair<ResultType, Object> pair = MutablePair.of(ResultType.INTEGER, -1);
        int id = generator++;
        DiscordAPI.results.put(id, pair);
        DiscordAPI.publish(id, "createchannel:" + name);
        try {
            synchronized (pair) {
                pair.wait(DiscordAPI.TIMEOUT);
            }
        } catch (Exception ignored) {
        }
        return (int) pair.getRight();
    }

    public static boolean deleteChannel(int channelId) {
        MutablePair<ResultType, Object> pair = MutablePair.of(ResultType.BOOLEAN, false);
        int id = generator++;
        DiscordAPI.results.put(id, pair);
        DiscordAPI.publish(id, "deletechannel:" + channelId);
        try {
            synchronized (pair) {
                pair.wait(DiscordAPI.TIMEOUT);
            }
        } catch (Exception ignored) {
        }
        return (boolean) pair.getRight();
    }

    public static List<UUID> movePlayers(@Nonnull List<UUID> uuids, int channelId) {
        MutablePair<ResultType, Object> pair = MutablePair.of(ResultType.UUID_LIST, new ArrayList<>());
        int id = generator++;
        DiscordAPI.results.put(id, pair);
        final String[] msg = {"move:" + channelId};
        uuids.forEach(uuid -> msg[0] += ":" + uuid.toString());
        DiscordAPI.publish(id, msg[0]);
        try {
            synchronized (pair) {
                pair.wait(DiscordAPI.TIMEOUT);
            }
        } catch (Exception ignored) {
        }
        return (List<UUID>) pair.getRight();
    }

    public static boolean isConnected(@Nonnull UUID player) {
        MutablePair<ResultType, Object> pair = MutablePair.of(ResultType.BOOLEAN, false);
        int id = generator++;
        DiscordAPI.results.put(id, pair);
        DiscordAPI.publish(id, "isconnected:" + player.toString());
        try {
            synchronized (pair) {
                pair.wait(DiscordAPI.TIMEOUT);
            }
        } catch (Exception ignored) {
        }
        return (boolean) pair.getRight();
    }

    private enum ResultType {
        UUID_LIST,
        BOOLEAN,
        INTEGER
    }

    private static class TeamSpeakConsumer implements IPacketsReceiver {
        @Override
        public void receive(String channel, String packet) {
            String[] args = packet.split("/");
            if (!args[0].equals(SamaGamesAPI.get().getServerName()))
                return;
            int id = Integer.parseInt(args[1]);
            MutablePair<ResultType, Object> result = DiscordAPI.results.get(id);
            DiscordAPI.results.remove(id);
            boolean ok = args.length > 1 && !args[1].equals("ERROR");
            if (!ok) {
                SamaGamesAPI.get().getPlugin().getLogger().severe(args.length > 2 ? "[DiscordAPI] Error : " + args[2] + "(packet = " + packet + ")" : "[DiscordAPI] Error : " + "Unknown" + "(packet = " + packet + ")");
            } else {
                String[] content = args[2].split(":");
                switch (result.getLeft()) {
                    case UUID_LIST:
                        List<UUID> uuid = new ArrayList<>();
                        for (int i = 1; i < content.length; i++)
                            uuid.add(UUID.fromString(content[i]));
                        result.setRight(uuid);
                        break;
                    case INTEGER:
                        result.setRight(Integer.parseInt(content[0]));
                        break;
                    case BOOLEAN:
                        result.setRight(content[0].equalsIgnoreCase("OK") || content[0].equalsIgnoreCase("true"));
                        break;
                    default:
                        break;
                }
            }
            synchronized (result) {
                result.notifyAll();
            }
        }
    }
}

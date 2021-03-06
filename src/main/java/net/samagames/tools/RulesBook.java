package net.samagames.tools;

import fr.farmvivi.api.commons.Servers;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class RulesBook {
    private final String title;
    private final Servers server;
    private final List<RulesBookPage> pages;
    private final Set<String> owners;
    private final Set<String> contributors;

    /**
     * Constructor
     *
     * @param title The name of the game
     */
    public RulesBook(String title, Servers server) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.server = server;
        this.pages = new ArrayList<>();
        this.owners = new HashSet<>();
        this.contributors = new HashSet<>();
    }

    /**
     * Based on SamaGamesAPI algorithm
     *
     * @param base The String to be centered
     */
    public static String getCenteredText(String base) {
        StringBuilder builder = new StringBuilder();
        int startPos = 12 - ChatColor.stripColor(base).length() / 2;

        for (int i = 0; i < startPos; i++) {
            builder.append(" ");
        }

        builder.append(base);

        return builder.toString();
    }

    /**
     * Add a page to the book
     *
     * @param title   Page's title
     * @param content Page's content
     * @return Current book
     */
    public RulesBook addPage(String title, String content) {
        return addPage(title, content, true);
    }

    /**
     * Add a page to the book
     *
     * @param title   Page's title
     * @param content Page's content (lines)
     * @param summary true if the page should be added to Book Summary
     * @return Current book
     */
    public RulesBook addPage(String title, String[] content, boolean summary) {
        return addPage(title, String.join("\n", content), summary);
    }

    /**
     * Add a page to the book
     *
     * @param title   Page's title
     * @param content Page's content
     * @param summary true if the page should be added to Book Summary
     * @return Current book
     */
    public RulesBook addPage(String title, String content, boolean summary) {
        pages.add(new RulesBookPage(
                ChatColor.translateAlternateColorCodes('&', title),
                ChatColor.translateAlternateColorCodes('&', content),
                summary
        ));
        return this;
    }

    /**
     * Add a developer to the book.
     *
     * @param name The developer's name
     * @return Current book
     */
    public RulesBook addOwner(String name) {
        owners.add(name);
        return this;
    }

    /**
     * Add a contributor to the projects.
     * Will be displayed in "Avec l'aide de" category.
     *
     * @param name The contributor's name
     * @return Current book
     */
    public RulesBook addContributor(String name) {
        contributors.add(name);
        return this;
    }

    /**
     * Make an itemstack from the book.
     * Should only be called once (optimisation issues).
     *
     * @return new ItemStack
     */
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();

        meta.setTitle(ChatColor.translateAlternateColorCodes('&', "&6&l" + ChatColor.stripColor(title) + " &7(Règles)"));

        StringBuilder main = new StringBuilder(ChatColor.translateAlternateColorCodes('&',
                "\n   ]--------------[" +
                        "\n") + getCenteredText(title) + ChatColor.translateAlternateColorCodes('&', "&0" +
                "\n") + getCenteredText(ChatColor.translateAlternateColorCodes('&', "par &l" + server.getDisplayName())) + ChatColor.translateAlternateColorCodes('&', "&0" +
                "\n   ]--------------[" +
                "\n\n\n"));
        int i = 1;
        for (RulesBookPage page : pages) {
            if (page.isInSummary())
                main.append(ChatColor.translateAlternateColorCodes('&', " &1" + i + ". &0")).append(page.getTitle()).append("\n");
            i++;
        }
        meta.addPage(main.toString());

        for (RulesBookPage page : pages)
            meta.addPage(getCenteredText(ChatColor.BOLD + page.getTitle()) + ChatColor.BLACK + "\n\n" + ChatColor.BLACK + page.getContent());

        StringBuilder credits = new StringBuilder(ChatColor.translateAlternateColorCodes('&', "\n\nJeu développé par :\n"));
        if (owners.isEmpty())
            credits.append(" - (Inconnu)\n");
        else
            for (String name : owners)
                credits.append(ChatColor.BLACK).append(" - ").append(ChatColor.BOLD).append(name).append("\n");
        if (!contributors.isEmpty()) {
            credits.append(ChatColor.BLACK).append("\nAvec l'aide de :\n");
            for (String name : contributors)
                credits.append(ChatColor.BLACK).append(" - ").append(ChatColor.BOLD).append(name).append("\n");
        }
        meta.addPage(credits.toString());

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Return the title of the book
     *
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    private static class RulesBookPage {
        private final String title;
        private final String content;
        private final boolean summary;

        private RulesBookPage(String title, String content, boolean summary) {
            this.title = title;
            this.content = content;
            this.summary = summary;
        }

        private String getTitle() {
            return title;
        }

        private String getContent() {
            return content;
        }

        private boolean isInSummary() {
            return summary;
        }
    }
}
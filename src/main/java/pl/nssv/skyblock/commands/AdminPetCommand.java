package pl.nssv.skyblock.commands;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.nssv.skyblock.SkyblocknNSSV;
public class AdminPetCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public AdminPetCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (!p.hasPermission("*")) return true;
        if (args.length == 0) {
            p.sendMessage(ChatColor.GREEN + "/adminpet create [name] - Create pet");
            p.sendMessage(ChatColor.GREEN + "/adminpet delete [name] - Delete pet");
            p.sendMessage(ChatColor.GREEN + "/adminpet icon [name] - Set icon");
            p.sendMessage(ChatColor.GREEN + "/adminpet exp [name] [multiplier] - Set exp multi");
            p.sendMessage(ChatColor.GREEN + "/adminpet gems [name] [multiplier] - Set gems multi");
            p.sendMessage(ChatColor.GREEN + "/adminpet give [name] [level] [rarity] - Give pet");
            p.sendMessage(ChatColor.GREEN + "/adminpet egg give|icon - Egg management");
            p.sendMessage(ChatColor.GREEN + "/adminpet list - List all pets");
            return true;
        }
        String action = args[0].toLowerCase();
        if (action.equals("list")) {
            p.sendMessage(ChatColor.GREEN + "Pets:");
            for (String petName : plugin.getPetManager().getPetList()) {
                String msg = " - " + petName;
                if (plugin.getPetManager().getPetExpMultiplier().containsKey(petName))
                    msg += " (Exp: " + plugin.getPetManager().getPetExpMultiplier().get(petName) + "x)";
                if (plugin.getPetManager().getPetGemsMultiplier().containsKey(petName))
                    msg += " (Gems: " + plugin.getPetManager().getPetGemsMultiplier().get(petName) + "x)";
                p.sendMessage(ChatColor.YELLOW + msg);
            }
            return true;
        }
        if (args.length < 2) {
            p.sendMessage(ChatColor.RED + "Pet name required!");
            return true;
        }
        String petName = args[1];
        switch (action) {
            case "create":
                if (plugin.getPetManager().getPetList().contains(petName)) {
                    p.sendMessage(ChatColor.RED + "Pet already exists!");
                    return true;
                }
                plugin.getPetManager().getPetList().add(petName);
                plugin.getPetManager().getPetIcons().put(petName, new ItemStack(Material.STONE));
                p.sendMessage(ChatColor.GREEN + "Pet created!");
                break;
            case "delete":
                plugin.getPetManager().getPetList().remove(petName);
                plugin.getPetManager().getPetIcons().remove(petName);
                plugin.getPetManager().getPetExpMultiplier().remove(petName);
                plugin.getPetManager().getPetGemsMultiplier().remove(petName);
                p.sendMessage(ChatColor.GREEN + "Pet deleted!");
                break;
            case "icon":
                if (!plugin.getPetManager().getPetList().contains(petName)) {
                    p.sendMessage(ChatColor.RED + "Pet doesn't exist!");
                    return true;
                }
                plugin.getPetManager().getPetIcons().put(petName, p.getInventory().getItemInMainHand());
                p.sendMessage(ChatColor.GREEN + "Icon set!");
                break;
            case "exp":
            case "gems":
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Multiplier required!");
                    return true;
                }
                double multi = Double.parseDouble(args[2]);
                if (action.equals("exp"))
                    plugin.getPetManager().getPetExpMultiplier().put(petName, multi);
                else
                    plugin.getPetManager().getPetGemsMultiplier().put(petName, multi);
                p.sendMessage(ChatColor.GREEN + "Multiplier set!");
                break;
            case "give":
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Usage: /adminpet give [name] [level] [rarity]");
                    return true;
                }
                int level = Integer.parseInt(args[2]);
                int rarity = Integer.parseInt(args[3]);
                plugin.getPetManager().givePet(p, petName, level, rarity);
                p.sendMessage(ChatColor.GREEN + "Pet given!");
                break;
            case "egg":
                if (petName.equals("give")) {
                    ItemStack egg = plugin.getPetManager().getPetEggIcon().clone();
                    p.getInventory().addItem(egg);
                    p.sendMessage(ChatColor.GREEN + "Egg given!");
                } else if (petName.equals("icon")) {
                    plugin.getPetManager().setPetEggIcon(p.getInventory().getItemInMainHand());
                    p.sendMessage(ChatColor.GREEN + "Egg icon set!");
                }
                break;
        }
        return true;
    }
}

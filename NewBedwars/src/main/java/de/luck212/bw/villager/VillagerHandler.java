package de.luck212.bw.villager;

import de.luck212.bw.forcetrades.ForceVillagerTrade;
import de.luck212.bw.gamestates.IngameState;
import de.luck212.bw.main.Main;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VillagerHandler implements CommandExecutor, Listener {
    private final String SHOP_SELECT_INVENTORY = "§fAuswahl",
                         SHOP_SELECT_BLOCKS = "Blöcke",
                         SHOP_SELECT_FOOD = "Essen",
                         SHOP_SELECT_BOW = "Bögen",
                         SHOP_SELECT_ARMOR = "Rüstung",
                         SHOP_SELECT_PICKAXE = "Spitzhacken",
                         SHOP_SELECT_WEAPONS = "Waffen";

    private Main plugin;

    public VillagerHandler(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if(player.hasPermission("bedwars.shop")) {
                if(args.length == 0) {
                    new ShopVillager(player.getLocation());
                    player.sendMessage("§aDer Shop wurde erfolgreich erstellt.");
                }else
                    player.sendMessage("§cBitte benutze §6/shopcreate§c!");
            }else
                player.sendMessage("§cDazu hast du keine Rechte!");
        }
        return false;
    }

    @EventHandler
    public void handleShopInteract(PlayerInteractEntityEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        if(!(event.getRightClicked() instanceof Villager)) return;
        Villager shop = (Villager) event.getRightClicked();
        if(shop.getCustomName().equals(ShopVillager.VILLAGER_NAME)) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            player.openInventory(createShopInventory(player));
        }
    }


    @EventHandler
    public void handleShopSelectClick(InventoryClickEvent event){
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(event.getClickedInventory().getTitle().equals(SHOP_SELECT_INVENTORY)){
            if(event.getCurrentItem().getItemMeta() == null) return;
            if(event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
            Player player = ((Player) event.getWhoClicked()).getPlayer();
            ItemStack clickedItem = event.getCurrentItem();
            ItemMeta clickedItemMeta = clickedItem.getItemMeta();


            //TODO (Noch einführen das dann Merchant Inventory geöffnet wird) = done / oder den Villager mit den Trades befüllen und noch alles custom machen
            EntityHuman e = ((CraftPlayer) player).getHandle();
            ForceVillagerTrade forceVillagerTrade = new ForceVillagerTrade(event.getClickedInventory().getName());
            forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 4), new ItemStack(Material.COOKED_BEEF) );


            switch (event.getCurrentItem().getItemMeta().getDisplayName()){
                case SHOP_SELECT_ARMOR:
                    forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 1), new ItemStack(Material.LEATHER_BOOTS, 1));
                    forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 1), new ItemStack(Material.LEATHER_HELMET, 1));
                    forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 1), new ItemStack(Material.LEATHER_LEGGINGS, 1));

                    ItemStack chainmailChest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);

                    chainmailChest.addEnchantment(Enchantment.DURABILITY, 1);
                    forceVillagerTrade.addTrade(new ItemStack(Material.IRON_INGOT, 1), chainmailChest);
                    chainmailChest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    forceVillagerTrade.addTrade(new ItemStack(Material.IRON_INGOT, 3), chainmailChest);
                    chainmailChest.addEnchantment(Enchantment.DURABILITY, 3);
                    forceVillagerTrade.addTrade(new ItemStack(Material.IRON_INGOT, 7), chainmailChest);
                    forceVillagerTrade.openTrade(player);
                    break;
                case SHOP_SELECT_BLOCKS:
                    forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 1), new ItemStack(Material.SANDSTONE, 4));
                    forceVillagerTrade.addTrade(new ItemStack(Material.IRON_INGOT, 3), new ItemStack(Material.IRON_BLOCK));
                    forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 1), new ItemStack(Material.LADDER, 4));
                    forceVillagerTrade.openTrade(player);
                    break;
                case SHOP_SELECT_BOW:
                    forceVillagerTrade.addTrade(new ItemStack(Material.GOLD_INGOT, 1), new ItemStack(Material.ARROW));

                    ItemStack bow = new ItemStack(Material.BOW);
                    bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                    forceVillagerTrade.addTrade(new ItemStack(Material.GOLD_INGOT, 4), bow);

                    bow.addEnchantment(Enchantment.ARROW_DAMAGE ,1);
                    forceVillagerTrade.addTrade(new ItemStack(Material.GOLD_INGOT, 7), bow);

                    bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
                    forceVillagerTrade.addTrade(new ItemStack(Material.GOLD_INGOT, 10), bow);
                    forceVillagerTrade.openTrade(player);
                    break;
                case SHOP_SELECT_FOOD:
                    forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 1), new ItemStack(Material.APPLE, 2));
                    forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 4), new ItemStack(Material.COOKED_BEEF));
                    forceVillagerTrade.addTrade(new ItemStack(Material.GOLD_INGOT, 2), new ItemStack(Material.GOLDEN_APPLE, 1));
                    forceVillagerTrade.openTrade(player);
                    break;
                case SHOP_SELECT_WEAPONS:
                    ItemStack stick = new ItemStack(Material.STICK);
                    stick.addEnchantment(Enchantment.KNOCKBACK, 2);
                    forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 8), stick);

                    ItemStack goldSword = new ItemStack(Material.GOLD_SWORD);
                    goldSword.addEnchantment(Enchantment.DURABILITY, 2);
                    forceVillagerTrade.addTrade(new ItemStack(Material.IRON_INGOT, 3), goldSword);

                    goldSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    forceVillagerTrade.addTrade(new ItemStack(Material.IRON_INGOT, 5), goldSword);

                    goldSword.addEnchantment(Enchantment.DAMAGE_ALL, 3);
                    forceVillagerTrade.addTrade(new ItemStack(Material.IRON_INGOT, 5), goldSword);
                    forceVillagerTrade.openTrade(player);
                    break;
                case SHOP_SELECT_PICKAXE:
                    ItemStack pickaxe = new ItemStack(Material.WOOD_PICKAXE);
                    pickaxe.addEnchantment(Enchantment.DURABILITY, 2);
                    forceVillagerTrade.addTrade(new ItemStack(Material.CLAY_BRICK, 4), pickaxe);

                    pickaxe = new ItemStack(Material.STONE_PICKAXE);
                    pickaxe.addEnchantment(Enchantment.DURABILITY, 2);
                    forceVillagerTrade.addTrade(new ItemStack(Material.IRON_INGOT, 2), pickaxe);

                    pickaxe = new ItemStack(Material.IRON_PICKAXE);
                    pickaxe.addEnchantment(Enchantment.DURABILITY, 2);
                    pickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);
                    forceVillagerTrade.addTrade(new ItemStack(Material.GOLD_INGOT, 1), pickaxe);
                    forceVillagerTrade.openTrade(player);
                    break;
                default:
                    break;
            }


        }

    }


    @EventHandler
    public void handleShopDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Villager)) return;
        Villager shop = (Villager) event.getEntity();
        if(!(shop.getCustomName().equals(ShopVillager.VILLAGER_NAME))) return;

        event.setCancelled(true);
        if(!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        if(player.hasPermission("bedwars.shop.kill")) {
            if(player.getItemInHand().getType() == Material.LAVA_BUCKET) {
                shop.remove();
                player.sendMessage("§aDu hast den Shop entfernt!");
            }
        }
    }



    private Inventory createShopInventory(Player player){
        Inventory shopSelectionInv = Bukkit.createInventory(player, 9*3, SHOP_SELECT_INVENTORY);

        ItemStack shopItem = new ItemStack(Material.SANDSTONE);
        ItemMeta shopItemMeta = shopItem.getItemMeta();
        shopItemMeta.setDisplayName(SHOP_SELECT_BLOCKS);
        shopItem.setItemMeta(shopItemMeta);
        shopSelectionInv.setItem(11, shopItem);

        shopItem = new ItemStack(Material.STICK);
        shopItem.addEnchantment(Enchantment.KNOCKBACK, 1);
        shopItemMeta.setDisplayName(SHOP_SELECT_WEAPONS);
        shopItem.setItemMeta(shopItemMeta);
        shopSelectionInv.setItem(13, shopItem);

        shopItem = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        shopItemMeta.setDisplayName(SHOP_SELECT_ARMOR);
        shopItem.setItemMeta(shopItemMeta);
        shopSelectionInv.setItem(15, shopItem);

        shopItem = new ItemStack(Material.BOW);
        shopItemMeta.setDisplayName(SHOP_SELECT_BOW);
        shopItem.setItemMeta(shopItemMeta);
        shopSelectionInv.setItem(17, shopItem);

        shopItem = new ItemStack(Material.COOKED_BEEF);
        shopItemMeta.setDisplayName(SHOP_SELECT_FOOD);
        shopItem.setItemMeta(shopItemMeta);
        shopSelectionInv.setItem(24, shopItem);

        shopItem = new ItemStack(Material.STONE_PICKAXE);
        shopItemMeta.setDisplayName(SHOP_SELECT_PICKAXE);
        shopItem.setItemMeta(shopItemMeta);
        shopSelectionInv.setItem(25, shopItem);

        for(int i = 0; i <= shopSelectionInv.getSize(); i++){
            if(shopSelectionInv.getItem(i) == null)
                shopSelectionInv.setItem(i, new ItemStack(Material.THIN_GLASS));
        }

        return shopSelectionInv;
    }
}
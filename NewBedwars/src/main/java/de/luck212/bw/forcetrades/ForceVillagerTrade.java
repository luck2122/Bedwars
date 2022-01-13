package de.luck212.bw.forcetrades;

import net.minecraft.server.v1_8_R1.*;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

//Kopiert von  https://bukkit.org/threads/force-open-custom-villager-trade-gui.341546/

public class ForceVillagerTrade {
    private String invname;
    private MerchantRecipeList l = new MerchantRecipeList();


    public ForceVillagerTrade(String invname) {
        this.invname = invname;
    }


    public ForceVillagerTrade addTrade(org.bukkit.inventory.ItemStack in, org.bukkit.inventory.ItemStack out) {
        l.add(new MerchantRecipe(CraftItemStack.asNMSCopy(in), CraftItemStack
                .asNMSCopy(out)));
        return this;
    }


    public ForceVillagerTrade addTrade(org.bukkit.inventory.ItemStack inOne, org.bukkit.inventory.ItemStack inTwo,
                                        org.bukkit.inventory.ItemStack out) {
        l.add(new MerchantRecipe(CraftItemStack.asNMSCopy(inOne),
                CraftItemStack.asNMSCopy(inTwo), CraftItemStack.asNMSCopy(out)));
        return this;
    }


    public void openTrade(Player who) {
        final EntityHuman e = ((CraftPlayer) who).getHandle();
        e.openTrade(new IMerchant() {
            //@Override
            public MerchantRecipeList getOffers(EntityHuman arg0) {
                return l;
            }

            //@Override
            public void a_(ItemStack arg0) {
            }

            //@Override
            public void a_(EntityHuman arg0) {
            }

            //@Override
            public EntityHuman u_() {
                return e;
            }

            //@Override
            public IChatBaseComponent getScoreboardDisplayName() {
                return ChatSerializer.a(invname);
            }

            //@Override
            public void a(MerchantRecipe arg0) {
            }
        });
    }
}
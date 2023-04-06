package xyz.bluspring.kilt.forgeinjects.world.inventory;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.bluspring.kilt.injections.world.inventory.AnvilMenuInjection;

@Mixin(AnvilMenu.class)
public class AnvilMenuInject implements AnvilMenuInjection {
    @Shadow @Final private DataSlot cost;

    @Override
    public void setMaximumCost(int value) {
        this.cost.set(value);
    }
}
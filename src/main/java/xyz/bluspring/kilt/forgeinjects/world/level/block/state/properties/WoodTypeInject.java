package xyz.bluspring.kilt.forgeinjects.world.level.block.state.properties;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import xyz.bluspring.kilt.helpers.mixin.CreateStatic;
import xyz.bluspring.kilt.injections.world.level.block.state.properties.WoodTypeInjection;

@Mixin(WoodType.class)
public class WoodTypeInject implements WoodTypeInjection {
    @CreateStatic
    private static WoodType create(String name) {
        return WoodTypeInjection.create(name);
    }
}

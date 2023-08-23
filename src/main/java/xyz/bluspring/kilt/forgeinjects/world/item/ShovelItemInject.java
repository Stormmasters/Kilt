package xyz.bluspring.kilt.forgeinjects.world.item;

import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import xyz.bluspring.kilt.helpers.mixin.CreateStatic;
import xyz.bluspring.kilt.injections.item.ShovelItemInjection;

@Mixin(ShovelItem.class)
public class ShovelItemInject implements ShovelItemInjection {
    @CreateStatic
    private static BlockState getShovelPathingState(BlockState originalState) {
        return ShovelItemInjection.getShovelPathingState(originalState);
    }
}

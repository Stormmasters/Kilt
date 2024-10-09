package xyz.bluspring.kilt.forgeinjects.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraftforge.common.ToolActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BambooSaplingBlock.class)
public abstract class BambooSaplingBlockInject {
    @WrapOperation(method = "getDestroyProgress", at = @At(value = "CONSTANT", args = "classValue=net/minecraft/world/item/SwordItem"))
    private boolean kilt$checkCanPerformSwordDig(Object object, Operation<Boolean> original, @Local(argsOnly = true) Player player) {
        return original.call(object) || player.getMainHandItem().canPerformAction(ToolActions.SWORD_DIG);
    }
}
package xyz.bluspring.kilt.forgeinjects.client.renderer.block.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import xyz.bluspring.kilt.helpers.mixin.CreateStatic;
import xyz.bluspring.kilt.injections.client.render.block.model.ItemTransformsTransformTypeInjection;

@Mixin(ItemTransforms.class)
public class ItemTransformsInject {
    @Mixin(ItemTransforms.TransformType.class)
    public static class TransformTypeInject implements ItemTransformsTransformTypeInjection, IExtensibleEnum {
        @CreateStatic
        private static ItemTransforms.TransformType create(String keyName, ResourceLocation serializeName) {
            return ItemTransformsTransformTypeInjection.create(keyName, serializeName);
        }

        @CreateStatic
        private static ItemTransforms.TransformType create(String keyName, ResourceLocation serializeName, ItemTransforms.TransformType fallback) {
            return ItemTransformsTransformTypeInjection.create(keyName, serializeName, fallback);
        }

        private String serializeName;
        private boolean isModded = false;
        @javax.annotation.Nullable
        private ItemTransforms.TransformType fallback;

        @Override
        public String getSerializeName() {
            if (serializeName == null) {
                // Make the serialized name later, since we can't do it as an initializer.
                var transformType = ((ItemTransforms.TransformType) (Object) this);

                switch (transformType.name()) {
                    case "THIRD_PERSON_LEFT_HAND" ->
                            serializeName = "thirdperson_lefthand";
                    case "THIRD_PERSON_RIGHT_HAND" ->
                            serializeName = "thirdperson_righthand";
                    case "FIRST_PERSON_LEFT_HAND" ->
                            serializeName = "firstperson_lefthand";
                    case "FIRST_PERSON_RIGHT_HAND" ->
                            serializeName = "firstperson_righthand";
                    default ->
                            serializeName = transformType.name().toLowerCase();
                }
            }

            return serializeName;
        }

        @Nullable
        @Override
        public ItemTransforms.TransformType fallback() {
            return fallback;
        }

        @Override
        public boolean isModded() {
            return isModded;
        }

        @Override
        public void setSerializeName(String name) {
            serializeName = name;
        }

        @Override
        public void setFallback(ItemTransforms.TransformType fallback) {
            this.fallback = fallback;
        }

        @Override
        public void setModded(boolean modded) {
            this.isModded = modded;
        }
    }
}

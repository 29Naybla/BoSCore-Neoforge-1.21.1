package com.x29naybla.bos_core.mixin;

import com.x29naybla.bos_core.common.entity.LeatherCollarMixinAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.item.component.DyedItemColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static com.x29naybla.bos_core.common.BoSEvents.DEFAULT_NAME_COLOR;

@Mixin(NameTagItem.class)
public abstract class NameTagItemMixin extends Item {

    public NameTagItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(at= {@At("HEAD")}, method = {"interactLivingEntity"}, cancellable = true)
    private void bos_interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        if (!(target instanceof Player)) {
            if (!player.level().isClientSide() && target.isAlive()) {
                target.setCustomName(bos_getNewName(stack, target));

                if (target instanceof Mob) {
                    ((Mob) target).setPersistenceRequired();
                }

                if (target instanceof Wolf wolf && wolf.isTame() && wolf.isOwnedBy(player)){
                    ((LeatherCollarMixinAccess) wolf).bos_setLeatherCollarColor(Objects.requireNonNull(stack.get(DataComponents.DYED_COLOR)).rgb());
                } else if (target instanceof Cat cat && cat.isTame() && cat.isOwnedBy(player)){
                    ((LeatherCollarMixinAccess) cat).bos_setLeatherCollarColor(Objects.requireNonNull(stack.get(DataComponents.DYED_COLOR)).rgb());
                }

                stack.shrink(1);
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(player.level().isClientSide()));
            cir.cancel();
            return;
        }
        cir.setReturnValue(InteractionResult.PASS);
        cir.cancel();
    }

    @Unique
    private MutableComponent bos_getNewName(ItemStack stack, LivingEntity entity) {
        int color = FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(stack, DEFAULT_NAME_COLOR));
        Style style = Style.EMPTY.withColor(color);

        String nameText;
        if (stack.get(DataComponents.CUSTOM_NAME) != null) {
            nameText = stack.getHoverName().getString();
        } else {
            if (entity.hasCustomName()) {
                nameText = Objects.requireNonNull(entity.getCustomName()).getString();
            } else {
                return null;
            }
        }
        return Component.literal(nameText).setStyle(style);
    }
}

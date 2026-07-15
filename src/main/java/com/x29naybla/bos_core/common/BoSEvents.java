package com.x29naybla.bos_core.common;

import com.x29naybla.bos_core.BoSCore;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

//@EventBusSubscriber(modid = BoSCore.MODID)
public class BoSEvents {

    /*
    @SubscribeEvent
    public static void mapPlacement(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!event.getLevel().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) event.getLevel();
            if(player.getItemInHand(event.getHand()).is(Items.FILLED_MAP) && player.isCrouching()) {
                assert event.getFace() != null;
                ItemFrame itemFrame = new ItemFrame(serverLevel, event.getPos(), event.getFace());
                itemFrame.setItem(player.getItemInHand(event.getHand()));

                if(event.getFace().equals(Direction.UP) && serverLevel.getBlockState(event.getPos()).isFaceSturdy(serverLevel, event.getPos(), Direction.UP)) {
                    itemFrame.setPos(event.getPos().getX(), event.getPos().getY()+1, event.getPos().getZ());
                } else if(event.getFace().equals(Direction.DOWN) && serverLevel.getBlockState(event.getPos()).isFaceSturdy(serverLevel, event.getPos(), Direction.DOWN)){
                    itemFrame.setPos(event.getPos().getX(), event.getPos().getY()-1, event.getPos().getZ());
                } else if(event.getFace().equals(Direction.SOUTH) && serverLevel.getBlockState(event.getPos()).isFaceSturdy(serverLevel, event.getPos(), Direction.SOUTH)){
                    itemFrame.setPos(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()+1);
                } else if(event.getFace().equals(Direction.NORTH) && serverLevel.getBlockState(event.getPos()).isFaceSturdy(serverLevel, event.getPos(), Direction.NORTH)){
                    itemFrame.setPos(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()-1);
                } else if(event.getFace().equals(Direction.WEST) && serverLevel.getBlockState(event.getPos()).isFaceSturdy(serverLevel, event.getPos(), Direction.WEST)){
                    itemFrame.setPos(event.getPos().getX()-1, event.getPos().getY(), event.getPos().getZ());
                } else if(event.getFace().equals(Direction.EAST) && serverLevel.getBlockState(event.getPos()).isFaceSturdy(serverLevel, event.getPos(), Direction.EAST)){
                    itemFrame.setPos(event.getPos().getX()+1, event.getPos().getY(), event.getPos().getZ());
                } else return;

                player.swing(event.getHand());
                if(!player.isCreative()) player.getItemInHand(event.getHand()).shrink(1);
                serverLevel.addFreshEntity(itemFrame);
            }
        }
    }

    @SubscribeEvent
    public static void mapBreaking(EntityTickEvent.Pre event){
        if(event.getEntity() instanceof ItemFrame itemFrame && itemFrame.hasFramedMap()){
            if(!itemFrame.survives()){
                itemFrame.playSound(itemFrame.getRemoveItemSound(), 1.0F, 1.0F);
                itemFrame.spawnAtLocation(itemFrame.getItem());
                itemFrame.kill();
            }
        }
    }

    @SubscribeEvent
    public static void mapHurt(AttackEntityEvent event){
        if(event.getTarget() instanceof ItemFrame itemFrame && itemFrame.hasFramedMap()){
            itemFrame.playSound(itemFrame.getRemoveItemSound(), 1.0F, 1.0F);
            itemFrame.spawnAtLocation(itemFrame.getItem());
            itemFrame.kill();
        }
    }
     */
}

package com.x29naybla.bos_core.common.block.entity;

import com.google.common.collect.Lists;
import com.x29naybla.bos_core.BoSCore;
import com.x29naybla.bos_core.common.block.entity.container.OvenMenu;
import com.x29naybla.bos_core.common.block.OvenBlock;
import com.x29naybla.bos_core.common.block.entity.inventory.OvenItemHandler;
import com.x29naybla.bos_core.common.recipe.AbstractBakingRecipe;
import com.x29naybla.bos_core.common.registry.BoSBlockEntities;
import com.x29naybla.bos_core.common.registry.BoSRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.x29naybla.bos_core.common.block.OvenBlock.LIT;
import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.isFuel;

@EventBusSubscriber(modid = BoSCore.MODID)
public class OvenBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer, Nameable, RecipeCraftingHolder, StackedContentsCompatible, Clearable {
    private static final int[] INGREDIENT_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int FUEL_SLOT = 9;
    private static final int OUTPUT_SLOT = 10;
    public final ItemStackHandler inventory;
    private final IItemHandler inputHandler;
    private final IItemHandler outputHandler;
    private int bakingProgress = 0;
    private int bakingTotalTime = 72;
    private int litTime = 0;
    private int fuelAmount = 0;
    private ItemStack activeFuel = ItemStack.EMPTY;
    private Component customName;
    protected final ContainerData containerData;
    private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;
    private final RecipeManager.CachedCheck<RecipeWrapper, AbstractBakingRecipe> quickCheck;

    public OvenBlockEntity(BlockPos pos, BlockState blockState) {
        super(BoSBlockEntities.OVEN.get(), pos, blockState);
        this.inventory = createHandler(this);
        this.inputHandler = new OvenItemHandler(inventory, Direction.UP);
        this.outputHandler = new OvenItemHandler(inventory, Direction.DOWN);
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.quickCheck = RecipeManager.createCheck(BoSRecipeTypes.BAKING.get());
        containerData  = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> OvenBlockEntity.this.bakingProgress;
                    case 1 -> OvenBlockEntity.this.bakingTotalTime;
                    case 2 -> OvenBlockEntity.this.litTime;
                    case 3 -> OvenBlockEntity.this.fuelAmount;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: OvenBlockEntity.this.bakingProgress = value;
                    case 1: OvenBlockEntity.this.bakingTotalTime = value;
                    case 2: OvenBlockEntity.this.litTime = value;
                    case 3: OvenBlockEntity.this.fuelAmount = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BoSBlockEntities.OVEN.get(),
                (be, context) -> {
                    if (context == Direction.UP) {
                        return be.inputHandler;
                    }
                    return be.outputHandler;
                }
        );
    }


    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        bakingProgress = tag.getInt("BakeTime");
        bakingTotalTime = tag.getInt("BakeTimeTotal");
        litTime = tag.getInt("LitTime");
        fuelAmount = tag.getInt("FuelAmount");
        activeFuel = ItemStack.parseOptional(registries, tag.getCompound("ActiveFuel"));
        if (tag.contains("CustomName", 8)) {
            this.customName = parseCustomNameSafe(tag.getString("CustomName"), registries);
        }
        CompoundTag compoundRecipes = tag.getCompound("RecipesUsed");
        for (String key : compoundRecipes.getAllKeys()) {
            usedRecipeTracker.put(ResourceLocation.parse(key), compoundRecipes.getInt(key));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("BakeTime", bakingProgress);
        tag.putInt("BakeTimeTotal", bakingTotalTime);
        tag.putInt("LitTime", litTime);
        tag.putInt("FuelAmount", fuelAmount);
        tag.put("ActiveFuel", activeFuel.saveOptional(registries));
        if (this.customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.customName, registries));
        }
        tag.put("Inventory", inventory.serializeNBT(registries));
        CompoundTag compoundRecipes = new CompoundTag();
        usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        tag.put("RecipesUsed", compoundRecipes);
    }

    private CompoundTag writeItems(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.put("Inventory", inventory.serializeNBT(registries));
        return compound;
    }

    /*
    public ItemStack getAsItem() {
        ItemStack stack = new ItemStack(BoSBlocks.OVEN.asItem());
        stack.applyComponents(collectComponents());
        return stack;
    }
     */


    public static void bakingTick(Level level, BlockPos pos, BlockState state, OvenBlockEntity ovenBlockEntity) {
        if (isFueled(ovenBlockEntity, pos, level)) {
            ovenBlockEntity.litTime--;
            if (ovenBlockEntity.litTime <= 0) {
                ovenBlockEntity.activeFuel = ItemStack.EMPTY;
            }
        } else {
            ovenBlockEntity.litTime = 0;
        }

        if (hasRecipe(ovenBlockEntity)) {
            ovenBlockEntity.bakingProgress++;
            ovenBlockEntity.setChanged(level, pos, state, true);
            if (ovenBlockEntity.bakingProgress == ovenBlockEntity.bakingTotalTime) {
                Optional<RecipeHolder<AbstractBakingRecipe>> recipe = ovenBlockEntity.quickCheck.getRecipeFor(new RecipeWrapper(ovenBlockEntity.inventory), level);
                recipe.ifPresent(abstractBakingRecipeRecipeHolder -> ovenBlockEntity.craftItem(abstractBakingRecipeRecipeHolder, ovenBlockEntity));
            }
        } else {
            ovenBlockEntity.bakingProgress = 0;
            ovenBlockEntity.bakingTotalTime = 72;
        }
    }

    private void craftItem(RecipeHolder<AbstractBakingRecipe> recipe, OvenBlockEntity ovenBlockEntity) {
        if (level == null) return;
        handleRemainingItem(ovenBlockEntity);

        assert ovenBlockEntity.level != null;
        ItemStack resultStack = recipe.value().getResultItem(ovenBlockEntity.level.registryAccess());
        ItemStack currentOutput = ovenBlockEntity.inventory.getStackInSlot(OUTPUT_SLOT);
        if (currentOutput.isEmpty()) {
            ovenBlockEntity.inventory.setStackInSlot(OUTPUT_SLOT, resultStack.copy());
        } else {
            currentOutput.grow(resultStack.getCount());
        }
        ovenBlockEntity.setRecipeUsed(recipe);

        ovenBlockEntity.bakingProgress = 0;
        ovenBlockEntity.bakingTotalTime = 72;
    }

    static boolean startCraftIfFueled(OvenBlockEntity ovenBlockEntity, BlockPos pos, Level level, AbstractBakingRecipe recipe) {
        if (!isFueled(ovenBlockEntity, pos, level) || !recipe.fuelMatches(ovenBlockEntity.activeFuel)) {
            if (!ovenBlockEntity.burnFuel(recipe))
                return false;
        }
        ovenBlockEntity.bakingTotalTime = recipe.getCookTime();
        return true;
    }

    private boolean burnFuel(AbstractBakingRecipe recipe) {
        assert this.level != null;
        if (!this.level.isClientSide) {
            var fuel = this.inventory.getStackInSlot(FUEL_SLOT).copy();

            // look for fuel definitions that specifically consider this recipe type
            int burnTime = fuel.getBurnTime(recipe.getType());
            // then prioritize smoking if none exist
            if (burnTime <= 0) {
                burnTime = fuel.getBurnTime(RecipeType.SMOKING);
            }
            // then default furnaces
            if (burnTime <= 0) {
                burnTime = fuel.getBurnTime(RecipeType.SMELTING);
            }

            if (burnTime > 0) {
                this.fuelAmount = burnTime;
                this.litTime = burnTime;
                this.activeFuel = fuel.copyWithCount(1);
                if (fuel.getCount() > 1) {
                    fuel.setCount(fuel.getCount() - 1);
                    this.inventory.setStackInSlot(FUEL_SLOT, fuel);
                } else {
                    this.inventory.setStackInSlot(FUEL_SLOT, fuel.getCraftingRemainingItem());
                }
                return true;
            }

        }
        return false;
    }

    public static void handleRemainingItem(OvenBlockEntity ovenBlockEntity) {
        for (int i = 0; i < 9; ++i) {
            ItemStack slotStack = ovenBlockEntity.inventory.getStackInSlot(i);
            if(slotStack.hasCraftingRemainingItem()) {
                if(slotStack.getCount() == 1) ovenBlockEntity.inventory.setStackInSlot(i, slotStack.getCraftingRemainingItem());
                else {
                    Direction direction = ovenBlockEntity.getBlockState().getValue(OvenBlock.FACING).getCounterClockWise();
                    double x = (double) ovenBlockEntity.worldPosition.getX() + 0.5 + (double) direction.getStepX() * 0.25;
                    double y = (double) ovenBlockEntity.worldPosition.getY() + 0.7;
                    double z = (double) ovenBlockEntity.worldPosition.getZ() + 0.5 + (double) direction.getStepZ() * 0.25;
                    spawnItemEntity(ovenBlockEntity.level, ovenBlockEntity.inventory.getStackInSlot(i).getCraftingRemainingItem(), x, y, z, (float) direction.getStepX() * 0.08F, 0.25, (float) direction.getStepZ() * 0.08F);
                    ovenBlockEntity.inventory.extractItem(i, 1, false);
                }
            } else {
                ovenBlockEntity.inventory.extractItem(i, 1, false);
            }
        }
    }

    public static void spawnItemEntity(Level level, ItemStack stack, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDeltaMovement(xMotion, yMotion, zMotion);
        level.addFreshEntity(entity);
    }

    private static boolean hasRecipe(OvenBlockEntity ovenBlockEntity) {
        Level level = ovenBlockEntity.level;
        BlockPos pos = ovenBlockEntity.getBlockPos();

        assert level != null;
        Optional<RecipeHolder<AbstractBakingRecipe>> recipeMatch = ovenBlockEntity.quickCheck.getRecipeFor(new RecipeWrapper(ovenBlockEntity.inventory), level);

        if (recipeMatch.isPresent()) {
            AbstractBakingRecipe recipe = recipeMatch.get().value();
            ItemStack result = recipe.getResultItem(level.registryAccess());
            if (canInsertAmountIntoOutputSlot(ovenBlockEntity.inventory, result)) {
                return startCraftIfFueled(ovenBlockEntity, pos, level, recipe);
            }
        }

        return false;
    }

    private static boolean canInsertAmountIntoOutputSlot(ItemStackHandler handler, ItemStack output) {
        ItemStack currentOutput = handler.getStackInSlot(OUTPUT_SLOT);
        if (currentOutput.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(currentOutput, output)) {
            return false;
        }
        return currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize();
    }

    static boolean isFueled(OvenBlockEntity ovenBlockEntity, BlockPos pos, Level level) {
        if (level.isClientSide) return false;
        if (ovenBlockEntity.litTime > 0) {
            ovenBlockEntity.setChanged(level, pos, ovenBlockEntity.getBlockState(), true);
            return true;
        } else {
            ovenBlockEntity.setChanged(level, pos, ovenBlockEntity.getBlockState(), false);
            return false;
        }
    }

    private void setChanged(Level pLevel, BlockPos pPos, BlockState pState, boolean b) {
        pLevel.setBlock(pPos, pState.setValue(LIT, b), 3);
        super.setChanged();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    public @NotNull Component getName() {
        if (this.customName != null) return this.customName;
        else return Component.translatable("container.oven");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.UP) {
            return INGREDIENT_SLOTS;
        } else {
            return new int[]{side == Direction.DOWN ? OUTPUT_SLOT : FUEL_SLOT};
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        if (direction == Direction.DOWN && index == OUTPUT_SLOT) {
            return true;
        }
        return direction != Direction.UP && index == FUEL_SLOT && !isFuel(stack);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.inventory.getSlots(); ++i) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return this.inventory.extractItem(slot, amount, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return this.inventory.extractItem(slot, 1, false);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.inventory.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if(this.level == null) return false;
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new OvenMenu(containerId, playerInventory, this, this.containerData);
    }

    @Override
    public void fillStackedContents(@NotNull StackedContents contents) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            contents.accountStack(this.getItem(i));
        }
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeID = recipe.id();
            usedRecipeTracker.addTo(recipeID, 1);
        }
    }

    @Override
    public @Nullable RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player, @NotNull List<ItemStack> items) {
        List<RecipeHolder<?>> usedRecipes = getRecipesToAwardAndPopExperience((ServerLevel) player.level(), player.position());
        player.awardRecipes(usedRecipes);
        usedRecipeTracker.clear();
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
        List<RecipeHolder<?>> list = this.getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
        player.awardRecipes(list);

        for (RecipeHolder<?> recipeholder : list) {
            if (recipeholder != null) {
                player.triggerRecipeCrafted(recipeholder, Collections.singletonList(this.inventory.getStackInSlot(OUTPUT_SLOT)));
            }
        }

        this.usedRecipeTracker.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 pos) {
        List<RecipeHolder<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : usedRecipeTracker.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                if (recipe.value() instanceof AbstractBakingRecipe bakingRecipe) {
                    list.add(recipe);
                    createExperience(level, pos, entry.getIntValue(), bakingRecipe.getExperience());
                }
            });
        }

        return list;
    }

    private static void createExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
        int expTotal = Mth.floor((float) craftedAmount * experience);
        float expFraction = Mth.frac((float) craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }

        ExperienceOrb.award(level, pos, expTotal);
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < getContainerSize(); ++i) {
            if (i != FUEL_SLOT && i!= OUTPUT_SLOT) {
                drops.add(inventory.getStackInSlot(i));
            }
        }
        return drops;
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return writeItems(new CompoundTag(), registries);
    }

    /*
    protected void inventoryChanged() {
        super.setChanged();
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
     */

    @Override
    protected void applyImplicitComponents(BlockEntity.@NotNull DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.customName = componentInput.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.customName);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("CustomName");
    }

    private ItemStackHandler createHandler(OvenBlockEntity ovenBlockEntity) {
        return new ItemStackHandler(11) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }
}

package de.crafty.eiv.common.builtin.villager;

import de.crafty.eiv.common.access.IEivEntity;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.mixin.world.entity.npc.*;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.util.EivUtil;
import de.crafty.eiv.common.rendering.EivGuiRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VillagerViewRecipe implements IEivViewRecipe {


    private final SlotContent offer, cost1, cost2;

    protected VillagerOffer villagerOffer;

    private Villager previewVillager = null;
    private boolean prevVillagerLookLeft, villagerLookLeft = false;
    private int lastHeadChange, currentTick = 0;

    private final Random random;

    public VillagerViewRecipe(VillagerOffer villagerOffer) {

        this.offer = SlotContent.of(villagerOffer.offerStacks());
        this.cost1 = SlotContent.of(villagerOffer.cost1());
        this.cost2 = SlotContent.of(villagerOffer.cost2());

        this.villagerOffer = villagerOffer;
        this.random = new Random();


        if (Minecraft.getInstance().level != null)
            this.prevVillagerLookLeft = this.villagerLookLeft = this.random.nextBoolean();
    }

    private VillagerViewRecipe(SlotContent offer, SlotContent cost1, SlotContent cost2, VillagerOffer villagerOffer) {
        this.offer = offer;
        this.cost1 = cost1;
        this.cost2 = cost2;
        this.villagerOffer = villagerOffer;
        this.random = new Random();

        if (Minecraft.getInstance().level != null)
            this.prevVillagerLookLeft = this.villagerLookLeft = this.random.nextBoolean();
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return VillagerViewType.INSTANCE;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindDependantSlot(0, this.offer::index, this.cost1);
        slotFillContext.bindSlot(1, this.cost2);

        slotFillContext.bindSlot(2, this.offer);

    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.cost1, this.cost2);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.offer);
    }


    @Override
    public void tick() {

        this.currentTick++;

        if (Minecraft.getInstance().level == null)
            return;

        if (this.currentTick - this.lastHeadChange < 3 * 20)
            return;

        this.prevVillagerLookLeft = this.villagerLookLeft;
        this.villagerLookLeft = this.random.nextBoolean();
        this.lastHeadChange = this.currentTick;

    }

    @Override
    public void initRecipe() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        this.previewVillager = EntityType.VILLAGER.create(level);
        if (this.previewVillager == null)
            return;


        this.previewVillager.setVillagerData(this.previewVillager.getVillagerData().setLevel(this.villagerOffer.professionLevel()).setType(VillagerType.PLAINS).setProfession(this.villagerOffer.profession()));
        this.previewVillager.setNoAi(true);
        this.previewVillager.setYHeadRot((this.villagerLookLeft ? -1.0F : 1.0F) * 15.0F);

        if (this.villagerOffer.requiredtype() != null)
            this.previewVillager.setVillagerData(this.previewVillager.getVillagerData().setType(this.villagerOffer.requiredtype()));

    }

    @Override
    public void fadeRecipe() {
        if (this.previewVillager != null)
            this.previewVillager.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Font font = Minecraft.getInstance().font;

        VillagerProfession profession = this.villagerOffer.profession();
        String namespace = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).getNamespace();
        String path = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).getPath();
        float scale = 0.75F;

        Component professionComp = Component.translatable("entity." + namespace + ".villager." + path).append(" - ").append(Component.translatable("merchant.level." + this.villagerOffer.professionLevel())).withStyle(ChatFormatting.DARK_GRAY);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, -(font.lineHeight) * scale, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawString(font, professionComp, 0, 0, -1, false);
        guiGraphics.pose().popPose();


        this.updateAnimation(partialTicks);
        this.renderVillager(guiGraphics, recipePosition, mouseX, mouseY, partialTicks);

        if (this.villagerOffer.requiredtype() == null)
            return;

        if (mouseX >= 0 && mouseX <= 24 && mouseY >= 0 && mouseY <= 36) {
            ResourceLocation typeLocation = BuiltInRegistries.VILLAGER_TYPE.getKey(this.villagerOffer.requiredtype());
            Component typeComponent = Component.translatable("view.eiv.type.trading." + typeLocation.getNamespace() + "." + typeLocation.getPath()).withStyle(ChatFormatting.GOLD);
            guiGraphics.renderTooltip(font, typeComponent, recipePosition.left() + mouseX, recipePosition.top() + mouseY);
        }
    }


    private void renderVillager(GuiGraphics guiGraphics, RecipePosition recipePosition, int mouseX, int mouseY, float partialTicks) {

        if (this.previewVillager == null)
            return;

        //EivGuiRenderHelper.renderEntityOnScreen(guiGraphics, this.previewVillager, recipePosition.left() + 2, recipePosition.top() + 2, recipePosition.left() + 2 + 20, recipePosition.top() + 2 + 32, 15.0F, new Vector3f(0, (32.0F / 15.0F / 2.0F), 0), new Quaternionf().rotationXYZ((float) Math.toRadians(180.0F), 0.0F, 0.0F), null);

    }

    private void renderChatVillager(GuiGraphics guiGraphics, RecipeChatEmbedding.ChatRecipeRenderer renderer) {
        if (this.previewVillager == null)
            return;

        ((IEivEntity) this.previewVillager).eiv$setEmbeddingData(new EmbeddingData(renderer.getCurrentAlpha()));
        renderer.renderEntity(guiGraphics, this.previewVillager, 5.0F, 21.5F, 5.0F + 20.0F, 21.5F + 32.0F, 15.0F, new Vector3f(0, (32.0F / 15.0F / 2.0F) + 0.75F, 0), new Quaternionf().rotationXYZ((float) Math.toRadians(180.0F), 0.0F, 0.0F), null);

    }

    @Override
    public void renderRecipeInChat(RecipeChatEmbedding.ChatRecipeRenderer renderer, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Font font = Minecraft.getInstance().font;

        VillagerProfession profession = this.villagerOffer.profession();
        String namespace = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).getNamespace();
        String path = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).getPath();
        float scale = 0.85F;

        Component professionComp = Component.translatable("entity." + namespace + ".villager." + path).append(" - ").append(Component.translatable("merchant.level." + this.villagerOffer.professionLevel())).withStyle(ChatFormatting.DARK_GRAY);

        float effectiveTextScale = 0.5F * scale;
        renderer.drawString(font, guiGraphics, professionComp, 6 * effectiveTextScale, (17 - font.lineHeight) * effectiveTextScale, -1, false, effectiveTextScale);

        this.updateAnimation(partialTicks);
        this.renderChatVillager(guiGraphics, renderer);

        if (this.villagerOffer.requiredtype() == null)
            return;

        if (mouseX >= 0 && mouseX <= 24 && mouseY >= 0 && mouseY <= 36) {
            float chatScaling = Minecraft.getInstance().options.chatScale().get().floatValue();

            ResourceLocation typeLocation = BuiltInRegistries.VILLAGER_TYPE.getKey(this.villagerOffer.requiredtype());
            Component typeComponent = Component.translatable("view.eiv.type.trading." + typeLocation.getNamespace() + "." + typeLocation.getPath()).withStyle(ChatFormatting.GOLD);
            guiGraphics.renderTooltip(font, typeComponent, mouseX + Math.round(renderer.getTotalXOffset() * renderer.getGuiScaling() * chatScaling), mouseY + renderer.getTotalYOffset());
        }

    }

    private void updateAnimation(float partialTicks) {
        if (this.villagerLookLeft != this.prevVillagerLookLeft && this.currentTick - this.lastHeadChange <= 0.25F * 20) {
            float pastTime = this.currentTick - this.lastHeadChange + partialTicks;
            float headRotationProgress = pastTime / (0.25F * 20.0F);
            this.previewVillager.setYHeadRot((this.villagerLookLeft ? -1.0F : 1.0F) * (15.0F * headRotationProgress));
        }
    }

    @Override
    public IEivViewRecipe asChatCopy() {
        return new VillagerViewRecipe(this.offer.copy(), this.cost1.copy(), this.cost2.copy(), this.villagerOffer);
    }


    public record VillagerOffer(VillagerProfession profession, int professionLevel,
                                @Nullable VillagerType requiredtype, List<ItemStack> offerStacks,
                                List<ItemStack> cost1, List<ItemStack> cost2, int villagerXp, int maxUses) {


        public static VillagerOffer of(VillagerProfession profession, int level, VillagerTrades.EmeraldForItems emeraldForItems) {
            EmeraldForItemsAccessor accessor = (EmeraldForItemsAccessor) emeraldForItems;

            return new VillagerOffer(profession, level, null, List.of(new ItemStack(accessor.getItem(), accessor.getCost())), List.of(), List.of(new ItemStack(Items.EMERALD)), accessor.getVillagerXp(), accessor.getMaxUses());
        }

        public static VillagerOffer of(VillagerProfession profession, int level, VillagerTrades.ItemsForEmeralds itemsForEmeralds) {
            ItemsForEmeraldsAccessor accessor = (ItemsForEmeraldsAccessor) itemsForEmeralds;

            return new VillagerOffer(profession, level, null, List.of(new ItemStack(accessor.itemStack().getItem(), accessor.numberOfItems())), List.of(new ItemStack(Items.EMERALD, accessor.emeraldCost())), List.of(), accessor.villagerXp(), accessor.maxUses());
        }

        public static VillagerOffer of(VillagerProfession profession, int level, VillagerTrades.SuspiciousStewForEmerald suspiciousStewForEmerald) {
            SuspiciousStewForEmeraldAccessor accessor = (SuspiciousStewForEmeraldAccessor) suspiciousStewForEmerald;

            ItemStack stack = new ItemStack(Items.SUSPICIOUS_STEW);
            SuspiciousStewItem.saveMobEffect(stack, accessor.effect(), accessor.duration());

            return new VillagerOffer(profession, level, null, List.of(stack), List.of(new ItemStack(Items.EMERALD, 1)), List.of(), accessor.xp(), 12);
        }

        public static List<VillagerOffer> of(VillagerProfession profession, int level, VillagerTrades.EnchantBookForEmeralds enchantBookForEmeralds) {
            EnchantBookForEmeraldsAccessor accessor = (EnchantBookForEmeraldsAccessor) enchantBookForEmeralds;

            List<VillagerOffer> offers = new ArrayList<>();

            BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isTradeable).forEach(enchantment -> {

                List<ItemStack> offerStacks = new ArrayList<>();
                List<ItemStack> costStacks = new ArrayList<>();

                for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
                    ItemStack stack = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, i));
                    offerStacks.add(stack);

                    int emeraldCostsMin = 2 + 3 * i;
                    int emeraldCostsMax = (5 + i * 10 - 1) + 3 * i;

                    if (enchantment.isTreasureOnly()) {
                        emeraldCostsMin *= 2;
                        emeraldCostsMax *= 2;
                    }

                    if (emeraldCostsMin > 64)
                        emeraldCostsMin = 64;

                    if (emeraldCostsMax > 64)
                        emeraldCostsMax = 64;

                    ItemStack costStack = new ItemStack(Items.EMERALD, emeraldCostsMin + (emeraldCostsMax - emeraldCostsMin) / 2);
                    EivUtil.addLoreLine(costStack, Component.literal(emeraldCostsMin + " - " + emeraldCostsMax).withStyle(ChatFormatting.GRAY));
                    costStacks.add(costStack);
                }

                offers.add(new VillagerOffer(profession, level, null, offerStacks, List.of(new ItemStack(Items.BOOK)), costStacks, accessor.villagerXp(), 12));
            });


            return offers;
        }

        public static VillagerOffer of(VillagerProfession profession, int level, VillagerTrades.TreasureMapForEmeralds treasureMapForEmeralds) {
            TreasureMapForEmeraldsAccessor accessor = (TreasureMapForEmeraldsAccessor) treasureMapForEmeralds;

            ItemStack mapStack = new ItemStack(Items.FILLED_MAP);
            MapItemSavedData.addTargetDecoration(mapStack, BlockPos.ZERO, "+", accessor.destinationType());
            mapStack.setHoverName(Component.translatable(accessor.displayName()));

            return new VillagerOffer(profession, level, null, List.of(mapStack), List.of(new ItemStack(Items.EMERALD, accessor.emeraldCost())), List.of(new ItemStack(Items.COMPASS)), accessor.villagerXp(), accessor.maxUses());
        }

        public static VillagerOffer of(VillagerProfession profession, int level, VillagerTrades.TippedArrowForItemsAndEmeralds tippedArrowForItemsAndEmeralds) {
            TippedArrowForItemsAndEmeraldsAccessor accessor = (TippedArrowForItemsAndEmeraldsAccessor) tippedArrowForItemsAndEmeralds;

            ItemStack emeraldCost = new ItemStack(Items.EMERALD, accessor.emeraldCost());
            List<ItemStack> offerStacks = new ArrayList<>();

            BuiltInRegistries.POTION.stream().filter(potion -> !potion.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(potion)).forEach(potion -> {
                ItemStack potionStack = PotionUtils.setPotion(new ItemStack(accessor.toItem().getItem(), accessor.toCount()), potion);
                offerStacks.add(potionStack);
            });

            return new VillagerOffer(profession, level, null, offerStacks, List.of(emeraldCost), List.of(new ItemStack(accessor.fromItem(), accessor.fromCount())), accessor.villagerXp(), accessor.maxUses());
        }

        public static VillagerOffer of(VillagerProfession profession, int level, VillagerTrades.EnchantedItemForEmeralds enchantedItemForEmeralds) {
            EnchantedItemForEmeraldsAccessor accessor = (EnchantedItemForEmeraldsAccessor) enchantedItemForEmeralds;

            ItemStack stack = new ItemStack(accessor.itemStack().getItem());

            List<ItemStack> offerStacks = new ArrayList<>();
            List<ItemStack> costStacks = new ArrayList<>();

            for (int i = 5; i < 15; i++) {
                List<EnchantmentInstance> availableEnchantments = EnchantmentHelper.getAvailableEnchantmentResults(i, stack, false);
                for (EnchantmentInstance enchantment : availableEnchantments) {
                    ItemStack costStack = new ItemStack(Items.EMERALD, Math.min(accessor.baseEmeraldCost() + i, 64));

                    ItemStack enchantedStack = stack.copy();
                    enchantedStack.enchant(enchantment.enchantment, enchantment.level);
                    offerStacks.add(enchantedStack);
                    costStacks.add(costStack);
                }
            }

            return new VillagerOffer(profession, level, null, offerStacks, costStacks, List.of(), accessor.villagerXp(), accessor.maxUses());
        }

        public static VillagerOffer of(VillagerProfession profession, int level, VillagerTrades.DyedArmorForEmeralds dyedArmorForEmeralds) {
            DyedArmorForEmeraldsAccessor accessor = (DyedArmorForEmeraldsAccessor) dyedArmorForEmeralds;

            ItemStack costStack = new ItemStack(Items.EMERALD, accessor.getValue());
            List<ItemStack> offerStacks = new ArrayList<>();

            for (DyeColor color : DyeColor.values()) {
                for (DyeColor color2 : DyeColor.values()) {
                    ItemStack stack = new ItemStack(accessor.getItem());
                    DyeableLeatherItem.dyeArmor(stack, List.of(DyeItem.byColor(color), DyeItem.byColor(color2)));
                    offerStacks.add(stack);
                }
            }

            return new VillagerOffer(profession, level, null, offerStacks, List.of(costStack), List.of(), accessor.getVillagerXp(), accessor.getMaxUses());
        }

        public static VillagerOffer of(VillagerProfession profession, int level, VillagerTrades.ItemsAndEmeraldsToItems itemsAndEmeraldsToItems) {
            ItemsAndEmeraldsToItemsAccessor accessor = (ItemsAndEmeraldsToItemsAccessor) itemsAndEmeraldsToItems;

            return new VillagerOffer(profession, level, null, List.of(new ItemStack(accessor.toItem().getItem(), accessor.toCount())), List.of(new ItemStack(Items.EMERALD, accessor.emeraldCost())), List.of(new ItemStack(accessor.fromItem().getItem(), accessor.fromCount())), accessor.getVillagerXp(), accessor.getMaxUses());
        }

        public static List<VillagerOffer> of(VillagerProfession profession, int level, VillagerTrades.EmeraldsForVillagerTypeItem emeraldsForVillagerTypeItem) {
            EmeraldsForVillagerTypeItemAccessor accessor = (EmeraldsForVillagerTypeItemAccessor) emeraldsForVillagerTypeItem;
            List<VillagerOffer> offers = new ArrayList<>();

            accessor.getTrades().forEach((villagerTypeResourceKey, item) -> {
                offers.add(new VillagerOffer(profession, level, BuiltInRegistries.VILLAGER_TYPE.get(villagerTypeResourceKey), List.of(new ItemStack(Items.EMERALD)), List.of(new ItemStack(item, accessor.getCost())), List.of(), accessor.getVillagerXp(), accessor.getMaxUses()));
            });

            return offers;
        }
    }
}

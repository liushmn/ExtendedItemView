package de.crafty.eiv.common.overlay;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.api.recipe.ItemView;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ItemFilters {


    protected static List<ItemStack> defaultFilter(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String itemName = stack.getDisplayName().getString().toLowerCase();

            if (itemName.startsWith(query.toLowerCase()))
                firstPrio.add(stack);
            else if (itemName.contains(query.toLowerCase()))
                secondPrio.add(stack);
        }

        List<ItemStack> results = new ArrayList<>();
        results.addAll(firstPrio);
        results.addAll(secondPrio);
        return results;
    }

    protected static List<ItemStack> modId(String query) {

        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String modName = CommonEIVClient.resolver().getModNameForItem(stack.getItem());
            if (modName == null)
                continue;

            modName = modName.toLowerCase();

            if (modName.startsWith(query.toLowerCase()))
                firstPrio.add(stack);
            else if (modName.contains(query.toLowerCase()))
                secondPrio.add(stack);

        }

        List<ItemStack> results = new ArrayList<>();
        results.addAll(firstPrio);
        results.addAll(secondPrio);
        return results;
    }

    protected static List<ItemStack> tag(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (TagKey<Item> tag : BuiltInRegistries.ITEM.getTags().map(HolderSet.Named::key).toList()) {
            String tagName = tag.location().getPath().toLowerCase();

            if(tagName.startsWith(query.toLowerCase())){
                BuiltInRegistries.ITEM.get(tag).ifPresent(items -> items.stream().map(itemHolder -> new ItemStack(itemHolder.value())).filter(item -> !firstPrio.contains(item)).forEach(firstPrio::add));
                firstPrio.forEach(stack -> {
                    firstPrio.addAll(ItemView.getStackSensitive().getOrDefault(stack.getItem(), new ArrayList<>()));
                });
            }
            else if(tagName.contains(query.toLowerCase())){
                BuiltInRegistries.ITEM.get(tag).ifPresent(items -> items.stream().map(itemHolder -> new ItemStack(itemHolder.value())).filter(item -> !firstPrio.contains(item) && !secondPrio.contains(item)).forEach(secondPrio::add));
                secondPrio.forEach(stack -> {
                    secondPrio.addAll(ItemView.getStackSensitive().getOrDefault(stack.getItem(), new ArrayList<>()));
                });
            }

        }

        List<ItemStack> results = new ArrayList<>();
        results.addAll(firstPrio);
        results.addAll(secondPrio);

        return results;
    }

    private static List<ItemStack> fullStackList(){
        List<ItemStack> results = new ArrayList<>();

        BuiltInRegistries.ITEM.forEach(item -> {
            results.add(new ItemStack(item));
            results.addAll(ItemView.getStackSensitive().getOrDefault(item, new ArrayList<>()));
        });

        return results;
    }
}

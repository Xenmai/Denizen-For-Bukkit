package com.denizenscript.denizen.objects.properties.item;

import com.denizenscript.denizen.objects.dItem;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;

public class ItemPlantgrowth implements Property {

    public static boolean describes(ObjectTag item) {
        return item instanceof dItem
                && (
                ((dItem) item).getItemStack().getData() instanceof Crops
                        || ((dItem) item).getItemStack().getData() instanceof NetherWarts
                        || ((dItem) item).getItemStack().getData() instanceof CocoaPlant
                        || ((dItem) item).getItemStack().getType().equals(Material.PUMPKIN_STEM)
                        || ((dItem) item).getItemStack().getType().equals(Material.MELON_STEM)
                        || ((dItem) item).getItemStack().getType().equals(Material.CARROT)
                        || ((dItem) item).getItemStack().getType().equals(Material.POTATO)
        );
    }

    public static ItemPlantgrowth getFrom(ObjectTag _item) {
        if (!describes(_item)) {
            return null;
        }
        else {
            return new ItemPlantgrowth((dItem) _item);
        }
    }

    public static final String[] handledTags = new String[] {
            "plant_growth"
    };

    public static final String[] handledMechs = new String[] {
            "plant_growth"
    };


    private ItemPlantgrowth(dItem _item) {
        item = _item;
    }

    dItem item;

    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <i@item.plant_growth>
        // @returns ElementTag
        // @mechanism dItem.plant_growth
        // @group properties
        // @description
        // Returns the growth level of a plant item as one of the following:
        // Wheat: SEEDED, GERMINATED, VERY_SMALL, SMALL, MEDIUM, TALL, VERY_TALL, RIPE
        // Nether Warts: SEEDED, STAGE_ONE, STAGE_TWO, RIPE
        // Cocoa Plants: SMALL, MEDIUM, LARGE
        // Pumpkin stem, melon stem, carrot, potato: 0-7
        // Deprecated as of MC 1.13, use <@link tag m@material.plant_growth> instead.
        // -->
        if (attribute.startsWith("plant_growth")) {
            if (item.getItemStack().getData() instanceof Crops) {
                return new ElementTag(((Crops) item.getItemStack().getData()).getState().name())
                        .getAttribute(attribute.fulfill(1));
            }
            else if (item.getItemStack().getData() instanceof NetherWarts) {
                return new ElementTag(((NetherWarts) item.getItemStack().getData()).getState().name())
                        .getAttribute(attribute.fulfill(1));
            }
            else if (item.getItemStack().getData() instanceof CocoaPlant) {
                return new ElementTag(((CocoaPlant) item.getItemStack().getData()).getSize().name())
                        .getAttribute(attribute.fulfill(1));
            }
            else {
                return new ElementTag(item.getItemStack().getData().getData())
                        .getAttribute(attribute.fulfill(1));
            }
        }

        return null;
    }


    @Override
    public String getPropertyString() {
        String state;
        if (item.getItemStack().getData() instanceof Crops) {
            state = ((Crops) item.getItemStack().getData()).getState().name();
        }
        else if (item.getItemStack().getData() instanceof NetherWarts) {
            state = ((NetherWarts) item.getItemStack().getData()).getState().name();
        }
        else if (item.getItemStack().getData() instanceof CocoaPlant) {
            state = ((CocoaPlant) item.getItemStack().getData()).getSize().name();
        }
        else {
            state = String.valueOf(item.getItemStack().getData().getData());
        }

        if (!state.equalsIgnoreCase("SEEDED") && !state.equalsIgnoreCase("0")) {
            return state;
        }
        else {
            return null;
        }
    }

    @Override
    public String getPropertyId() {
        return "plant_growth";
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dItem
        // @name plant_growth
        // @input Element
        // @description
        // Changes the growth level of plant items.
        // See <@link tag i@item.plant_growth> for valid inputs.
        // Deprecated as of MC 1.13, use <@link mechanism dMaterial.plant_growth> instead.
        // @tags
        // <i@item.is_crop>
        // <i@item.plant_growth>
        // -->

        if (mechanism.matches("plant_growth")) {
            ElementTag inputValue = new ElementTag(mechanism.getValue().asString().toUpperCase());
            if (item.getItemStack().getData() instanceof Crops && inputValue.matchesEnum(CropState.values())) {
                ((Crops) item.getItemStack().getData()).setState(CropState.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
            else if (item.getItemStack().getData() instanceof NetherWarts && inputValue.matchesEnum(NetherWartsState.values())) {
                ((NetherWarts) item.getItemStack().getData()).setState(NetherWartsState.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
            else if (item.getItemStack().getData() instanceof CocoaPlant && inputValue.matchesEnum(CocoaPlant.CocoaPlantSize.values())) {
                ((CocoaPlant) item.getItemStack().getData()).setSize(CocoaPlant.CocoaPlantSize.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
            else if (mechanism.requireInteger()) {
                item.getItemStack().getData().setData((byte) mechanism.getValue().asInt());
            }
        }
    }
}
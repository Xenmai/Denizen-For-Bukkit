package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.dEntity;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;

public class EntityArmorBonus implements Property {

    public static boolean describes(ObjectTag entity) {
        if (!(entity instanceof dEntity)) {
            return false;
        }
        return ((dEntity) entity).isLivingEntity();
    }

    public static EntityArmorBonus getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityArmorBonus((dEntity) entity);
    }

    public static final String[] handledTags = new String[] {
            "armor_bonus"
    };

    public static final String[] handledMechs = new String[] {
            "armor_bonus"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityArmorBonus(dEntity ent) {
        entity = ent;
    }

    dEntity entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        if (entity.getLivingEntity().getAttribute(org.bukkit.attribute.Attribute.GENERIC_ARMOR).getValue() > 0.0) {
            return getArmorBonus().asString();
        }
        return null;
    }

    @Override
    public String getPropertyId() {
        return "armor_bonus";
    }

    public ElementTag getArmorBonus() {
        return new ElementTag(entity.getLivingEntity().getAttribute(org.bukkit.attribute.Attribute.GENERIC_ARMOR).getValue());
    }


    ///////////
    // ObjectTag Attributes
    ////////

    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <e@entity.armor_bonus>
        // @returns ElementTag(Decimal)
        // @mechanism dEntity.armor_bonus
        // @group attributes
        // @description
        // Returns the entity's base armor bonus.
        // -->
        if (attribute.startsWith("armor_bonus")) {
            return getArmorBonus().getAttribute(attribute.fulfill(1));
        }


        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dEntity
        // @name armor_bonus
        // @input Element(Decimal)
        // @description
        // Sets the entity's base armor bonus.
        // @tags
        // <e@entity.armor_bonus>
        // -->
        if (mechanism.matches("armor_bonus") && mechanism.requireDouble()) {
            entity.getLivingEntity().getAttribute(org.bukkit.attribute.Attribute.GENERIC_ARMOR)
                    .setBaseValue(mechanism.getValue().asDouble());
        }

    }
}
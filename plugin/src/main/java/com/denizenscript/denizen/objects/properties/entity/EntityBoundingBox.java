package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.util.BoundingBox;
import com.denizenscript.denizen.objects.dEntity;
import com.denizenscript.denizen.objects.dLocation;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityBoundingBox implements Property {

    public static boolean describes(ObjectTag object) {
        return object instanceof dEntity;
    }

    public static EntityBoundingBox getFrom(ObjectTag object) {
        if (!describes(object)) {
            return null;
        }
        else {
            return new EntityBoundingBox((dEntity) object);
        }
    }

    public static final String[] handledTags = new String[] {
            "bounding_box"
    };

    public static final String[] handledMechs = new String[] {
            "bounding_box"
    };


    private static Set<UUID> modifiedBoxes = new HashSet<>();

    public static void remove(UUID uuid) {
        if (modifiedBoxes.contains(uuid)) {
            modifiedBoxes.remove(uuid);
        }
    }

    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityBoundingBox(dEntity entity) {
        this.entity = entity;
    }

    dEntity entity;

    private ListTag getBoundingBox() {
        BoundingBox boundingBox = NMSHandler.getInstance().getEntityHelper().getBoundingBox(entity.getBukkitEntity());
        ListTag list = new ListTag();
        list.add(new dLocation(boundingBox.getLow().toLocation(entity.getWorld())).identify());
        list.add(new dLocation(boundingBox.getHigh().toLocation(entity.getWorld())).identify());
        return list;
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        if (entity.isCitizensNPC()) {
            return null;
        }
        if (modifiedBoxes.contains(entity.getUUID())) {
            return getBoundingBox().identify();
        }
        return null;
    }

    @Override
    public String getPropertyId() {
        return "bounding_box";
    }

    @Override
    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <e@entity.bounding_box>
        // @returns ListTag(dLocation)
        // @mechanism dEntity.bounding_box
        // @group properties
        // @description
        // Returns the collision bounding box of the entity in the format "<low>|<high>", essentially a cuboid with decimals.
        // -->
        if (attribute.startsWith("bounding_box")) {
            return getBoundingBox().getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dEntity
        // @name bounding_box
        // @input ListTag(dLocation)
        // @description
        // Changes the collision bounding box of the entity in the format "<low>|<high>", essentially a cuboid with decimals.
        // @tags
        // <e@entity.bounding_box>
        // -->

        if (mechanism.matches("bounding_box")) {
            if (entity.isCitizensNPC()) {
                // TODO: Allow editing NPC boxes properly?
                return;
            }
            List<dLocation> locations = mechanism.valueAsType(ListTag.class).filter(dLocation.class, mechanism.context);
            if (locations.size() == 2) {
                BoundingBox boundingBox = new BoundingBox(locations.get(0).toVector(), locations.get(1).toVector());
                NMSHandler.getInstance().getEntityHelper().setBoundingBox(entity.getBukkitEntity(), boundingBox);
                modifiedBoxes.add(entity.getUUID());
            }
            else {
                Debug.echoError("Must specify exactly 2 dLocations in the format '<low>|<high>'!");
            }
        }
    }
}
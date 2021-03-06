package com.denizenscript.denizen.events.player;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerPicksUpScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player picks up item
    // player picks up <item>
    // player takes item
    // player takes <item>
    //
    // @Regex ^on player (picks up|takes) [^\s]+$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a player picks up an item.
    //
    // @Context
    // <context.item> returns the ItemTag.
    // <context.entity> returns a EntityTag of the item.
    // <context.location> returns a LocationTag of the item's location.
    //
    // @Determine
    // "ITEM:" + ItemTag to changed the item being picked up.
    //
    // -->

    public PlayerPicksUpScriptEvent() {
        instance = this;
    }

    public static PlayerPicksUpScriptEvent instance;
    public ItemTag item;
    public boolean itemChanged;
    public EntityTag entity;
    public LocationTag location;
    public PlayerPickupItemEvent event;

    private static final Set<UUID> editedItems = new HashSet<>();

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        if (CoreUtilities.xthArgEquals(3, lower, "from")) {
            return false;
        }
        return lower.startsWith("player picks up") || lower.startsWith("player takes");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String iTest = path.eventArgLowerAt(1).equals("picks") ?
                path.eventArgLowerAt(3) : path.eventArgLowerAt(2);
        if (!tryItem(item, iTest)) {
            return false;
        }
        return runInCheck(path, location);
    }

    @Override
    public String getName() {
        return "PlayerPicksUp";
    }

    @Override
    public boolean applyDetermination(ScriptPath path, ObjectTag determinationObj) {
        String determination = determinationObj.toString();
        String lower = CoreUtilities.toLowerCase(determination);
        if (lower.startsWith("item:")) {
            item = ItemTag.valueOf(determination.substring("item:".length()), path.container);
            itemChanged = true;
            return true;
        }
        return super.applyDetermination(path, determinationObj);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(new PlayerTag(event.getPlayer()), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("item")) {
            return item;
        }
        else if (name.equals("entity")) {
            return entity;
        }
        else if (name.equals("location")) {
            return location;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlayerPicksUp(PlayerPickupItemEvent event) {
        if (EntityTag.isNPC(event.getPlayer())) {
            return;
        }
        Item itemEntity = event.getItem();
        UUID itemUUID = itemEntity.getUniqueId();
        if (editedItems.contains(itemUUID)) {
            editedItems.remove(itemUUID);
            return;
        }
        location = new LocationTag(itemEntity.getLocation());
        item = new ItemTag(itemEntity.getItemStack());
        entity = new EntityTag(itemEntity);
        itemChanged = false;
        this.event = event;
        fire(event);
        if (itemChanged) {
            itemEntity.setItemStack(item.getItemStack());
            editedItems.add(itemUUID);
            event.setCancelled(true);
        }
    }
}

package com.denizenscript.denizen.events.player;

import com.denizenscript.denizen.objects.dEntity;
import com.denizenscript.denizen.objects.dItem;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerFishesScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player fishes (<entity>/<item>) (while <state>)
    //
    // @Regex ^on player fishes( [^\s]+)?( while [^\s]+)?$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a player uses a fishing rod.
    //
    // @Context
    // <context.hook> returns a dEntity of the hook.
    // <context.state> returns an ElementTag of the fishing state.
    // <context.entity> returns a dEntity of the entity that got caught.
    // <context.item> returns a dItem of the item gotten, if any.
    //
    // -->

    public PlayerFishesScriptEvent() {
        instance = this;
    }

    public static PlayerFishesScriptEvent instance;
    public dEntity hook;
    public ElementTag state;
    public dEntity entity;
    public dItem item;
    public PlayerFishEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("player fishes");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String fish = path.eventArgLowerAt(2);

        if (!fish.isEmpty() && !fish.equals("in") && !fish.equals("while")) {
            if (entity == null) {
                return false;
            }
            if (!tryEntity(entity, fish)) {
                if (item == null) {
                    return false;
                }
                if (!tryItem(item, fish)) {
                    return false;
                }
            }
        }

        String[] data = path.eventArgsLower;
        for (int index = 2; index < data.length; index++) {
            if (data[index].equals("while") && !data[index + 1].equalsIgnoreCase(state.asString())) {
                return false;
            }
        }

        if (!runInCheck(path, hook.getLocation())) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "PlayerFishes";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(dEntity.isPlayer(event.getPlayer()) ? dEntity.getPlayerFrom(event.getPlayer()) :
                dEntity.isPlayer(event.getCaught()) ? dEntity.getPlayerFrom(event.getCaught()) : null,
                dEntity.isCitizensNPC(event.getPlayer()) ? dEntity.getNPCFrom(event.getPlayer()) :
                        dEntity.isCitizensNPC(event.getCaught()) ? dEntity.getNPCFrom(event.getCaught()) : null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("hook")) {
            return hook;
        }
        else if (name.equals("entity") && entity != null) {
            return entity.getDenizenObject();
        }
        else if (name.equals("item") && item != null) {
            return item;
        }
        else if (name.equals("state")) {
            return state;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlayerFishes(PlayerFishEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        Entity hookEntity = NMSHandler.getInstance().getEntityHelper().getFishHook(event);
        dEntity.rememberEntity(hookEntity);
        hook = new dEntity(hookEntity);
        state = new ElementTag(event.getState().toString());
        item = null;
        entity = null;
        Entity caughtEntity = event.getCaught();
        if (caughtEntity != null) {
            dEntity.rememberEntity(caughtEntity);
            entity = new dEntity(caughtEntity);
            if (caughtEntity instanceof Item) {
                item = new dItem(((Item) caughtEntity).getItemStack());
            }
        }
        this.event = event;
        fire(event);
        dEntity.forgetEntity(hookEntity);
        dEntity.forgetEntity(caughtEntity);
    }
}
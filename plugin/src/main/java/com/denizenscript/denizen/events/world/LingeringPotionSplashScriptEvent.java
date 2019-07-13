package com.denizenscript.denizen.events.world;

import com.denizenscript.denizen.objects.dEntity;
import com.denizenscript.denizen.objects.dItem;
import com.denizenscript.denizen.objects.dLocation;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;

public class LingeringPotionSplashScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // lingering potion splash
    // lingering <item> splashes
    //
    // @Regex ^on lingering [^\s]+ splashes$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a lingering splash potion breaks open
    //
    // @Context
    // <context.potion> returns a dItem of the potion that broke open.
    // <context.location> returns the dLocation the splash potion broke open at.
    // <context.entity> returns a dEntity of the splash potion.
    // <context.radius> returns the radius of the effect cloud.
    // <context.duration> returns the lingering duration of the effect cloud.
    //
    // -->

    public LingeringPotionSplashScriptEvent() {
        instance = this;
    }

    public static LingeringPotionSplashScriptEvent instance;
    public LingeringPotionSplashEvent event;
    public dLocation location;
    public ElementTag duration;
    public dEntity entity;
    public ElementTag radius;
    public dItem item;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String cmd = CoreUtilities.getXthArg(2, lower);
        if (!CoreUtilities.getXthArg(0, lower).equals("lingering")) {
            return false;
        }
        if (!cmd.equals("splash") && !cmd.equals("splashes")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean matches(ScriptPath path) {
        String iTest = CoreUtilities.getXthArg(1, path.event);
        if (!tryItem(item, iTest)) {
            return false;
        }
        if (runInCheck(path, location)) {
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "LingeringPotionSplash";
    }


    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("location")) {
            return location;
        }
        else if (name.equals("radius")) {
            return radius;
        }
        else if (name.equals("duration")) {
            return duration;
        }
        else if (name.equals("potion")) {
            return item;
        }
        else if (name.equals("entity")) {
            return entity;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        AreaEffectCloud cloud = event.getAreaEffectCloud();
        item = new dItem(event.getEntity().getItem());
        duration = new ElementTag(cloud.getDuration());
        entity = new dEntity(event.getEntity());
        location = entity.getLocation();
        radius = new ElementTag(cloud.getRadius());
        this.event = event;
        fire(event);
    }
}
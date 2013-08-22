package net.aufdemrand.denizen.scripts.commands.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.aufdemrand.denizen.objects.dList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.aufdemrand.denizen.exceptions.CommandExecutionException;
import net.aufdemrand.denizen.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizen.objects.aH;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.objects.aH.ArgumentType;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.scripts.ScriptEntry;
import net.aufdemrand.denizen.scripts.ScriptRegistry;
import net.aufdemrand.denizen.scripts.commands.AbstractCommand;
import net.aufdemrand.denizen.scripts.containers.core.FormatScriptContainer;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizen.utilities.debugging.dB.Messages;

/**
 * Sends a message to the Player.
 * 
 * @author Jeremy Schroeder
 * Version 1.0 Last Updated 11/29 1:11
 */

public class NarrateCommand extends AbstractCommand {

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {


        if (scriptEntry.getArguments().size() > 4) 
            throw new InvalidArgumentsException(Messages.ERROR_LOTS_OF_ARGUMENTS);

        // Iterate through arguments
        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {
            if (!scriptEntry.hasObject("format") && arg.matchesPrefix("format")) {
                FormatScriptContainer format = null;
                String formatStr = arg.asElement().asString();
                format = ScriptRegistry.getScriptContainerAs(formatStr, FormatScriptContainer.class);
                
                if(format != null) dB.echoDebug("... format set to: " + formatStr);
                else dB.echoError("... could not find format for: " + formatStr);
                scriptEntry.addObject("format", format);
                
            }
            // Add players to target list
            else if ((arg.matchesPrefix("target") || arg.matchesPrefix("targets"))) {
                scriptEntry.addObject("targets", ( (dList)arg.asType(dList.class)).filter(dPlayer.class));
            }
            else {
                if (!scriptEntry.hasObject("text"))
                    scriptEntry.addObject("text", arg.asElement());
                else
                    dB.echoError("Unknown argument " + arg.raw_value);
            }
        }
        
        // If there are no targets, check if you can add this player
        // to the targets
        if (!scriptEntry.hasObject("targets"))
            scriptEntry.addObject("targets", (scriptEntry.hasPlayer() ? Arrays.asList(scriptEntry.getPlayer()) : null));
        
        
        if (!scriptEntry.hasObject("text"))
            throw new InvalidArgumentsException(Messages.ERROR_NO_TEXT);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {
        // Get objects
        List<dPlayer> targets = (List<dPlayer>) scriptEntry.getObject("targets");
        String text = scriptEntry.getElement("text").asString();
        FormatScriptContainer format = (FormatScriptContainer) scriptEntry.getObject("format");

        // Report to dB
        dB.report(getName(),
                 aH.debugObj("Narrating", text)
                 + aH.debugObj("Targets", targets)
                 + (format != null ? aH.debugObj("Format", format.getName()) : ""));
        
        for (dPlayer player : targets) {
            if (player != null && player.isOnline())
                player.getPlayerEntity().sendMessage(format != null ? format.getFormattedText(scriptEntry) : text);
            else
                dB.echoError("Narrated to non-existent or offline player!");
        }
    }

}
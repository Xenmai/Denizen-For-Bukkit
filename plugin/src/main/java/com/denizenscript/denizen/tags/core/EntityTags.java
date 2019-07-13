package com.denizenscript.denizen.tags.core;

import com.denizenscript.denizen.objects.dEntity;
import com.denizenscript.denizencore.objects.TagRunnable;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;

public class EntityTags {

    public EntityTags() {
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                entityTags(event);
            }
        }, "entity");
    }

    //////////
    //  ReplaceableTagEvent handler
    ////////

    public void entityTags(ReplaceableTagEvent event) {

        if (!event.matches("entity") || event.replaced()) {
            return;
        }

        dEntity entity = null;

        if (event.hasNameContext()) {
            entity = dEntity.valueOf(event.getNameContext(), event.getAttributes().context);
        }

        if (entity == null) {
            return;
        }

        Attribute attribute = event.getAttributes();
        event.setReplacedObject(CoreUtilities.autoAttrib(entity, attribute.fulfill(1)));

    }
}
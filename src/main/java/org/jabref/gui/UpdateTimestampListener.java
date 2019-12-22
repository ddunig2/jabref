package org.jabref.gui;

import org.jabref.model.entry.event.EntryChangedEvent;
import org.jabref.preferences.JabRefPreferences;

import com.google.common.eventbus.Subscribe;

/**
 * Updates the timestamp of changed entries if the feature is enabled
 */
class UpdateTimestampListener {
    private final JabRefPreferences jabRefPreferences;

    /**
     * The jabRefPreferences are required, because they are queried at each call. The user can change the preferences
     * and this listener behaves differently. This implementation seems to be less complex then re-registering this
     * listener or reconfiguring this listener if the preferences changed.
     *
     * @param jabRefPreferences the global JabRef preferences.
     */
    UpdateTimestampListener(JabRefPreferences jabRefPreferences) {
        this.jabRefPreferences = jabRefPreferences;
    }

    @Subscribe
    public void listen(EntryChangedEvent event) {
        if (jabRefPreferences.getTimestampPreferences().includeTimestamps()) {
            event.getBibEntry().setField(jabRefPreferences.getTimestampPreferences().getTimestampField(),
                    jabRefPreferences.getTimestampPreferences().now());
        }
    }
}

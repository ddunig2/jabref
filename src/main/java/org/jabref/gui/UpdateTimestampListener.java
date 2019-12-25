package org.jabref.gui;

import java.util.function.Supplier;

import org.jabref.logic.preferences.TimestampPreferences;
import org.jabref.model.entry.event.EntryChangedEvent;

import com.google.common.eventbus.Subscribe;

/**
 * Updates the timestamp of changed entries if the feature is enabled
 */
class UpdateTimestampListener {
    private final Supplier<TimestampPreferences> timestampPreferencesSupplier;

    /**
     * The timestampPreferencesSupplier is required, because timestampPreferences are queried at each call. The user can
     * change the preferences and this listener behaves differently. This implementation seems to be less complex then
     * re-registering this listener or reconfiguring this listener if the preferences changed.
     */
    UpdateTimestampListener(Supplier<TimestampPreferences> timestampPreferencesSupplier) {
        this.timestampPreferencesSupplier = timestampPreferencesSupplier;
    }

    @Subscribe
    public void listen(EntryChangedEvent event) {
        TimestampPreferences timestampPreferences = timestampPreferencesSupplier.get();
        if (timestampPreferences.includeTimestamps()) {
            event.getBibEntry().setField(timestampPreferences.getTimestampField(), timestampPreferences.now());
        }
    }
}

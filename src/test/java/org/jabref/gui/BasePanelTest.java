package org.jabref.gui;

import java.util.Collections;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import org.jabref.JabRefGUI;
import org.jabref.gui.autocompleter.AutoCompletePreferences;
import org.jabref.gui.entryeditor.EntryEditorPreferences;
import org.jabref.gui.externalfiletype.ExternalFileTypes;
import org.jabref.gui.groups.GroupViewMode;
import org.jabref.gui.keyboard.KeyBindingRepository;
import org.jabref.gui.maintable.MainTablePreferences;
import org.jabref.logic.importer.ImportFormatPreferences;
import org.jabref.logic.preferences.TimestampPreferences;
import org.jabref.logic.util.UpdateFieldPreferences;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.InternalField;
import org.jabref.model.metadata.FilePreferences;
import org.jabref.model.util.DummyFileUpdateMonitor;
import org.jabref.preferences.PreviewPreferences;
import org.jabref.testutils.category.GUITest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@GUITest
@ExtendWith(ApplicationExtension.class)
public class BasePanelTest {

    private BasePanel panel;
    private BibDatabaseContext bibDatabaseContext;
    private BasePanelPreferences basePanelPreferences;

    @Start
    public void onStart(Stage stage) {
        new JabRefGUI(stage, Collections.emptyList(), true);
        JabRefFrame frame = mock(JabRefFrame.class, RETURNS_MOCKS);
        ExternalFileTypes externalFileTypes = mock(ExternalFileTypes.class);
        bibDatabaseContext = new BibDatabaseContext();
        basePanelPreferences = new BasePanelPreferences(
                mock(MainTablePreferences.class, RETURNS_MOCKS),
                mock(AutoCompletePreferences.class, RETURNS_MOCKS),
                mock(EntryEditorPreferences.class, RETURNS_MOCKS),
                mock(KeyBindingRepository.class, RETURNS_MOCKS),
                mock(PreviewPreferences.class, RETURNS_MOCKS),
                0.5
        );
        FilePreferences filePreferences = mock(FilePreferences.class);
        ImportFormatPreferences importFormatPreferences = mock(ImportFormatPreferences.class);
        when(importFormatPreferences.getKeywordSeparator()).thenReturn(',');
        UpdateFieldPreferences updateFieldPreferences = mock(UpdateFieldPreferences.class);
        StateManager stateManager = mock(StateManager.class);
        TimestampPreferences timestampPreferences = new TimestampPreferences(false, false, InternalField.TIMESTAMP, "yyyy-MM-dd-HH-mm-ss", false);
        panel = new BasePanel(
                frame,
                basePanelPreferences,
                bibDatabaseContext,
                externalFileTypes,
                GroupViewMode.UNION,
                filePreferences,
                importFormatPreferences,
                updateFieldPreferences,
                new DummyFileUpdateMonitor(),
                stateManager,
                columnPreferences -> {
                },
                () -> timestampPreferences);

        stage.setScene(new Scene(panel));
        stage.show();
    }

    @Test
    void dividerPositionIsRestoredOnReopenEntryEditor(FxRobot robot) {
        BibEntry entry = new BibEntry();
        bibDatabaseContext.getDatabase().insertEntry(entry);

        SplitPane splitPane = robot.lookup(".split-pane").query();

        robot.interact(() -> panel.showAndEdit(entry));
        robot.interact(() -> splitPane.getDividers().get(0).setPosition(0.8));
        robot.sleep(1000);

        robot.interact(() -> panel.closeBottomPane());
        robot.sleep(1000);
        robot.interact(() -> panel.showAndEdit(entry));

        assertEquals(0.8, splitPane.getDividers().get(0).getPosition(), 0.1);
    }
}

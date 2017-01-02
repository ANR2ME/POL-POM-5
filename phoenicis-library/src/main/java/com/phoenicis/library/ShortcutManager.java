package com.phoenicis.library;

import com.phoenicis.library.dto.ShortcutDTO;
import com.playonlinux.scripts.interpreter.InteractiveScriptSession;
import com.playonlinux.scripts.interpreter.ScriptInterpreter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class ShortcutManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortcutManager.class);
    private static final String ENCODING = "UTF-8";
    private final String shortcutDirectory;
    private final LibraryManager libraryManager;
    private final ScriptInterpreter scriptInterpreter;

    ShortcutManager(String shortcutDirectory,
                    LibraryManager libraryManager, ScriptInterpreter scriptInterpreter) {
        this.shortcutDirectory = shortcutDirectory;
        this.libraryManager = libraryManager;
        this.scriptInterpreter = scriptInterpreter;
    }

    public void createShortcut(ShortcutDTO shortcutDTO) {
        final String baseName = shortcutDTO.getName();
        final File shortcutDirectoryFile = new File(this.shortcutDirectory);

        final File scriptFile = new File(shortcutDirectoryFile, baseName + ".shortcut");
        final File iconFile = new File(shortcutDirectoryFile, baseName + ".icon");
        final File miniatureFile = new File(shortcutDirectoryFile, baseName + ".miniature");
        final File descriptionFile = new File(shortcutDirectoryFile, baseName + ".description");


        if(!shortcutDirectoryFile.exists()) {
            shortcutDirectoryFile.mkdirs();
        }

        try {
            FileUtils.writeStringToFile(scriptFile, shortcutDTO.getScript(), ENCODING);
            if (shortcutDTO.getDescription() != null) {
                FileUtils.writeStringToFile(descriptionFile, shortcutDTO.getDescription(), ENCODING);
            }
            if (shortcutDTO.getIcon() != null) {
                FileUtils.writeByteArrayToFile(iconFile, shortcutDTO.getIcon());
            }
            if (shortcutDTO.getMiniature() != null) {
                FileUtils.writeByteArrayToFile(miniatureFile, shortcutDTO.getMiniature());
            }
        } catch(IOException e) {
            LOGGER.warn("Error while creating shortcut", e);
        } finally {
            libraryManager.refresh();
        }
    }

    public void uninstallFromShortcut(ShortcutDTO shortcutDTO, Consumer<Exception> errorCallback) {
        final InteractiveScriptSession interactiveScriptSession = scriptInterpreter.createInteractiveSession();

        interactiveScriptSession.eval("include([\"Functions\", \"Shortcuts\", \"Reader\"]);",
                ignored -> interactiveScriptSession.eval(
                        "new ShortcutReader()",
                        output -> {
                            final ScriptObjectMirror shortcutReader = (ScriptObjectMirror) output;
                            shortcutReader.callMember("of", shortcutDTO);
                            shortcutReader.callMember("uninstall");
                        },
                        errorCallback),
                errorCallback
        );
    }

    public void deleteShortcut(ShortcutDTO shortcutDTO) {
        final String baseName = shortcutDTO.getName();
        final File shortcutDirectory = new File(this.shortcutDirectory);

        final File scriptFile = new File(shortcutDirectory, baseName + ".shortcut");
        final File iconFile = new File(shortcutDirectory, baseName + ".icon");
        final File miniatureFile = new File(shortcutDirectory, baseName + ".miniature");
        final File descriptionFile = new File(shortcutDirectory, baseName + ".description");


        if(scriptFile.exists()) {
            scriptFile.delete();
        }

        if(iconFile.exists()) {
            iconFile.delete();
        }

        if(miniatureFile.delete()) {
            miniatureFile.delete();
        }

        if(descriptionFile.exists()) {
            descriptionFile.delete();
        }

        libraryManager.refresh();
    }
}

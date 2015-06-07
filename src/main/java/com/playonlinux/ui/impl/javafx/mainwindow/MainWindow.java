/*
 * Copyright (C) 2015 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.playonlinux.ui.impl.javafx.mainwindow;

import com.playonlinux.domain.PlayOnLinuxException;
import com.playonlinux.ui.api.PlayOnLinuxWindow;
import com.playonlinux.ui.impl.javafx.common.PlayOnLinuxScene;
import com.playonlinux.ui.impl.javafx.common.SelfManagedWindow;
import com.playonlinux.ui.impl.javafx.mainwindow.center.ViewCenter;
import com.playonlinux.ui.impl.javafx.mainwindow.myapps.ViewMyApps;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

import static com.playonlinux.domain.Localisation.translate;

public class MainWindow extends Stage implements PlayOnLinuxWindow {

    private MainWindowHeader headerPane;
    private ViewMyApps myApps;
    private ViewCenter center;
    private VBox rootPane;

    public void setUpWindow() {
        rootPane = new VBox();

        myApps = new ViewMyApps(this);
        center = new ViewCenter(this);

        Scene scene = new PlayOnLinuxScene(rootPane);
        headerPane = new MainWindowHeader();

        goTo(myApps);

        this.setScene(scene);
        this.setTitle(translate("${application.name}"));
        this.show();



        this.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(translate("${application.name}"));
            alert.setHeaderText(translate("Are you sure you want to close all ${application.name} windows?"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Platform.exit();
            } else {
                event.consume();
            }
        });

    }

    public void setUpEvents() throws PlayOnLinuxException {

        this.headerPane.setMyAppsEvent(evt -> goTo(myApps));
        this.headerPane.setCenterEvent(evt -> goTo(center));

        myApps.setUpEvents();
        center.setUpEvents();
    }

    private void goTo(Node view) {
        rootPane.getChildren().clear();
        rootPane.getChildren().addAll(headerPane, view);
    }


}


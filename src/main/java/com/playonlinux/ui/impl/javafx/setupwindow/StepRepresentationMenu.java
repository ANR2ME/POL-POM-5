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

package com.playonlinux.ui.impl.javafx.setupwindow;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import com.playonlinux.common.messages.CancelerSynchroneousMessage;

import java.util.List;


public class StepRepresentationMenu extends StepRepresentationMessage {
    List<String> menuItems;
    ListView<String> listViewWidget;

    public StepRepresentationMenu(SetupWindowJavaFXImplementation parent, CancelerSynchroneousMessage messageWaitingForResponse, String textToShow,
                                  List<String> menuItems) {
        super(parent, messageWaitingForResponse, textToShow);

        this.menuItems = menuItems;
    }

    @Override
    protected void drawStepContent() {
        super.drawStepContent();
        listViewWidget = new ListView<>();

        listViewWidget.setItems(FXCollections.observableArrayList(menuItems));
        listViewWidget.setLayoutX(10);
        listViewWidget.setLayoutY(40);
        listViewWidget.setPrefSize(500, 240);

        this.addToContentPanel(listViewWidget);
    }

    @Override
    protected void setStepEvents() {
        this.setNextButtonAction(event ->
            ((CancelerSynchroneousMessage) this.getMessageAwaitingForResponse()).
                    setResponse(listViewWidget.getFocusModel().getFocusedItem())
        );
    }

}

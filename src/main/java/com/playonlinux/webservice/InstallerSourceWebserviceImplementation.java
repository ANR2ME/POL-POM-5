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

package com.playonlinux.webservice;

import com.playonlinux.common.api.webservice.InstallerSource;
import com.playonlinux.common.dto.DownloadEnvelopeDTO;
import com.playonlinux.common.dto.DownloadStateDTO;
import com.playonlinux.common.api.services.BackgroundService;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.playonlinux.common.dto.AvailableCategoriesDTO;

import java.net.URL;
import java.util.Observable;
import java.util.concurrent.Semaphore;

/**
 * This class download scripts from a playonlinux web service and converts it into DTOs
 */
public class InstallerSourceWebserviceImplementation extends Observable
        implements BackgroundService, InstallerSource {

    private final URL url;
    private DownloadStateDTO.State state = DownloadStateDTO.State.WAITING;
    private Semaphore updateSemaphore = new Semaphore(1);
    private AvailableCategoriesDTO categories;

    public InstallerSourceWebserviceImplementation(URL url) {
        this.url = url;
    }

    synchronized public void populate() {
        try {
            categories = null;

            updateSemaphore.acquire();
            this.state = DownloadStateDTO.State.DOWNLOADING;
            this.setChanged();
            this.update();

            try {
                categories = new RestTemplate().getForObject(this.url.toString(), AvailableCategoriesDTO.class);
                this.state = DownloadStateDTO.State.SUCCESS;
            } catch(RestClientException e) {
                e.printStackTrace();
                this.state = DownloadStateDTO.State.FAILED;
            } finally {
                this.update();
            }
        } catch (InterruptedException ignored) {
            // If the semaphore is interrupted, we just ignore the download request.
        } finally {
            updateSemaphore.release();
        }
    }


    private synchronized void update() {
        DownloadEnvelopeDTO<AvailableCategoriesDTO> envelopeDTO = new DownloadEnvelopeDTO<>();
        DownloadStateDTO downloadStateDTO = new DownloadStateDTO();
        downloadStateDTO.setState(this.state);

        envelopeDTO.setDownloadState(downloadStateDTO);
        envelopeDTO.setEnvelopeContent(categories);

        this.setChanged();
        this.notifyObservers(envelopeDTO);
    }

    @Override
    public void shutdown() {
        // Nothing to do to shutdown this service
    }

    @Override
    synchronized public void start() {
        new Thread() {
            @Override
            public void run() {
                populate();
            }
        }.start();
    }
}

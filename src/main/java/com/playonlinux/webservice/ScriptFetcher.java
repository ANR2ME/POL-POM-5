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

import org.springframework.web.client.RestTemplate;
import com.playonlinux.webservice.dto.AvailableCategories;

/**
 * This class download com.playonlinux.scripts from a com.playonlinux.webservice web service and converts it into DTOs
 */
public class ScriptFetcher {
    private final String url;

    ScriptFetcher(String url) {
        this.url = url;
    }

    AvailableCategories fetchCategories() {
        return new RestTemplate().getForObject(this.url, AvailableCategories.class);
    }
}

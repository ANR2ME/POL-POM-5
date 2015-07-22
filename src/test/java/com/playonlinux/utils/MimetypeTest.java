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

package com.playonlinux.utils;

import com.playonlinux.app.PlayOnLinuxException;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;


public class MimetypeTest {
    final URL inputUrl = MimetypeTest.class.getResource("./archive");

    @Test
    public void testGetMimetype() throws PlayOnLinuxException {
        assertEquals("application/x-gzip", Mimetype.getMimetype(new File(inputUrl.getPath(), "pol.txt.gz")));
        assertEquals("application/x-bzip2", Mimetype.getMimetype(new File(inputUrl.getPath(), "pol.txt.bz2")));
        assertEquals("application/x-gzip", Mimetype.getMimetype(new File(inputUrl.getPath(), "test2.tar.gz")));
        assertEquals("application/x-bzip2", Mimetype.getMimetype(new File(inputUrl.getPath(), "test3.tar.bz2")));
    }
}
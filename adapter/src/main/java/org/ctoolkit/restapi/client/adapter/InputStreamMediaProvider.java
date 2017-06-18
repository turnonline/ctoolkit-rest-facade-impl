/*
 * Copyright (c) 2017 Comvai, s.r.o. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.ctoolkit.restapi.client.adapter;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;

/**
 * The concrete implementation that works with {@link AbstractInputStreamContent}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class InputStreamMediaProvider
        implements MediaProvider
{
    private AbstractInputStreamContent content;

    /**
     * Creates an input stream content instance from the given input.
     */
    InputStreamMediaProvider( File file )
    {
        this.content = new FileContent( null, file );
    }

    /**
     * Creates an input stream content instance from the given input.
     */
    InputStreamMediaProvider( InputStream inputStream )
    {
        this.content = new InputStreamContent( null, inputStream );
    }

    /**
     * Creates an input stream content instance from the given input.
     */
    InputStreamMediaProvider( byte[] array )
    {
        this.content = new ByteArrayContent( null, array );
    }

    /**
     * Creates an input stream content instance from the given input.
     */
    InputStreamMediaProvider( byte[] array, int offset, int length )
    {
        this.content = new ByteArrayContent( null, array, offset, length );
    }

    @Override
    public AbstractInputStreamContent getMedia()
    {
        return content;
    }

    @Override
    public void setType( @Nullable String type )
    {
        content.setType( type );
    }

    @Override
    public void setCloseInputStream( boolean closeInputStream )
    {
        content.setCloseInputStream( closeInputStream );
    }
}

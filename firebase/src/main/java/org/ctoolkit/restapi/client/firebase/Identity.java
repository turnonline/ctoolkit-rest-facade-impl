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

package org.ctoolkit.restapi.client.firebase;

import org.ctoolkit.restapi.client.adapter.Constants;

import java.io.Serializable;
import java.util.Date;

/**
 * The parsed token as Identity.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class Identity
        implements Serializable
{
    /**
     * The identity toolkit default token cookie name.
     */
    public static final String GTOKEN = Constants.IDENTITY_GTOKEN;

    private static final long serialVersionUID = 4181935959696889561L;

    private Date issuedAt;

    private Date expiration;

    private String localId;

    private String email;

    private boolean emailVerified;

    private String displayName;

    private String providerId;

    private String photoUrl;

    public Identity()
    {
    }

    public Date getIssuedAt()
    {
        return issuedAt;
    }

    public void setIssuedAt( Date issuedAt )
    {
        this.issuedAt = issuedAt;
    }

    public Date getExpiration()
    {
        return expiration;
    }

    public void setExpiration( Date expiration )
    {
        this.expiration = expiration;
    }

    public String getLocalId()
    {
        return localId;
    }

    public void setLocalId( String localId )
    {
        this.localId = localId;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public boolean isEmailVerified()
    {
        return emailVerified;
    }

    public void setEmailVerified( boolean emailVerified )
    {
        this.emailVerified = emailVerified;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getProviderId()
    {
        return providerId;
    }

    public void setProviderId( String providerId )
    {
        this.providerId = providerId;
    }

    public String getPhotoUrl()
    {
        return photoUrl;
    }

    public void setPhotoUrl( String photoUrl )
    {
        this.photoUrl = photoUrl;
    }
}

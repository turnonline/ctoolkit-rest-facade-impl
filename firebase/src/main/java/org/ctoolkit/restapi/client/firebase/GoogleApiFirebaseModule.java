/*
 * Copyright (c) 2018 Comvai, s.r.o. All Rights Reserved.
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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Strings;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * The Google Firebase Admin based identity module.
 * The following properties are allowed to be configured via dependency injection:
 * <ul>
 * <li>credential.firebase.on</li>
 * <li>credential.firebase.fileName</li>
 * <li>credential.firebase.projectId</li>
 * <li>credential.firebase.databaseName</li>
 * <li>credential.firebase.serviceAccountEmail</li>
 * </ul>
 * If credentialOn is true (by default is false) these properties become mandatory
 * and will be used to authenticate calls. If databaseName value is missing the projectId will be used.
 * <p>
 * In order to create a custom token the service Identity and Access Management (IAM) API
 * must be enabled and service account ID "Service Account Token Creator" role
 * must have the 'iam.serviceAccounts.signBlob' permission for the custom token creation to work.
 * </p>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 * @see <a href="https://firebase.google.com/docs/admin/setup">Add Firebase to your app</a>
 */
public class GoogleApiFirebaseModule
        extends AbstractModule
{
    public static final String API_PREFIX = "firebase";

    private static final Logger logger = LoggerFactory.getLogger( GoogleApiFirebaseModule.class );

    @Provides
    @Singleton
    FirebaseAuth provideIdentityHandler( IdentityApiInit init )
            throws IOException
    {
        logger.info( "credential.firebase.on: " + init.credentialOn );
        logger.info( "credential.firebase.fileName:" + init.fileName );
        logger.info( "credential.firebase.projectId: " + init.projectId );
        logger.info( "credential.firebase.databaseName: " + init.databaseName );
        logger.info( "credential.firebase.serviceAccountEmail: " + init.serviceAccount );

        FirebaseOptions options;
        String databaseName = init.databaseName;
        String projectId = init.projectId;

        if ( Strings.isNullOrEmpty( databaseName ) && projectId != null )
        {
            databaseName = projectId;
        }

        if ( Strings.isNullOrEmpty( databaseName ) )
        {
            String msg = "The property 'credential.firebase.databaseName' is mandatory.";
            throw new IllegalArgumentException( msg );
        }

        String databaseUrl = "https://" + databaseName + ".firebaseio.com";
        logger.info( "The final Firebase database URL: " + databaseUrl );

        if ( !init.credentialOn )
        {
            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setCredentials( GoogleCredentials.getApplicationDefault() )
                    .setDatabaseUrl( databaseUrl );

            if ( !Strings.isNullOrEmpty( init.serviceAccount ) )
            {
                builder.setServiceAccountId( init.serviceAccount );
            }

            options = builder.build();

            logger.info( "Firebase-admin built with application default credentials." );
        }
        else
        {
            if ( Strings.isNullOrEmpty( init.fileName ) )
            {
                String msg = "The property 'credential.firebase.fileName' for current configuration is mandatory.";
                throw new IllegalArgumentException( msg );
            }

            URL url = GoogleApiFirebaseModule.class.getResource( init.fileName );
            if ( url == null )
            {
                String msg = "The file defined by property 'credential.firebase.fileName' "
                        + init.fileName + " has not been found.";
                throw new IllegalArgumentException( msg );
            }
            FileInputStream serviceAccount = new FileInputStream( url.getPath() );

            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setCredentials( GoogleCredentials.fromStream( serviceAccount ) )
                    .setDatabaseUrl( databaseUrl );

            if ( !Strings.isNullOrEmpty( init.serviceAccount ) )
            {
                builder.setServiceAccountId( init.serviceAccount );
            }

            options = builder.build();

            logger.info( "Firebase-admin built with credentials from file." );
        }

        FirebaseApp.initializeApp( options );

        return FirebaseAuth.getInstance();
    }

    static class IdentityApiInit
    {
        @Inject( optional = true )
        @Named( "credential.firebase.on" )
        boolean credentialOn = false;

        @Inject( optional = true )
        @Named( "credential.firebase.fileName" )
        String fileName;

        @Inject( optional = true )
        @Named( "credential.firebase.projectId" )
        String projectId;

        @Inject( optional = true )
        @Named( "credential.firebase.databaseName" )
        String databaseName;

        @Inject( optional = true )
        @Named( "credential.firebase.serviceAccountEmail" )
        String serviceAccount;
    }
}

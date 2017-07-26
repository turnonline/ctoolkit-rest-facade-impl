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

package org.ctoolkit.restapi.client.agent.adaptee;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.ctoolkit.api.agent.Agent;
import org.ctoolkit.api.agent.AgentRequest;
import org.ctoolkit.api.agent.model.ExportBatchCollection;
import org.ctoolkit.api.agent.model.ExportItem;
import org.ctoolkit.api.agent.model.ExportJob;
import org.ctoolkit.api.agent.model.ImportBatchCollection;
import org.ctoolkit.api.agent.model.ImportItem;
import org.ctoolkit.api.agent.model.ImportJob;
import org.ctoolkit.api.agent.model.KindMetaDataCollection;
import org.ctoolkit.api.agent.model.MetadataAuditCollection;
import org.ctoolkit.api.agent.model.MigrationBatchCollection;
import org.ctoolkit.api.agent.model.MigrationItem;
import org.ctoolkit.api.agent.model.MigrationJob;
import org.ctoolkit.api.agent.model.PropertyMetaDataCollection;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.adapter.Constants;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The extended {@link Agent} overriding default functionality. The purpose is to be able change the root URL
 * per request call.
 * See {@link CustomizedCtoolkitAgent#buildCustomizedRequestUrl(RequestCredential, String, String, AgentRequest)}.
 * <p>
 * In the original generated class we have changed the visibility of the {@link Agent.Builder}
 * and related constructor {@link Agent#Agent(Agent.Builder) }
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class CustomizedCtoolkitAgent
        extends Agent
{
    CustomizedCtoolkitAgent( Builder builder )
    {
        super( builder );
    }

    public Audit audit()
    {
        return new Audit();
    }

    public ExportBatch exportBatch()
    {
        return new ExportBatch();
    }

    public ImportBatch importBatch()
    {
        return new ImportBatch();
    }

    public MigrationBatch migrationBatch()
    {
        return new MigrationBatch();
    }

    public Metadata metadata()
    {
        return new Metadata();
    }

    private GenericUrl buildCustomizedRequestUrl( RequestCredential credential,
                                                  String servicePath,
                                                  String uriTemplate,
                                                  AgentRequest request )
    {
        String rootUrl = credential.getEndpointUrl();
        if ( Strings.isNullOrEmpty( rootUrl ) )
        {
            return null;
        }

        checkNotNull( servicePath );
        checkNotNull( uriTemplate );
        checkNotNull( request );

        if ( !rootUrl.endsWith( "/" ) )
        {
            rootUrl = rootUrl + "/";
        }
        String url = rootUrl + servicePath;
        return new GenericUrl( UriTemplate.expand( url, uriTemplate, request, true ) );
    }

    private void setRequestApiKey( AgentRequest request, RequestCredential credential )
    {
        checkNotNull( request );
        String apiKey = credential.getApiKey();
        boolean disableGZipContent = credential.isDisableGZipContent();

        if ( disableGZipContent && !request.getRequestHeaders().isEmpty() )
        {
            // for new request by default there is a 'accept-encoding' header with 'gzip'
            String acceptEncoding = request.getRequestHeaders().getAcceptEncoding();
            if ( !Strings.isNullOrEmpty( acceptEncoding ) && "gzip".equals( acceptEncoding ) )
            {
                request.getRequestHeaders().setAcceptEncoding( null );
            }
        }
        if ( !Strings.isNullOrEmpty( apiKey ) )
        {
            request.getRequestHeaders().put( Constants.IDENTITY_GTOKEN, apiKey );
        }
        request.setDisableGZipContent( disableGZipContent );
    }

    public static class Builder
            extends Agent.Builder
    {

        public Builder( HttpTransport transport,
                        JsonFactory jsonFactory,
                        HttpRequestInitializer httpRequestInitializer )
        {
            super( transport, jsonFactory, httpRequestInitializer );
        }

        @Override
        public CustomizedCtoolkitAgent build()
        {
            return new CustomizedCtoolkitAgent( this );
        }
    }

    public class Audit
            extends Agent.Audit
    {
        public List list() throws IOException
        {
            List result = new List();
            initialize( result );
            return result;
        }

        class List
                extends Agent.Audit.List
        {
            private RequestCredential credential;

            List()
            {
                super();
            }

            public MetadataAuditCollection execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }
    }

    public class ExportBatch
            extends Agent.ExportBatch
    {
        public Delete delete( Long id ) throws IOException
        {
            Delete result = new Delete( id );
            initialize( result );
            return result;
        }

        public Get get( Long id ) throws IOException
        {
            Get result = new Get( id );
            initialize( result );
            return result;
        }

        public Insert insert( org.ctoolkit.api.agent.model.ExportBatch content ) throws IOException
        {
            Insert result = new Insert( content );
            initialize( result );
            return result;
        }

        public List list() throws IOException
        {
            List result = new List();
            initialize( result );
            return result;
        }

        public Update update( Long id, org.ctoolkit.api.agent.model.ExportBatch content )
                throws IOException
        {
            Update result = new Update( id, content );
            initialize( result );
            return result;
        }

        public Item item()
        {
            return new Item();
        }

        public Job job()
        {
            return new Job();
        }

        class Delete
                extends Agent.ExportBatch.Delete
        {
            private RequestCredential credential;

            Delete( Long id )
            {
                super( id );
            }

            public Void execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        class Get
                extends Agent.ExportBatch.Get
        {
            private RequestCredential credential;

            Get( Long id )
            {
                super( id );
            }

            public org.ctoolkit.api.agent.model.ExportBatch execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        class Insert
                extends Agent.ExportBatch.Insert
        {
            private RequestCredential credential;

            Insert( org.ctoolkit.api.agent.model.ExportBatch content )
            {
                super( content );
            }

            public org.ctoolkit.api.agent.model.ExportBatch execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        class List
                extends Agent.ExportBatch.List
        {
            private RequestCredential credential;

            List()
            {
                super();
            }

            public ExportBatchCollection execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        class Update
                extends Agent.ExportBatch.Update
        {
            private RequestCredential credential;

            Update( Long id, org.ctoolkit.api.agent.model.ExportBatch content )
            {
                super( id, content );
            }

            public org.ctoolkit.api.agent.model.ExportBatch execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        public class Item
                extends Agent.ExportBatch.Item
        {
            public Delete delete( Long metadataId, Long id ) throws IOException
            {
                Delete result = new Delete( metadataId, id );
                initialize( result );
                return result;
            }

            public Get get( Long metadataId, Long id ) throws IOException
            {
                Get result = new Get( metadataId, id );
                initialize( result );
                return result;
            }

            public Insert insert( Long metadataId, ExportItem content )
                    throws IOException
            {
                Insert result = new Insert( metadataId, content );
                initialize( result );
                return result;
            }

            public Update update( Long metadataId, Long id, ExportItem content )
                    throws IOException
            {
                Update result = new Update( metadataId, id, content );
                initialize( result );
                return result;
            }

            class Delete
                    extends Agent.ExportBatch.Item.Delete
            {
                private RequestCredential credential;

                Delete( Long metadataId, Long id )
                {
                    super( metadataId, id );
                }

                public Void execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Get
                    extends Agent.ExportBatch.Item.Get
            {
                private RequestCredential credential;

                Get( Long metadataId, Long id )
                {
                    super( metadataId, id );
                }

                public ExportItem execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Insert
                    extends Agent.ExportBatch.Item.Insert
            {
                private RequestCredential credential;

                Insert( Long metadataId, ExportItem content )
                {
                    super( metadataId, content );
                }

                public ExportItem execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Update
                    extends Agent.ExportBatch.Item.Update
            {
                private RequestCredential credential;

                Update( Long metadataId, Long id, ExportItem content )
                {
                    super( metadataId, id, content );
                }

                public ExportItem execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }
        }

        public class Job
                extends Agent.ExportBatch.Job
        {
            public Cancel cancel( Long id ) throws IOException
            {
                Cancel result = new Cancel( id );
                initialize( result );
                return result;
            }

            public Progress progress( Long id ) throws IOException
            {
                Progress result = new Progress( id );
                initialize( result );
                return result;
            }

            public Start start( Long id ) throws IOException
            {
                Start result = new Start( id );
                initialize( result );
                return result;
            }

            class Cancel
                    extends Agent.ExportBatch.Job.Cancel
            {
                private RequestCredential credential;

                Cancel( Long id )
                {
                    super( id );
                }

                public ExportJob execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Progress
                    extends Agent.ExportBatch.Job.Progress
            {
                private RequestCredential credential;

                Progress( Long id )
                {
                    super( id );
                }

                public ExportJob execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Start
                    extends Agent.ExportBatch.Job.Start
            {
                private RequestCredential credential;

                Start( Long id )
                {
                    super( id );
                }

                public ExportJob execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }
        }
    }

    public class ImportBatch
            extends Agent.ImportBatch
    {
        public Delete delete( Long id ) throws IOException
        {
            Delete result = new Delete( id );
            initialize( result );
            return result;
        }

        public Get get( Long id ) throws IOException
        {
            Get result = new Get( id );
            initialize( result );
            return result;
        }

        public Insert insert( org.ctoolkit.api.agent.model.ImportBatch content ) throws IOException
        {
            Insert result = new Insert( content );
            initialize( result );
            return result;
        }

        public List list() throws IOException
        {
            List result = new List();
            initialize( result );
            return result;
        }

        public Update update( Long id, org.ctoolkit.api.agent.model.ImportBatch content )
                throws IOException
        {
            Update result = new Update( id, content );
            initialize( result );
            return result;
        }

        public Item item()
        {
            return new Item();
        }

        public Job job()
        {
            return new Job();
        }

        class Delete
                extends Agent.ImportBatch.Delete
        {
            private RequestCredential credential;

            Delete( Long id )
            {
                super( id );
            }

            public Void execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        class Get
                extends Agent.ImportBatch.Get
        {
            private RequestCredential credential;

            Get( Long id )
            {
                super( id );
            }

            public org.ctoolkit.api.agent.model.ImportBatch execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        class Insert
                extends Agent.ImportBatch.Insert
        {
            private RequestCredential credential;

            Insert( org.ctoolkit.api.agent.model.ImportBatch content )
            {
                super( content );
            }

            public org.ctoolkit.api.agent.model.ImportBatch execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        class List
                extends Agent.ImportBatch.List
        {
            private RequestCredential credential;

            List()
            {
                super();
            }

            public ImportBatchCollection execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        class Update
                extends Agent.ImportBatch.Update
        {
            private RequestCredential credential;

            Update( Long id, org.ctoolkit.api.agent.model.ImportBatch content )
            {
                super( id, content );
            }

            public org.ctoolkit.api.agent.model.ImportBatch execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        public class Item
                extends Agent.ImportBatch.Item
        {
            public Delete delete( Long metadataId, Long id ) throws IOException
            {
                Delete result = new Delete( metadataId, id );
                initialize( result );
                return result;
            }

            public Get get( Long metadataId, Long id ) throws IOException
            {
                Get result = new Get( metadataId, id );
                initialize( result );
                return result;
            }

            public Insert insert( Long metadataId, ImportItem content )
                    throws IOException
            {
                Insert result = new Insert( metadataId, content );
                initialize( result );
                return result;
            }

            public Update update( Long metadataId, Long id, ImportItem content )
                    throws IOException
            {
                Update result = new Update( metadataId, id, content );
                initialize( result );
                return result;
            }

            class Delete
                    extends Agent.ImportBatch.Item.Delete
            {
                private RequestCredential credential;

                Delete( Long metadataId, Long id )
                {
                    super( metadataId, id );
                }

                public Void execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Get
                    extends Agent.ImportBatch.Item.Get
            {
                private RequestCredential credential;

                Get( Long metadataId, Long id )
                {
                    super( metadataId, id );
                }

                public ImportItem execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Insert
                    extends Agent.ImportBatch.Item.Insert
            {
                private RequestCredential credential;

                Insert( Long metadataId, ImportItem content )
                {
                    super( metadataId, content );
                }

                public ImportItem execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Update
                    extends Agent.ImportBatch.Item.Update
            {
                private RequestCredential credential;

                Update( Long metadataId, Long id, ImportItem content )
                {
                    super( metadataId, id, content );
                }

                public ImportItem execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }
        }

        public class Job
                extends Agent.ImportBatch.Job
        {
            public Cancel cancel( Long id ) throws IOException
            {
                Cancel result = new Cancel( id );
                initialize( result );
                return result;
            }

            public Progress progress( Long id ) throws IOException
            {
                Progress result = new Progress( id );
                initialize( result );
                return result;
            }

            public Start start( Long id )
                    throws IOException
            {
                Start result = new Start( id );
                initialize( result );
                return result;
            }

            class Cancel
                    extends Agent.ImportBatch.Job.Cancel
            {
                private RequestCredential credential;

                Cancel( Long id )
                {
                    super( id );
                }

                public ImportJob execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Progress
                    extends Agent.ImportBatch.Job.Progress
            {
                private RequestCredential credential;

                Progress( Long id )
                {
                    super( id );
                }

                public ImportJob execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            class Start
                    extends Agent.ImportBatch.Job.Start
            {
                private RequestCredential credential;

                Start( Long id )
                {
                    super( id );
                }

                public ImportJob execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }
        }
    }

    public class MigrationBatch
            extends Agent.MigrationBatch
    {
        public Delete delete( Long id ) throws java.io.IOException
        {
            Delete result = new Delete( id );
            initialize( result );
            return result;
        }

        public class Delete
                extends Agent.MigrationBatch.Delete
        {
            private RequestCredential credential;

            Delete( Long id )
            {
                super( id );
            }

            public void execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        public Agent.MigrationBatch.Get get( Long id ) throws java.io.IOException
        {
            Get result = new Get( id );
            initialize( result );
            return result;
        }

        public class Get
                extends Agent.MigrationBatch.Get
        {
            private RequestCredential credential;

            Get( Long id )
            {
                super( id );
            }

            public org.ctoolkit.api.agent.model.MigrationBatch execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        public Agent.MigrationBatch.Insert insert( org.ctoolkit.api.agent.model.MigrationBatch content )
                throws java.io.IOException
        {
            Insert result = new Insert( content );
            initialize( result );
            return result;
        }

        public class Insert
                extends Agent.MigrationBatch.Insert
        {
            private RequestCredential credential;

            protected Insert( org.ctoolkit.api.agent.model.MigrationBatch content )
            {
                super( content );
            }

            public org.ctoolkit.api.agent.model.MigrationBatch execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        public Agent.MigrationBatch.List list() throws java.io.IOException
        {
            List result = new List();
            initialize( result );
            return result;
        }

        public class List
                extends Agent.MigrationBatch.List
        {
            private RequestCredential credential;

            protected List()
            {
                super( );
            }

            public MigrationBatchCollection execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        public Agent.MigrationBatch.Update update( Long id, org.ctoolkit.api.agent.model.MigrationBatch content )
                throws java.io.IOException
        {
            Update result = new Update( id, content );
            initialize( result );
            return result;
        }

        public class Update
                extends Agent.MigrationBatch.Update
        {
            private RequestCredential credential;

            protected Update( Long id, org.ctoolkit.api.agent.model.MigrationBatch content )
            {
                super( id, content );
            }

            public org.ctoolkit.api.agent.model.MigrationBatch execute( RequestCredential credential ) throws IOException
            {
                this.credential = checkNotNull( credential );
                setRequestApiKey( this, credential );
                return super.execute();
            }

            @Override
            public GenericUrl buildHttpRequestUrl()
            {
                String servicePath = this.getAbstractGoogleClient().getServicePath();
                String uriTemplate = this.getUriTemplate();
                GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                if ( url == null )
                {
                    return super.buildHttpRequestUrl();
                }
                else
                {
                    return url;
                }
            }
        }

        public Agent.MigrationBatch.Item item()
        {
            return new Item();
        }

        public class Item extends Agent.MigrationBatch.Item
        {
            public Agent.MigrationBatch.Item.Delete delete( Long metadataId, Long id ) throws java.io.IOException
            {
                Delete result = new Delete( metadataId, id );
                initialize( result );
                return result;
            }

            public class Delete
                    extends Agent.MigrationBatch.Item.Delete
            {
                private RequestCredential credential;

                protected Delete( Long metadataId, Long id )
                {
                    super( metadataId, id );
                }

                public void execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            public Agent.MigrationBatch.Item.Get get( Long metadataId, Long id ) throws java.io.IOException
            {
                Get result = new Get( metadataId, id );
                initialize( result );
                return result;
            }

            public class Get
                    extends Agent.MigrationBatch.Item.Get
            {
                private RequestCredential credential;

                protected Get( Long metadataId, Long id )
                {
                    super( metadataId, id );
                }

                public MigrationItem execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            public Agent.MigrationBatch.Item.Insert insert( Long metadataId, org.ctoolkit.api.agent.model.MigrationItem content )
                    throws java.io.IOException
            {
                Insert result = new Insert( metadataId, content );
                initialize( result );
                return result;
            }

            public class Insert
                    extends Agent.MigrationBatch.Item.Insert
            {
                private RequestCredential credential;

                protected Insert( Long metadataId, org.ctoolkit.api.agent.model.MigrationItem content )
                {
                    super( metadataId, content );
                }

                public MigrationItem execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            public Agent.MigrationBatch.Item.Update update( Long metadataId, Long id, org.ctoolkit.api.agent.model.MigrationItem content )
                    throws java.io.IOException
            {
                Update result = new Update( metadataId, id, content );
                initialize( result );
                return result;
            }

            public class Update
                    extends Agent.MigrationBatch.Item.Update
            {
                private RequestCredential credential;

                protected Update( Long metadataId, Long id, org.ctoolkit.api.agent.model.MigrationItem content )
                {
                    super( metadataId, id, content);
                }

                public MigrationItem execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

        }

        public Agent.MigrationBatch.Job job()
        {
            return new Job();
        }

        public class Job extends Agent.MigrationBatch.Job
        {
            public Agent.MigrationBatch.Job.Cancel cancel( Long id ) throws java.io.IOException
            {
                Cancel result = new Cancel( id );
                initialize( result );
                return result;
            }

            public class Cancel
                    extends Agent.MigrationBatch.Job.Cancel
            {
                private RequestCredential credential;

                protected Cancel( Long id )
                {
                    super( id );
                }

                public void execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            public Agent.MigrationBatch.Job.Progress progress( Long id ) throws java.io.IOException
            {
                Progress result = new Progress( id );
                initialize( result );
                return result;
            }

            public class Progress
                    extends Agent.MigrationBatch.Job.Progress
            {
                private RequestCredential credential;

                protected Progress( Long id )
                {
                    super( id );
                }

                public MigrationJob execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            public Agent.MigrationBatch.Job.Start start( Long id ) throws java.io.IOException
            {
                Start result = new Start( id );
                initialize( result );
                return result;
            }

            public class Start
                    extends Agent.MigrationBatch.Job.Start
            {
                private RequestCredential credential;

                protected Start( Long id )
                {
                    super( id );
                }

                public MigrationJob execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

        }
    }

    public class Metadata
            extends Agent.Metadata
    {
        public Kind kind()
        {
            return new Kind();
        }

        public class Kind
                extends Agent.Metadata.Kind
        {
            public List list() throws IOException
            {
                List result = new List();
                initialize( result );
                return result;
            }

            public Property property()
            {
                return new Property();
            }

            class List
                    extends Agent.Metadata.Kind.List
            {
                private RequestCredential credential;

                List()
                {
                    super();
                }

                public KindMetaDataCollection execute( RequestCredential credential ) throws IOException
                {
                    this.credential = checkNotNull( credential );
                    setRequestApiKey( this, credential );
                    return super.execute();
                }

                @Override
                public GenericUrl buildHttpRequestUrl()
                {
                    String servicePath = this.getAbstractGoogleClient().getServicePath();
                    String uriTemplate = this.getUriTemplate();
                    GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                    if ( url == null )
                    {
                        return super.buildHttpRequestUrl();
                    }
                    else
                    {
                        return url;
                    }
                }
            }

            public class Property
                    extends Agent.Metadata.Kind.Property
            {
                public List list( String kind ) throws IOException
                {
                    List result = new List( kind );
                    initialize( result );
                    return result;
                }

                class List
                        extends Agent.Metadata.Kind.Property.List
                {
                    private RequestCredential credential;

                    List( String kind )
                    {
                        super( kind );
                    }

                    public PropertyMetaDataCollection execute( RequestCredential credential ) throws IOException
                    {
                        this.credential = checkNotNull( credential );
                        setRequestApiKey( this, credential );
                        return super.execute();
                    }

                    @Override
                    public GenericUrl buildHttpRequestUrl()
                    {
                        String servicePath = this.getAbstractGoogleClient().getServicePath();
                        String uriTemplate = this.getUriTemplate();
                        GenericUrl url = buildCustomizedRequestUrl( credential, servicePath, uriTemplate, this );

                        if ( url == null )
                        {
                            return super.buildHttpRequestUrl();
                        }
                        else
                        {
                            return url;
                        }
                    }
                }
            }
        }
    }
}

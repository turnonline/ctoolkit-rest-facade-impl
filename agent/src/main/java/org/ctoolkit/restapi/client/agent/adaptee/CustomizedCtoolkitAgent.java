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
import org.ctoolkit.api.agent.CtoolkitAgent;
import org.ctoolkit.api.agent.CtoolkitAgentRequest;
import org.ctoolkit.api.agent.model.ChangeBatchCollection;
import org.ctoolkit.api.agent.model.ChangeItem;
import org.ctoolkit.api.agent.model.ChangeJobInfo;
import org.ctoolkit.api.agent.model.ExportBatchCollection;
import org.ctoolkit.api.agent.model.ExportItem;
import org.ctoolkit.api.agent.model.ExportJobInfo;
import org.ctoolkit.api.agent.model.ImportBatchCollection;
import org.ctoolkit.api.agent.model.ImportItem;
import org.ctoolkit.api.agent.model.ImportJobInfo;
import org.ctoolkit.api.agent.model.KindMetaDataCollection;
import org.ctoolkit.api.agent.model.MetadataAuditCollection;
import org.ctoolkit.api.agent.model.PropertyMetaDataCollection;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.adapter.Constants;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The extended {@link CtoolkitAgent} overriding default functionality. The purpose is to be able change the root URL
 * per request call.
 * See {@link CustomizedCtoolkitAgent#buildCustomizedRequestUrl(RequestCredential, String, String, CtoolkitAgentRequest)}.
 * <p>
 * In the original generated class we have changed the visibility of the {@link CtoolkitAgent.Builder}
 * and related constructor {@link CtoolkitAgent#CtoolkitAgent(CtoolkitAgent.Builder) }
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class CustomizedCtoolkitAgent
        extends CtoolkitAgent
{
    CustomizedCtoolkitAgent( Builder builder )
    {
        super( builder );
    }

    public Audit audit()
    {
        return new Audit();
    }

    public ChangeBatch changeBatch()
    {
        return new ChangeBatch();
    }

    public ExportBatch exportBatch()
    {
        return new ExportBatch();
    }

    public ImportBatch importBatch()
    {
        return new ImportBatch();
    }

    public Metadata metadata()
    {
        return new Metadata();
    }

    private GenericUrl buildCustomizedRequestUrl( RequestCredential credential,
                                                  String servicePath,
                                                  String uriTemplate,
                                                  CtoolkitAgentRequest request )
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

    private void setRequestApiKey( CtoolkitAgentRequest request, RequestCredential credential )
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
            request.getRequestHeaders().put( Constants.OBO_TOKEN, apiKey );
        }
        request.setDisableGZipContent( disableGZipContent );
    }

    public static class Builder
            extends CtoolkitAgent.Builder
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
            extends CtoolkitAgent.Audit
    {
        public List list() throws IOException
        {
            List result = new List();
            initialize( result );
            return result;
        }

        class List
                extends CtoolkitAgent.Audit.List
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

    public class ChangeBatch
            extends CtoolkitAgent.ChangeBatch
    {
        public Delete delete( String id ) throws IOException
        {
            Delete result = new Delete( id );
            initialize( result );
            return result;
        }

        public Get get( String id ) throws IOException
        {
            Get result = new Get( id );
            initialize( result );
            return result;
        }

        public Insert insert( org.ctoolkit.api.agent.model.ChangeBatch content ) throws IOException
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

        public Patch patch( String id, org.ctoolkit.api.agent.model.ChangeBatch content )
                throws IOException
        {
            Patch result = new Patch( id, content );
            initialize( result );
            return result;
        }

        public Update update( String id, org.ctoolkit.api.agent.model.ChangeBatch content )
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
                extends CtoolkitAgent.ChangeBatch.Delete
        {
            private RequestCredential credential;

            Delete( String id )
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
                extends CtoolkitAgent.ChangeBatch.Get
        {
            private RequestCredential credential;

            Get( String id )
            {
                super( id );
            }

            public org.ctoolkit.api.agent.model.ChangeBatch execute( RequestCredential credential ) throws IOException
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
                extends CtoolkitAgent.ChangeBatch.Insert
        {
            private RequestCredential credential;

            Insert( org.ctoolkit.api.agent.model.ChangeBatch content )
            {
                super( content );
            }

            public org.ctoolkit.api.agent.model.ChangeBatch execute( RequestCredential credential ) throws IOException
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
                extends CtoolkitAgent.ChangeBatch.List
        {
            private RequestCredential credential;

            List()
            {
                super();
            }

            public ChangeBatchCollection execute( RequestCredential credential ) throws IOException
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

        class Patch
                extends CtoolkitAgent.ChangeBatch.Patch
        {
            private RequestCredential credential;

            Patch( String id, org.ctoolkit.api.agent.model.ChangeBatch content )
            {
                super( id, content );
            }

            public org.ctoolkit.api.agent.model.ChangeBatch execute( RequestCredential credential ) throws IOException
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
                extends CtoolkitAgent.ChangeBatch.Update
        {
            private RequestCredential credential;

            Update( String id, org.ctoolkit.api.agent.model.ChangeBatch content )
            {
                super( id, content );
            }

            public org.ctoolkit.api.agent.model.ChangeBatch execute( RequestCredential credential ) throws IOException
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
                extends CtoolkitAgent.ChangeBatch.Item
        {
            public Delete delete( String metadataId, String id ) throws IOException
            {
                Delete result = new Delete( metadataId, id );
                initialize( result );
                return result;
            }

            public Get get( String metadataId, String id ) throws IOException
            {
                Get result = new Get( metadataId, id );
                initialize( result );
                return result;
            }

            public Insert insert( String metadataId, ChangeItem content )
                    throws IOException
            {
                Insert result = new Insert( metadataId, content );
                initialize( result );
                return result;
            }

            public Patch patch( String metadataId, String id, ChangeItem content )
                    throws IOException
            {
                Patch result = new Patch( metadataId, id, content );
                initialize( result );
                return result;
            }

            public Update update( String metadataId, String id, ChangeItem content )
                    throws IOException
            {
                Update result = new Update( metadataId, id, content );
                initialize( result );
                return result;
            }

            class Delete
                    extends CtoolkitAgent.ChangeBatch.Item.Delete
            {
                private RequestCredential credential;

                Delete( String metadataId, String id )
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
                    extends CtoolkitAgent.ChangeBatch.Item.Get
            {
                private RequestCredential credential;

                Get( String metadataId, String id )
                {
                    super( metadataId, id );
                }

                public ChangeItem execute( RequestCredential credential ) throws IOException
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
                    extends CtoolkitAgent.ChangeBatch.Item.Insert
            {
                private RequestCredential credential;

                Insert( String metadataId, ChangeItem content )
                {
                    super( metadataId, content );
                }

                public ChangeItem execute( RequestCredential credential ) throws IOException
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

            class Patch
                    extends CtoolkitAgent.ChangeBatch.Item.Patch
            {
                private RequestCredential credential;

                Patch( String metadataId, String id, ChangeItem content )
                {
                    super( metadataId, id, content );
                }

                public ChangeItem execute( RequestCredential credential ) throws IOException
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
                    extends CtoolkitAgent.ChangeBatch.Item.Update
            {
                private RequestCredential credential;

                Update( String metadataId, String id, ChangeItem content )
                {
                    super( metadataId, id, content );
                }

                public ChangeItem execute( RequestCredential credential ) throws IOException
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
                extends CtoolkitAgent.ChangeBatch.Job
        {
            public Cancel cancel( String id ) throws IOException
            {
                Cancel result = new Cancel( id );
                initialize( result );
                return result;
            }

            public Delete delete( String id ) throws IOException
            {
                Delete result = new Delete( id );
                initialize( result );
                return result;
            }

            public Progress progress( String id ) throws IOException
            {
                Progress result = new Progress( id );
                initialize( result );
                return result;
            }

            public Start start( String id ) throws IOException
            {
                Start result = new Start( id );
                initialize( result );
                return result;
            }

            class Cancel
                    extends CtoolkitAgent.ChangeBatch.Job.Cancel
            {
                private RequestCredential credential;

                Cancel( String id )
                {
                    super( id );
                }

                public ChangeJobInfo execute( RequestCredential credential ) throws IOException
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

            class Delete
                    extends CtoolkitAgent.ChangeBatch.Job.Delete
            {
                private RequestCredential credential;

                Delete( String id )
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

            class Progress
                    extends CtoolkitAgent.ChangeBatch.Job.Progress
            {
                private RequestCredential credential;

                Progress( String id )
                {
                    super( id );
                }

                public ChangeJobInfo execute( RequestCredential credential ) throws IOException
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
                    extends CtoolkitAgent.ChangeBatch.Job.Start
            {
                private RequestCredential credential;

                Start( String id )
                {
                    super( id );
                }

                public ChangeJobInfo execute( RequestCredential credential ) throws IOException
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

    public class ExportBatch
            extends CtoolkitAgent.ExportBatch
    {
        public Delete delete( String id ) throws IOException
        {
            Delete result = new Delete( id );
            initialize( result );
            return result;
        }

        public Get get( String id ) throws IOException
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

        public Patch patch( String id, org.ctoolkit.api.agent.model.ExportBatch content )
                throws IOException
        {
            Patch result = new Patch( id, content );
            initialize( result );
            return result;
        }

        public Update update( String id, org.ctoolkit.api.agent.model.ExportBatch content )
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

        public Migrate migrate()
        {
            return new Migrate();
        }

        class Delete
                extends CtoolkitAgent.ExportBatch.Delete
        {
            private RequestCredential credential;

            Delete( String id )
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
                extends CtoolkitAgent.ExportBatch.Get
        {
            private RequestCredential credential;

            Get( String id )
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
                extends CtoolkitAgent.ExportBatch.Insert
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
                extends CtoolkitAgent.ExportBatch.List
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

        class Patch
                extends CtoolkitAgent.ExportBatch.Patch
        {
            private RequestCredential credential;

            Patch( String id, org.ctoolkit.api.agent.model.ExportBatch content )
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

        class Update
                extends CtoolkitAgent.ExportBatch.Update
        {
            private RequestCredential credential;

            Update( String id, org.ctoolkit.api.agent.model.ExportBatch content )
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
                extends CtoolkitAgent.ExportBatch.Item
        {
            public Delete delete( String metadataId, String id ) throws IOException
            {
                Delete result = new Delete( metadataId, id );
                initialize( result );
                return result;
            }

            public Get get( String metadataId, String id ) throws IOException
            {
                Get result = new Get( metadataId, id );
                initialize( result );
                return result;
            }

            public Insert insert( String metadataId, ExportItem content )
                    throws IOException
            {
                Insert result = new Insert( metadataId, content );
                initialize( result );
                return result;
            }

            public Patch patch( String metadataId, String id, ExportItem content )
                    throws IOException
            {
                Patch result = new Patch( metadataId, id, content );
                initialize( result );
                return result;
            }

            public Update update( String metadataId, String id, ExportItem content )
                    throws IOException
            {
                Update result = new Update( metadataId, id, content );
                initialize( result );
                return result;
            }

            class Delete
                    extends CtoolkitAgent.ExportBatch.Item.Delete
            {
                private RequestCredential credential;

                Delete( String metadataId, String id )
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
                    extends CtoolkitAgent.ExportBatch.Item.Get
            {
                private RequestCredential credential;

                Get( String metadataId, String id )
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
                    extends CtoolkitAgent.ExportBatch.Item.Insert
            {
                private RequestCredential credential;

                Insert( String metadataId, ExportItem content )
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

            class Patch
                    extends CtoolkitAgent.ExportBatch.Item.Patch
            {
                private RequestCredential credential;

                Patch( String metadataId, String id, ExportItem content )
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

            class Update
                    extends CtoolkitAgent.ExportBatch.Item.Update
            {
                private RequestCredential credential;

                Update( String metadataId, String id, ExportItem content )
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
                extends CtoolkitAgent.ExportBatch.Job
        {
            public Cancel cancel( String id ) throws IOException
            {
                Cancel result = new Cancel( id );
                initialize( result );
                return result;
            }

            public Delete delete( String id ) throws IOException
            {
                Delete result = new Delete( id );
                initialize( result );
                return result;
            }

            public Progress progress( String id ) throws IOException
            {
                Progress result = new Progress( id );
                initialize( result );
                return result;
            }

            public Start start( String id ) throws IOException
            {
                Start result = new Start( id );
                initialize( result );
                return result;
            }

            class Cancel
                    extends CtoolkitAgent.ExportBatch.Job.Cancel
            {
                private RequestCredential credential;

                Cancel( String id )
                {
                    super( id );
                }

                public ExportJobInfo execute( RequestCredential credential ) throws IOException
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

            class Delete
                    extends CtoolkitAgent.ExportBatch.Job.Delete
            {
                private RequestCredential credential;

                Delete( String id )
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

            class Progress
                    extends CtoolkitAgent.ExportBatch.Job.Progress
            {
                private RequestCredential credential;

                Progress( String id )
                {
                    super( id );
                }

                public ExportJobInfo execute( RequestCredential credential ) throws IOException
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
                    extends CtoolkitAgent.ExportBatch.Job.Start
            {
                private RequestCredential credential;

                Start( String id )
                {
                    super( id );
                }

                public ExportJobInfo execute( RequestCredential credential ) throws IOException
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

        public class Migrate
                extends CtoolkitAgent.ExportBatch.Migrate
        {
            public Insert insert( String id ) throws IOException
            {
                Insert result = new Insert( id );
                initialize( result );
                return result;
            }

            class Insert
                    extends CtoolkitAgent.ExportBatch.Migrate.Insert
            {
                private RequestCredential credential;

                Insert( String id )
                {
                    super( id );
                }

                public org.ctoolkit.api.agent.model.ImportBatch execute( RequestCredential credential )
                        throws IOException
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
            extends CtoolkitAgent.ImportBatch
    {
        public Delete delete( String id ) throws IOException
        {
            Delete result = new Delete( id );
            initialize( result );
            return result;
        }

        public Get get( String id ) throws IOException
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

        public Patch patch( String id, org.ctoolkit.api.agent.model.ImportBatch content )
                throws IOException
        {
            Patch result = new Patch( id, content );
            initialize( result );
            return result;
        }

        public Update update( String id, org.ctoolkit.api.agent.model.ImportBatch content )
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
                extends CtoolkitAgent.ImportBatch.Delete
        {
            private RequestCredential credential;

            Delete( String id )
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
                extends CtoolkitAgent.ImportBatch.Get
        {
            private RequestCredential credential;

            Get( String id )
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
                extends CtoolkitAgent.ImportBatch.Insert
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
                extends CtoolkitAgent.ImportBatch.List
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

        class Patch
                extends CtoolkitAgent.ImportBatch.Patch
        {
            private RequestCredential credential;

            Patch( String id, org.ctoolkit.api.agent.model.ImportBatch content )
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

        class Update
                extends CtoolkitAgent.ImportBatch.Update
        {
            private RequestCredential credential;

            Update( String id, org.ctoolkit.api.agent.model.ImportBatch content )
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
                extends CtoolkitAgent.ImportBatch.Item
        {
            public Delete delete( String metadataId, String id ) throws IOException
            {
                Delete result = new Delete( metadataId, id );
                initialize( result );
                return result;
            }

            public Get get( String metadataId, String id ) throws IOException
            {
                Get result = new Get( metadataId, id );
                initialize( result );
                return result;
            }

            public Insert insert( String metadataId, ImportItem content )
                    throws IOException
            {
                Insert result = new Insert( metadataId, content );
                initialize( result );
                return result;
            }

            public Patch patch( String metadataId, String id, ImportItem content )
                    throws IOException
            {
                Patch result = new Patch( metadataId, id, content );
                initialize( result );
                return result;
            }

            public Update update( String metadataId, String id, ImportItem content )
                    throws IOException
            {
                Update result = new Update( metadataId, id, content );
                initialize( result );
                return result;
            }

            class Delete
                    extends CtoolkitAgent.ImportBatch.Item.Delete
            {
                private RequestCredential credential;

                Delete( String metadataId, String id )
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
                    extends CtoolkitAgent.ImportBatch.Item.Get
            {
                private RequestCredential credential;

                Get( String metadataId, String id )
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
                    extends CtoolkitAgent.ImportBatch.Item.Insert
            {
                private RequestCredential credential;

                Insert( String metadataId, ImportItem content )
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

            class Patch
                    extends CtoolkitAgent.ImportBatch.Item.Patch
            {
                private RequestCredential credential;

                Patch( String metadataId, String id, ImportItem content )
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

            class Update
                    extends CtoolkitAgent.ImportBatch.Item.Update
            {
                private RequestCredential credential;

                Update( String metadataId, String id, ImportItem content )
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
                extends CtoolkitAgent.ImportBatch.Job
        {
            public Cancel cancel( String id ) throws IOException
            {
                Cancel result = new Cancel( id );
                initialize( result );
                return result;
            }

            public Delete delete( String id ) throws IOException
            {
                Delete result = new Delete( id );
                initialize( result );
                return result;
            }

            public Progress progress( String id ) throws IOException
            {
                Progress result = new Progress( id );
                initialize( result );
                return result;
            }

            public Start start( String id, org.ctoolkit.api.agent.model.ImportBatch content )
                    throws IOException
            {
                Start result = new Start( id, content );
                initialize( result );
                return result;
            }

            class Cancel
                    extends CtoolkitAgent.ImportBatch.Job.Cancel
            {
                private RequestCredential credential;

                Cancel( String id )
                {
                    super( id );
                }

                public ImportJobInfo execute( RequestCredential credential ) throws IOException
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

            class Delete
                    extends CtoolkitAgent.ImportBatch.Job.Delete
            {
                private RequestCredential credential;

                Delete( String id )
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

            class Progress
                    extends CtoolkitAgent.ImportBatch.Job.Progress
            {
                private RequestCredential credential;

                Progress( String id )
                {
                    super( id );
                }

                public ImportJobInfo execute( RequestCredential credential ) throws IOException
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
                    extends CtoolkitAgent.ImportBatch.Job.Start
            {
                private RequestCredential credential;

                Start( String id, org.ctoolkit.api.agent.model.ImportBatch content )
                {
                    super( id, content );
                }

                public ImportJobInfo execute( RequestCredential credential ) throws IOException
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
            extends CtoolkitAgent.Metadata
    {
        public Kind kind()
        {
            return new Kind();
        }

        public class Kind
                extends CtoolkitAgent.Metadata.Kind
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
                    extends CtoolkitAgent.Metadata.Kind.List
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
                    extends CtoolkitAgent.Metadata.Kind.Property
            {
                public List list( String kind ) throws IOException
                {
                    List result = new List( kind );
                    initialize( result );
                    return result;
                }

                class List
                        extends CtoolkitAgent.Metadata.Kind.Property.List
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

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

package org.ctoolkit.restapi.client.agent.model;

/**
 * Map reduce job state
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public enum JobState
{
    /**
     * Job is currently executing.
     */
    RUNNING,
    /**
     * Job has completed successfully.
     */
    COMPLETED_SUCCESSFULLY,
    /**
     * Job was stopped through PipelineService#stopPipeline(String).
     */
    STOPPED_BY_REQUEST,
    /**
     * Job execution was stopped due to unhandled failure.
     */
    STOPPED_BY_ERROR,
    /**
     * Job has failed and is going to retry later.
     */
    WAITING_TO_RETRY,
    /**
     * Job was cancelled either through
     * PipelineService#cancelPipeline(String) or due to unhandled
     * failure in a sibling job.
     */
    CANCELED_BY_REQUEST
}

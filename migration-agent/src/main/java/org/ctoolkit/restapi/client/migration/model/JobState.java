package org.ctoolkit.restapi.client.migration.model;

/**
 * Map reduce job state
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
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

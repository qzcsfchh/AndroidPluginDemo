package io.github.qzcsfchh.plugin.filesizer

import org.apache.tools.ant.BuildEvent
import org.apache.tools.ant.BuildListener
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

class FileSizerPlugin implements Plugin<Project> {
    static final String GROUP = 'andfun'

    @Override
    void apply(Project project) {
        def fileSizer = project.extensions.create('fileSizer', FileSizer)
        project.afterEvaluate {
            project.task("fileSizer", type: FileSizerTask) {
                group GROUP
                description 'statistic files size including resource and code.'
                project.logger.println fileSizer
                addPaths resolveFilePaths(project, fileSizer)
            }
            if (fileSizer.enableBuildLog){
                BuildCycleListener listener = new BuildCycleListener()
                project.gradle.addListener(listener)
            }

        }
    }

    static Set<File> resolveFilePaths(Project project, FileSizer fileSizer) {
        def sourceSets = project.android.sourceSets
        project.logger.println sourceSets
        //TODO handle productFlavors.
        Set<File> paths = new LinkedHashSet<>()
        if (fileSizer.includeCode) {
            paths.addAll(sourceSets.main.java.getSrcDirs())
        }
        if (fileSizer.includeResource) {
            paths.addAll(sourceSets.main.res.getSrcDirs())
        }
        return paths
    }


    static class BuildCycleListener implements TaskExecutionListener, BuildListener, ProjectEvaluationListener {

        @Override
        void buildStarted(BuildEvent buildEvent) {
            buildEvent.project.log "[$GROUP] buildStarted: "+buildEvent.task.getTaskName()
        }

        @Override
        void buildFinished(BuildEvent buildEvent) {
            buildEvent.project.log "[$GROUP] buildFinished: "+buildEvent.task.getTaskName()
        }

        @Override
        void targetStarted(BuildEvent buildEvent) {
            buildEvent.project.log "[$GROUP] buildTargetStarted: "+buildEvent.task.getTaskName()
        }

        @Override
        void targetFinished(BuildEvent buildEvent) {
            buildEvent.project.log "[$GROUP] buildTargetFinished: "+buildEvent.task.getTaskName()
        }

        @Override
        void taskStarted(BuildEvent buildEvent) {
            buildEvent.project.log "[$GROUP] buildTaskStarted: "+buildEvent.task.getTaskName()
        }

        @Override
        void taskFinished(BuildEvent buildEvent) {
            buildEvent.project.log "[$GROUP] buildTaskFinished: "+buildEvent.task.getTaskName()
        }

        @Override
        void messageLogged(BuildEvent buildEvent) {
            buildEvent.project.log "[$GROUP] buildMessageLogged: "+buildEvent.task.getTaskName()
        }

        @Override
        void beforeExecute(Task task) {
            task.project.logger.println "[$GROUP] taskBeforeExecute: "+task.getName()
        }

        @Override
        void afterExecute(Task task, TaskState state) {
            task.project.logger.println "[$GROUP] taskAfterExecute: "+task.getName()
        }

        @Override
        void beforeEvaluate(Project project) {
            project.logger.println "[$GROUP] projectBeforeEvaluate: " + project.getName()
        }

        @Override
        void afterEvaluate(Project project, ProjectState state) {
            project.logger.println "[$GROUP] projectAfterEvaluate: " + project.getName()
        }
    }

}
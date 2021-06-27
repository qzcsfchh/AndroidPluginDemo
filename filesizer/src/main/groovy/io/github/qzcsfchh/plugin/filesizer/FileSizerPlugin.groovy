package io.github.qzcsfchh.plugin.filesizer

import org.gradle.api.Plugin
import org.gradle.api.Project

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


}
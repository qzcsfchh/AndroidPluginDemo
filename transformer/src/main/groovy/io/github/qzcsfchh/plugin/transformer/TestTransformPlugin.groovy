package io.github.qzcsfchh.plugin.transformer

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

public class TestTransformPlugin implements Plugin<Project>{
    public static final String GROUP = 'andfun'

    @Override
    void apply(Project project) {
        def transformer = project.extensions.create('transformer', Transformer)
        // registerTransform必须在评估完成前进行注册，afterEvaluate阶段注册是无效的
        def android = project.extensions.findByType(AppExtension);
        android.registerTransform(new TransformerTransform())
        project.afterEvaluate {
            project.logger.println transformer
        }

    }
}
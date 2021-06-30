package io.github.qzcsfchh.plugin.transformer

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Format
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project


class TransformerTransform extends Transform {

    private final Project mProject

    TransformerTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        /*
         * 1. 会出现在 app/build/intermediates/transforms 目录下；
         * 2. 在gradle tasks下面也能找到；
         */
        return "${TestTransformPlugin.GROUP}Transformer"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        /* 指定输入的类型：
        通过这里设定，可以指定我们要处理的文件类型，这样确保其他类型的文件不会传入 */
        /**
         * {@link TransformManager#CONTENT_CLASS}:表示需要处理 java 的 class 文件
         * {@link TransformManager#CONTENT_JARS}:表示需要处理 java 的 class 与 资源文件
         * {@link TransformManager#CONTENT_RESOURCES}:表示需要处理 java 的资源文件
         * {@link TransformManager#CONTENT_NATIVE_LIBS}:表示需要处理 native 库的代码。
         * {@link TransformManager#CONTENT_DEX}:表示需要处理 DEX 文件。
         * {@link TransformManager#CONTENT_DEX_WITH_RESOURCES}:表示需要处理 DEX 与 java 的资源文件。
         * */
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        /* 指定Transfrom的作用范围： */
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        /*
        如果返回 true，TransformInput 会包含一份修改的文件列表；
        如果返回 false，则会进行全量编译，并且会删除上一次的输出内容；
        */
        return false
    }


    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        /* 必须要重写该方法，哪怕什么都不做，都需要把上一个transform的输入完整地传递给下一个transform，否则apk将会是空的 */

        def startTime = System.currentTimeMillis()

        // Transform的inputs有两种类型：
        // 1.directoryInputs：目录，源码以及R.class、BuildConfig.class以及R$XXX.class等
        // 2.jarInputs：jar包，一般是第三方依赖库jar文件

        transformInvocation.inputs.forEach {
            it.directoryInputs.each {

                def dst = transformInvocation.outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                println "[directoryInputs]: input=${it.file.absolutePath}"
                println "[directoryInputs]: dst=${dst.getAbsolutePath()}"
                // todo 通常在这里做字节码的注入，这里我们什么都不做直接将输入拷贝到输出
                InjectUtil.inject(it.file.absolutePath, mProject)


                FileUtils.copyDirectory(it.file, dst)
            }
            it.jarInputs.each {
                def md5ForJar = DigestUtils.md5Hex(it.file.getAbsolutePath())
                def jarName = it.name.endsWith('.jar') ? it.name.substring(0, it.name.length() - 4) : it.name
                // 生成输出路径 + md5：重命名输出文件（同目录copyFile会冲突）
                // 生成输出路径 dest：./app/build/intermediates/transforms/xxxTransform/...
                def dst = transformInvocation.outputProvider.getContentLocation(jarName + md5ForJar, it.contentTypes, it.scopes, Format.JAR)
                println "[jarInputs]: input=${it.file.absolutePath}"
                println "[jarInputs]: dst=${dst.getAbsolutePath()}"
                //todo 这里执行字节码的注入，不操作字节码的话也要将输入路径拷贝到输出路径
                FileUtils.copyFile(it.file, dst)
            }
        }


        println "--------------- TransformerTransform visit end, cost${(System.currentTimeMillis() - startTime) / 1000} ---------------"
    }
}
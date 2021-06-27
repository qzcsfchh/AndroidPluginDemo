package io.github.qzcsfchh.plugin.filesizer

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class FileSizerTask extends DefaultTask {
    private Set<File> paths
    private final List<RecordItem> codeRecordItems = new ArrayList<>()
    private final List<RecordItem> resRecordItems = new ArrayList<>()
    private final isWindows = org.gradle.internal.os.OperatingSystem.current().isWindows()

    void addPaths(Set<File> paths) {
        this.paths = paths
    }

    @TaskAction
    def start() {
        codeRecordItems.clear()
        resRecordItems.clear()

        if (paths != null && !paths.isEmpty()) {
            paths.forEach { f ->
                calcFileSizeInDir(f)
            }
        }

        printResult()
    }

    void printResult() {
        File dstFile = new File("${project.buildDir.path}/${FileSizerPlugin.GROUP}/filesizer.json")
        if (!dstFile.exists()) {
            if (!dstFile.parentFile.exists()) {
                dstFile.parentFile.mkdirs()
            }
            dstFile.createNewFile()
        }
        JsonObject json = new JsonObject()
        if (!codeRecordItems.isEmpty()) {
            def array = new JsonArray()
            for (RecordItem item : codeRecordItems) {
                array.add(item.toJson())
            }
            json.add("codeRecordItems", array)
        }
        if (!resRecordItems.isEmpty()) {
            def array = new JsonArray()
            for (RecordItem item : resRecordItems) {
                array.add(item.toJson())
            }
            json.add("resRecordItems", array)
        }


        BufferedWriter writer = null
        try {
            writer = new BufferedWriter(new FileWriter(dstFile))
            writer.write(json.toString())
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            if (writer != null)
                writer.close()
        }
    }

    void calcFileSizeInDir(File dir) {
        def files = dir.listFiles()
        for (File file : files) {
            if (file.isFile()) {
                if (isResource(file)) {
                    resRecordItems.add(new RecordItem(file.length(), file.path))
                } else {
                    codeRecordItems.add(new RecordItem(file.length(), file.path))
                }
            } else {
                calcFileSizeInDir(file)
            }
        }
    }

    boolean isResource(File file) {
        return isWindows?file.path.contains('main\\res'):file.path.contains('main/res')
    }

}
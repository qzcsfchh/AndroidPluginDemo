package io.github.qzcsfchh.plugin.filesizer

class FileSizer {
    boolean includeCode = true
    boolean includeResource = true
    boolean enableBuildLog = false


    @Override
    public String toString() {
        return "FileSizer{" +
                "includeCode=" + includeCode +
                ", includeResource=" + includeResource +
                ", enableBuildLog=" + enableBuildLog +
                '}';
    }
}
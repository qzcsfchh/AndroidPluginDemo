package io.github.qzcsfchh.plugin.filesizer

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

class RecordItem {
    long size
    String path

    RecordItem(long size, String path) {
        this.size = size
        this.path = path
    }

    JsonObject toJson() {
        def json = new JsonObject()
        json.add("size", new JsonPrimitive(size))
        json.add("path", new JsonPrimitive(path))
        return json
    }
}
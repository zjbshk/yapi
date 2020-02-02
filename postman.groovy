import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject

def dealJSON(exportStr, JsonElement jsonElement) {
    def postMan = new PostMan()

    def info = new CollectionInfo()
    postMan.info = info
    info.name = "未命名"

    postMan.item = new ArrayList<>()

    def jsonArray = jsonElement.getAsJsonArray()

    for (classification in jsonArray) {
        assert classification instanceof JsonObject
        def itemList = classification.get("list").getAsJsonArray()
        for (itemRequest in itemList) {
            assert itemRequest instanceof JsonObject

            def item = new Item()
            postMan.item.add(item)

            item.name = itemRequest.get("title").getAsString()

            Request request = new Request()
            item.request = request

            request.method = itemRequest.get("method").getAsString()

            URLContent url = new URLContent()
            request.url = url

            def path = itemRequest.get("path").getAsString()
            url.raw = path
            url.host = Arrays.asList("{{host}}")
            url.path = Arrays.asList(path)

            request.header = new ArrayList<>()

            def headParamList = itemRequest.get("req_headers").getAsJsonArray()
            for (headParamYapi in headParamList) {
                assert headParamYapi instanceof JsonObject
                HeadParam headParam = new HeadParam()
                headParam.key = headParamYapi.get("name").getAsString()
                headParam.value = headParamYapi.get("value")
                request.header.add(headParam)
            }
        }

    }

    def gson = new Gson()
    println gson.toJson(postMan)
}

class PostMan {
    CollectionInfo info
    List<Item> item
}

class CollectionInfo {
    String _postman_id
    String name
    String schema = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
}

class Item {
    String name
    Request request
}

class Request {
    String method
    List<HeadParam> header
    BodyContent body
    URLContent url
    List response
}

class BodyContent {
    String mode
    List<Param> urlencoded
}

class URLContent {
    String raw
    List<String> host
    List<String> path
}

class Param {
    String key
    String value
    String type
}

class HeadParam {
    String key
//    String name
    String value
//    String type
}
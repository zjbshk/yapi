import com.google.gson.JsonElement
import com.google.gson.JsonObject

def dealJSON(exportStr, JsonElement jsonElement) {
    def postMan = new PostMan()
    postMan.info.name = "未命名"

    def jsonArray = jsonElement.getAsJsonArray()

    for (classification in jsonArray) {
        assert classification instanceof JsonObject
        def itemList = classification.get("list").getAsJsonArray()
        for (itemRequest in itemList) {
            assert itemRequest instanceof JsonObject
            def item = new Item()
            item.name = itemRequest.get("title").getAsString()

            Request request = new Request()
            item.request = request

            request.method = itemRequest.get("method").getAsString()

            URLContent url = new URLContent()
            request.url = url

            url.raw = itemRequest.get("path").getAsString()

            request.header = new ArrayList<>()

            def headParamList = itemRequest.get("req_headers").getAsJsonArray()
            for (headParamYapi in headParamList) {
                assert headParamYapi instanceof JsonObject
                HeadParam headParam = new HeadParam()
                headParam.name = headParamYapi.get("name").getAsString()
                headParam.value = headParamYapi.get("value")
                request.header.add(headParam)
            }



        }
    }

}

class PostMan {
    CollectionInfo info
    List<Item> item
}

class CollectionInfo {
    String _postman_id
    String name
    String schema
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
    String name
    String value
    String type
}
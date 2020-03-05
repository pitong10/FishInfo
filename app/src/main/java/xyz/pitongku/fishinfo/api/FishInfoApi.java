package xyz.pitongku.fishinfo.api;

public class FishInfoApi {

    String BASE_URL = "mobile.fishinfojatim.net/rest_json/";
    String getDataTable(String arrayName, String tableName, String filter, String values){
        return BASE_URL + arrayName + "/" + tableName + "?param="+ filter + "&data=" + values;
    }
}

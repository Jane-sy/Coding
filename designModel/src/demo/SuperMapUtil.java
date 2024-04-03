package demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * @author shiyou
 * @date 2023年03月30日 8:58
 * @description 仅处理最简单的地理数据格式
 */
public class SuperMapUtil {


    public static void main(String[] args) {
        String spjson = "{\"fieldNames\":[],\"geometry\":{\"parts\":[3],\"id\":\"b274b2bb-ecfe-4a93-a176-1ee97460cfda\",\"type\":\"REGION\",\"partTopo\":[1],\"points\":[{\"X\":118.81949470383023,\"Y\":32.04630783081993},{\"X\":118.81881046968209,\"Y\":32.045279696012415},{\"X\":118.81955690693434,\"Y\":32.045411508812734},{\"X\":118.81949470383023,\"Y\":32.04630783081993}]},\"fieldValues\":[]}";
        System.out.println(toGeoJson(spjson));

        String geojson = "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"coordinates\":[[[118.81949470383023,32.04630783081993],[118.81881046968209,32.045279696012415],[118.81955690693434,32.045411508812734],[118.81949470383023,32.04630783081993]]],\"type\":\"Polygon\"}}";
        System.out.println(toSimpleJson(geojson));
    }

    /**
     * 转换为GeoJson
     * 支持feature
     *
     * @param superJson 超图json
     * @return {@link String}
     */
    public static String toGeoJson(String superJson) {
        // 定义返回的GeoJson结构
        JSONObject feature = JSONObject.parseObject("{\"geometry\":{\"coordinates\":[],\"type\":\"Polygon\"},\"type\":\"Feature\",\"properties\":{}}");
        // 解析传入的超图json
        JSONObject superGeo = JSON.parseObject(superJson);

        // 处理属性
        JSONArray fields = superGeo.getJSONArray(GeoTag.FIELD_NAMES.tag);
        JSONArray values = superGeo.getJSONArray(GeoTag.FIELD_VALUES.tag);
        JSONObject properties = new JSONObject();
        for (int i = 0; i < fields.size(); i++) {
            properties.put(fields.getString(i), values.get(i));
        }
        properties.put(GeoTag.NAME.tag, "EPSG:4490");
        // 整合结果
        feature.put(GeoTag.TYPE.tag, GeoTag.FEATURE.tag);
        feature.put(GeoTag.PROPERTIES.tag, properties);
        feature.put(GeoTag.GEOMETRY.tag, toGeoJson(superGeo.getJSONObject(GeoTag.GEOMETRY.tag)));

        return JSON.toJSONString(feature);
    }

    /**
     * 转换为超图json
     * 支持feature和geometry
     *
     * @param geoJson GeoJson
     * @return {@link String}
     */
    public static String toSimpleJson(String geoJson) {
        // 定义结果
        JSONObject feature = JSONObject.parseObject("{\"fieldNames\":[],\"fieldValues\":[],\"geometry\":{}}");
        // 解析geoJson
        JSONObject geo = JSONObject.parseObject(geoJson);

        // 如果包含geometry字段认为是feature格式，否则看作geometry格式
        if (geo.containsKey(GeoTag.GEOMETRY.tag)) {
            // 处理属性信息
            JSONObject properties = geo.getJSONObject(GeoTag.PROPERTIES.tag);
            if (!properties.isEmpty()) {
                feature.put(GeoTag.FIELD_NAMES.tag, properties.keySet());
                feature.put(GeoTag.FIELD_VALUES.tag, properties.values());
            }
            // 处理图形信息
            geo = geo.getJSONObject(GeoTag.GEOMETRY.tag);
        }
        feature.put(GeoTag.GEOMETRY.tag, toSimpleJson(geo));

        return JSON.toJSONString(feature);
    }

    /**
     * 转换为GeoJson（geometry层级）
     *
     * @param superJson 超级json
     * @return {@link JSONObject}
     */
    private static JSONObject toGeoJson(JSONObject superJson) {
        JSONObject geometry = new JSONObject();
        // 处理地理类型
        String type = superJson.getString(GeoTag.TYPE.tag);
        geometry.put(GeoTag.TYPE.tag, GeoType.getGeoJsonType(type));
        // 处理坐标点
        JSONArray points = superJson.getJSONArray(GeoTag.POINTS.tag);
        switch (type) {
            case "POINT":
            case "LINE":
                geometry.put(GeoTag.COORDINATES.tag, toCoordinates(points));
                break;
            case "REGION":
                geometry.put(GeoTag.COORDINATES.tag, Collections.singletonList(toCoordinates(points)));
                break;
        }

        return geometry;
    }

    /**
     * 超图坐标点转为GeoJson坐标点
     *
     * @param points 点集
     * @return {@link JSONArray}
     */
    private static JSONArray toCoordinates(JSONArray points) {
        JSONArray coordinates = new JSONArray();

        for (int i = 0; i < points.size(); i++) {
            JSONObject point = points.getJSONObject(i);
            coordinates.add(i, Arrays.asList(point.getDoubleValue("X"), point.getDoubleValue("Y")));
        }

        return coordinates;
    }

    /**
     * 转换为SimpleJson（geometry层级）
     *
     * @param geoJson geo json
     * @return {@link JSONObject}
     */
    private static JSONObject toSimpleJson(JSONObject geoJson) {
        JSONObject result = new JSONObject();
        // 处理地理类型
        String type = geoJson.getString(GeoTag.TYPE.tag);
        result.put(GeoTag.TYPE.tag, GeoType.getSuperMapType(type));
        // 处理坐标点
        JSONArray coordinates = geoJson.getJSONArray(GeoTag.COORDINATES.tag);
        switch (type) {
            case "MultiPoint":
            case "LineString":
                result.put(GeoTag.PARTS.tag, Collections.singletonList(coordinates.size()));
                break;
            case "Polygon":
                coordinates = coordinates.getJSONArray(0);
                result.put(GeoTag.PARTS.tag, Collections.singletonList(coordinates.size() - 1));
                break;
        }
        result.put(GeoTag.POINTS.tag, toPoints(coordinates));
        // 处理其它信息
        result.put(GeoTag.PART_TOPO.tag, Collections.singletonList(1));
        result.put(GeoTag.STYLE.tag, null);
        result.put(GeoTag.ID.tag, UUID.randomUUID());

        return result;
    }

    /**
     * 转换为超图json坐标点
     *
     * @param coordinates 坐标
     * @return {@link JSONArray}
     */
    private static JSONArray toPoints(JSONArray coordinates) {
        JSONArray points = new JSONArray();

        for (int i = 0; i < coordinates.size(); i++) {
            JSONObject point = new JSONObject();
            point.put("X", coordinates.getJSONArray(i).getDoubleValue(0));
            point.put("Y", coordinates.getJSONArray(i).getDoubleValue(1));
            points.add(i, point);
        }

        return points;
    }

    /**
     * 标记枚举
     *
     * @author gt
     * @date 2023/03/30
     */
    enum GeoTag {
        TYPE("type"), FEATURES("features"), GEOMETRY("geometry"),
        POINTS("points"), FIELD_NAMES("fieldNames"), FIELD_VALUES("fieldValues"), PARTS("parts"), PART_TOPO("partTopo"),
        STYLE("style"), ID("id"),
        COORDINATES("coordinates"), PROPERTIES("properties"), FEATURE("Feature"), NAME("name");

        private final String tag;

        GeoTag(String tag) {
            this.tag = tag;
        }
    }

    /**
     * 地理类型（仅点线面基础类型）
     *
     * @author gt
     * @date 2023/03/30
     */
    enum GeoType {
        POINT("MultiPoint"), LINE("LineString"), REGION("Polygon");

        private final String type;

        GeoType(String type) {
            this.type = type;
        }

        /**
         * 根据geoJson地理类型获取超图地理类型
         *
         * @param type 类型
         * @return {@link String}
         */
        public static String getSuperMapType(String type) {
            for (GeoType geoType : GeoType.values()) {
                if (geoType.type.equals(type)) {
                    return geoType.name();
                }
            }
            return null;
        }

        /**
         * 根据超图地理类型获取geoJson地理类型
         *
         * @param type 类型
         * @return {@link String}
         */
        public static String getGeoJsonType(String type) {
            for (GeoType geoType : GeoType.values()) {
                if (geoType.name().equals(type)) {
                    return geoType.type;
                }
            }
            return null;
        }
    }
}

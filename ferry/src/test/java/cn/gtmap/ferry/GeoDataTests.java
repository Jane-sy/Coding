package cn.gtmap.ferry;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geojson.GeoJSON;
import org.geotools.geojson.GeoJSONUtil;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.json.simple.JSONArray;
import org.json.simple.JSONStreamAware;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.GeometryFixer;
import org.locationtech.jts.noding.Noder;
import org.locationtech.jts.noding.ValidatingNoder;
import org.locationtech.jts.noding.snap.SnappingNoder;
import org.locationtech.jts.operation.overlayng.OverlayNG;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.*;
import java.util.*;

/**
 * @author ShiYou
 * @date 2024年01月24日 14:11
 * Description:
 */
@Slf4j
public class GeoDataTests {

    @Test
    public void geoToolsTest() throws IOException {
        // 开始时间
        long start = System.currentTimeMillis();

        // 输入geometry
//        List<Geometry> inGeometry = getGeometryList();
        List<Geometry> inGeometry = readJSON();

        List<Geometry> outGeometry = new ArrayList<>();
        for (Geometry geometry : inGeometry) {
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                outGeometry.addAll(intersection(geometry.getGeometryN(i)));
            }
        }

        // 结果转化为GeoJSON
        if (outGeometry.size() > 0) {
            List<SimpleFeature> simpleFeatureList = new ArrayList<>();
            for (Geometry out : outGeometry) {
                simpleFeatureList.add(geo2FC(out, null));
//                log.info("xxx:{}", toGeoJSON(out));
            }
            DefaultFeatureCollection defaultFeatureCollection = list2FC(simpleFeatureList);
//            String s = toGeoJSON(defaultFeatureCollection);

            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            List<Map> features = new ArrayList<>();
            for (Geometry geometry : outGeometry) {
                Map temp = new HashMap();
                temp.put("type", "Feature");
                String s = toGeoJSON(geometry);
                temp.put("geometry", JSON.parseObject(s, Map.class));
                features.add(temp);
            }

            ReferencedEnvelope bounds = defaultFeatureCollection.getBounds();

            result.put("type", "FeatureCollection");
            result.put("bbox", new JSONStreamAware() {
                public void writeJSONString(Writer out) throws IOException {
                    JSONArray.writeJSONString(Arrays.asList(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()), out);
                }
            });
            result.put("features", features);
            OutputStream os = new ByteArrayOutputStream();
            GeoJSONUtil.encode(result, os);

            log.info("叠加结果：{}", os.toString());
        }

        // 结束时间
        long end = System.currentTimeMillis();
        log.info("分析耗时：{}ms", end - start);
    }

    /**
     * 读取Shp文件
     */
    private List<Geometry> getGeometryList() throws IOException {
        List<Geometry> geometryList = new ArrayList<>();
        File file = new File("F:\\1.shp");
//        File file = new File("F:\\cs\\养老设施独立用地0711\\中心城区独立占地机构养老设施（其中湘雅为配建，不占用地）.shp");
        Map<String, Object> map = new HashMap<>();
        map.put("url", file.toURI().toURL());

        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> source =
                dataStore.getFeatureSource(typeName);

        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures();
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                Geometry defaultGeometry = (Geometry) feature.getDefaultGeometry();
//                defaultGeometry = TopologyPreservingSimplifier.simplify(defaultGeometry, 0.05);
//                defaultGeometry = getPolygonByInteriorPoint(defaultGeometry);
                geometryList.add(defaultGeometry);
            }
        }

        dataStore.dispose();

        return geometryList;
    }

    /**
     * 转SimpleFeature
     *
     * @param geometry
     * @param crs
     * @return
     */
    private SimpleFeature geo2FC(Geometry geometry, CoordinateReferenceSystem crs) {
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName("Feature");
        typeBuilder.setCRS(crs);
        typeBuilder.add("geometry", Geometry.class);
        SimpleFeatureType simpleFeatureType = typeBuilder.buildFeatureType();

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(simpleFeatureType);
        builder.add(geometry);
        return builder.buildFeature(IdUtil.nanoId());
    }

    /**
     * 转FeatureCollection
     *
     * @param simpleFeatures
     * @return
     */
    private DefaultFeatureCollection list2FC(List<SimpleFeature> simpleFeatures) {
        DefaultFeatureCollection defaultFeatureCollection = new DefaultFeatureCollection();
        defaultFeatureCollection.addAll(simpleFeatures);
        return defaultFeatureCollection;
    }

    /**
     * 转geojson
     *
     * @param o
     * @return
     */
    private String toGeoJSON(Object o) {
        /*
         * 这种方式会有精度丢失问题
         * 原因：geoTools中默认GeometryJSON的大数精度为4，这种方式不能进行修改
         * */
        try (OutputStream os = new ByteArrayOutputStream()) {
            GeoJSON.write(o, os);
            return os.toString();
        } catch (IOException e) {
            log.error("转GeoJSON出错：{}", e.getMessage());
        } catch (NullPointerException e) {
            log.error("数据为空：{}", e.getMessage());
        }

        return "";
    }

    /**
     * 转geojson
     *
     * @param o
     * @return
     */
    private String toGeoJSON(Geometry o) {
        try (OutputStream os = new ByteArrayOutputStream()) {
            GeometryJSON geometryJson = new GeometryJSON(14);
            geometryJson.write(o, os);
            return os.toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 叠加
     *
     * @param geometry
     * @return
     * @throws IOException
     */
    private List<Geometry> intersection(Geometry geometry) throws IOException {
        // 连接参数
        Map<String, Object> params = new HashMap<>();
        params.put("dbtype", "postgis");
        params.put("host", "192.168.50.22");
        params.put("port", "5432");
        params.put("schema", "public");
        params.put("database", "glsj");
        params.put("user", "postgres");
        params.put("passwd", "gtis");
        params.put("preparedStatements", true);
        params.put("encode functions", true);

        // 连接数据库
        DataStore dataStore = DataStoreFinder.getDataStore(params);

        // 打印所有表名
//        String[] typeNames = dataStore.getTypeNames();
//        log.info("所有表名：{}", Arrays.asList(typeNames).toString());

        // 获取指定表
        SimpleFeatureSource featureSource = dataStore.getFeatureSource("zxcqydght_2023");

        // 设置查询语句
        Filter filter;
        try {
            String geoField = featureSource.getSchema().getGeometryDescriptor().getLocalName();
            String wkt = geometry.toText();
            filter = ECQL.toFilter(String.format("INTERSECTS(%s, %s)", geoField, wkt));
        } catch (CQLException e) {
            log.error("构建查询语句失败：{}", e.getMessage());
            return new ArrayList<>();
        }

        Query query = new Query(featureSource.getName().getLocalPart());
        query.setFilter(filter);

        // 获取数据
        SimpleFeatureCollection features = featureSource.getFeatures(query);

        long start = System.currentTimeMillis();
        String s = toGeoJSON(features);
        long end = System.currentTimeMillis();
        log.info("格式转换耗时：{}", end - start);

        List<Geometry> outGeometry = new ArrayList<>();
        SimpleFeatureIterator sft = features.features();

        geometry = transform(geometry);

        while (sft.hasNext()) {
            SimpleFeature next = sft.next();
            Geometry defaultGeometry = (Geometry) next.getDefaultGeometry();
//            defaultGeometry = TopologyPreservingSimplifier.simplify(defaultGeometry, 0.05);
//            defaultGeometry = getPolygonByInteriorPoint(defaultGeometry);
//            Geometry intersection = geometry.intersection(defaultGeometry);
            for (int i = 0; i < defaultGeometry.getNumGeometries(); i++) {
                Geometry geometryN = defaultGeometry.getGeometryN(i);

                try {
                    geometryN = transform(geometryN);

//                    OverlayNG overlayNG = new OverlayNG(geometry, geometryN, null, OverlayNG.INTERSECTION);
//                    overlayNG.setStrictMode(true);
//                    Geometry intersection = overlayNG.getResult();

                    // 设置节点最小距离，单位一般是米（需转为投影坐标系）
                    Geometry intersection = OverlayNG.overlay(geometryN, geometry, OverlayNG.INTERSECTION, getNoder(0.001));
//                    Geometry intersection = geometry.intersection(geometryN);

                    if (!intersection.isEmpty()) {
                        outGeometry.add(intersection);
                    }
                } catch (TopologyException e) {
//                    geometryN = fix(geometryN);
//                    Geometry intersection = OverlayNG.overlay(geometry, geometryN, OverlayNG.INTERSECTION, new ValidatingNoder(snapNoder));
//                    if (!intersection.isEmpty()) {
//                        outGeometry.add(intersection);
//                    }
                }
            }
        }
        dataStore.dispose();

        return outGeometry;
    }

    private Geometry getPolygonByInteriorPoint(Geometry geometry) {
        List<Polygon> polygons = new ArrayList<>();

        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) geometry.getGeometryN(i);
            LineString exteriorRing = geometry.getFactory().createLinearRing();
            Coordinate[] coordinates = exteriorRing.union(exteriorRing).getCoordinates();
            polygons.add(polygon.getFactory().createPolygon(coordinates));
        }

        return geometry.getFactory().createMultiPolygon(polygons.toArray(new Polygon[]{}));
    }

    /**
     * 拓扑修复
     *
     * @param geometry
     * @return
     */
    private Geometry fix(Geometry geometry) {
        GeometryFixer geometryFixer = new GeometryFixer(geometry);
        geometryFixer.setKeepMulti(true);
        return geometryFixer.getResult();
    }

    /**
     * 坐标系转换
     *
     * @param geometry
     * @return
     */
    private Geometry transform(Geometry geometry) {

        try {
            CoordinateReferenceSystem crsSource = CRS.decode("EPSG:4490", true);
//            CoordinateReferenceSystem crsTarget = CRS.decode("EPSG:4490", true);
            CoordinateReferenceSystem crsTarget = CRS.decode("EPSG:4528", true);
            MathTransform transform = CRS.findMathTransform(crsSource, crsTarget, true);

            return JTS.transform(geometry, transform);
        } catch (Exception e) {
            return geometry;
        }
    }

    /**
     * 读取geojson
     *
     * @return
     * @throws IOException
     */
    private List<Geometry> readJSON() throws IOException {
        List<Geometry> result = new ArrayList<>();

        String json = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"coordinates\":[[[111.67623580829274,29.064372369120576],[111.67623580829274,29.06235971899264],[111.67800066361991,29.06235971899264],[111.67800066361991,29.064372369120576],[111.67623580829274,29.064372369120576]]],\"type\":\"Polygon\"}}]}";
        FeatureJSON featureJSON = new FeatureJSON();
        FeatureCollection featureCollection = featureJSON.readFeatureCollection(json);
        FeatureIterator features = featureCollection.features();
        while (features.hasNext()) {
            SimpleFeature next = (SimpleFeature) features.next();
            Geometry defaultGeometry = (Geometry) next.getDefaultGeometry();
            result.add(defaultGeometry);
        }

        return result;
    }

    private Noder getNoder(double tolerance) {
        SnappingNoder snapNoder = new SnappingNoder(tolerance);
        return new ValidatingNoder(snapNoder);
    }
}

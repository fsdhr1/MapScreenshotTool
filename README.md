# MapScreenshotTool 使用示例 源码在master分支
 
## 根据WKT或GeoJSON格式的地图坐标及网片影像源生成地图截图

# MapScreenshotTool Usage Example

## Generating Map Screenshots Based on WKT or GeoJSON Coordinates and Tile Image Source

### Example Code

```java
// Import necessary classes and packages (actual import statements omitted for brevity, add as needed)

// Define WKT-formatted geometry data
String wkt = "POLYGON((106.241577 28.305915, 106.24154 28.305932, ..., 106.241577 28.305915))";

// Convert WKT string to Geometry object
Geometry geometry = Transformation.wkt2BoxGeometry(wkt);

// Create a Feature object from the Geometry object
Feature feature = Feature.fromGeometry(geometry);

// Create a list of Features and add the Feature object
List<Feature> features = new ArrayList<>();
features.add(feature);

// Set screenshot parameters
ScreenshotsSetting screenshotsSetting = new ScreenshotsSetting();
screenshotsSetting.setWidth(256);
screenshotsSetting.setHeight(256);
screenshotsSetting.setMargin(100);
screenshotsSetting.setDrawRect(false);

// Call the createMapScreenshots method of MapScreenshotToolCGCS2000 to generate screenshots
// Note: featureCollection and waterList need to be initialized according to actual conditions, this is example code
List<Object> mapScreenshots = MapScreenshotToolCGCS2000.createMapScreenshots(
    featureCollection.toJson(), 
    waterList, 
    "https://t0.tianditu.gov.cn/DataServer?T=img_c&x={x}&y={y}&l={z}&tk=9859bd0c23cc35a037ced54d1e8a753f", 
    screenshotsSetting
);

// Get the generated screenshot (BufferedImage object)
BufferedImage finalImg = (BufferedImage) mapScreenshots.get(0);

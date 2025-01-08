package com.grandtech.tools;

/**
 * @program: MapScreenshot
 * @description:
 * @author: 冯帅
 * @create: 2023-02-24 17:03
 **/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;

public class StringCompressor {

    public static void main(String[] args) throws IOException {
        String str = "{\"fldvals\":[{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"06\",\"fieldsort\":1,\"fieldvisible\":1,\"fldaliasname\":\"日期\",\"fldname\":\"date\",\"fldtype\":\"date\",\"fldval\":\"2023-02-23\",\"isInit\":true,\"notnull\":true,\"paths\":[],\"required\":1,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"07\",\"fieldsort\":2,\"fieldvisible\":1,\"fldaliasname\":\"日期时间\",\"fldname\":\"date_time\",\"fldtype\":\"timestamp(6) without time zone\",\"fldval\":null,\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"02\",\"fieldsort\":3,\"fieldvisible\":1,\"fldaliasname\":\"身份证号\",\"fldname\":\"shenfen_id\",\"fldtype\":\"character varying(255)\",\"fldval\":null,\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"04\",\"fieldsort\":4,\"fieldvisible\":1,\"fldaliasname\":\"手机号\",\"fldname\":\"phone\",\"fldtype\":\"character varying(255)\",\"fldval\":\"15998897560\",\"isInit\":true,\"notnull\":true,\"paths\":[],\"required\":1,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"10\",\"fieldsort\":5,\"fieldvisible\":1,\"fldaliasname\":\"数量\",\"fldname\":\"num\",\"fldtype\":\"integer\",\"fldval\":\"1\",\"isInit\":true,\"notnull\":true,\"paths\":[],\"required\":1,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"11\",\"fieldsort\":6,\"fieldvisible\":1,\"fldaliasname\":\"重量\",\"fldname\":\"zhongliang\",\"fldtype\":\"numeric(38,2)\",\"fldval\":null,\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"03\",\"fieldsort\":7,\"fieldvisible\":1,\"fldaliasname\":\"银行卡号\",\"fldname\":\"bank_id\",\"fldtype\":\"character varying(255)\",\"fldval\":null,\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"08\",\"fieldsort\":8,\"fieldvisible\":1,\"fldaliasname\":\"作物\",\"fldname\":\"zuowu\",\"fldtype\":\"character varying(255)\",\"fldval\":\"玉米\",\"isInit\":true,\"notnull\":true,\"paths\":[],\"required\":1,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"05\",\"fieldsort\":9,\"fieldvisible\":1,\"fldaliasname\":\"面积\",\"fldname\":\"mj\",\"fldtype\":\"numeric(38,2)\",\"fldval\":\"37.25\",\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"01\",\"fieldsort\":10,\"fieldvisible\":1,\"fldaliasname\":\"现场拍照\",\"fldname\":\"photo\",\"fldtype\":\"text\",\"fldval\":\"[]\",\"isInit\":false,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"12\",\"fieldsort\":11,\"fieldvisible\":1,\"fldaliasname\":\"电子签名\",\"fldname\":\"sign\",\"fldtype\":\"character varying(255)\",\"fldval\":null,\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_ bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":\"09\",\"fieldsort\":12,\"fieldvisible\":1,\"fldaliasname\":\"说明\",\"fldname\":\"shuoming\",\"fldtype\":\"text\",\"fldval\":null,\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":null,\"fieldsort\":0,\"fieldvisible\":0,\"fldaliasname\":\"\",\"fldname\":\"shape\",\"fldtype\":\"geometry(Geometry,4326)\",\"fldval\":\"POLYGON ((106.5757701 37.4685196, 106.575917 37.4669656, 106.5774637 37.4672298, 106.5775322 37.4687216, 106.5757701 37.4685196))\",\"isInit\":true,\"notnull\":true,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":null,\"fieldsort\":0,\"fieldvisible\":0,\"fldaliasname\":\"\",\"fldname\":\"qhdm\",\"fldtype\":\"character varying(255)\",\"fldval\":\"640303101220\",\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":null,\"fieldsort\":0,\"fieldvisible\":0,\"fldaliasname\":\"\",\"fldname\":\"qhmc\",\"fldtype\":\"character varying(255)\",\"fldval\":\"小泉村\",\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":null,\"fieldsort\":0,\"fieldvisible\":0,\"fldaliasname\":\"\",\"fldname\":\"objectid\",\"fldtype\":\"integer\",\"fldval\":null,\"isInit\":true,\"notnull\":true,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":null,\"fieldsort\":0,\"fieldvisible\":0,\"fldaliasname\":\"\",\"fldname\":\"username\",\"fldtype\":\"character varying(255)\",\"fldval\":\"test_nx0223\",\"isInit\":true,\"notnull\":true,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":null,\"fieldsort\":0,\"fieldvisible\":0,\"fldaliasname\":\"\",\"fldname\":\"task_id\",\"fldtype\":\"integer\",\"fldval\":\"71\",\"isInit\":true,\"notnull\":true,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"},{\"dicBeans\":null,\"dicname\":null,\"fieldextendkey\":null,\"fieldsort\":0,\"fieldvisible\":0,\"fldaliasname\":\"\",\"fldname\":\"from_id\",\"fldtype\":\"character varying(255)\",\"fldval\":null,\"isInit\":true,\"notnull\":false,\"paths\":[],\"required\":0,\"signPath\":null,\"tablename\":\"test_bthc20230223\"}],\"tableId\":\"67\"}";
        byte[] compressed = compress(str);
        String decompressed = decompress(compressed);
        System.out.println("Original: " + str);
        System.out.println("Compressed: " + new String(compressed, StandardCharsets.ISO_8859_1));
        System.out.println("Decompressed: " + decompressed);
    }

    public static byte[] compress(String str) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return baos.toByteArray();
    }

    public static String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
        GZIPInputStream gzip = new GZIPInputStream(bais);
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        while ((len = gzip.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        gzip.close();
        return baos.toString(String.valueOf(StandardCharsets.UTF_8));
    }
}


package com.huilian.hanzi2pinyin;

import com.huilian.Utils;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源文件加载类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 */
public final class PinyinResource {

    private PinyinResource() {
    }

    protected static Reader newClassPathReader(String classpath) {
        InputStream is = PinyinResource.class.getResourceAsStream(classpath);
        try {
            return new InputStreamReader(is, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    protected static Reader newFileReader(String path) throws FileNotFoundException {
        try {
            return new InputStreamReader(new FileInputStream(path), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    protected static Map<String, String> getResource(Reader reader) {
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        try {
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("=");
                map.put(tokens[0], tokens[1]);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    protected static Map<String, String> getPinyinResource() {
        return getResource(newClassPathReader("/com/huilian/hanzi2pinyin_data/pinyin.dict"));
    }

    protected static Map<String, String> getMutilPinyinResource() {
        return getResource(newClassPathReader("/com/huilian/hanzi2pinyin_data/mutil_pinyin.dict"));
    }

    protected static Map<String, String> getChineseResource() {
        return getResource(newClassPathReader("/com/huilian/hanzi2pinyin_data/chinese.dict"));
    }
}

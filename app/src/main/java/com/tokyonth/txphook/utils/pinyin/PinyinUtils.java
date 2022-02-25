package com.tokyonth.txphook.utils.pinyin;

import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;

public class PinyinUtils {

    private static final String DEFAULT_GROUP_NAME = "#";

    /**
     * 获取字符串对应的拼音字符串，分两个步骤
     * 一、先对每个字符进行处理，规则：
     * 1、中文字符，返回对应的大写的拼音
     * 2、字母，转换成大写字母
     * 3、其他，不作处理
     * 二、判断字符串的首个字符是否是字母或汉字，如果是，不做处理，否则在第一步转换后的结果前面添加
     * 一个Unicode值比字母大的任意一个字符（保证compare排序的时候位置在字母后面）
     */
    public static String getPinyin(String str) {
        StringBuilder resultSb = new StringBuilder();
        if (!TextUtils.isEmpty(str)) {
            for (char item : str.trim().toCharArray()) {
                String itemStr = Character.toString(item);
                if (isChinese(itemStr)) {
                    resultSb.append(Pinyin.toPinyin(item).toUpperCase());
                } else if (isLetter(itemStr)) {
                    resultSb.append(Character.toString(item).toUpperCase());
                } else {
                    resultSb.append(Character.toString(item));
                }
            }
        }
        if (DEFAULT_GROUP_NAME.equals(getLetter(str))) {
            resultSb.insert(0, "~");
        }
        return resultSb.toString();
    }

    /**
     * 判断是否是字母
     */
    private static boolean isLetter(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String regex = "^[a-zA-Z]+$";
        return str.matches(regex);
    }

    /**
     * 判断是否是中文
     */
    private static boolean isChinese(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String regex = "^[\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }

    /**
     * 获取字符串对应的groupName
     * 规则：
     * 1、字母直接转换成大写字母
     * 2、中文转换成拼音并返回拼音首个字符对应的大写字母
     * 3、其他返回#
     */
    public static String getLetter(String name) {
        String result = DEFAULT_GROUP_NAME;
        if (!TextUtils.isEmpty(name)) {
            String firstNameLetter = String.valueOf(name.charAt(0));
            if (isLetter(firstNameLetter)) {
                result = firstNameLetter.toUpperCase();
            } else if (isChinese(firstNameLetter)) {
                result = String.valueOf(Pinyin.toPinyin(firstNameLetter.charAt(0)).charAt(0))
                        .toUpperCase();
            }
        }
        return result;
    }

}

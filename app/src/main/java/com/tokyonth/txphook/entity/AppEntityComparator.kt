package com.tokyonth.txphook.entity;

import com.tokyonth.txphook.utils.pinyin.PinyinUtils;

import java.util.Comparator;

public class AppEntityComparator implements Comparator<AppEntity> {

    @Override
    public int compare(AppEntity o1, AppEntity o2) {
        String pinYin1 = PinyinUtils.getPinyin(o1.getAppName());
        String pinYin2 = PinyinUtils.getPinyin(o2.getAppName());
        return pinYin1.compareTo(pinYin2);
    }

}

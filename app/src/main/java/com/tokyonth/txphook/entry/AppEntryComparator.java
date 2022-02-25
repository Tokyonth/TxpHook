package com.tokyonth.txphook.entry;

import com.tokyonth.txphook.utils.pinyin.PinyinUtils;

import java.util.Comparator;

public class AppEntryComparator implements Comparator<AppEntry> {

    @Override
    public int compare(AppEntry o1, AppEntry o2) {
        String pinYin1 = PinyinUtils.getPinyin(o1.getAppName());
        String pinYin2 = PinyinUtils.getPinyin(o2.getAppName());
        return pinYin1.compareTo(pinYin2);
    }

}

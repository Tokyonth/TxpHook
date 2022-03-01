package com.tokyonth.txphook.entity

import com.tokyonth.txphook.utils.pinyin.PinyinUtils
import java.util.Comparator

class AppEntityComparator : Comparator<AppEntity> {

    override fun compare(o1: AppEntity, o2: AppEntity): Int {
        val pinYin1 = PinyinUtils.getPinyin(o1.appName)
        val pinYin2 = PinyinUtils.getPinyin(o2.appName)
        return pinYin1.compareTo(pinYin2)
    }

}

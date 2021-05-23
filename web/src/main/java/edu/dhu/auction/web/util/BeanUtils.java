package edu.dhu.auction.web.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ReflectUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeanUtils {

    public static <S, T> void copy(S source, T target) {
        BeanUtil.copyProperties(source, target, CopyOptions.create().ignoreNullValue());
    }

    public static <S, T> T copy(S source, Class<T> clazz) {
        T target = ReflectUtil.newInstance(clazz);
        BeanUtil.copyProperties(source, target, CopyOptions.create().ignoreNullValue());
        return target;
    }

    public static <S, T> List<T> copyList(List<S> sources, Class<T> clazz) {
        List<T> targets = new ArrayList<>(sources.size());
        for (S source : sources) {
            T target = ReflectUtil.newInstance(clazz);
            BeanUtil.copyProperties(source, target, CopyOptions.create().ignoreNullValue());
            targets.add(target);
        }
        return targets;
    }

    public static <S, T> Set<T> copySet(Set<S> sources, Class<T> clazz) {
        Set<T> targets = new HashSet<>(sources.size());
        for (S source : sources) {
            T target = ReflectUtil.newInstance(clazz);
            BeanUtil.copyProperties(source, target, CopyOptions.create().ignoreNullValue());
            targets.add(target);
        }
        return targets;
    }
}

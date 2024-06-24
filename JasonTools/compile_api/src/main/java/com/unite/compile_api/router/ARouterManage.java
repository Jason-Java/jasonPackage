package com.unite.compile_api.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.unite.annotation.router.ARouterBean;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年04月12日
 */
public class ARouterManage {
    private static final String regex = "^[a-zA-Z]+(/[a-zA-Z]+)*$";
    private static final String classPrefix = "ARouterPath$$";
    //    private static volatile ARouterManage instance;
    private static volatile Map<String, IARouterPath> cacheMap = new HashMap<>();
    private String group;
    private String path;
    private int flag;

    private ARouterManage() {

    }

    public ARouterManage(String group, String path) {
        this.group = group;
        this.path = path;
    }

//    public static ARouterManage getInstance() {
//        if (instance == null) {
//            synchronized (ARouterManage.class) {
//                if (instance == null) {
//                    instance = new ARouterManage();
//                }
//            }
//        }
//        return instance;
//    }

    /**
     * 加载路由
     *
     * @param path
     */
    public static ARouterManage loadARouter(String path) {
        if (!path.matches(regex)) {
            throw new RuntimeException("路径不符合规范");
        }
        int index = path.indexOf('/');
        String group = path.substring(0, index);
        IARouterPath routerPath = cacheMap.get(group);
        if (routerPath == null) {
            String className = "com.unite.auto_generate." + classPrefix + group;
            try {
                Class clazz = Class.forName(className);
                routerPath = (IARouterPath) clazz.newInstance();
                cacheMap.put(group, routerPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ARouterManage(group, path);
    }

    public ARouterManage setIntentFlag(int flags) {
        this.flag = flags;
        return this;
    }

    public void navigation(Activity activity) {
        navigation(activity, null);
    }

    public void navigation(Activity activity, Bundle bundle) {
        IARouterPath routerPath = cacheMap.get(group);
        ARouterBean bean = routerPath.loadPath().get(path);
        if (bean == null) {
            throw new RuntimeException("未找到指定的路由（" + path + ")");
        }
        Intent intent = new Intent(activity, bean.getClazz());
        intent.setFlags(flag);
        if (bundle != null)
            intent.putExtras(bundle);
        activity.startActivity(intent);
    }


}

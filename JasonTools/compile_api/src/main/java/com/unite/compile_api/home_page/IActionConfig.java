package com.unite.compile_api.home_page;

import android.app.Activity;

/**
 * 导航栏配置类
 */
public interface IActionConfig {

    /**
     * 导航栏按钮有几个 范围1~4
     */
    int getNavigationBarButtonCount();

    /**
     * 获取第几个菜单被选中
     * @return
     */
    int getMenuSelected();

    /**
     * 获取导航栏第一个Button的icon图片资源
     *
     * @return
     */
    int getMenuOneIcn();

    /**
     * 获取导航栏第一个按钮的名字
     *
     * @return
     */
    String getMenuOneText();

    /**
     * 获取导航栏第二个Button的icon图片资源
     *
     * @return
     */
    int getMenuTwoIcn();

    /**
     * 获取导航栏第一个按钮的名字
     *
     * @return
     */
    String getMenuTwoText();

    /**
     * 获取导航栏第三个Button的icon图片资源
     *
     * @return
     */
    int getMenuThreeIcn();

    /**
     * 获取导航栏第一个按钮的名字
     *
     * @return
     */
    String getMenuThreeText();



    /**
     * 获取导航栏第四个Button的icon图片资源
     *
     * @return
     */
    int getMenuFourIcn();
    /**
     * 获取导航栏第四个个按钮的名字
     *
     * @return
     */
    String getMenuFourText();
    /**
     * 获取导航栏第五个Button的icon图片资源
     *
     * @return
     */
    int getMenuFiveIcn();
    /**
     * 获取导航栏第五个按钮的名字
     *
     * @return
     */
    String getMenuFiveText();
    /**
     * 获取第一个Fragment的名字
     *
     * @return
     */
    String getFirstFragmentName();

    /**
     * 获取第一个按钮的功能
     */
    void callMenuOneAction(Activity activity);

    /**
     * 获取第二个按钮的功能
     */
    void callMenuTwoAction(Activity activity);

    /**
     * 获取第三个按钮的功能
     */
    void callMenuThreeAction(Activity activity);
    /**
     * 获取第四个按钮的功能
     */
    void callMenuFourAction(Activity activity);
    /**
     * 获取第三个按钮的功能
     */
    void callMenuFiveAction(Activity activity);
}

package com.unite.annotation.router;

import javax.lang.model.element.Element;

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
public class ARouterBean {
    public ARouterBean() {

    }
    public ARouterBean(String path, String group, Class clazz) {
        this.path = path;
        this.group = group;
        this.clazz = clazz;
    }

    private String path;
    private String group;
    private Element element;
    private Class clazz;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}

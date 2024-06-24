package com.unite.compile_api.router;


import com.unite.annotation.router.ARouterBean;

import java.util.Map;

public interface IARouterPath {

    Map<String, ARouterBean> loadPath();
}

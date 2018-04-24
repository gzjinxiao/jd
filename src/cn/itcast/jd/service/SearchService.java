package src.cn.itcast.jd.service;

import src.cn.itcast.jd.po.Result;

public interface SearchService {

    Result searchProduct(String queryString, String catalog_name,
                         String price, Integer page, String sort);

}

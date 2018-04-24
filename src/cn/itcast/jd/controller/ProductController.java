package src.cn.itcast.jd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import src.cn.itcast.jd.po.Result;
import src.cn.itcast.jd.service.SearchService;

/**
 *
 */
@Controller
public class ProductController {
    @Autowired
    private SearchService searchService;
    @RequestMapping("/list.action")
    public String list(ModelMap modelMap, String queryString, String catalog_name,
                       String price, Integer page, String sort
    ){
        Result result=searchService.searchProduct(queryString, catalog_name,
                price, page, sort);
        modelMap.addAttribute("result", result);
        // 回显参数
        modelMap.addAttribute("queryString",queryString);
        modelMap.addAttribute("catalog_name",catalog_name);
        modelMap.addAttribute("price",price);
        modelMap.addAttribute("sort",sort);

        return "product_list";
    }
}

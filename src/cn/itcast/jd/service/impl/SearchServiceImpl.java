package src.cn.itcast.jd.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.cn.itcast.jd.po.Product;
import src.cn.itcast.jd.po.Result;
import src.cn.itcast.jd.service.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class SearchServiceImpl implements SearchService{
    @Autowired
    private HttpSolrServer httpSolrServer;
    public Result searchProduct(String queryString, String catalog_name, String price, Integer page, String sort) {
        SolrQuery sq = new SolrQuery();
        Result result = new Result();
        if (StringUtils.isNotBlank(queryString)){
            sq.setQuery(queryString);
        }else {
            sq.setQuery("*:*");
        }
        sq.set("df","product_keywords");
        if (StringUtils.isNotBlank(catalog_name)) {
            catalog_name = "catalog_name:" + catalog_name;
        }if (StringUtils.isNotBlank(price)){
            String[] split = price.split("-");
            price="price:"+"[ "+split[0]+"TO "+split[1]+" ]";
        }
        sq.setFilterQueries(catalog_name,price);
        if (page==null){
            page=1;//默认从第一页开始搜索
        }
        int pageSize=10;
        sq.setStart((page-1)*pageSize);
        sq.setRows(pageSize);
        //升降序
        if ("1".equals(sort)){
            sq.setSort("product_price", SolrQuery.ORDER.asc);
        }else {
            sq.setSort("product_price", SolrQuery.ORDER.desc);
        }
        sq.setHighlight(true);
        sq.addHighlightField("product_name");
        sq.setHighlightSimplePre("<font color='red'>");
        sq.setHighlightSimplePost("</font>");
        QueryResponse queryResponse=null;
        try {
           queryResponse = httpSolrServer.query(sq);
            SolrDocumentList resultList = queryResponse.getResults();
            Map <String, Map <String, List <String>>> highlighting = queryResponse.getHighlighting();

            result.setCurPage(page);
            int total= (int) resultList.getNumFound();
            int pageCount=0;
            if (total%pageSize==0){
                //页数
                pageCount=total/pageSize;
            }else {
                pageCount=total/pageSize+1;
            }
            result.setPageCount(pageCount);
            result.setRecordCount(total);
            //封装productList
            List<Product> productList=new ArrayList <Product>();
            for(SolrDocument doc:resultList){
                Product product= new Product();
                String pid=doc.get("id").toString();
                String pname="";
                List <String> list = highlighting.get(pid).get("product_name");
                if (list!=null&&list.size()>0){
                    pname=list.get(0);
                }else{
                    pname=doc.get("product_name").toString();
                }
                String ppicture = doc.get("product_picture").toString();
                String pprice = doc.get("product_price").toString();
                product.setName(pname);
                product.setPid(pid);
                product.setPicture(ppicture);
                product.setPrice(pprice);
                productList.add(product);

            }
                result.setProductList(productList);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        return result;

    }
}

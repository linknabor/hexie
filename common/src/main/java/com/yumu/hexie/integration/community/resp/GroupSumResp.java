package com.yumu.hexie.integration.community.resp;

import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-18 16:01
 */
public class GroupSumResp {

    private List<SearchVo> searchVoList; //汇总信息
    private ProductVo productVo; //商品集合

    public List<SearchVo> getSearchVoList() {
        return searchVoList;
    }

    public void setSearchVoList(List<SearchVo> searchVoList) {
        this.searchVoList = searchVoList;
    }

    public ProductVo getProductVo() {
        return productVo;
    }

    public void setProductVo(ProductVo productVo) {
        this.productVo = productVo;
    }

    @Override
    public String toString() {
        return "GroupOrderResp{" +
                "searchVoList=" + searchVoList +
                ", productVo=" + productVo +
                '}';
    }

    public static class ProductVo{
        private int totalNum; //总商品数
        private int verifyNum; //未核销数
        private List<Product> products; //商品列表

        public int getTotalNum() {
            return totalNum;
        }

        public void setTotalNum(int totalNum) {
            this.totalNum = totalNum;
        }

        public int getVerifyNum() {
            return verifyNum;
        }

        public void setVerifyNum(int verifyNum) {
            this.verifyNum = verifyNum;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        @Override
        public String toString() {
            return "ProductVo{" +
                    "totalNum='" + totalNum + '\'' +
                    ", verifyNum='" + verifyNum + '\'' +
                    ", products=" + products +
                    '}';
        }
    }

    public static class Product {
        private String id; //商品ID
        private String name; //商品名称
        private int num; //商品数量
        private int verify; //未核销数

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getVerify() {
            return verify;
        }

        public void setVerify(int verify) {
            this.verify = verify;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", num=" + num +
                    ", verify=" + verify +
                    '}';
        }
    }

    public static class SearchVo {
        private String name; //名称
        private String num; //值

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        @Override
        public String toString() {
            return "searchVo{" +
                    "name='" + name + '\'' +
                    ", num='" + num + '\'' +
                    '}';
        }
    }
}

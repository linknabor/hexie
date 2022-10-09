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
        return "GroupSumResp{" +
                "searchVoList=" + searchVoList +
                ", productVo=" + productVo +
                '}';
	}

	public static class ProductVo{
        private int totalNum; //总商品数
        private int verifyNum; //未核销数
        private List<GroupProductSumVo> products; //商品列表

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

        public List<GroupProductSumVo> getProducts() {
            return products;
        }

        public void setProducts(List<GroupProductSumVo> products) {
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

    //汇总筛选项
    public static class SearchVo {
        private String name; //名称
        private String num; //值
        private String message; //提示内容

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

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "SearchVo{" +
                    "name='" + name + '\'' +
                    ", num='" + num + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

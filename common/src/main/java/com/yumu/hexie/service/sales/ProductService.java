package com.yumu.hexie.service.sales;

import com.yumu.hexie.model.commonsupport.info.Product;


public interface ProductService {

    Product getProduct(long productId);

    void checkSalable(Product product, int count);

    void freezeCount(Product product, int count);

    void unfreezeCount(long productId, int count);

    void saledCount(long productId, int count);
}

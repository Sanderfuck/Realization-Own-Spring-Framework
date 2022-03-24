package com.sander;

import org.springframework.beans.factory.anotation.Autowired;
import org.springframework.beans.factory.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private PromotionsService promotionsService;

    public PromotionsService getPromotionsService() {
        return promotionsService;
    }

    public void setPromotionsService(PromotionsService promotionsService) {
        this.promotionsService = promotionsService;
    }
}

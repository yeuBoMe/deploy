package com.computerShop.demo1.domain.dto;

import java.util.List;
import java.util.Optional;

public class ProductCriteriaDTO {
    private Optional<String> pageOptional;
    private Optional<String> nameOptional;
    private Optional<String> sortOptional;
    private Optional<List<String>> listFactoryOptional;
    private Optional<List<String>> listPriceOptional;
    private Optional<List<String>> listTargetOptional;

    public Optional<String> getPageOptional() {
        return pageOptional;
    }

    public void setPageOptional(Optional<String> pageOptional) {
        this.pageOptional = pageOptional;
    }

    public Optional<String> getNameOptional() {
        return nameOptional;
    }

    public void setNameOptional(Optional<String> nameOptional) {
        this.nameOptional = nameOptional;
    }

    public Optional<String> getSortOptional() {
        return sortOptional;
    }

    public void setSortOptional(Optional<String> sortOptional) {
        this.sortOptional = sortOptional;
    }

    public Optional<List<String>> getListFactoryOptional() {
        return listFactoryOptional;
    }

    public void setListFactoryOptional(Optional<List<String>> listFactoryOptional) {
        this.listFactoryOptional = listFactoryOptional;
    }

    public Optional<List<String>> getListPriceOptional() {
        return listPriceOptional;
    }

    public void setListPriceOptional(Optional<List<String>> listPriceOptional) {
        this.listPriceOptional = listPriceOptional;
    }

    public Optional<List<String>> getListTargetOptional() {
        return listTargetOptional;
    }

    public void setListTargetOptional(Optional<List<String>> listTargetOptional) {
        this.listTargetOptional = listTargetOptional;
    }

}

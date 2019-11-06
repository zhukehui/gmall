package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.vo.SearchParamVO;
import com.atguigu.gmall.search.vo.SearchResponse;

/**
 * @author eternity
 * @create 2019-11-05 19:45
 */
public interface SearchService {
    SearchResponse search(SearchParamVO searchParamVO);
}

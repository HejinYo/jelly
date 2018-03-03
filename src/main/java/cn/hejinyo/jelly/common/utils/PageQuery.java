package cn.hejinyo.jelly.common.utils;

import lombok.Getter;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询参数
 */
@Getter
public class PageQuery extends HashMap<String, Object> {

    //当前页
    private static final String PAGENUM = "pageNum";
    //每页的数量
    private static final String PAGESIZE = "pageSize";
    //排序字段
    private static final String SIDX = "sidx";
    //排序方式
    private static final String SORT = "sort";

    //简单查询
    private static final String QUERY_KEY = "queryKey";
    private static final String QUERY_VALUE = "queryValue";

    private int pageNum;
    private int pageSize;
    private String order;

    private PageQuery() {
    }

    public static PageQuery build(Map<String, Object> pageParam, Map<String, Object> queryParam) {
        return init(pageParam, queryParam);
    }

    public static PageQuery build(Map<String, Object> pageParam) {
        return init(pageParam, null);
    }

    private static PageQuery init(Map<String, Object> pageParam, Map<String, Object> queryParam) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.putAll(pageParam);
        pageQuery.pageNum = MapUtils.getInteger(pageParam, PAGENUM, 1);
        pageQuery.pageSize = MapUtils.getInteger(pageParam, PAGESIZE, 10);
        pageQuery.put(PAGENUM, pageQuery.pageNum);
        pageQuery.put(PAGESIZE, pageQuery.pageSize);

        String sidx = StringUtils.underscoreName(MapUtils.getString(pageParam, SIDX, ""));
        if (StringUtils.isNotBlank(sidx)) {
            String sort = (MapUtils.getString(pageParam, SORT, "ASC")).toUpperCase().contains("ASC") ? "ASC" : "DESC";
            pageQuery.order = sidx + " " + sort;
            pageQuery.put(SIDX, sidx);
            pageQuery.put(SORT, sort);
        }

        if (queryParam != null) {
            pageQuery.putAll(queryParam);
            return pageQuery;
        }

        String queryValue = MapUtils.getString(pageParam, QUERY_VALUE);
        if (StringUtils.isNotBlank(queryValue)) {
            pageQuery.put(MapUtils.getString(pageParam, QUERY_KEY), queryValue);
        }

        return pageQuery;
    }

}

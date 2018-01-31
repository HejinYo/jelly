package cn.hejinyo.jelly.common.utils;

import lombok.Getter;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;

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

    private int pageNum;
    private int pageSize;
    private String order;

    public PageQuery(HashMap<String, Object> parameters) {
        this.putAll(parameters);
        this.pageNum = Integer.parseInt(parameters.get(PAGENUM).toString());
        this.pageSize = Integer.parseInt(parameters.get(PAGESIZE).toString());
        String sidx = StringUtils.underscoreName(MapUtils.getString(parameters, SIDX, ""));
        if (StringUtils.isNotBlank(sidx)) {
            String sort = MapUtils.getString(parameters, SORT, "DESC");
            this.order = sidx + " " + sort;
            this.put(SIDX, sidx);
            this.put(SORT, sort);
        }
        this.put(PAGENUM, pageNum);
        this.put(PAGESIZE, pageSize);
    }

}

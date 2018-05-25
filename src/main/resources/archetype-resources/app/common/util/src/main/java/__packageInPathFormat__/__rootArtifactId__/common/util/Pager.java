#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页类
 * <p/>
 * Created by haiyang.song on 15/11/9.
 */
public class Pager<T> {
    private int totalPage;     //总页数
    private int totalCount;    //总记录数
    private int pageNo;   //当前页
    private int pageSize;      //每页的数量
    private int firstPage;
    private int prePage;
    private int nextPage;
    private int lastPage;
    private boolean isFirst;
    private boolean isLast;
    private List<T> list;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * 分页处理
     *
     * @param page_no     当前页
     * @param page_size   每页的数量
     * @param total_count 总记录数
     */
    public void paging(int page_no, int page_size, int total_count) {
        this.pageNo = page_no;
        this.pageSize = page_size;
        this.totalCount = total_count;

        if (page_no < 1) {
            this.pageNo = 1;
        }

        this.totalPage = (this.totalCount + page_size - 1) / page_size;
        this.firstPage = 1;
        this.lastPage = totalPage;

        if (this.pageNo > 1) {
            this.prePage = this.pageNo - 1;
        } else {
            this.prePage = 1;
        }

        if (this.pageNo < totalPage) {
            this.nextPage = this.pageNo + 1;
        } else {
            this.nextPage = totalPage;
        }

        if (this.pageNo <= 1) {
            this.isFirst = true;
        } else {
            this.isFirst = false;
        }

        if (this.pageNo >= totalPage) {
            this.isLast = true;
        } else {
            this.isLast = false;
        }
    }

    public Map<String, Object> getPaperMap() {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("page_no", this.getPageNo());
        resMap.put("page_size", this.getPageSize());
        resMap.put("total_count", this.getTotalCount());
        resMap.put("total_page", this.getTotalPage());
        return resMap;
    }

}

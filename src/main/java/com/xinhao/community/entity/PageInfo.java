package com.xinhao.community.entity;

/**
 * @Xinhao
 * @Date 2022/1/20
 * @Descrption
 */
public class PageInfo {
    private int current = 1;
    private int limit = 10;
    private int rows;
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getPageNum(){
        if(rows%limit == 0){
            return rows/limit;
        }else {
            return rows/limit + 1;
        }
    }

    public int getOffset(){
        return current * limit - limit;
    }

    public int getFrom(){
        return current - 2 >= 1 ? current - 2: 1;
    }
    public int getTo(){
        return current + 2 <= getPageNum()? current + 2: getPageNum();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

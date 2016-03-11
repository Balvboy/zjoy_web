/**   
* 文件名：Page.java   
*   
* 版本信息：   
* 日期：2014-1-7   
* Copyright     
* 版权所有   
*   
*/
package zjoy.web.common.dao;

import java.io.Serializable;
import java.util.List;


public class Page<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 页码，第几页 */
	private long page = 1;
	/** 总页数 */
	@SuppressWarnings("unused")
	private long totalPages;
	/** 每页的大小  */
	private int rows = 10;
	/** 总记录数  */
	private long total;	
	/** 此页携带的数据 */
	private List<T> list;
	/** 排序字段 ，如果是多个排序字段，以逗号隔开 */
	private String sorts;
	/** 排序顺序 ，如果是多个排序字段，以逗号隔开，并与排序字段sort一一对应 */
	private String orders;
	
	public Page(int rows){
		this.rows = rows;
	}
	
	public Page(){
		
	}
	
	public long getTotalPages() {
		return (this.total + rows - 1) / rows;
	}
	
	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getSorts() {
		return sorts;
	}
	public void setSorts(String sorts) {
		this.sorts = sorts;
	}
	public String getOrders() {
		return orders;
	}
	public void setOrders(String orders) {
		this.orders = orders;
	}

	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	
}

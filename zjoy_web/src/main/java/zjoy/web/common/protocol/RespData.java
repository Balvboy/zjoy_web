package zjoy.web.common.protocol;

import java.io.Serializable;


public class RespData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String respCode="1";
	private String respDesc="操作成功！";
	private Object data; 
	private boolean isShow = false;
	
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
	
	
	
	
	
	
	
	
	
}

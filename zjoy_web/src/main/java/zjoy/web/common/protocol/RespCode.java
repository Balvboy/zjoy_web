package zjoy.web.common.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class RespCode implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String msg;
	private static Map<String,RespCode> respCodeMap=new HashMap<String,RespCode>();
	
	public RespCode(String code,String msg){
		this.code=code;
		this.msg=msg;
	}
	
	
	public static RespData getRespData(String type){
		RespData respData=new RespData();
		respData.setRespCode(respCodeMap.get(type).getCode());
		respData.setRespDesc(respCodeMap.get(type).getMsg());
		return respData;
	}
	
	public static RespData getRespData(String type,Object data){
		RespData respData=new RespData();
		respData.setRespCode(respCodeMap.get(type).getCode());
		respData.setRespDesc(respCodeMap.get(type).getMsg());
		respData.setData(data);
		return respData;
	}
	
	public static RespData getRespData(String type,Object data,boolean isShow){
		RespData respData=new RespData();
		respData.setRespCode(respCodeMap.get(type).getCode());
		respData.setRespDesc(respCodeMap.get(type).getMsg());
		respData.setShow(isShow);
		respData.setData(data);
		return respData;
	}
	
	public static RespData getRespData(String type,boolean isShow){
		RespData respData=new RespData();
		respData.setRespCode(respCodeMap.get(type).getCode());
		respData.setRespDesc(respCodeMap.get(type).getMsg());
		respData.setShow(isShow);
		return respData;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}

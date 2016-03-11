package zjoy.web.common.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.context.ContextLoader;

public class HttpUtils {
	
	public static String POST="1";
	public static String GET="2";

	
	
	public static Map<String,String> sendPost(String url, String params) {
		SSLContext sslcontext= null;
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = ContextLoader.getCurrentWebApplicationContext().getResource("WEB-INF/conf/mykeystore.jks").getInputStream();
			try {  
				keyStore.load(instream, "1234567890".toCharArray());
            } catch (CertificateException e) {  
                e.printStackTrace();  
            } finally {  
                try {  
                    instream.close();  
                } catch (Exception ignore) {  
                }  
            }  
			sslcontext = SSLContexts.custom().loadTrustMaterial(keyStore, new TrustSelfSignedStrategy()).build();
		} catch (KeyManagementException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        //SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,new String[] { "TLSv1" },null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		CloseableHttpClient httpclient = HttpClients.custom().setSslcontext(sslcontext).build();
		
		Map<String,String> result = new HashMap<String,String>();
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Authorization","Bearer "+HXUtils.token);
			httpPost.setEntity(new StringEntity(params,"UTF-8"));
			CloseableHttpResponse response2 = httpclient.execute(httpPost);
			try {
				System.out.println(response2.getStatusLine());
				result.put("code", response2.getStatusLine().getStatusCode()+"");
				HttpEntity entity2 = response2.getEntity();
				String content = convertStreamToString(entity2.getContent());
				result.put("content", content);
				EntityUtils.consume(entity2);
			} finally {
				response2.close();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//获取 Token
	public static void  updateToken(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("grant_type", HXUtils.grant_type);
		map.put("client_id",HXUtils.client_id);
		map.put("client_secret", HXUtils.client_secret);
		String params = JSONObject.fromObject(map).toString();
		Map<String,String> result = sendPost("https://a1.easemob.com/qijiakeji/entplus/token", params);
		if("200".equals(result.get("code"))){
			Map map1 = parseJSON2Map(result.get("content"));
			String token = map1.get("access_token").toString();
			HXUtils.token = token;
		}
	}
	
	//json 转map
	public static Map<String, Object> parseJSON2Map(String jsonStr){  
        Map<String, Object> map = new HashMap<String, Object>();  
        //最外层解析  
        JSONObject json = JSONObject.fromObject(jsonStr);  
        for(Object k : json.keySet()){  
            Object v = json.get(k);   
            //如果内层还是数组的话，继续解析  
           /* if(v instanceof JSONArray){  
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();  
                Iterator<JSONObject> it = ((JSONArray)v).iterator();  
                while(it.hasNext()){  
                    JSONObject json2 = it.next();  
                    list.add(parseJSON2Map(json2.toString()));  
                }  
                map.put(k.toString(), list);  
            } else { */ 
                map.put(k.toString(), v);  
            }  
        return map;  
    }  
	
	
	/**
	 * 将流转换为字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String convertStreamToString(InputStream is)
			throws UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"utf-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	
	//处理 http请求
	public static Map<String,String> httpHandler(String url,Map<String,Object> map,String methodType){
		Map<String,String> result = null;
		String params = JSONObject.fromObject(map).toString();
		if("1".equals(methodType)){
			result = sendPost(url,params);
			if(!"200".equals(result.get("code"))){
				updateToken();
				result = sendPost(url, params);
			}
		}
//		if("2".equals(methodType)){
//			result = sendGet(url);
//			if(!"200".equals(result)){
//				updateToken();
//				result = sendGet(url);
//			}
//		}
		return result;
	}

	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
//		 Map<String,String > map = new HashMap<String,String>();
//		 map.put("grant_type","client_credentials");
//		 map.put("client_id", "YXA6bfEJYN2bEeSBIA9tgO_New");
//		 map.put("client_secret", "YXA6xOQvXAJXMDzXlezj7ZqqPJzDT5o");
//		 String params = "{" +  "'grant_type':'client_credentials', " +"'client_id':'YXA6bfEJYN2bEeSBIA9tgO_New'," +"'client_secret':'YXA6xOQvXAJXMDzXlezj7ZqqPJzDT5o'}";
//		 
//		 Map<String,String > map1 = new HashMap<String,String>();
//		 map1.put("username", "zhouyang2");
//		 map1.put("password", "123456");
//		 String m1 = JSONObject.fromObject(map1).toString();
		 
//		 String result = sendPost("https://a1.easemob.com/qijiakeji/entplus/users",m1);
//		 String result = sendPost("https://a1.easemob.com/qijiakeji/entplus/users",map1);
//		 String result = updateToken();
//		 System.out.println(result);
		 
		 	Map<String,Object> map = new HashMap<String,Object>();
			List<String> users = new ArrayList<String>();
			//users.add("18301494097");
			//users.add("13261997167");
//			users.add("15624163308");
//			users.add("18515438919");
			users.add("15600158878");
			map.put("target_type", "users");
			map.put("target", users);
			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("type", "txt");
			map1.put("msg", "发动机和反抗 1231");
			map.put("msg",map1);
			map.put("from", "qj002");
			HttpUtils.httpHandler(HXUtils.sendMsgUrl, map, HttpUtils.POST);
		
//		 Map<String,Object > map1 = new HashMap<String,Object>();
//		 map1.put("username", "15600158878");
//		 map1.put("password", "123456");
//		 HttpUtils.httpHandler(HXUtils.sendMsgUrl, map, HttpUtils.POST);
			
	}

}
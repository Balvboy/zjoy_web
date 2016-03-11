package zjoy.web.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.ssl.HttpsURLConnection;

/**
 * 微信公众平台对接通用方法
 * @author liubotao
 *
 */
public class WeiXinUtils {

    // 与接口配置信息中的Token要一致  
    private static String token = "weixinHtml"; 
	private final static Logger logger = LoggerFactory.getLogger(WeiXinUtils.class);
	
	//公众号APPID
	public static final String APPID = "wxddcb1e24243a1921";
	//公众号密钥
	public static final String SECRET = "1c52a63398b0d60726f8332c77a6681c";
	//EncodingAESKey
	public static final String EncodingAESKey = "WwDctoWaiOUgzP0PTalcu1fPSYBaRJsXlaYkDoTlTao";
	
	public static String access_token =  null;   //token
	
	public static String ticket = null; 	//签名
	
	public static String nonceStr="qijiakeji";
	
	/**
     * 发起https请求并获取结果
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * 
     */
    public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce) {
        	logger.error("Weixin server connection timed out.");
        } catch (Exception e) {
        	logger.error("https request error:{}", e);
        }
        return null;
    }

    /** 
     * 验证签名 
     * @param signature 
     * @param timestamp 
     * @param nonce 
     * @return 
     */  
    public static boolean checkSignature(String signature, String timestamp, String nonce) {  
        String[] arr = new String[] {token, timestamp, nonce };  
        // 将token、timestamp、nonce三个参数进行字典序排序  
        Arrays.sort(arr);  
        StringBuilder content = new StringBuilder();  
        for (int i = 0; i < arr.length; i++) {  
            content.append(arr[i]);  
        }  
        MessageDigest md = null;  
        String tmpStr = null;  
  
        try {  
            md = MessageDigest.getInstance("SHA-1");  
            // 将三个参数字符串拼接成一个字符串进行sha1加密  
            byte[] digest = md.digest(content.toString().getBytes());  
            tmpStr = byteToStr(digest);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
  
        content = null;  
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信  
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;  
    } 
    
    /**
     * 获取签名
     * @author zhouyang
     * @createDate 2015年8月20日
     * @description
     * @param timestamp
     * @param nonceStr
     * @param url
     * @param ticket
     * @return
     */
    public static  String GetSignature(long timestamp, String nonceStr, String url,String ticket){
    	String[] arr = new String[] {token, timestamp+"", nonceStr, };  
    	String signature=null;
        String str = "jsapi_ticket="+ticket+"&noncestr="+nonceStr+"&timetamp="+timestamp+"&url="+url;
     // 对string1进行sha1签名，得到signature
    	try {
	    	MessageDigest reset = MessageDigest.getInstance("SHA-1");
	    	reset.update(str.getBytes("utf-8"));
            byte[] hash=reset.digest();
            Formatter formatter = new Formatter();
            for(byte b:hash){
            	formatter.format("%02x", b);
            }
            signature = formatter.toString();
            formatter.close();
    	} catch (NoSuchAlgorithmException e) {
    		e.printStackTrace();
    	} catch (UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}     
         	return signature;
	}
    
    public static String GetSignature(Map<String,String> map) {
		int index = 0;
		String []paramArr = new String[4];
		for(Map.Entry<String,String> entry :map.entrySet()){
			paramArr[index] = entry.getKey();
			index++;
		}
		Arrays.sort(paramArr);  
		StringBuilder result = new StringBuilder();
//		for(int i=0;i<paramArr.length;i++){
//			result.append(i!=0?"&":""+paramArr[i]+"="+map.get(paramArr[i]));
//		}
		result.append("jsapi_ticket="+map.get("jsapi_ticket")).append("&noncestr="+map.get("noncestr")).append("&timestamp="+map.get("timestamp").substring(0,10)).append("&url="+map.get("url"));
		System.out.println(result.toString());
        MessageDigest md = null;  
        String tmpStr = null;  
        try {  
            md = MessageDigest.getInstance("SHA-1");  
            // 将三个参数字符串拼接成一个字符串进行sha1加密  
            byte[] digest = md.digest(result.toString().getBytes());  
            tmpStr = byteToStr(digest);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
  
       // content = null;  
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信  
        return tmpStr.toLowerCase();  
	}
    
    /** 
     * 将字节数组转换为十六进制字符串 
     *  
     * @param byteArray 
     * @return 
     */  
    private static String byteToStr(byte[] byteArray) {  
        String strDigest = "";  
        for (int i = 0; i < byteArray.length; i++) {  
            strDigest += byteToHexStr(byteArray[i]);  
        }  
        return strDigest;  
    } 
    
    /** 
     * 将字节转换为十六进制字符串 
     *  
     * @param mByte 
     * @return 
     */  
    private static String byteToHexStr(byte mByte) {  
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
        char[] tempArr = new char[2];  
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];  
        tempArr[1] = Digit[mByte & 0X0F];  
        String s = new String(tempArr);  
        return s;  
    }
    
    
    /**
     * getUrl
     * @param url
     * @return
     */
    public static String getUrl(String url){
        String result = null;
        try {
            // 根据地址获取请求
            HttpGet request = new HttpGet(url);
            // 获取当前客户端对象
            HttpClient httpClient = new DefaultHttpClient();
            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(request);
            
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result= EntityUtils.toString(response.getEntity());
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

	public static String getAccessToken() {
		if(access_token!=null){
			return access_token;
		}else{
			return null;
		}
	}

	
}

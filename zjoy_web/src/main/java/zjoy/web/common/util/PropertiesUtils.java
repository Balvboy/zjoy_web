/**   
 * 文件名：PropertiesHelper.java   
 *   
 * 版本信息：   
 * 日期：2014-1-7   
 * 版权所有   
 *   
 */
package zjoy.web.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

public class PropertiesUtils {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
	private static Map<String,Properties> propertiesMap=new HashMap<String,Properties>();
	
	public static Properties getProperties(String resName){

		InputStream input = null;
		try {
			input = ContextLoader.getCurrentWebApplicationContext().getResource("WEB-INF/conf/"+resName).getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//		String url = PropertiesUtils.class.getResource("").getPath().replaceAll("%20", " ");  
//        String path = url.substring(0, url.indexOf("WEB-INF")) + "WEB-INF/conf/"+resName;  		
		InputStream is=null;
		Properties properties = propertiesMap.get(resName);
		try {
			if(properties==null){
				
				properties=new Properties();
				properties.load(input); 				
				logger.debug("加载配置信息成功");
			}
		} catch (Exception e) {
			logger.warn("加载配置文件出错");
			logger.error(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			} 
		}
		return properties;
	}

	public static String getPropertyValue(String propertyName){
		Properties properties = getProperties("conf.properties");
		String str = properties.getProperty(propertyName);
		return str;
	}
	
}

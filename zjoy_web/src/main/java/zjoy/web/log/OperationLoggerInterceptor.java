/**   
* 文件名：OperationLoggerInterceptor.java   
*   
* 版本信息：   
* 日期：2014-1-17   
* 版权所有   
*   
*/
package zjoy.web.log;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import zjoy.web.common.util.DateUtil;

/**   
 *    
 * 项目名称：bms   
 * 类名称：OperationLoggerInterceptor   
 * 类描述：   
 * 创建人：lzh   
 * 创建时间：2014-1-17 下午4:00:52   
 * 修改人：lzh   
 * 修改时间：2014-1-17 下午4:00:52   
 * 修改备注：   
 * @version    
 *    
 */
public class OperationLoggerInterceptor extends HandlerInterceptorAdapter{
	private final static Logger logger = Logger.getLogger(OperationLoggerInterceptor.class);
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception{
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		return super.preHandle(request, response, handler);
	}
}

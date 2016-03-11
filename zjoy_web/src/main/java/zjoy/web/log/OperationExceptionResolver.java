package zjoy.web.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateJdbcException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import zjoy.web.common.protocol.RespCode;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 使用spring的统一异常处理
 * 处理数据库连接异常，sql语句执行异常等
 * @author qiyanbo
 *
 */
@Component
public class OperationExceptionResolver implements  HandlerExceptionResolver{
	private final static Logger logger = LoggerFactory.getLogger(OperationExceptionResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request,  
            HttpServletResponse response, Object handler, Exception ex) {
//		response.setCharacterEncoding("UTF-8");
//        Map<String,Object> map = new HashMap<String,Object>();  
//        ex.printStackTrace();
//        try {
//        	ObjectMapper om=new ObjectMapper();
//        	String json="";
//        	if(ex instanceof HibernateJdbcException) {  
//        		json=om.writeValueAsString(RespCode.getRespData(RespCode.ERROR));
//        	}
//        	if(ex instanceof Exception) {  
//        		json=om.writeValueAsString(RespCode.getRespData(RespCode.ERROR,ex.getMessage()));
//        	}
//			response.getWriter().print(json);
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//		}
        return new ModelAndView();  
    }  
}

package zjoy.web.common.dao.hibernate;

import org.hibernate.dialect.*;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StringType;

public class MySQLExtendDialect extends MySQLDialect {
	 
    public MySQLExtendDialect(){
        super();
        registerFunction("convert_gbk", 
                 new SQLFunctionTemplate(StringType.INSTANCE, "convert(?1 using gbk)") );
    }
     
} 

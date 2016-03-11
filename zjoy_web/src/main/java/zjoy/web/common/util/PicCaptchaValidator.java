package zjoy.web.common.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

public class PicCaptchaValidator {

	private ApplicationContext context ;

    private RedisTemplate redisTemplate;

    String key ="key13800";
    
	/*private void init(){
        context = new ClassPathXmlApplicationContext("PoolConf.xml");
        redisTemplate= context.getBean("redisTemplate",RedisTemplate.class);
    }*/
    
	public PicCaptchaValidator() {
		super();
	}

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
     * redis中设置键值对
     */
    public void setRedis(){
        redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                 redisConnection.set(key.getBytes(),(System.currentTimeMillis()+"").getBytes());
                return 1L;
            }
        });
    }
    
    /**
     * 获取redis中的imei对应的值
     */
    public String getRedis(){
    	
    	Object execute = redisTemplate.execute(new RedisCallback() {
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.get(key.getBytes());
            }
        });
    	
		if(execute != null){
			return new String((byte[])execute);
		}else{
			return null;
		}
    }
}

package zjoy.web.common.util;

public class HXUtils {
	public static final String grant_type = "client_credentials";
	public static final String client_id = "YXA6bfEJYN2bEeSBIA9tgO_New";
	public static final String  client_secret = "YXA6xOQvXAJXMDzXlezj7ZqqPJzDT5o";
	public static String token = "YWMt9u7WLuHUEeSI1oOhiYRS2wAAAU3npmfMEiq1PVcZDeUPvrWdHw-b-ub6-m8";
	
	
	public static String registerUrl = "https://a1.easemob.com/qijiakeji/entplus/users";
	public static String sendMsgUrl = "https://a1.easemob.com/qijiakeji/entplus/messages";
//	public static String changePwdUrl = 
	
	public static String getAddFriendUrl(String user,String add ){
		String url = "https://a1.easemob.com/qijiakeji/entplus/users/"+user+"/contacts/users/"+add;
		return url;
	}
	
	public static String getChangePwdUrl(String mobile){
		String url = "https://a1.easemob.com/qijiakeji/entplus/users/"+mobile+"/password";
		return url;
	}
	
}

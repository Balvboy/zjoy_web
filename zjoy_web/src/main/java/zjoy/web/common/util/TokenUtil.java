package zjoy.web.common.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.UUID;

public class TokenUtil {
	
	
	public static final String secret="QijiaEntplus@LX";
	public static final String issuer="QijiaAPI"; 
	public static final String key="cmbjxccwtnylfyshlzdszcymcswwsyxh"; 
	
	private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	public static String getToken(String imei) {
		

		
//		Both 'payload' and 'claims' cannot both be specified		
/*		
 *    方式1  
 *               String token = Jwts.builder()
				.setSubject(mobile)
				.setExpiration(new Date(System.currentTimeMillis() + 1000*60*30))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setIssuer(issuer)
				.signWith(signatureAlgorithm, secret + mobile)
				.compact();
//方式3 通过payload string设置				
		*/
//		   方式2	
		Map<String,Object> clm=new HashMap<String, Object>();
		clm.put("param1", "ABCD");
		clm.put("param2", "XYZMN");	
		
		String token = Jwts.builder()
				.setClaims(clm)//方式2 要先setClaims再设置其他参数 否则会被冲掉
				.setSubject(imei)
				.setExpiration(new Date(System.currentTimeMillis() + 1000*60*30))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setIssuer(issuer)
				.signWith(signatureAlgorithm, secret + imei)
				.compact();			
		

		return token;
	}

}

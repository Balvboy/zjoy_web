package zjoy.web.common.sendMail;

import java.io.FileOutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import zjoy.web.common.util.PropertiesUtils;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

public class SendH5Mail {
	
	final static Properties prop = new Properties();
	{
	
		prop.put("mail.transport.protocol", "smtp");
		prop.put("mail.smtp.host", "smtp.ym.163.com");  
		prop.put("mail.smtp.port", "465"); 
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.socketFactory.port", "465");
		prop.put("mail.smtp.starttls.enable","true");
		prop.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.socketFactory.fallback", "false"); 
		prop.put("mail.smtp.auth", "true"); 
		
	}
	
	//发送者
	final static String from="support@entplus.cn";
	private Authenticator auth = new MailAuthenticator(from, "qijia@123");
	
	final static String title="企业信用报告";
	//cid读取附件中的图片来显示
	final static String msgTemplate="您好,<br/>" +
													"请查看您下载的企业报告：<br/>" +
													"${companyName} 的企业报告<br/>" +
													"<img src='http://www.entplus.cn/images/2wm.png' width='200px' height='200px'><br/>" +
													"扫一扫上方二维码，关注我们的动态！<br/>" +
													"技术支持：北京企嘉科技有限公司";
	private String logo="demo\\p2273326145.jpg";
	
	private String companyName="企嘉科技";
	private String reportPDF="demo\\jumo.zip";
	//接收者
	private String recipient="cuijiajun@entplus.cn";
	//邮件文件保存路径
	private String path = "";
	
	
    public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getReportPDF() {
		return reportPDF;
	}

	public void setReportPDF(String reportPDF) {
		this.reportPDF = reportPDF;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	
	public void doSend() throws Exception{
		Properties properties = PropertiesUtils.getProperties("conf.properties");
		String emailFrom = properties.getProperty("from_email");
		String emailPassWord = properties.getProperty("from_password");
		
		if(emailFrom != null  && !"".equals(emailFrom) && emailPassWord != null && !"".equals(emailPassWord)){
			auth = new MailAuthenticator(emailFrom, emailPassWord); 
		}
		
        Session session = Session.getDefaultInstance(prop, auth);
        //是否打印debug消息
        //session.setDebug(true);
        
        Message message = createMixedMail(session);

        Transport ts = session.getTransport();  

        ts.connect();
        ts.sendMessage(message, message.getAllRecipients());
        ts.close();		
        
	}
	
	/**
	 * 解析接收者数组
	 * @return
	 * @throws AddressException 
	 */
	private InternetAddress[] parseRecipient() throws AddressException{
		//TODO 解析的参数需要和前端商定
		String[] recipientArray = recipient.split(",");
		InternetAddress[] addresses = new InternetAddress[recipientArray.length];
		for(int i = 0; i < recipientArray.length; i++){
			addresses[i] = new InternetAddress(recipientArray[i]);
		}
		return addresses;
	}
	
    /**
     * @Method: createMixedMail
     * @param session
     * @return
     * @throws Exception
     */ 
    private MimeMessage createMixedMail(Session session) throws Exception {
        //创建邮件
        MimeMessage message = new MimeMessage(session);
        
        //设置邮件的基本信息
        message.setFrom(new InternetAddress(from));
        //message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        //设置多个接收者
        message.setRecipients(Message.RecipientType.TO, parseRecipient());
        message.setSubject(companyName +"-"+ title);
        
        //正文
        MimeBodyPart text = new MimeBodyPart();
        String msg= msgTemplate.replace("${companyName}", companyName);
        text.setContent(msg,"text/html;charset=UTF-8");
        
        //图片*从附件中取得图片的方式会产生额外的附件*
        /*MimeBodyPart image = new MimeBodyPart();
        image.setDataHandler(new DataHandler(new FileDataSource(logo)));
        image.setContentID("logoImg.jpg");*/
        
        //附件1
        MimeBodyPart attach = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource(reportPDF));
        attach.setDataHandler(dh);
        attach.setFileName(MimeUtility.encodeText(dh.getName()));
        
        /*//附件2
        MimeBodyPart attach2 = new MimeBodyPart();
        DataHandler dh2 = new DataHandler(new FileDataSource("demo\\图片.zip"));
        attach2.setDataHandler(dh2);
        attach2.setFileName(MimeUtility.encodeText(dh2.getName()));*/
        
        //描述关系:正文文本和正文图片
        MimeMultipart zhengwen = new MimeMultipart();
        zhengwen.addBodyPart(text);
        //zhengwen.addBodyPart(image);
        zhengwen.setSubType("related");
        
        //代表正文的bodypart
        MimeBodyPart content = new MimeBodyPart();
        content.setContent(zhengwen);
        
        //描述关系:正文和附件
        MimeMultipart total = new MimeMultipart();
        total.addBodyPart(attach);//添加附件1
//        total.addBodyPart(attach2);//添加附件2
        total.addBodyPart(content);//添加正文
        total.setSubType("mixed");
        
        message.setContent(total);
        message.saveChanges();
        
        message.writeTo(new FileOutputStream(path + recipient +"-"+ System.currentTimeMillis() +"MixedMail.eml"));//保存備查
        //返回创建好的的邮件
        return message;
    }
    
    //取消内部类，避免替换文件重复

	public static void main(String[] args) throws Exception {
		SendH5Mail s = new SendH5Mail();
		s.setCompanyName("齐嘉科技");
		s.setRecipient("cuijiajun@entplus.cn");
		s.setReportPDF("E:\\测试附件中文名.txt");
		s.doSend();
    }
}
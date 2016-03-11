package zjoy.web.common.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import zjoy.web.common.protocol.RespData;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {
	
	public static final String replaceFrotATagWithBlank = "<a(.[^<>]*)>";
	public static final String replaceBackATagWithBlank = "</a>";
	public static final String FrontImgTag = "<img(.[^<>]*)>";
	public static final String FrontBigImgTag = "<IMG(.[^<>]*)>";
	public static final String BackImgTag ="</img>";
	public static final String BackBigImgTag = "</IMG>";
	private static String uploadNumber ;  //1测试 (生产) 2开发
	private static Properties properties;
	
	private static int[] personList = {0,50,150,500,1000,5000,10000,10000};	//人员规模
	private static String[] personStr = {"50名员工以下","50-150名员工","150-500名员工","500-1000名员工","1000-5000名员工","5000-10000名员工","超过10000名员工"};
	
	private static int[] fundList = {0,10,50,100,500,1000,5000,10000,100000};	//资金规模
	private static String[] fundStr = {"10万以下","10万-50万","50万-100万","100万-500万","500万-1000万","1000万-5000万","5000万-1亿","1亿以上"};
	
	static{
		properties = PropertiesUtils.getProperties("conf.properties");
		uploadNumber = properties.getProperty("uploadNumber");
		
	}
	
	/**
	 * 根据属性名获取配置文件的属性值
	 * @author zhouyang
	 * @createDate 2015年9月28日
	 * @description
	 */
	public static String getPropertyValue(String propertyName){
		return properties.getProperty(propertyName);
	}
	/**
	 * 根据制定的regex过滤字符串
	 * @author zhouyang
	 * @createDate 2015年9月8日
	 * @description
	 * @param richText
	 * @param regex
	 * @param replaceStr
	 * @return
	 */
	public static String handle(String richText,String regex,String replaceStr){
		String result=richText.replace("（", "(").replace("）", ")");
       //String reg = "\\(([\\u4e00-\\u9fa5\\w]*)\\)[\\w<>/\\s]*[@]([\\w-]*)[@]" ;
       Pattern p = Pattern.compile(regex);
       Matcher m = p.matcher(richText);
       while(m.find()){
    	  result = result.replace(m.group(0), replaceStr); 
       }
       return result;
   }
	/**
	 * 去掉a标签和 <img></img>标签
	 * @author zhouyang
	 * @createDate 2015年9月8日
	 * @description
	 * @param source
	 * @return
	 */
	public static String replaceATag(String source){
		//String result = handle(handle(source,replaceFrotATagWithBlank,""),replaceBackATagWithBlank,"");
		String result = source.replaceAll("href.{0,2}=.{0,2}\"(.+?)\"", "");
		result = handle(handle(result,FrontImgTag,""),BackImgTag,"");
		result = handle(handle(result,FrontBigImgTag,""),BackBigImgTag,"");
		//过滤   style="background-color:yellow;color:red;"
		result = result.replace("color:red", "");
		return result.replace("background-color:yellow", "");
		
	}
	
	
	public static String replaceATagApi(String source){
		//过滤a标签
		String result = handle(handle(source,replaceFrotATagWithBlank,""),replaceBackATagWithBlank,"");
		result = handle(handle(result,FrontImgTag,""),BackImgTag,"");
		result = handle(handle(result,FrontBigImgTag,""),BackBigImgTag,"");
		//过滤   style="background-color:yellow;color:red;"
		result = result.replace("color:red", "");
		return result.replace("background-color:yellow", "");
		
	}
	
	/**
	 * 去掉字符串中的汉字
	 * @author zhouyang
	 * @createDate 2015年9月8日
	 * @description
	 * @param source
	 * @return
	 */
	public static String replaceHanziAndLetter(String source){
		String result = null;
		if(source!=null){
			result = source.replaceAll("[\\u4e00-\\u9fa5]", "").replaceAll("[a-z]+", "").trim();
		}
		return result;
	}
	
	
	/**
	 * 通用上传图片功能
	 * @param file spring的上传文件
	 * @param uploadFile 对应conf.properties文件中要上传到的目录
	 * @return 图片名称
	 * @throws IOException 
	 */
	public static String commonUploadImage(MultipartFile[] files,HttpSession session,String uploadFile) throws IOException{
		//读取配置文件
		String path = properties.getProperty(uploadFile);
		String image_name = null;
		for(MultipartFile file:files){
//			InputStream input = file.getInputStream();
//			Map<String, Object> map = PackageUtil.analysiIpa(input);
//			Set<String> keySet = map.keySet();
//			System.out.println("......IPA信息....。。。。。。。。。。。。。。。。。。................");
//			for (String key : keySet) {
//				System.out.println(key+"="+map.get(key));
//			}
			String fileName = UUID.randomUUID().toString();
			String ext = file.getOriginalFilename().substring(file.getName().lastIndexOf(".") + 1);
			File tempFile = new File(getImgDir(session) + path + fileName+ "." + ext);
			if (!tempFile.exists()) {
				tempFile.createNewFile();
			}
			file.transferTo(tempFile);
			if(image_name == null){
				image_name = tempFile.getName(); 				
			}else{
				image_name += ","+tempFile.getName();
			}
		}
		return image_name;
	}
	/**
	 * 上传单个图片
	 * @author zhouyang
	 * @createDate 2015年9月8日
	 * @description
	 * @param file
	 * @param session
	 * @param uploadFile
	 * @return
	 * @throws IOException
	 */
	public static String commonUploadSingleImg(MultipartFile file,HttpSession session,String uploadFile) throws IOException{
		String path = properties.getProperty(uploadFile);
		String image_name = null;
		String fileName = UUID.randomUUID().toString();
		String ext = file.getOriginalFilename().substring(file.getName().lastIndexOf(".") + 1);
		String rootPath = getImgDir(session);
		String parentPath = rootPath +path;
		File tempFile = new File(parentPath+ fileName+ "." + ext);
		if (!tempFile.exists()) {
			File parentFile = new File(parentPath);
			if(!parentFile.exists()){
				File ppFile = parentFile.getParentFile();
				if(!ppFile.exists()){
					ppFile.mkdir();
				}
				parentFile.mkdir();
			}
			tempFile.createNewFile();
		}
		file.transferTo(tempFile);
		image_name = tempFile.getName(); 				
		return image_name; 
	}
	
	
	/**
	 * 上传单个图片,并且创建压缩图片，命名规则为在原图片前添加small
	 * @author zhouyang
	 * @createDate 2015年9月8日
	 * @description
	 * @param file
	 * @param session
	 * @param uploadFile
	 * @return
	 * @throws IOException
	 */
	public static String commonUploadSingleImg(MultipartFile file,HttpSession session,String uploadFile,boolean isCreateSmallImg) throws IOException{
		String path = properties.getProperty(uploadFile);
		String image_name = null;
		String fileName = UUID.randomUUID().toString();
		String ext = file.getOriginalFilename().substring(file.getName().lastIndexOf(".") + 1);
		String rootPath = getImgDir(session);
		String parentPath = rootPath +path;
		File tempFile = new File(parentPath+ fileName+ "." + ext);
		if (!tempFile.exists()) {
			File parentFile = new File(parentPath);
			if(!parentFile.exists()){
				File ppFile = parentFile.getParentFile();
				if(!ppFile.exists()){
					ppFile.mkdir();
				}
				parentFile.mkdir();
			}
			tempFile.createNewFile();
		}
		file.transferTo(tempFile);
		String[] array = new String[2];
		image_name = tempFile.getName();
		if(isCreateSmallImg){
			compressPic(parentPath+ fileName+ "." + ext, parentPath+ "small"+fileName+ "." + ext, (float)0.5);
		}
		return image_name; 
	}
	
	/**
	 * 获取文件地址
	 * @param session
	 * @return
	 */
	public static String getImgDir(HttpSession session) {
		if(uploadNumber.equals("1")){
			//服务器
			 String path=session.getServletContext().getRealPath("/");
			 int pos=path.lastIndexOf("/", path.length()-1);
			 pos=path.lastIndexOf("/", pos-1);
			 path=path.substring(0, pos+1);
			 return path;
		}else{
			//本地
			String serverPath = session.getServletContext().getRealPath("/");
			int last = serverPath.lastIndexOf("\\");
			String temp = serverPath.substring(0, last);
			int twiceLast = temp.lastIndexOf("\\");
			String finalStr = temp.substring(0, twiceLast + 1);
			return finalStr;
		}
	}
	
	/**
	 * 过滤emoji 或者 其他非文字类型的字符
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source) {
		if (!containsEmoji(source)) {
			return source;// 如果不包含，直接返回
		}
		// 到这里铁定包含
		StringBuilder buf = null;
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);

			if (isEmojiCharacter(codePoint)) {
				if (buf == null) {
					buf = new StringBuilder(source.length());
				}

				buf.append(codePoint);
			}
		}

		if (buf == null) {
			return source;// 如果没有找到 emoji表情，则返回源字符串
		} else {
			if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
				buf = null;
				return source;
			} else {
				return buf.toString();
			}
		}

	}
    
    /**
     * 检测是否有emoji字符
     * @param source
     * @return 一旦含有就抛出
     */
    public static boolean containsEmoji(String source) {
        if (StringUtils.isBlank(source)) {
            return false;
        }
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
 
            if (isEmojiCharacter(codePoint)) {
                //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }
        return false;
    }
	
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
	
	/**
	 * 企业信息资金和人员规模信息转化
	 * @param value
	 * @param type	1 人员规模 2资金规模
	 * @return
	 */
	public static String matchForFundOrPerson(String value,int type){
		String result = "";
		try {
			if(value ==null || "".equals(value)){
				return result;
			}
			int valueFlag = 0;//value(开区间：0; 闭区间：1)
			if(value.contains("-")){
				value = value.replaceAll("[\\u4e00-\\u9fa5]", "").replaceAll("[a-z]+", "").split("-")[1];
				valueFlag = 1;
			}else{
				value = value.replaceAll("[\\u4e00-\\u9fa5]", "").replaceAll("[a-z]+", "");
			}
			Double dval = Double.parseDouble(value);
			Integer val = dval.intValue();
			int flag = 0;
			
			if(type == 1){
				//开区间只判断两边的数据
				if(valueFlag == 0){
					if(val >= personList[7]){result = personStr[6];}
					if(val <= personList[1]){result = personStr[0];}
				}else{
					for(int i = 0; i < personList.length-2;i++){
						if(personList[i]< val && val <= personList[i+1]){
							flag = i;
						}
					}
					result = personStr[flag];
				}
			}else if(type == 2){
				if(val<fundList[8]){
					for(int i = 1; i < fundList.length-1;i++){
						if(fundList[i]<= val && val <= fundList[i+1]){
							flag = i;
						}
					}
					result = fundStr[flag];
				}else{
					result = fundStr[7];
				}
			}
		} catch (Exception e) {
			return result;
		}
		return result;
	}
	
	
	/**
	 * 压缩图片
	 */
    public static boolean compressPic(String srcFilePath,String descFilePath,float compressRange)
    {
        File file = null;
        BufferedImage src = null;
        FileOutputStream out = null;
        ImageWriter imgWrier;
        ImageWriteParam imgWriteParams;

        // 指定写图片的方式为 jpg
        imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
        imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);
        // 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
        imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
        // 这里指定压缩的程度，参数qality是取值0~1范围内，
        imgWriteParams.setCompressionQuality(compressRange);
        imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
        ColorModel colorModel = ColorModel.getRGBdefault();
        // 指定压缩时使用的色彩模式
        imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
        try{
                file = new File(srcFilePath);
                src = ImageIO.read(file);
                out = new FileOutputStream(descFilePath);
                imgWrier.reset();
                // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何 OutputStream构造
                imgWrier.setOutput(ImageIO.createImageOutputStream(out));
                // 调用write方法，就可以向输入流写图片
                imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);
                out.flush();
                out.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
	public static boolean isNullObject(Object detail) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if(detail==null)
			return true;
		int count = 0;
		int fieldCount = detail.getClass().getDeclaredFields().length;
		Method[] methods = detail.getClass().getMethods();
		for(Method method:methods){
			if(method.getName().toLowerCase().contains("get")){
				Object result = method.invoke(detail);
				if(result==null){
					count++;
				}else{
					break;
				}
			}
		}
		if(count==fieldCount){
			return true;
		}
		return false;
	}
	
    /**
     * @author zhouyang
     * @createDate 2015年11月10日
     * @description  Object to json
     */
	public static String getResp(RespData respData) {
		String json = "";
		try {
			ObjectMapper om = new ObjectMapper();
			json = om.writeValueAsString(respData);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return json;
	}
	
	public static String objectToString(Object obj){
		if(obj==null){
			return null;
		}else{
			return obj.toString();
		}
	}
	
	public static Integer objectToInt(Object obj){
		if(isEmptyString(objectToString(obj))){
			return null;
		}else{
			return Integer.valueOf(objectToString(obj));
		}
	}
	
	public static Date objectToDate(Object obj){
		if(obj==null){
			return null;
		}else{
			return (Date)obj;
		}
	}
	
	public static String noDataRightNow(String source){
		if(source==null){
			return "暂无数据";
		}if("".equals(source.trim())){
			return "暂无数据";
		}
		return source;
	}
	
	public static boolean isEmptyString(String str){
		if(str==null){
			return true;
		}if("".equals(str.trim())){
			return true;
		}
		return false;
	}
	
	public static String getCreateTimeStr(String fei_esdate){
		if(fei_esdate!=null&&!"".equals(fei_esdate)){
			int year = DateUtil.getYearsBetweenNow("yyyy-MM-dd", fei_esdate);
			if(year==0){
				return "创立不足1年";
			}else{
				return "创立约"+year+"年";
			}
		}
		return "";
	}
	
	public static String getImagePath(String imgPath){
		if (imgPath != null && !"".equals(imgPath)) {
			if (imgPath.startsWith("http")) {
				return imgPath.trim();
			} else {
				String img = properties.getProperty("server")
						+ properties.getProperty("companylogo.path") + imgPath;
				return img.trim();
			}
		}
		return "";
	}
}

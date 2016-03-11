package zjoy.web.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PinYin2Abbreviation {
	private final static Logger logger = LoggerFactory.getLogger(PinYin2Abbreviation.class);
	// 简体中文的编码范围从B0A1（45217）一直到F7FE（63486）
	private static int BEGIN = 45217;
	private static int END = 63486;

	// 按照声 母表示，这个表是在GB2312中的出现的第一个汉字，也就是说“啊”是代表首字母a的第一个汉字。
	// i, u, v都不做声母, 自定规则跟随前面的字母
	private static char[] chartable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', };

	// 二十六个字母区间对应二十七个端点
	// GB2312码汉字区间十进制表示
	private static int[] table = new int[27];

	// 对应首字母区间表
	private static char[] initialtable = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 't', 't', 'w', 'x', 'y', 'z', };

	// 初始化
	static {
		for (int i = 0; i < 26; i++) {
			table[i] = gbValue(chartable[i]);// 得到GB2312码的首字母区间端点表，十进制。
		}
		table[26] = END;// 区间表结尾
	}

	// ------------------------public方法区------------------------
	// 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串 最重要的一个方法，思路如下：一个个字符读入、判断、输出

	public static String cn2py(String SourceStr) {
		String Result = "";
		int StrLength = SourceStr.length();
		int i;
		try {
			for (i = 0; i < StrLength; i++) {
				Result += Char2Initial(SourceStr.charAt(i));
			}
		} catch (Exception e) {
			Result = "";
			logger.error(e.getMessage(), e);
		}
		return Result;
	}
	
	public static void main(String[] args) {
		
		System.out.println(cn2py("言"));
	}

	// ------------------------private方法区------------------------
	/**
	 * 输入字符,得到他的声母,英文字母返回对应的大写字母,其他非简体汉字返回 '0' 　　* 　　
	 */
	private static char Char2Initial(char ch) {
		// 对英文字母的处理：小写字母转换为大写，大写的直接返回
		if (ch >= 'a' && ch <= 'z') {
			return (char) (ch - 'a' + 'A');
		}
		if (ch >= 'A' && ch <= 'Z') {
			return ch;
		}
		// 对非英文字母的处理：转化为首字母，然后判断是否在码表范围内，
		// 若不是，则直接返回。
		// 若是，则在码表内的进行判断。
		int gb = gbValue(ch);// 汉字转换首字母
		if ((gb < BEGIN) || (gb > END))// 在码表区间之前，直接返回
		{
			return '#';
		}
		int i;
		for (i = 0; i < 26; i++) {// 判断匹配码表区间，匹配到就break,判断区间形如“[,)”
			if ((gb >= table[i]) && (gb < table[i + 1])) {
				break;
			}
		}
		if (gb == END) {// 补上GB2312区间最右端
			i = 25;
		}
		return initialtable[i]; // 在码表区间中，返回首字母
	}

	/**
	 * 取出汉字的编码 cn 汉字 　　
	 */
	private static int gbValue(char ch) {// 将一个汉字（GB2312）转换为十进制表示。
		String str = new String();
		str += ch;
		try {
			byte[] bytes = str.getBytes("GB2312");
			if (bytes.length < 2) {
				return 0;
			}
			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		}
	}
	
    /** 
     * 将汉字转换为全拼 
     *  
     * @param src 
     * @return String 
     */  
    public static String getPinYin(String src) {  
        char[] t1 = null;  
        t1 = src.toCharArray();  
        // System.out.println(t1.length);  
        String[] t2 = new String[t1.length];  
        // System.out.println(t2.length);  
        // 设置汉字拼音输出的格式  
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();  
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);  
        String t4 = "";  
        int t0 = t1.length;  
        try {  
            for (int i = 0; i < t0; i++) {  
                // 判断是否为汉字字符  
                // System.out.println(t1[i]);  
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {  
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中  
                    t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后  
                } else {  
                    // 如果不是汉字字符，直接取出字符并连接到字符串t4后  
                    t4 += Character.toString(t1[i]);  
                }  
            }  
        } catch (BadHanyuPinyinOutputFormatCombination e) {  
            e.printStackTrace();  
        }  
        return t4;  
    }  
  
    /** 
     * 提取每个汉字的首字母 
     *  
     * @param str 
     * @return String 
     */  
    public static String getPinYinHeadChar(String str) {  
        String convert = "";  
        for (int j = 0; j < str.length(); j++) {  
            char word = str.charAt(j);  
            // 提取汉字的首字母  
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);  
            if (pinyinArray != null) {  
                convert += pinyinArray[0].charAt(0);  
            } else {  
                convert += word;  
            }  
        }  
        return convert;  
    }  
  
    /** 
     * 将字符串转换成ASCII码 
     *  
     * @param cnStr 
     * @return String 
     */  
    public static String getCnASCII(String cnStr) {  
        StringBuffer strBuf = new StringBuffer();  
        // 将字符串转换成字节序列  
        byte[] bGBK = cnStr.getBytes();  
        for (int i = 0; i < bGBK.length; i++) {  
            // System.out.println(Integer.toHexString(bGBK[i] & 0xff));  
            // 将每个字符转换成ASCII码  
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff)+" ");  
        }  
        return strBuf.toString();  
    }

//	public static void main(String[] args) throws Exception {
//	//	System.out.println(cn2py("，重庆重视发展IT行业，大多数外企，如，IBM等进驻山城"));
//		 String cnStr = "";  
//	     System.out.println(getPinYin(cnStr));  
//	     System.out.println(getPinYinHeadChar(cnStr)); 
//	}
}
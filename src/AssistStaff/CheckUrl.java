package AssistStaff;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CheckUrl {
	
	public CheckUrl(){
		
	}
	
	public static String Check(String url){
		String [] tmp_page1 = url.split("http://");
		if (tmp_page1.length ==1){
			url = "http://" + url;
		}
		if (isDouban(url))
			return "Douban";
		else if (isTieba(url))
			return "Tieba";
		else if (isTianya(url))
			return "Tianya";
		else
			return "false";
	}
	
	private static boolean isDouban (String pInput) {
		if (pInput == null ){
			return false;
		}
		String regEx =	"http://(www\\.)?douban\\.com/group/topic/\\d{8}/(\\?start=(\\d{1,}00|0))?" ;
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(pInput);
		return matcher.matches();
	} 

	private static boolean isTieba (String pInput) {
		if (pInput == null ){
			return false;
		}
		String regEx =	"http://tieba\\.baidu\\.com/(p/\\d{8,}((\\?see_lz=1(&pn=\\d{1,})?)|(\\?pn=\\d{1,}))?|f\\?kz=\\d{8,})" ;
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(pInput);
		return matcher.matches();
	}

	private static boolean isTianya (String pInput) {
		if (pInput == null ){
			return false ;
		}
		String regEx =	"http://tianya\\.cn/bbs/post-.*-\\d*-\\d{1,10}\\.shtml" ;
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(pInput);
		return	matcher.matches();
	}

}

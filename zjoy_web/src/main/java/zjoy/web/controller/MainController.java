package zjoy.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/main")
public class MainController {

	@RequestMapping(value="getmain",method=GET)
	@ResponseBody
	public String mainGet(HashMap<String,String> hashMap){
		hashMap.put("state", "200");
		hashMap.put("info", "ok");
		return "/index.html";
	}
	
	@RequestMapping(value="postmain",method=POST)
	@ResponseBody
	public Object mainPost(HashMap<String,String> hashMap){
		hashMap.put("state", "200");
		hashMap.put("info", "ok");
		return hashMap;
	}
	
	@RequestMapping(value="test",method=POST)
	@ResponseBody
	public Object test(HashMap<String,String> hashMap){
		hashMap.put("state", "200");
		hashMap.put("info", "ok");
		return hashMap;
	}
	
}

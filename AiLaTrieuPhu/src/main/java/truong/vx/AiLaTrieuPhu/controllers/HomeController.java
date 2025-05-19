package truong.vx.AiLaTrieuPhu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import truong.vx.AiLaTrieuPhu.models.NguoiChoi;
import truong.vx.AiLaTrieuPhu.repositories.NguoiChoiRepository;

@Controller
public class HomeController {
	 @GetMapping("/home")
	    public String showStartPage() {
		 return "views/Home";  
	    }
	 
	 @Autowired
	    private NguoiChoiRepository nguoiChoiRepository;
	 
	 
	 @GetMapping("/batdau")
	    public String showNhapThongTinPage(Model model) {
	        model.addAttribute("nguoichoi", new NguoiChoi());
	        return "views/batdau";
	    }
	 
	  @PostMapping("/luu-thong-tin")
	    public String luuNguoiChoi(@ModelAttribute("nguoichoi") NguoiChoi nguoiChoi) {
	        nguoiChoiRepository.save(nguoiChoi);
	        // Sau khi lưu xong có thể chuyển tới màn hình bắt đầu chơi hoặc thông báo
	        return "redirect:/choigame"; // hoặc return "views/choigame" nếu bạn chưa dùng redirect
	    }
	  
	  @GetMapping("/choigame")
	  public String choiGame(Model model) {
		  return "views/cauhoi";
	  }
	 
	 

}

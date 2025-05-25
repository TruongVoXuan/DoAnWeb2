package truong.vx.AiLaTrieuPhu.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import truong.vx.AiLaTrieuPhu.models.PhienChoi;
import truong.vx.AiLaTrieuPhu.repositories.NguoiChoiRepository;
import truong.vx.AiLaTrieuPhu.repositories.PhienChoiRepository;

@Controller
public class PhienChoiController {
	@Autowired
	private PhienChoiRepository phienChoiRepository;

	@Autowired
	private NguoiChoiRepository nguoiChoiRepository;
	
	@GetMapping("/phienchoi/chitiet")
	public String chiTietPhienChoi(@RequestParam("phienchoiId") int id, Model model) {
	    Optional<PhienChoi> phienChoiOpt = phienChoiRepository.findById(id);

	    if (phienChoiOpt.isPresent()) {
	        PhienChoi phienChoi = phienChoiOpt.get();
	        model.addAttribute("phienChoi", phienChoi);
	        return "views/chitiet-phienchoi";
	    } else {
	        model.addAttribute("message", "Không tìm thấy phiên chơi.");
	        return "views/error";
	    }
	}
}

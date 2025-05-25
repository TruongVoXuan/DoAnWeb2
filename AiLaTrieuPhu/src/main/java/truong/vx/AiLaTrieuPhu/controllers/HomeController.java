package truong.vx.AiLaTrieuPhu.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import truong.vx.AiLaTrieuPhu.models.CauHoi;
import truong.vx.AiLaTrieuPhu.models.NguoiChoi;
import truong.vx.AiLaTrieuPhu.models.PhienChoi;
import truong.vx.AiLaTrieuPhu.repositories.CauHoiRepository;
import truong.vx.AiLaTrieuPhu.repositories.NguoiChoiRepository;
import truong.vx.AiLaTrieuPhu.repositories.PhienChoiRepository;

@Controller
public class HomeController {

    @Autowired
    private NguoiChoiRepository nguoiChoiRepository;

    @Autowired
    private PhienChoiRepository phienChoiRepository;

    @Autowired
    private CauHoiRepository cauHoiRepository;

    @Autowired
    private GameController gameController;

    @GetMapping("/home")
    public String showStartPage() {
        return "views/Home";
    }

    @GetMapping("/batdau")
    public String showNhapThongTinPage(Model model) {
        model.addAttribute("nguoichoi", new NguoiChoi());
        return "views/batdau";
    }

    @PostMapping("/luu-thong-tin")
    public String luuNguoiChoi(@ModelAttribute("nguoichoi") NguoiChoi nguoiChoi) {
        nguoiChoiRepository.save(nguoiChoi);
        return "redirect:/choigame";
    }

    @GetMapping("/choigame")
    public String choiGame(Model model, HttpSession session) {
        int capDo = 1;
        List<CauHoi> danhSachCauHoi = cauHoiRepository.findByCapdo(capDo);
        if (danhSachCauHoi.isEmpty()) {
            throw new RuntimeException("Không tìm thấy câu hỏi cho cấp độ " + capDo);
        }

        Random random = new Random();
        CauHoi cauHoi = danhSachCauHoi.get(random.nextInt(danhSachCauHoi.size()));

        // ✅ Lấy người chơi mới nhất
        NguoiChoi nguoiChoiMoi = nguoiChoiRepository.findTopByOrderByIdDesc();

        // ✅ Tạo phiên chơi mới
        PhienChoi phienChoi = new PhienChoi();
        phienChoi.setNguoichoi(nguoiChoiMoi);
        phienChoi.setThoigianBatdau(LocalDateTime.now());
        phienChoi.setSoCaudung(0);
        phienChoi = phienChoiRepository.save(phienChoi);

        // ✅ Lưu thông tin vào session
        session.setAttribute("phienChoiId", phienChoi.getId());
        session.setAttribute("soCauDung", 0); // bắt đầu từ 0
        session.setAttribute("daDung5050", false);   // lưu seesion cờ 5050
        session.setAttribute("daDungAudience", false); //lưu session cho hỏi ý kiến khán giả

        // ✅ Gửi dữ liệu ra giao diện
        model.addAttribute("cauHoi", cauHoi);
        model.addAttribute("capDo", capDo);
        model.addAttribute("currentMoney", 0);
        model.addAttribute("mucThuong", gameController.getMucThuongFormatted());

        return "views/cauhoi";
    }
    
    
    
    @PostMapping("/set-5050-used")
    @ResponseBody
    public void set5050Used(HttpSession session) {
        session.setAttribute("daDung5050", true);
    }
    
    
    @PostMapping("/set-audience-used")
    @ResponseBody
    public void setAudienceUsed(HttpSession session) {
        session.setAttribute("daDungAudience", true);
    }
}
	
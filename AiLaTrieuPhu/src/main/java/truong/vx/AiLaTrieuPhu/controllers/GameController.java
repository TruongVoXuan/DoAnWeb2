package truong.vx.AiLaTrieuPhu.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import truong.vx.AiLaTrieuPhu.models.CauHoi;
import truong.vx.AiLaTrieuPhu.models.PhienChoi;
import truong.vx.AiLaTrieuPhu.repositories.CauHoiRepository;
import truong.vx.AiLaTrieuPhu.repositories.PhienChoiRepository;

@Controller
public class GameController {

    @Autowired
    private CauHoiRepository cauHoiRepository;
    
    @Autowired
    private PhienChoiRepository phienChoiRepository;

    private final int[] mucthuong = {
        200, 400, 600, 1000, 2000, 3000,
        6000, 10000, 14000, 22000, 30000,
        40000, 60000, 85000, 150000
    };

    public List<String> getMucThuongFormatted() {
        return Arrays.stream(mucthuong)
                .mapToObj(value -> String.format("%,d", value * 1000) + " VNĐ")
                .toList();
    }

    public int getMucThuongByCapDo(int capDo) {
        return capDo >= 1 && capDo <= mucthuong.length ? mucthuong[capDo - 1] : 0;
    }

    @PostMapping("/choigame")
    public String checkAnswer(@RequestParam int capDo,
                              @RequestParam String dapAnChon,
                              Model model,HttpSession session) {

        List<CauHoi> danhSachCauHoi = cauHoiRepository.findByCapdo(capDo);
        if (danhSachCauHoi.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy câu hỏi cho cấp độ " + capDo);
            return "views/error";
        }

        
        Boolean daDung5050 = (Boolean) session.getAttribute("daDung5050");
        model.addAttribute("daDung5050", daDung5050 != null && daDung5050);
        
        
        Boolean daDungAudience = (Boolean) session.getAttribute("daDungAudience");
        model.addAttribute("daDungAudience", daDungAudience != null && daDungAudience);
        
        CauHoi cauHoiHienTai = danhSachCauHoi.get(0);
        boolean isCorrect = cauHoiHienTai.getDapAnDung().trim().equalsIgnoreCase(dapAnChon.trim());

        
        
        if (isCorrect) {
        	
        	 // ✅ Tăng số câu đúng trong session
            Integer soCauDung = (Integer) session.getAttribute("soCauDung");
            if (soCauDung == null) soCauDung = 0;
            soCauDung++;
            session.setAttribute("soCauDung", soCauDung);
            
            
        	
            model.addAttribute("cauHoi", cauHoiHienTai); // ✅ bổ sung
            model.addAttribute("capDo", capDo);
            model.addAttribute("currentMoney", getMucThuongByCapDo(capDo));
            model.addAttribute("dapAnChon", dapAnChon);
            model.addAttribute("isCorrect", true);
            model.addAttribute("mucThuong", getMucThuongFormatted()); // ✅ bổ sung để hiển thị thang tiền
            return "views/cauhoi";
        } else {
        	
        	  // ✅ Cập nhật kết thúc phiên chơi
            Integer phienChoiId = (Integer) session.getAttribute("phienChoiId");
            Integer soCauDung = (Integer) session.getAttribute("soCauDung");
            if (phienChoiId != null && soCauDung != null) {
                Optional<PhienChoi> optionalPhienChoi = phienChoiRepository.findById(phienChoiId);
                if (optionalPhienChoi.isPresent()) {
                    PhienChoi phien = optionalPhienChoi.get();
                    phien.setThoigianKetthuc(LocalDateTime.now());
                    phien.setSoCaudung(soCauDung);
                    phienChoiRepository.save(phien);
                }
                model.addAttribute("phienChoiId", phienChoiId); // ✅ THÊM MỚI để truyền qua HTML
            }
    
            model.addAttribute("message", "Đáp án sai! Trò chơi kết thúc.");
            model.addAttribute("money", getMucThuongByCapDo(capDo - 1));
            return "views/ketthuc";
            
        }
    }

    @PostMapping("/choitiep")
    public String choiTiep(@RequestParam int capDo, Model model, HttpSession session) {
        List<CauHoi> danhSachCauHoiMoi = cauHoiRepository.findByCapdo(capDo);
  

        CauHoi cauHoiMoi = danhSachCauHoiMoi.get(0);
        model.addAttribute("cauHoi", cauHoiMoi);
        model.addAttribute("capDo", capDo);
        model.addAttribute("mucThuong", getMucThuongFormatted());
        model.addAttribute("isCorrect", false); 
        
        // đặt cờ 5050 vào seesion  để ẩn đi 5050 khi chơi tiếp 
        Boolean daDung5050 = (Boolean) session.getAttribute("daDung5050");
        model.addAttribute("daDung5050", daDung5050 != null && daDung5050);
        
        Boolean daDungAudience = (Boolean) session.getAttribute("daDungAudience");
        model.addAttribute("daDungAudience", daDungAudience != null && daDungAudience);
        
        return "views/cauhoi";
    }
    @PostMapping("/ketthuc")
    public String ketThuc(@RequestParam int capDo, Model model , HttpSession session) {
    	
    	
    	
    	  // ✅ Cập nhật thời gian kết thúc nếu người chơi tự ngưng
        Integer phienChoiId = (Integer) session.getAttribute("phienChoiId");
        Integer soCauDung = (Integer) session.getAttribute("soCauDung");
        if (phienChoiId != null && soCauDung != null) {
            Optional<PhienChoi> optionalPhienChoi = phienChoiRepository.findById(phienChoiId);
            if (optionalPhienChoi.isPresent()) {
                PhienChoi phien = optionalPhienChoi.get();
                phien.setThoigianKetthuc(LocalDateTime.now());
                phien.setSoCaudung(soCauDung);
                phienChoiRepository.save(phien);
            }
            
            model.addAttribute("phienChoiId", phienChoiId); // ✅ THÊM MỚI để truyền qua HTML
        }
    	
    	
    	
        model.addAttribute("message", "Bạn đã dừng cuộc chơi.");
        model.addAttribute("money", getMucThuongByCapDo(capDo));
        return "views/ngungchoi";
    }
    
    
    
    
    
    @PostMapping("/trogiup-audience")
    @ResponseBody
    public Map<String, Integer> hoiKhanGia(@RequestParam String dapAnDung) {
        Map<String, Integer> ketQua = new HashMap<>();
        Random random = new Random();

        int dung = 50 + random.nextInt(21); // 50% - 70%
        int sai1 = random.nextInt(100 - dung + 1);
        int sai2 = random.nextInt(100 - dung - sai1 + 1);
        int sai3 = 100 - dung - sai1 - sai2;

        List<String> dapAn = Arrays.asList("A", "B", "C", "D");
        List<String> saiDapAn = new ArrayList<>(dapAn);
        saiDapAn.remove(dapAnDung);

        ketQua.put(dapAnDung, dung);
        ketQua.put(saiDapAn.get(0), sai1);
        ketQua.put(saiDapAn.get(1), sai2);
        ketQua.put(saiDapAn.get(2), sai3);

        return ketQua;
    }

}

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
import org.springframework.web.bind.annotation.GetMapping;
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
                              @RequestParam int cauHoiId,
                              Model model,HttpSession session) {


			Optional<CauHoi> optionalCauHoi = cauHoiRepository.findById(cauHoiId);
			if (!optionalCauHoi.isPresent()) {
			model.addAttribute("errorMessage", "Không tìm thấy câu hỏi.");
			return "views/error";
			}


        
        Boolean daDung5050 = (Boolean) session.getAttribute("daDung5050");
        model.addAttribute("daDung5050", daDung5050 != null && daDung5050);
        
        
        Boolean daDungAudience = (Boolean) session.getAttribute("daDungAudience");
        model.addAttribute("daDungAudience", daDungAudience != null && daDungAudience);
        

        CauHoi cauHoiHienTai = optionalCauHoi.get();

        boolean isCorrect = cauHoiHienTai.getDapAnDung().trim().equalsIgnoreCase(dapAnChon.trim());

        
        
        if (isCorrect) {
        	
        	 // ✅ Tăng số câu đúng trong session
            Integer soCauDung = (Integer) session.getAttribute("soCauDung");
            if (soCauDung == null) soCauDung = 0;
            soCauDung++;
            session.setAttribute("soCauDung", soCauDung);
            
            
            //Sử lý để end game khi chọn câu đúng
            if (capDo == 15) {
                // Lưu kết quả vào DB
                Integer phienChoiId = (Integer) session.getAttribute("phienChoiId");
                if (phienChoiId != null) {
                    Optional<PhienChoi> optionalPhienChoi = phienChoiRepository.findById(phienChoiId);
                    if (optionalPhienChoi.isPresent()) {
                        PhienChoi phien = optionalPhienChoi.get();
                        phien.setThoigianKetthuc(LocalDateTime.now());
                        phien.setSoCaudung(soCauDung);
                        phienChoiRepository.save(phien);
                        model.addAttribute("phienChoiId", phienChoiId);
                    }
                }

                model.addAttribute("message", "Chúc mừng bạn đã chiến thắng trò chơi!");
                model.addAttribute("money", getMucThuongByCapDo(capDo));
                return "views/wingame";
            }
            
            
        	
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
    

      
            model.addAttribute("cauHoi", cauHoiHienTai);
            model.addAttribute("capDo", capDo);
            model.addAttribute("dapAnChon", dapAnChon);
            model.addAttribute("dapAnDung", cauHoiHienTai.getDapAnDung());
            model.addAttribute("isCorrect", false);
            model.addAttribute("daChonSai", true);
            model.addAttribute("mucThuong", getMucThuongFormatted());

            model.addAttribute("message", "Đáp án sai! Trò chơi kết thúc.");
            model.addAttribute("money", getMucThuongByCapDo(capDo - 1));
            model.addAttribute("phienChoiId", phienChoiId);  // thêm để gửi sang ketthuc
            return "views/cauhoi";

            
        }
    }
    

    @PostMapping("/choitiep")
    public String choiTiep(@RequestParam int capDo, Model model, HttpSession session) {
        List<CauHoi> danhSachCauHoiMoi = cauHoiRepository.findByCapdo(capDo);

        if (danhSachCauHoiMoi == null || danhSachCauHoiMoi.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy câu hỏi cho cấp độ " + capDo);
            return "views/error";
        }

        // ✅ Random 1 câu hỏi bất kỳ từ danh sách
        Random random = new Random();
        int index = random.nextInt(danhSachCauHoiMoi.size());
        CauHoi cauHoiMoi = danhSachCauHoiMoi.get(index);

        // ✅ Lưu câu hỏi hiện tại vào session
        session.setAttribute("cauHoiHienTai", cauHoiMoi);

        // ✅ Truyền dữ liệu sang view
        model.addAttribute("cauHoi", cauHoiMoi);
        model.addAttribute("capDo", capDo);
        model.addAttribute("mucThuong", getMucThuongFormatted());
        model.addAttribute("isCorrect", false);
        model.addAttribute("daChonSai", false);


        // ✅ Truyền thông tin trợ giúp (nếu đã dùng)
        Boolean daDung5050 = (Boolean) session.getAttribute("daDung5050");
        model.addAttribute("daDung5050", daDung5050 != null && daDung5050);

        Boolean daDungAudience = (Boolean) session.getAttribute("daDungAudience");
        model.addAttribute("daDungAudience", daDungAudience != null && daDungAudience);

        return "views/cauhoi";
    }

    @PostMapping("/ngungchoi")
    public String ngungChoi(@RequestParam int capDo, Model model, HttpSession session) {

        // ✅ Xử lý lưu thông tin phiên chơi
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
            model.addAttribute("phienChoiId", phienChoiId);
        }

        // ✅ Truyền các thuộc tính cần thiết
        model.addAttribute("money", getMucThuongByCapDo(capDo));

        // ✅ Truyền cờ xác định đây là "ngừng chơi" chứ KHÔNG phải "chọn sai"
        model.addAttribute("daChonSai", false); // hoặc không truyền luôn cũng được

        return "views/ngungchoi"; // View này sẽ quyết định hiển thị câu nào
    }



    @PostMapping("/ketthuc")
    public String ketThuc(@RequestParam int capDo,
                          @RequestParam(required = false) Integer phienChoiId,
                          Model model, HttpSession session) {

        if (phienChoiId == null) {
            phienChoiId = (Integer) session.getAttribute("phienChoiId");
        }

        Integer soCauDung = (Integer) session.getAttribute("soCauDung");

        if (phienChoiId != null && soCauDung != null) {
            Optional<PhienChoi> optionalPhienChoi = phienChoiRepository.findById(phienChoiId);
            if (optionalPhienChoi.isPresent()) {
                PhienChoi phien = optionalPhienChoi.get();
                phien.setThoigianKetthuc(LocalDateTime.now());
                phien.setSoCaudung(soCauDung);
                phienChoiRepository.save(phien);
            }
            model.addAttribute("phienChoiId", phienChoiId);
        }

        model.addAttribute("money", getMucThuongByCapDo(capDo - 1)); // -1 vì sai câu đó
        model.addAttribute("daChonSai", true); // ✅ Cờ để view biết là do chọn sai

        return "views/ketthuc";
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

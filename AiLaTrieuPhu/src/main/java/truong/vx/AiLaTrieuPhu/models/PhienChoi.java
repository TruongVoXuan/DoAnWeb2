package truong.vx.AiLaTrieuPhu.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "phienchoi")
public class PhienChoi {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nguoichoi_id")
    private NguoiChoi nguoichoi;

    private LocalDateTime thoigianBatdau;
    private LocalDateTime thoigianKetthuc;
    private int soCaudung;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public NguoiChoi getNguoichoi() {
		return nguoichoi;
	}
	public void setNguoichoi(NguoiChoi nguoichoi) {
		this.nguoichoi = nguoichoi;
	}
	public LocalDateTime getThoigianBatdau() {
		return thoigianBatdau;
	}
	public void setThoigianBatdau(LocalDateTime thoigianBatdau) {
		this.thoigianBatdau = thoigianBatdau;
	}
	public LocalDateTime getThoigianKetthuc() {
		return thoigianKetthuc;
	}
	public void setThoigianKetthuc(LocalDateTime thoigianKetthuc) {
		this.thoigianKetthuc = thoigianKetthuc;
	}
	public int getSoCaudung() {
		return soCaudung;
	}
	public void setSoCaudung(int soCaudung) {
		this.soCaudung = soCaudung;
	}
    
}

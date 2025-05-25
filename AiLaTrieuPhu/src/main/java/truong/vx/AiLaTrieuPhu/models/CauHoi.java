package truong.vx.AiLaTrieuPhu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "cauhoi")
public class CauHoi {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String noidung;

    @Column(name = "phuongan_a")
    private String phuongAnA;

    @Column(name = "phuongan_b")
    private String phuongAnB;

    @Column(name = "phuongan_c")
    private String phuongAnC;

    @Column(name = "phuongan_d")
    private String phuongAnD;

    @Column(name = "dapan_dung", length = 1)
    private String dapAnDung;


    private int capdo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNoidung() {
		return noidung;
	}

	public void setNoidung(String noidung) {
		this.noidung = noidung;
	}

	public String getPhuongAnA() {
		return phuongAnA;
	}

	public void setPhuongAnA(String phuongAnA) {
		this.phuongAnA = phuongAnA;
	}

	public String getPhuongAnB() {
		return phuongAnB;
	}

	public void setPhuongAnB(String phuongAnB) {
		this.phuongAnB = phuongAnB;
	}

	public String getPhuongAnC() {
		return phuongAnC;
	}

	public void setPhuongAnC(String phuongAnC) {
		this.phuongAnC = phuongAnC;
	}

	public String getPhuongAnD() {
		return phuongAnD;
	}

	public void setPhuongAnD(String phuongAnD) {
		this.phuongAnD = phuongAnD;
	}

	public String getDapAnDung() {
		return dapAnDung;
	}

	public void setDapAnDung(String dapAnDung) {
		this.dapAnDung = dapAnDung;
	}

	public int getCapdo() {
		return capdo;
	}

	public void setCapdo(int capdo) {
		this.capdo = capdo;
	}
    
    

}

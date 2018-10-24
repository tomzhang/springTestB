package com.jk51.model.health;

import java.io.Serializable;

/**
 * @author 
 */
public class YbMemberHealth implements Serializable {
    private Integer id;

    /**
     * 会员ID 对应yb_member.id
     */
    private Integer memberId;

    /**
     * 卡号 手机号或者身份证号
     */
    private String cardNo;

    /**
     * 身高
     */
    private Float height;

    /**
     * 体重
     */
    private Float weight;

    /**
     * BMI
     */
    private Float bmi;

    /**
     * 收缩压
     */
    private Integer sbp;

    /**
     * 舒张压
     */
    private Integer dbp;

    /**
     * 脉搏
     */
    private Integer pulse;

    /**
     * 血氧
     */
    private Integer bo;

    /**
     * 脂肪率
     */
    private Float fat;

    /**
     * 基础代谢
     */
    private Integer bmr;

    /**
     * 水分含量
     */
    private Integer water;

    /**
     * 腰围
     */
    private Float waistline;

    /**
     * 臀围
     */
    private Float hipline;

    /**
     * 腰臀比
     */
    private Float whr;

    /**
     * PEF
     */
    private Integer PEF;

    /**
     * FVC
     */
    private Float FVC;

    /**
     * FEV1
     */
    private Float FEV1;

    /**
     * 体温
     */
    private Float TT;

    /**
     * 左眼视力
     */
    private Float visionLeft;

    /**
     * 右眼视力
     */
    private Float visionRight;

    /**
     * 体质检测结果
     */
    private String CMP;

    /**
     * 血糖
     */
    private Float glu;

    /**
     * 测量时间与上次用餐时间的差(0,1,2 其中0 代表∞空腹)
     */
    private Integer hoursAfterMeal;

    /**
     * 尿酸
     */
    private Float ua;

    /**
     * 胆固醇
     */
    private Float chol;

    /**
     * 糖化血红蛋白
     */
    private Float NGSP;

    /**
     * 总胆固醇
     */
    private Float CHOL1;

    /**
     * 甘油三酯
     */
    private Float TG;

    /**
     * 高密度脂蛋白
     */
    private Float HDL;

    /**
     * 低密度脂蛋白
     */
    private Float LDL;

    /**
     * 肺功能结果值
     */
    private String FGCParam;

    /**
     * 肺功能数据值
     */
    private String FGCData;

    /**
     * 尿胆原 +-
     */
    private String URO;

    /**
     * 潜血 +1
     */
    private String BLD;

    /**
     * 胆红素 -
     */
    private String BIL;

    /**
     * 酮体 -
     */
    private String KET;

    /**
     * 尿糖 0
     */
    private String BC_GLU;

    /**
     * 尿蛋白 -
     */
    private String PRO;

    /**
     * 酸碱度
     */
    private Float PH;

    /**
     * 尿亚硝酸盐 +
     */
    private String NIT;

    /**
     * 尿白细胞
     */
    private String LEU;

    /**
     * 尿比重
     */
    private Float SG;

    /**
     * 维生素C
     */
    private String VC;

    /**
     * 心电图地址
     */
    private String ecgpng_url;

    /**
     * 心电图结果
     */
    private String ecg_result;

    /**
     * 骨密度仪图片文件
     */
    private String bd_png;

    /**
     * 骨密度仪xml文件
     */
    private String bd_xml;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getBmi() {
        return bmi;
    }

    public void setBmi(Float bmi) {
        this.bmi = bmi;
    }

    public Integer getSbp() {
        return sbp;
    }

    public void setSbp(Integer sbp) {
        this.sbp = sbp;
    }

    public Integer getDbp() {
        return dbp;
    }

    public void setDbp(Integer dbp) {
        this.dbp = dbp;
    }

    public Integer getPulse() {
        return pulse;
    }

    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }

    public Integer getBo() {
        return bo;
    }

    public void setBo(Integer bo) {
        this.bo = bo;
    }

    public Float getFat() {
        return fat;
    }

    public void setFat(Float fat) {
        this.fat = fat;
    }

    public Integer getBmr() {
        return bmr;
    }

    public void setBmr(Integer bmr) {
        this.bmr = bmr;
    }

    public Integer getWater() {
        return water;
    }

    public void setWater(Integer water) {
        this.water = water;
    }

    public Float getWaistline() {
        return waistline;
    }

    public void setWaistline(Float waistline) {
        this.waistline = waistline;
    }

    public Float getHipline() {
        return hipline;
    }

    public void setHipline(Float hipline) {
        this.hipline = hipline;
    }

    public Float getWhr() {
        return whr;
    }

    public void setWhr(Float whr) {
        this.whr = whr;
    }

    public Integer getPEF() {
        return PEF;
    }

    public void setPEF(Integer PEF) {
        this.PEF = PEF;
    }

    public Float getFVC() {
        return FVC;
    }

    public void setFVC(Float FVC) {
        this.FVC = FVC;
    }

    public Float getFEV1() {
        return FEV1;
    }

    public void setFEV1(Float FEV1) {
        this.FEV1 = FEV1;
    }

    public Float getTT() {
        return TT;
    }

    public void setTT(Float TT) {
        this.TT = TT;
    }

    public Float getVisionLeft() {
        return visionLeft;
    }

    public void setVisionLeft(Float visionLeft) {
        this.visionLeft = visionLeft;
    }

    public Float getVisionRight() {
        return visionRight;
    }

    public void setVisionRight(Float visionRight) {
        this.visionRight = visionRight;
    }

    public String getCMP() {
        return CMP;
    }

    public void setCMP(String CMP) {
        this.CMP = CMP;
    }

    public Float getGlu() {
        return glu;
    }

    public void setGlu(Float glu) {
        this.glu = glu;
    }

    public Integer getHoursAfterMeal() {
        return hoursAfterMeal;
    }

    public void setHoursAfterMeal(Integer hoursAfterMeal) {
        this.hoursAfterMeal = hoursAfterMeal;
    }

    public Float getUa() {
        return ua;
    }

    public void setUa(Float ua) {
        this.ua = ua;
    }

    public Float getChol() {
        return chol;
    }

    public void setChol(Float chol) {
        this.chol = chol;
    }

    public Float getNGSP() {
        return NGSP;
    }

    public void setNGSP(Float NGSP) {
        this.NGSP = NGSP;
    }

    public Float getCHOL1() {
        return CHOL1;
    }

    public void setCHOL1(Float CHOL1) {
        this.CHOL1 = CHOL1;
    }

    public Float getTG() {
        return TG;
    }

    public void setTG(Float TG) {
        this.TG = TG;
    }

    public Float getHDL() {
        return HDL;
    }

    public void setHDL(Float HDL) {
        this.HDL = HDL;
    }

    public Float getLDL() {
        return LDL;
    }

    public void setLDL(Float LDL) {
        this.LDL = LDL;
    }

    public String getFGCParam() {
        return FGCParam;
    }

    public void setFGCParam(String FGCParam) {
        this.FGCParam = FGCParam;
    }

    public String getFGCData() {
        return FGCData;
    }

    public void setFGCData(String FGCData) {
        this.FGCData = FGCData;
    }

    public String getURO() {
        return URO;
    }

    public void setURO(String URO) {
        this.URO = URO;
    }

    public String getBLD() {
        return BLD;
    }

    public void setBLD(String BLD) {
        this.BLD = BLD;
    }

    public String getBIL() {
        return BIL;
    }

    public void setBIL(String BIL) {
        this.BIL = BIL;
    }

    public String getKET() {
        return KET;
    }

    public void setKET(String KET) {
        this.KET = KET;
    }

    public String getBC_GLU() {
        return BC_GLU;
    }

    public void setBC_GLU(String BC_GLU) {
        this.BC_GLU = BC_GLU;
    }

    public String getPRO() {
        return PRO;
    }

    public void setPRO(String PRO) {
        this.PRO = PRO;
    }

    public Float getPH() {
        return PH;
    }

    public void setPH(Float PH) {
        this.PH = PH;
    }

    public String getNIT() {
        return NIT;
    }

    public void setNIT(String NIT) {
        this.NIT = NIT;
    }

    public String getLEU() {
        return LEU;
    }

    public void setLEU(String LEU) {
        this.LEU = LEU;
    }

    public Float getSG() {
        return SG;
    }

    public void setSG(Float SG) {
        this.SG = SG;
    }

    public String getVC() {
        return VC;
    }

    public void setVC(String VC) {
        this.VC = VC;
    }

    public String getEcgpng_url() {
        return ecgpng_url;
    }

    public void setEcgpng_url(String ecgpng_url) {
        this.ecgpng_url = ecgpng_url;
    }

    public String getEcg_result() {
        return ecg_result;
    }

    public void setEcg_result(String ecg_result) {
        this.ecg_result = ecg_result;
    }

    public String getBd_png() {
        return bd_png;
    }

    public void setBd_png(String bd_png) {
        this.bd_png = bd_png;
    }

    public String getBd_xml() {
        return bd_xml;
    }

    public void setBd_xml(String bd_xml) {
        this.bd_xml = bd_xml;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        YbMemberHealth other = (YbMemberHealth) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getMemberId() == null ? other.getMemberId() == null : this.getMemberId().equals(other.getMemberId()))
            && (this.getCardNo() == null ? other.getCardNo() == null : this.getCardNo().equals(other.getCardNo()))
            && (this.getHeight() == null ? other.getHeight() == null : this.getHeight().equals(other.getHeight()))
            && (this.getWeight() == null ? other.getWeight() == null : this.getWeight().equals(other.getWeight()))
            && (this.getBmi() == null ? other.getBmi() == null : this.getBmi().equals(other.getBmi()))
            && (this.getSbp() == null ? other.getSbp() == null : this.getSbp().equals(other.getSbp()))
            && (this.getDbp() == null ? other.getDbp() == null : this.getDbp().equals(other.getDbp()))
            && (this.getPulse() == null ? other.getPulse() == null : this.getPulse().equals(other.getPulse()))
            && (this.getBo() == null ? other.getBo() == null : this.getBo().equals(other.getBo()))
            && (this.getFat() == null ? other.getFat() == null : this.getFat().equals(other.getFat()))
            && (this.getBmr() == null ? other.getBmr() == null : this.getBmr().equals(other.getBmr()))
            && (this.getWater() == null ? other.getWater() == null : this.getWater().equals(other.getWater()))
            && (this.getWaistline() == null ? other.getWaistline() == null : this.getWaistline().equals(other.getWaistline()))
            && (this.getHipline() == null ? other.getHipline() == null : this.getHipline().equals(other.getHipline()))
            && (this.getWhr() == null ? other.getWhr() == null : this.getWhr().equals(other.getWhr()))
            && (this.getPEF() == null ? other.getPEF() == null : this.getPEF().equals(other.getPEF()))
            && (this.getFVC() == null ? other.getFVC() == null : this.getFVC().equals(other.getFVC()))
            && (this.getFEV1() == null ? other.getFEV1() == null : this.getFEV1().equals(other.getFEV1()))
            && (this.getTT() == null ? other.getTT() == null : this.getTT().equals(other.getTT()))
            && (this.getVisionLeft() == null ? other.getVisionLeft() == null : this.getVisionLeft().equals(other.getVisionLeft()))
            && (this.getVisionRight() == null ? other.getVisionRight() == null : this.getVisionRight().equals(other.getVisionRight()))
            && (this.getCMP() == null ? other.getCMP() == null : this.getCMP().equals(other.getCMP()))
            && (this.getGlu() == null ? other.getGlu() == null : this.getGlu().equals(other.getGlu()))
            && (this.getHoursAfterMeal() == null ? other.getHoursAfterMeal() == null : this.getHoursAfterMeal().equals(other.getHoursAfterMeal()))
            && (this.getUa() == null ? other.getUa() == null : this.getUa().equals(other.getUa()))
            && (this.getChol() == null ? other.getChol() == null : this.getChol().equals(other.getChol()))
            && (this.getNGSP() == null ? other.getNGSP() == null : this.getNGSP().equals(other.getNGSP()))
            && (this.getCHOL1() == null ? other.getCHOL1() == null : this.getCHOL1().equals(other.getCHOL1()))
            && (this.getTG() == null ? other.getTG() == null : this.getTG().equals(other.getTG()))
            && (this.getHDL() == null ? other.getHDL() == null : this.getHDL().equals(other.getHDL()))
            && (this.getLDL() == null ? other.getLDL() == null : this.getLDL().equals(other.getLDL()))
            && (this.getFGCParam() == null ? other.getFGCParam() == null : this.getFGCParam().equals(other.getFGCParam()))
            && (this.getFGCData() == null ? other.getFGCData() == null : this.getFGCData().equals(other.getFGCData()))
            && (this.getURO() == null ? other.getURO() == null : this.getURO().equals(other.getURO()))
            && (this.getBLD() == null ? other.getBLD() == null : this.getBLD().equals(other.getBLD()))
            && (this.getBIL() == null ? other.getBIL() == null : this.getBIL().equals(other.getBIL()))
            && (this.getKET() == null ? other.getKET() == null : this.getKET().equals(other.getKET()))
            && (this.getBC_GLU() == null ? other.getBC_GLU() == null : this.getBC_GLU().equals(other.getBC_GLU()))
            && (this.getPRO() == null ? other.getPRO() == null : this.getPRO().equals(other.getPRO()))
            && (this.getPH() == null ? other.getPH() == null : this.getPH().equals(other.getPH()))
            && (this.getNIT() == null ? other.getNIT() == null : this.getNIT().equals(other.getNIT()))
            && (this.getLEU() == null ? other.getLEU() == null : this.getLEU().equals(other.getLEU()))
            && (this.getSG() == null ? other.getSG() == null : this.getSG().equals(other.getSG()))
            && (this.getVC() == null ? other.getVC() == null : this.getVC().equals(other.getVC()))
            && (this.getEcgpng_url() == null ? other.getEcgpng_url() == null : this.getEcgpng_url().equals(other.getEcgpng_url()))
            && (this.getEcg_result() == null ? other.getEcg_result() == null : this.getEcg_result().equals(other.getEcg_result()))
            && (this.getBd_png() == null ? other.getBd_png() == null : this.getBd_png().equals(other.getBd_png()))
            && (this.getBd_xml() == null ? other.getBd_xml() == null : this.getBd_xml().equals(other.getBd_xml()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getMemberId() == null) ? 0 : getMemberId().hashCode());
        result = prime * result + ((getCardNo() == null) ? 0 : getCardNo().hashCode());
        result = prime * result + ((getHeight() == null) ? 0 : getHeight().hashCode());
        result = prime * result + ((getWeight() == null) ? 0 : getWeight().hashCode());
        result = prime * result + ((getBmi() == null) ? 0 : getBmi().hashCode());
        result = prime * result + ((getSbp() == null) ? 0 : getSbp().hashCode());
        result = prime * result + ((getDbp() == null) ? 0 : getDbp().hashCode());
        result = prime * result + ((getPulse() == null) ? 0 : getPulse().hashCode());
        result = prime * result + ((getBo() == null) ? 0 : getBo().hashCode());
        result = prime * result + ((getFat() == null) ? 0 : getFat().hashCode());
        result = prime * result + ((getBmr() == null) ? 0 : getBmr().hashCode());
        result = prime * result + ((getWater() == null) ? 0 : getWater().hashCode());
        result = prime * result + ((getWaistline() == null) ? 0 : getWaistline().hashCode());
        result = prime * result + ((getHipline() == null) ? 0 : getHipline().hashCode());
        result = prime * result + ((getWhr() == null) ? 0 : getWhr().hashCode());
        result = prime * result + ((getPEF() == null) ? 0 : getPEF().hashCode());
        result = prime * result + ((getFVC() == null) ? 0 : getFVC().hashCode());
        result = prime * result + ((getFEV1() == null) ? 0 : getFEV1().hashCode());
        result = prime * result + ((getTT() == null) ? 0 : getTT().hashCode());
        result = prime * result + ((getVisionLeft() == null) ? 0 : getVisionLeft().hashCode());
        result = prime * result + ((getVisionRight() == null) ? 0 : getVisionRight().hashCode());
        result = prime * result + ((getCMP() == null) ? 0 : getCMP().hashCode());
        result = prime * result + ((getGlu() == null) ? 0 : getGlu().hashCode());
        result = prime * result + ((getHoursAfterMeal() == null) ? 0 : getHoursAfterMeal().hashCode());
        result = prime * result + ((getUa() == null) ? 0 : getUa().hashCode());
        result = prime * result + ((getChol() == null) ? 0 : getChol().hashCode());
        result = prime * result + ((getNGSP() == null) ? 0 : getNGSP().hashCode());
        result = prime * result + ((getCHOL1() == null) ? 0 : getCHOL1().hashCode());
        result = prime * result + ((getTG() == null) ? 0 : getTG().hashCode());
        result = prime * result + ((getHDL() == null) ? 0 : getHDL().hashCode());
        result = prime * result + ((getLDL() == null) ? 0 : getLDL().hashCode());
        result = prime * result + ((getFGCParam() == null) ? 0 : getFGCParam().hashCode());
        result = prime * result + ((getFGCData() == null) ? 0 : getFGCData().hashCode());
        result = prime * result + ((getURO() == null) ? 0 : getURO().hashCode());
        result = prime * result + ((getBLD() == null) ? 0 : getBLD().hashCode());
        result = prime * result + ((getBIL() == null) ? 0 : getBIL().hashCode());
        result = prime * result + ((getKET() == null) ? 0 : getKET().hashCode());
        result = prime * result + ((getBC_GLU() == null) ? 0 : getBC_GLU().hashCode());
        result = prime * result + ((getPRO() == null) ? 0 : getPRO().hashCode());
        result = prime * result + ((getPH() == null) ? 0 : getPH().hashCode());
        result = prime * result + ((getNIT() == null) ? 0 : getNIT().hashCode());
        result = prime * result + ((getLEU() == null) ? 0 : getLEU().hashCode());
        result = prime * result + ((getSG() == null) ? 0 : getSG().hashCode());
        result = prime * result + ((getVC() == null) ? 0 : getVC().hashCode());
        result = prime * result + ((getEcgpng_url() == null) ? 0 : getEcgpng_url().hashCode());
        result = prime * result + ((getEcg_result() == null) ? 0 : getEcg_result().hashCode());
        result = prime * result + ((getBd_png() == null) ? 0 : getBd_png().hashCode());
        result = prime * result + ((getBd_xml() == null) ? 0 : getBd_xml().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", cardNo=").append(cardNo);
        sb.append(", height=").append(height);
        sb.append(", weight=").append(weight);
        sb.append(", bmi=").append(bmi);
        sb.append(", sbp=").append(sbp);
        sb.append(", dbp=").append(dbp);
        sb.append(", pulse=").append(pulse);
        sb.append(", bo=").append(bo);
        sb.append(", fat=").append(fat);
        sb.append(", bmr=").append(bmr);
        sb.append(", water=").append(water);
        sb.append(", waistline=").append(waistline);
        sb.append(", hipline=").append(hipline);
        sb.append(", whr=").append(whr);
        sb.append(", PEF=").append(PEF);
        sb.append(", FVC=").append(FVC);
        sb.append(", FEV1=").append(FEV1);
        sb.append(", TT=").append(TT);
        sb.append(", visionLeft=").append(visionLeft);
        sb.append(", visionRight=").append(visionRight);
        sb.append(", CMP=").append(CMP);
        sb.append(", glu=").append(glu);
        sb.append(", hoursAfterMeal=").append(hoursAfterMeal);
        sb.append(", ua=").append(ua);
        sb.append(", chol=").append(chol);
        sb.append(", NGSP=").append(NGSP);
        sb.append(", CHOL1=").append(CHOL1);
        sb.append(", TG=").append(TG);
        sb.append(", HDL=").append(HDL);
        sb.append(", LDL=").append(LDL);
        sb.append(", FGCParam=").append(FGCParam);
        sb.append(", FGCData=").append(FGCData);
        sb.append(", URO=").append(URO);
        sb.append(", BLD=").append(BLD);
        sb.append(", BIL=").append(BIL);
        sb.append(", KET=").append(KET);
        sb.append(", BC_GLU=").append(BC_GLU);
        sb.append(", PRO=").append(PRO);
        sb.append(", PH=").append(PH);
        sb.append(", NIT=").append(NIT);
        sb.append(", LEU=").append(LEU);
        sb.append(", SG=").append(SG);
        sb.append(", VC=").append(VC);
        sb.append(", ecgpng_url=").append(ecgpng_url);
        sb.append(", ecg_result=").append(ecg_result);
        sb.append(", bd_png=").append(bd_png);
        sb.append(", bd_xml=").append(bd_xml);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
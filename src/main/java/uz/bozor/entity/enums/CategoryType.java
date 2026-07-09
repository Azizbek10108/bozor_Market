package uz.bozor.entity.enums;

public enum CategoryType {

    // Kiyim-kechak va aksessuarlar
    KIYIM_KECHAK("Kiyim-kechak"),
    AYOLLAR_KIYIM("Ayollar kiyimi"),
    ERKAKLAR_KIYIM("Erkaklar kiyimi"),
    BOLALAR_KIYIM("Bolalar kiyimi"),
    SPORT_KIYIM("Sport kiyim va poyabzal"),
    POYABZAL("Poyabzal"),
    SUMKA_AKSESSUAR("Sumka va aksessuarlar"),

    // Oziq-ovqat
    OZIQ_OVQAT("Oziq-ovqat"),
    MEVA_SABZAVOT("Meva va sabzavot"),
    GOʻSHT_BALIQ("Go'sht va baliq"),
    NON_KONDITYER("Non va konditeriya"),
    SUT_MAHSULOTLARI("Sut mahsulotlari"),
    ICHIMLIKLAR("Ichimliklar"),

    // Go'zallik va parvarish
    PARFYUMERIYA("Parfyumeriya va atir"),
    KOSMETIKA("Kosmetika va go'zallik"),
    KIMYO_GIGIYENA("Kimyo va gigiyena"),

    // Elektronika va texnika
    ELEKTRONIKA("Elektronika"),
    TELEFON_AKSESSUAR("Telefon va aksessuarlar"),
    MAISHIY_TEXNIKA("Maishiy texnika"),
    KOMPYUTER("Kompyuter va noutbuk"),

    // Uy va qurilish
    QURILISH_MATERIALLARI("Qurilish materiallari"),
    MEBEL("Mebel"),
    UY_JIHOZLARI("Uy jihozlari va idish-tovoq"),
    GULDASTA_OʻSIMLIK("Gul va o'simliklar"),
    ELEKTR_JIHOZLARI("Elektr jihozlari"),

    // Transport
    AVTOMOBIL("Avtomobil ehtiyot qismlari"),
    VELOSIPED_SKUTER("Velosiped va skuter"),

    // Salomatlik
    TIBBIYOT("Tibbiyot va dorilar"),
    OPTIKA("Optika va ko'zoynak"),
    SPORT_ANJOM("Sport anjomlar va fitness"),

    // Bolalar
    BOLALAR("Bolalar mahsulotlari"),
    OʻYINCHOQ("O'yinchoqlar"),

    // Xizmatlar va boshqalar
    QISHLOQ_XOJALIGI("Qishloq xo'jaligi"),
    KITOB_QIRTASIYA("Kitob va qirtasiya"),
    ZEV_ZARGARLIK("Zev va zargarlik buyumlari"),
    HAYVONLAR("Uy hayvonlari uchun"),
    BOSHQA("Boshqa tovarlar");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

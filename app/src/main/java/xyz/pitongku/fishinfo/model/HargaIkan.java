package xyz.pitongku.fishinfo.model;

public class HargaIkan {
    private String id;
    private String responden;
    private String harga;
    private String volume;

    public String getPasar() {
        return pasar;
    }

    public void setPasar(String pasar) {
        this.pasar = pasar;
    }

    private String pasar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResponden() {
        return responden;
    }

    public void setResponden(String responden) {
        this.responden = responden;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getVolume() {
        return volume;
    }

    public HargaIkan(String id, String responden, String harga, String pasar, String volume) {
        this.id = id;
        this.responden = responden;
        this.harga = harga;
        this.pasar = pasar;
        this.volume = volume;
    }
}

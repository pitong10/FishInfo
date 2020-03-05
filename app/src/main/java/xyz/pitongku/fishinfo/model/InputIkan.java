package xyz.pitongku.fishinfo.model;

public class InputIkan {
    private String id, namaIkan, value, volume, utama;

    public InputIkan(String id, String namaIkan, String value, String volume, String utama) {
        this.id = id;
        this.namaIkan = namaIkan;
        this.value = value;
        this.volume = volume;
        this.utama = utama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaIkan() {
        return namaIkan;
    }

    public void setNamaIkan(String namaIkan) {
        this.namaIkan = namaIkan;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVolume() {
        return volume;
    }

    public String getUtama() {
        return utama;
    }
}

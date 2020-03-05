package xyz.pitongku.fishinfo.model;

public class JmlIkan {
    private String id, namaIkan, volume, jumlah;

    public JmlIkan(String id, String namaIkan, String volume, String jumlah) {
        this.id = id;
        this.namaIkan = namaIkan;
        this.volume = volume;
        this.jumlah = jumlah;
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String value) {
        this.volume = volume;
    }

    public String getJumlah() { return jumlah; }

    public void setJumlah(String jumlah) { this.jumlah = jumlah; }
}

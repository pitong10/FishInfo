package xyz.pitongku.fishinfo.model;

public class Ikan {
    private String id, namaIkan;

    public Ikan(String id, String namaIkan){
        this.id = id;
        this.namaIkan = namaIkan;
    }

    public String getId() {return id;}

    public void setId(String id) { this.id = id;}

    public String getNamaIkan() {
        return namaIkan;
    }

    public void setNamaIkan(String namaIkan) {
        this.namaIkan = namaIkan;
    }
}

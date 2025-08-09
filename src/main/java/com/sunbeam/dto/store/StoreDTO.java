package com.sunbeam.dto.store;

public class StoreDTO {
    private Long id;
    private String name;
    private String area;
    private int status;

    public StoreDTO() {
    }

    public StoreDTO(Long id, String name, String area, int status) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StoreDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", area='" + area + '\'' +
                ", status=" + status +
                '}';
    }
}

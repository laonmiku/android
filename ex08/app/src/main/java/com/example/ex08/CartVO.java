package com.example.ex08;

public class CartVO {
    private String index;
    private String name;
    private String image;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "CartVO{" +
                "index='" + index + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

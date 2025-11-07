package com.utec.ioteste.logica.modelos;

public class DifTempRooms implements Comparable<DifTempRooms>{

    String src;
    double dif;

    public DifTempRooms(){
    }

    public DifTempRooms(String src, double dif) {
        this.src = src;
        this.dif = dif;
    }

    //Si la diferencia es menor movelo a la izquierda de la lista, si es mayor a la derecha
    @Override
    public int compareTo(DifTempRooms d){
        if(this.dif < d.getDif()){
            return -1;
        }
        if(this.dif > d.getDif()){
            return 1;
        }
        return 0;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public double getDif() {
        return dif;
    }

    public void setDif(double dif) {
        this.dif = dif;
    }
}

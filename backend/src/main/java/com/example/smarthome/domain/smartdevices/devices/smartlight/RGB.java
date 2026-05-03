package com.example.smarthome.domain.smartdevices.devices.smartlight;

// This class will be used to represent RBG values for the smart light class.
// Setters will enforce invariants for RBG values
public class RGB {

    private int R;
    private int G;
    private int B;

    public RGB(){
        setR(0);
        setG(0);
        setB(0);
    }

    public RGB(int R, int G, int B){
        setR(R);
        setG(G);
        setB(B);
    }

    public int getR(){
        return this.R;
    }

    // Invariant: R must be between 0 and 255
    public void setR(int R){

        if (R < 0 || R > 255){
            throw new IllegalArgumentException("The R value must be between 0 and 255. Try again!");
        }
        else {
            this.R = R;
        }
    }

    public int getG(){
        return this.G;
    }

    // Invariant: G must be between 0 and 255
    public void setG(int G){
        if (G < 0 || G > 255) {
            throw new IllegalArgumentException("The G value must be between 0 and 255. Try again!");
        }
        else{
            this.G = G;
        }
    }

    public int getB(){
        return this.B;
    }

    // Invariant: B must be between 0 and 255
    public void setB(int B){
        if (B < 0 || B > 255) {
            throw new IllegalArgumentException("The B value must be between 0 and 255. Try again!");
        }
        else{
            this.B = B;
        }
    }

    public int[] getColor(){

        int[] color = {getR() ,getG() ,getB()};
        return  color;
    }

    @Override
    public String toString(){
        return "Red: " + this.R + " Green: " + this.G + " Blue: " + this.B;
    }
}

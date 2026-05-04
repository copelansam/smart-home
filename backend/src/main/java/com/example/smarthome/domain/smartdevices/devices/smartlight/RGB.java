package com.example.smarthome.domain.smartdevices.devices.smartlight;

/**
 * Represents an RGB (Red, Green, Blue) color.
 *
 * <p>
 * Each color component must be within the range [0, 255].
 * Validation is enforced through setters to maintain this invariant.
 * </p>
 *
 * <p>
 * This class is used by smart light devices to represent and manage color state.
 * </p>
 */
public class RGB {

    /** Red component [0–255]. */
    private int R;

    /** Green component [0–255]. */
    private int G;

    /** Blue component [0–255]. */
    private int B;

    /**
     * Constructs a color initialized to black (0, 0, 0).
     */
    public RGB(){
        setR(0);
        setG(0);
        setB(0);
    }

    /**
     * Constructs an RGB color with specified values.
     *
     * @param R red component [0–255]
     * @param G green component [0–255]
     * @param B blue component [0–255]
     * @throws IllegalArgumentException if any value is outside [0, 255]
     */
    public RGB(int R, int G, int B){
        setR(R);
        setG(G);
        setB(B);
    }

    public int getR(){
        return this.R;
    }

    /**
     * Sets the red component.
     *
     * @param R value between 0 and 255
     * @throws IllegalArgumentException if value is out of range
     */
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

    /**
     * Sets the green component.
     *
     * @param G value between 0 and 255
     * @throws IllegalArgumentException if value is out of range
     */
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

    /**
     * Sets the blue component.
     *
     * @param B value between 0 and 255
     * @throws IllegalArgumentException if value is out of range
     */
    public void setB(int B){
        if (B < 0 || B > 255) {
            throw new IllegalArgumentException("The B value must be between 0 and 255. Try again!");
        }
        else{
            this.B = B;
        }
    }

    /**
     * @return the RGB color as an array in the format [R, G, B]
     */
    public int[] getColor(){

        int[] color = {getR() ,getG() ,getB()};
        return  color;
    }

    /**
     * @return a human-readable string representation of the color
     */
    @Override
    public String toString(){
        return "Red: " + this.R + " Green: " + this.G + " Blue: " + this.B;
    }
}

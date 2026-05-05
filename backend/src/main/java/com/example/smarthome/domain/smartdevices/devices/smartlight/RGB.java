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
public record RGB(

        /** Red component [0–255]. */
        int R,

        /** Green component [0–255]. */
        int G,

        /** Blue component [0–255]. */
        int B

) {

    /**
     * Compact constructor used for validation.
     *
     * @throws IllegalArgumentException if any value is outside [0, 255]
     */
    public RGB {
        validate(R, "R");
        validate(G, "G");
        validate(B, "B");
    }

    /**
     * Constructs a color initialized to black (0, 0, 0).
     */
    public RGB() {
        this(0, 0, 0);
    }

    /**
     * Validates a color component.
     */
    private static void validate(int value, String component) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(
                    component + " value must be between 0 and 255."
            );
        }
    }

    /**
     * @return the RGB color as an array in the format [R, G, B]
     */
    public int[] getColor() {
        return new int[]{R, G, B};
    }

    /**
     * @return a human-readable string representation of the color
     */
    @Override
    public String toString() {
        return "Red: " + R + " Green: " + G + " Blue: " + B;
    }
}
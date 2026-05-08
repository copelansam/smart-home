package com.example.smarthome.domain;

import com.example.smarthome.domain.smartdevices.devices.smartlight.RGB;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.devices.smartfan.FanSpeed;
import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.Temperature;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.ThermostatMode;
import com.example.smarthome.domain.smartdevices.devices.smartdoorlock.SmartDoorLock;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Smart Home domain layer.
 *
 * Covers:
 * - State machine transitions (valid and invalid) for all device types
 * - Boundary conditions (brightness, temperature, fan speed, RGB)
 * - Device creation and factory reset
 * - Business rule invariants
 *
 * IMPORTANT — transition strings must match enum names (e.g. "TURN_LIGHT_ON"),
 * since getActionFromString() matches on action.name(), not the UI label.
 *
 * IMPORTANT — UUID is null in unit tests because @GeneratedValue only fires
 * on JPA persistence. UUID assertions belong in integration tests.
 *
 * NOTE — LightOffState, LightOnState, FanOffState, FanOnState, DoorUnlockedState,
 * and DoorLockedState do not null-guard the result of getActionFromString() before
 * passing it into a switch, causing a NullPointerException for fully unrecognized
 * strings. ThermostatOffState and ThermostatHeatingState do have a null guard and
 * correctly return a failed CallResult. For the states without a null guard, invalid
 * transition tests use a valid action name that is simply not allowed in the current
 * state (e.g. TURN_LIGHT_OFF from Off), which hits the default branch cleanly.
 * This is a known production bug: those state classes should add a null check before
 * the switch statement, matching the pattern already used in ThermostatOffState.
 */
public class SmartHomeUnitTests {

    // =========================================================================
    // SMART LIGHT TESTS
    // =========================================================================

    @Nested
    @DisplayName("SmartLight")
    class SmartLightTests {

        private SmartLight light;

        @BeforeEach
        void setUp() {
            light = new SmartLight("Living Room Light", "Living Room");
        }

        // --- Device Creation ---

        @Test
        @DisplayName("Factory reset initializes light with correct defaults")
        void creation_defaultState() {
            assertAll(
                    "Light factory defaults",
                    () -> assertEquals("Living Room Light", light.getName()),
                    () -> assertEquals("Living Room", light.getLocation()),
                    () -> assertEquals(DeviceType.LIGHT, light.getDeviceType()),
                    () -> assertFalse(light.getIsOn()),
                    () -> assertEquals(100, light.getBrightnessPercentage()),
                    () -> assertArrayEquals(new int[]{255, 255, 255}, light.getColor().getColor()),
                    () -> assertEquals("Light Off", light.getState())
            );
        }

        @Test
        @DisplayName("Factory reset restores all defaults")
        void creation_factoryReset_restoresDefaults() {
            light.setBrightnessPercentage(50);
            light.setColor(new RGB(100, 100, 100));
            light.factoryReset();

            assertAll(
                    () -> assertEquals(100, light.getBrightnessPercentage()),
                    () -> assertArrayEquals(new int[]{255, 255, 255}, light.getColor().getColor()),
                    () -> assertFalse(light.getIsOn()),
                    () -> assertEquals("Light Off", light.getState())
            );
        }

        // --- Brightness Boundary Conditions ---

        @Test
        @DisplayName("Brightness boundary: minimum valid value (10) is accepted")
        void brightness_minimumValid() {
            assertDoesNotThrow(() -> light.setBrightnessPercentage(10));
            assertEquals(10, light.getBrightnessPercentage());
        }

        @Test
        @DisplayName("Brightness boundary: maximum valid value (100) is accepted")
        void brightness_maximumValid() {
            assertDoesNotThrow(() -> light.setBrightnessPercentage(100));
            assertEquals(100, light.getBrightnessPercentage());
        }

        @Test
        @DisplayName("Brightness boundary: below minimum (9) is rejected")
        void brightness_belowMinimum_throws() {
            assertThrows(IllegalArgumentException.class, () -> light.setBrightnessPercentage(9));
        }

        @Test
        @DisplayName("Brightness boundary: above maximum (101) is rejected")
        void brightness_aboveMaximum_throws() {
            assertThrows(IllegalArgumentException.class, () -> light.setBrightnessPercentage(101));
        }

        @Test
        @DisplayName("Brightness boundary: zero is rejected")
        void brightness_zero_throws() {
            assertThrows(IllegalArgumentException.class, () -> light.setBrightnessPercentage(0));
        }

        @Test
        @DisplayName("Brightness boundary: negative value is rejected")
        void brightness_negative_throws() {
            assertThrows(IllegalArgumentException.class, () -> light.setBrightnessPercentage(-1));
        }

        // --- State Machine: OFF state ---

        @Test
        @DisplayName("State machine: light starts in Off state")
        void stateMachine_initialState_isOff() {
            assertEquals("Light Off", light.getState());
        }

        @Test
        @DisplayName("State machine: TURN_LIGHT_ON is valid from Off state")
        void stateMachine_offState_turnOn_succeeds() {
            CallResult result = light.execute("TURN_LIGHT_ON", Map.of());
            assertTrue(result.getIsSuccess());
            assertTrue(light.getIsOn());
            assertEquals("Light On", light.getState());
        }

        @Test
        @DisplayName("State machine: TURN_LIGHT_OFF is invalid from Off state")
        void stateMachine_offState_turnOff_fails() {
            // TURN_LIGHT_OFF is a known action not valid in this state — hits default branch
            CallResult result = light.execute("TURN_LIGHT_OFF", Map.of());
            assertFalse(result.getIsSuccess());
            assertEquals("Invalid Transition from current state", result.getMessage());
        }

        @Test
        @DisplayName("State machine: UPDATE_BRIGHTNESS is invalid from Off state")
        void stateMachine_offState_updateBrightness_fails() {
            // UPDATE_BRIGHTNESS hits the explicit rejection branch in LightOffState
            CallResult result = light.execute("UPDATE_BRIGHTNESS", Map.of("brightnessPercentage", 50));
            assertFalse(result.getIsSuccess());
        }

        @Test
        @DisplayName("State machine: UPDATE_COLOR is invalid from Off state")
        void stateMachine_offState_updateColor_fails() {
            // UPDATE_COLOR hits the explicit rejection branch in LightOffState
            CallResult result = light.execute("UPDATE_COLOR",
                    Map.of("redValue", 100, "greenValue", 100, "blueValue", 100));
            assertFalse(result.getIsSuccess());
        }

        // --- State Machine: ON state ---

        @Test
        @DisplayName("State machine: TURN_LIGHT_OFF is valid from On state")
        void stateMachine_onState_turnOff_succeeds() {
            light.execute("TURN_LIGHT_ON", Map.of());
            CallResult result = light.execute("TURN_LIGHT_OFF", Map.of());
            assertTrue(result.getIsSuccess());
            assertFalse(light.getIsOn());
            assertEquals("Light Off", light.getState());
        }

        @Test
        @DisplayName("State machine: TURN_LIGHT_ON is invalid from On state")
        void stateMachine_onState_turnOn_fails() {
            light.execute("TURN_LIGHT_ON", Map.of());
            // TURN_LIGHT_ON is a known action not valid in On state — hits default branch
            CallResult result = light.execute("TURN_LIGHT_ON", Map.of());
            assertFalse(result.getIsSuccess());
        }

        @Test
        @DisplayName("State machine: UPDATE_BRIGHTNESS is valid from On state")
        void stateMachine_onState_updateBrightness_succeeds() {
            light.execute("TURN_LIGHT_ON", Map.of());
            CallResult result = light.execute("UPDATE_BRIGHTNESS", Map.of("brightnessPercentage", 50));
            assertTrue(result.getIsSuccess());
            assertEquals(50, light.getBrightnessPercentage());
        }

        @Test
        @DisplayName("State machine: UPDATE_COLOR is valid from On state")
        void stateMachine_onState_updateColor_succeeds() {
            light.execute("TURN_LIGHT_ON", Map.of());
            // Parameter keys must match LightOnState: "redValue", "greenValue", "blueValue"
            CallResult result = light.execute("UPDATE_COLOR",
                    Map.of("redValue", 100, "greenValue", 150, "blueValue", 200));
            assertTrue(result.getIsSuccess());
            assertArrayEquals(new int[]{100, 150, 200}, light.getColor().getColor());
        }

        // --- ExtraProperties ---

        @Test
        @DisplayName("getExtraProperties returns color and brightness")
        void extraProperties_containsColorAndBrightness() {
            var props = light.getExtraProperties();
            assertTrue(props.containsKey("color"));
            assertTrue(props.containsKey("brightnessPercentage"));
        }
    }

    // =========================================================================
    // RGB TESTS
    // =========================================================================

    @Nested
    @DisplayName("RGB")
    class RGBTests {

        @Test
        @DisplayName("Valid RGB values are accepted")
        void rgb_validValues_accepted() {
            assertDoesNotThrow(() -> new RGB(0, 0, 0));
            assertDoesNotThrow(() -> new RGB(255, 255, 255));
            assertDoesNotThrow(() -> new RGB(128, 64, 32));
        }

        @Test
        @DisplayName("R below 0 is rejected")
        void rgb_rBelowZero_throws() {
            assertThrows(IllegalArgumentException.class, () -> new RGB(-1, 0, 0));
        }

        @Test
        @DisplayName("G below 0 is rejected")
        void rgb_gBelowZero_throws() {
            assertThrows(IllegalArgumentException.class, () -> new RGB(0, -1, 0));
        }

        @Test
        @DisplayName("B below 0 is rejected")
        void rgb_bBelowZero_throws() {
            assertThrows(IllegalArgumentException.class, () -> new RGB(0, 0, -1));
        }

        @Test
        @DisplayName("R above 255 is rejected")
        void rgb_rAbove255_throws() {
            assertThrows(IllegalArgumentException.class, () -> new RGB(256, 0, 0));
        }

        @Test
        @DisplayName("G above 255 is rejected")
        void rgb_gAbove255_throws() {
            assertThrows(IllegalArgumentException.class, () -> new RGB(0, 256, 0));
        }

        @Test
        @DisplayName("B above 255 is rejected")
        void rgb_bAbove255_throws() {
            assertThrows(IllegalArgumentException.class, () -> new RGB(0, 0, 256));
        }

        @Test
        @DisplayName("getColor returns correct array")
        void rgb_getColor_returnsArray() {
            RGB rgb = new RGB(10, 20, 30);
            assertArrayEquals(new int[]{10, 20, 30}, rgb.getColor());
        }

        @Test
        @DisplayName("Default constructor creates black (0,0,0)")
        void rgb_defaultConstructor_isBlack() {
            RGB rgb = new RGB();
            assertArrayEquals(new int[]{0, 0, 0}, rgb.getColor());
        }
    }

    // =========================================================================
    // SMART FAN TESTS
    // =========================================================================

    @Nested
    @DisplayName("SmartFan")
    class SmartFanTests {

        private SmartFan fan;

        @BeforeEach
        void setUp() {
            fan = new SmartFan("Bedroom Fan", "Bedroom");
        }

        // --- Device Creation ---

        @Test
        @DisplayName("Factory reset initializes fan with correct defaults")
        void creation_defaultState() {
            assertAll(
                    "Fan factory defaults",
                    () -> assertEquals("Bedroom Fan", fan.getName()),
                    () -> assertEquals("Bedroom", fan.getLocation()),
                    () -> assertEquals(DeviceType.FAN, fan.getDeviceType()),
                    () -> assertFalse(fan.getIsOn()),
                    () -> assertEquals(FanSpeed.MEDIUM, fan.getSpeed()),
                    () -> assertEquals("Fan Off", fan.getState())
            );
        }

        @Test
        @DisplayName("Factory reset restores all defaults")
        void creation_factoryReset_restoresDefaults() {
            fan.setSpeed(FanSpeed.HIGH);
            fan.factoryReset();

            assertAll(
                    () -> assertFalse(fan.getIsOn()),
                    () -> assertEquals(FanSpeed.MEDIUM, fan.getSpeed()),
                    () -> assertEquals("Fan Off", fan.getState())
            );
        }

        // --- Fan Speed Boundary Conditions ---

        @Test
        @DisplayName("Fan speed: LOW is a valid value")
        void fanSpeed_low_isValid() {
            fan.setSpeed(FanSpeed.LOW);
            assertEquals(FanSpeed.LOW, fan.getSpeed());
        }

        @Test
        @DisplayName("Fan speed: MEDIUM is a valid value")
        void fanSpeed_medium_isValid() {
            fan.setSpeed(FanSpeed.MEDIUM);
            assertEquals(FanSpeed.MEDIUM, fan.getSpeed());
        }

        @Test
        @DisplayName("Fan speed: HIGH is a valid value")
        void fanSpeed_high_isValid() {
            fan.setSpeed(FanSpeed.HIGH);
            assertEquals(FanSpeed.HIGH, fan.getSpeed());
        }

        @Test
        @DisplayName("FanSpeed enum has exactly 3 values: LOW, MEDIUM, HIGH")
        void fanSpeed_enumValues_areComplete() {
            assertEquals(3, FanSpeed.values().length);
        }

        @Test
        @DisplayName("FanSpeed descriptions are human-readable")
        void fanSpeed_descriptions_areReadable() {
            assertEquals("Low", FanSpeed.LOW.getDescription());
            assertEquals("Medium", FanSpeed.MEDIUM.getDescription());
            assertEquals("High", FanSpeed.HIGH.getDescription());
        }

        // --- State Machine: OFF state ---

        @Test
        @DisplayName("State machine: fan starts in Off state")
        void stateMachine_initialState_isOff() {
            assertEquals("Fan Off", fan.getState());
        }

        @Test
        @DisplayName("State machine: TURN_FAN_ON is valid from Off state")
        void stateMachine_offState_turnOn_succeeds() {
            CallResult result = fan.execute("TURN_FAN_ON", Map.of());
            assertTrue(result.getIsSuccess());
            assertTrue(fan.getIsOn());
            assertEquals("Fan On", fan.getState());
        }

        @Test
        @DisplayName("State machine: TURN_FAN_OFF is invalid from Off state")
        void stateMachine_offState_turnOff_fails() {
            // TURN_FAN_OFF is a known action not valid in Off state — hits default branch
            CallResult result = fan.execute("TURN_FAN_OFF", Map.of());
            assertFalse(result.getIsSuccess());
            assertEquals("Invalid Transition from current state", result.getMessage());
        }

        @Test
        @DisplayName("State machine: UPDATE_SPEED is invalid from Off state")
        void stateMachine_offState_updateSpeed_fails() {
            // UPDATE_SPEED is a known action not valid in Off state — hits default branch
            CallResult result = fan.execute("UPDATE_SPEED", Map.of("speed", "HIGH"));
            assertFalse(result.getIsSuccess());
        }

        // --- State Machine: ON state ---

        @Test
        @DisplayName("State machine: TURN_FAN_OFF is valid from On state")
        void stateMachine_onState_turnOff_succeeds() {
            fan.execute("TURN_FAN_ON", Map.of());
            CallResult result = fan.execute("TURN_FAN_OFF", Map.of());
            assertTrue(result.getIsSuccess());
            assertFalse(fan.getIsOn());
            assertEquals("Fan Off", fan.getState());
        }

        @Test
        @DisplayName("State machine: TURN_FAN_ON is invalid from On state")
        void stateMachine_onState_turnOn_fails() {
            fan.execute("TURN_FAN_ON", Map.of());
            // TURN_FAN_ON is a known action not valid in On state — hits default branch
            CallResult result = fan.execute("TURN_FAN_ON", Map.of());
            assertFalse(result.getIsSuccess());
        }

        @Test
        @DisplayName("State machine: UPDATE_SPEED is valid from On state")
        void stateMachine_onState_updateSpeed_succeeds() {
            fan.execute("TURN_FAN_ON", Map.of());
            CallResult result = fan.execute("UPDATE_SPEED", Map.of("speed", "HIGH"));
            assertTrue(result.getIsSuccess());
            assertEquals(FanSpeed.HIGH, fan.getSpeed());
        }

        // --- ExtraProperties ---

        @Test
        @DisplayName("getExtraProperties returns speed")
        void extraProperties_containsSpeed() {
            assertTrue(fan.getExtraProperties().containsKey("speed"));
        }
    }

    // =========================================================================
    // SMART THERMOSTAT TESTS
    // =========================================================================

    @Nested
    @DisplayName("SmartThermostat")
    class SmartThermostatTests {

        private SmartThermostat thermostat;

        @BeforeEach
        void setUp() {
            thermostat = new SmartThermostat("Main Thermostat", "Hallway");
        }

        // --- Device Creation ---

        @Test
        @DisplayName("Factory reset initializes thermostat with correct defaults")
        void creation_defaultState() {
            assertAll(
                    "Thermostat factory defaults",
                    () -> assertEquals("Main Thermostat", thermostat.getName()),
                    () -> assertEquals("Hallway", thermostat.getLocation()),
                    () -> assertEquals(DeviceType.THERMOSTAT, thermostat.getDeviceType()),
                    () -> assertFalse(thermostat.getIsOn()),
                    () -> assertEquals(ThermostatMode.AUTO, thermostat.getMode()),
                    () -> assertEquals(60.0, thermostat.getAmbientTemperature().temperature()),
                    () -> assertEquals(75.0, thermostat.getDesiredTemperature().temperature()),
                    () -> assertEquals("Thermostat Off", thermostat.getState())
            );
        }

        @Test
        @DisplayName("Factory reset restores all defaults")
        void creation_factoryReset_restoresDefaults() {
            thermostat.setDesiredTemperature(70);
            thermostat.setMode(ThermostatMode.HEAT);
            thermostat.factoryReset();

            assertAll(
                    () -> assertEquals(ThermostatMode.AUTO, thermostat.getMode()),
                    () -> assertEquals(75.0, thermostat.getDesiredTemperature().temperature()),
                    () -> assertEquals(60.0, thermostat.getAmbientTemperature().temperature()),
                    () -> assertFalse(thermostat.getIsOn()),
                    () -> assertEquals("Thermostat Off", thermostat.getState())
            );
        }

        // --- Temperature Boundary Conditions ---

        @Test
        @DisplayName("Desired temperature boundary: minimum valid (60°F) is accepted")
        void temperature_minimumValid() {
            assertDoesNotThrow(() -> thermostat.setDesiredTemperature(60));
            assertEquals(60.0, thermostat.getDesiredTemperature().temperature());
        }

        @Test
        @DisplayName("Desired temperature boundary: maximum valid (80°F) is accepted")
        void temperature_maximumValid() {
            assertDoesNotThrow(() -> thermostat.setDesiredTemperature(80));
            assertEquals(80.0, thermostat.getDesiredTemperature().temperature());
        }

        @Test
        @DisplayName("Desired temperature boundary: below minimum (59°F) is rejected")
        void temperature_belowMinimum_throws() {
            assertThrows(IllegalArgumentException.class, () -> thermostat.setDesiredTemperature(59));
        }

        @Test
        @DisplayName("Desired temperature boundary: above maximum (81°F) is rejected")
        void temperature_aboveMaximum_throws() {
            assertThrows(IllegalArgumentException.class, () -> thermostat.setDesiredTemperature(81));
        }

        @Test
        @DisplayName("Desired temperature boundary: zero is rejected")
        void temperature_zero_throws() {
            assertThrows(IllegalArgumentException.class, () -> thermostat.setDesiredTemperature(0));
        }

        @Test
        @DisplayName("Desired temperature boundary: negative value is rejected")
        void temperature_negative_throws() {
            assertThrows(IllegalArgumentException.class, () -> thermostat.setDesiredTemperature(-10));
        }

        @Test
        @DisplayName("Ambient temperature can be set freely (no range restriction)")
        void ambientTemperature_canBeSetFreely() {
            assertDoesNotThrow(() -> thermostat.setAmbientTemperature(30));
            assertEquals(30.0, thermostat.getAmbientTemperature().temperature());
        }

        @Test
        @DisplayName("updateAmbientTemperature increases temperature by positive delta")
        void ambientTemperature_updatePositiveDelta() {
            thermostat.setAmbientTemperature(60);
            thermostat.updateAmbientTemperature(5);
            assertEquals(65.0, thermostat.getAmbientTemperature().temperature());
        }

        @Test
        @DisplayName("updateAmbientTemperature decreases temperature by negative delta")
        void ambientTemperature_updateNegativeDelta() {
            thermostat.setAmbientTemperature(70);
            thermostat.updateAmbientTemperature(-5);
            assertEquals(65.0, thermostat.getAmbientTemperature().temperature());
        }

        // --- State Machine: OFF state ---

        @Test
        @DisplayName("State machine: thermostat starts in Off state")
        void stateMachine_initialState_isOff() {
            assertEquals("Thermostat Off", thermostat.getState());
        }

        @Test
        @DisplayName("State machine: POWER_THERMOSTAT_ON is valid from Off state")
        void stateMachine_offState_powerOn_succeeds() {
            CallResult result = thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            assertTrue(result.getIsSuccess());
            assertEquals("Thermostat Idle", thermostat.getState());
        }

        @Test
        @DisplayName("State machine: POWER_THERMOSTAT_OFF is invalid from Off state")
        void stateMachine_offState_powerOff_fails() {
            CallResult result = thermostat.execute("POWER_THERMOSTAT_OFF", Map.of());
            assertFalse(result.getIsSuccess());
        }

        @Test
        @DisplayName("State machine: START_HEATING is invalid from Off state")
        void stateMachine_offState_startHeating_fails() {
            CallResult result = thermostat.execute("START_HEATING", Map.of());
            assertFalse(result.getIsSuccess());
        }

        @Test
        @DisplayName("State machine: STOP_HEATING is invalid from Off state")
        void stateMachine_offState_stopHeating_fails() {
            CallResult result = thermostat.execute("STOP_HEATING", Map.of());
            assertFalse(result.getIsSuccess());
        }

        @Test
        @DisplayName("State machine: UPDATE_DESIRED_TEMP is valid from Off state (updatable field)")
        void stateMachine_offState_updateDesiredTemp_succeeds() {
            // ThermostatOffState explicitly handles UPDATE_DESIRED_TEMP as an updatable field
            CallResult result = thermostat.execute("UPDATE_DESIRED_TEMP", Map.of("desiredTemp", 70.0));
            assertTrue(result.getIsSuccess());
            assertEquals(70.0, thermostat.getDesiredTemperature().temperature());
        }

        @Test
        @DisplayName("State machine: UPDATE_MODE is valid from Off state (updatable field)")
        void stateMachine_offState_updateMode_succeeds() {
            // ThermostatOffState explicitly handles UPDATE_MODE as an updatable field
            CallResult result = thermostat.execute("UPDATE_MODE", Map.of("mode", "HEAT"));
            assertTrue(result.getIsSuccess());
            assertEquals(ThermostatMode.HEAT, thermostat.getMode());
        }

        @Test
        @DisplayName("State machine: unknown transition is invalid from Off state")
        void stateMachine_offState_unknownTransition_fails() {
            // ThermostatOffState has a null guard so unknown strings return a failed CallResult
            CallResult result = thermostat.execute("DOES_NOT_EXIST", Map.of());
            assertFalse(result.getIsSuccess());
            assertEquals("Invalid Transition from current state", result.getMessage());
        }

        // --- State Machine: HEATING state ---

        @Test
        @DisplayName("State machine: STOP_HEATING transitions Heating → Idle")
        void stateMachine_heatingState_stopHeating_transitionsToIdle() {
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.execute("START_HEATING", Map.of());

            CallResult result = thermostat.execute("STOP_HEATING", Map.of());
            assertTrue(result.getIsSuccess());
            assertEquals("Thermostat Idle", thermostat.getState());
        }

        @Test
        @DisplayName("State machine: POWER_THERMOSTAT_OFF is valid from Heating state")
        void stateMachine_heatingState_powerOff_succeeds() {
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.execute("START_HEATING", Map.of());

            CallResult result = thermostat.execute("POWER_THERMOSTAT_OFF", Map.of());
            assertTrue(result.getIsSuccess());
            assertEquals("Thermostat Off", thermostat.getState());
        }

        @Test
        @DisplayName("State machine: UPDATE_DESIRED_TEMP is valid from Heating state")
        void stateMachine_heatingState_updateDesiredTemp_succeeds() {
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.execute("START_HEATING", Map.of());

            CallResult result = thermostat.execute("UPDATE_DESIRED_TEMP", Map.of("desiredTemp", 72.0));
            assertTrue(result.getIsSuccess());
            assertEquals(72.0, thermostat.getDesiredTemperature().temperature());
        }

        @Test
        @DisplayName("State machine: UPDATE_MODE is valid from Heating state")
        void stateMachine_heatingState_updateMode_succeeds() {
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.execute("START_HEATING", Map.of());

            CallResult result = thermostat.execute("UPDATE_MODE", Map.of("mode", "HEAT"));
            assertTrue(result.getIsSuccess());
            assertEquals(ThermostatMode.HEAT, thermostat.getMode());
        }

        @Test
        @DisplayName("State machine: START_HEATING is invalid from Heating state (already heating)")
        void stateMachine_heatingState_startHeating_fails() {
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.execute("START_HEATING", Map.of());

            CallResult result = thermostat.execute("START_HEATING", Map.of());
            assertFalse(result.getIsSuccess());
        }

        @Test
        @DisplayName("State machine: unknown transition is invalid from Heating state")
        void stateMachine_heatingState_unknownTransition_fails() {
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.execute("START_HEATING", Map.of());

            // ThermostatHeatingState has a null guard so unknown strings return a failed CallResult
            CallResult result = thermostat.execute("BOGUS_ACTION", Map.of());
            assertFalse(result.getIsSuccess());
            assertEquals("Invalid Transition from current state", result.getMessage());
        }

        // --- Mode ---

        @Test
        @DisplayName("Thermostat mode can be set to HEAT, COOL, and AUTO")
        void mode_allValuesAccepted() {
            thermostat.setMode(ThermostatMode.HEAT);
            assertEquals(ThermostatMode.HEAT, thermostat.getMode());

            thermostat.setMode(ThermostatMode.COOL);
            assertEquals(ThermostatMode.COOL, thermostat.getMode());

            thermostat.setMode(ThermostatMode.AUTO);
            assertEquals(ThermostatMode.AUTO, thermostat.getMode());
        }

        // --- ExtraProperties ---

        @Test
        @DisplayName("getExtraProperties returns desired temp, ambient temp, and mode")
        void extraProperties_containsAllFields() {
            var props = thermostat.getExtraProperties();
            assertAll(
                    () -> assertTrue(props.containsKey("desiredTemperature")),
                    () -> assertTrue(props.containsKey("ambientTemperature")),
                    () -> assertTrue(props.containsKey("mode"))
            );
        }
    }

    // =========================================================================
    // SMART DOOR LOCK TESTS
    // =========================================================================

    @Nested
    @DisplayName("SmartDoorLock")
    class SmartDoorLockTests {

        private SmartDoorLock doorLock;

        @BeforeEach
        void setUp() {
            doorLock = new SmartDoorLock("Front Door", "Entrance");
        }

        // --- Device Creation ---

        @Test
        @DisplayName("Factory reset initializes door lock with correct defaults")
        void creation_defaultState() {
            assertAll(
                    "Door lock factory defaults",
                    () -> assertEquals("Front Door", doorLock.getName()),
                    () -> assertEquals("Entrance", doorLock.getLocation()),
                    () -> assertEquals(DeviceType.DOORLOCK, doorLock.getDeviceType()),
                    () -> assertTrue(doorLock.getIsOn(), "Door lock should always be considered 'on'"),
                    () -> assertEquals("Door Unlocked", doorLock.getState())
            );
        }

        @Test
        @DisplayName("Invariant: door lock isOn is always true for UI filtering purposes")
        void invariant_isOnAlwaysTrue() {
            assertTrue(doorLock.getIsOn());
        }

        @Test
        @DisplayName("Factory reset restores unlocked state")
        void creation_factoryReset_restoresUnlocked() {
            doorLock.execute("LOCK", Map.of());
            assertEquals("Door Locked", doorLock.getState());

            doorLock.factoryReset();
            assertEquals("Door Unlocked", doorLock.getState());
        }

        @Test
        @DisplayName("getExtraProperties returns empty map")
        void extraProperties_isEmpty() {
            assertTrue(doorLock.getExtraProperties().isEmpty());
        }

        // --- State Machine: UNLOCKED state ---

        @Test
        @DisplayName("State machine: door lock starts in Unlocked state")
        void stateMachine_initialState_isUnlocked() {
            assertEquals("Door Unlocked", doorLock.getState());
        }

        @Test
        @DisplayName("State machine: LOCK is valid from Unlocked state")
        void stateMachine_unlockedState_lock_succeeds() {
            CallResult result = doorLock.execute("LOCK", Map.of());
            assertTrue(result.getIsSuccess());
            assertEquals("Door Locked", doorLock.getState());
        }

        @Test
        @DisplayName("State machine: UNLOCK is invalid from Unlocked state")
        void stateMachine_unlockedState_unlock_fails() {
            // UNLOCK is a known action not valid in Unlocked state — hits default branch safely
            CallResult result = doorLock.execute("UNLOCK", Map.of());
            assertFalse(result.getIsSuccess());
            assertEquals("Invalid Transition from current state", result.getMessage());
        }

        // --- State Machine: LOCKED state ---

        @Test
        @DisplayName("State machine: UNLOCK is valid from Locked state")
        void stateMachine_lockedState_unlock_succeeds() {
            doorLock.execute("LOCK", Map.of());
            CallResult result = doorLock.execute("UNLOCK", Map.of());
            assertTrue(result.getIsSuccess());
            assertEquals("Door Unlocked", doorLock.getState());
        }

        @Test
        @DisplayName("State machine: LOCK is invalid from Locked state")
        void stateMachine_lockedState_lock_fails() {
            doorLock.execute("LOCK", Map.of());
            // LOCK is a known action not valid in Locked state — hits default branch safely
            CallResult result = doorLock.execute("LOCK", Map.of());
            assertFalse(result.getIsSuccess());
            assertEquals("Invalid Transition from current state", result.getMessage());
        }
    }

    // =========================================================================
    // CALL RESULT TESTS
    // =========================================================================

    @Nested
    @DisplayName("CallResult")
    class CallResultTests {

        @Test
        @DisplayName("Default constructor creates a failed result with standard message")
        void defaultConstructor_isFailure() {
            CallResult result = new CallResult();
            assertAll(
                    () -> assertFalse(result.getIsSuccess()),
                    () -> assertEquals("Invalid Transition from current state", result.getMessage()),
                    () -> assertNull(result.getLog())
            );
        }

        @Test
        @DisplayName("Parameterized constructor sets all fields correctly")
        void parameterizedConstructor_setsFields() {
            CallResult result = new CallResult("Success message", true, null);
            assertAll(
                    () -> assertTrue(result.getIsSuccess()),
                    () -> assertEquals("Success message", result.getMessage()),
                    () -> assertNull(result.getLog())
            );
        }
    }

    // =========================================================================
    // TEMPERATURE VALUE OBJECT TESTS
    // =========================================================================

    @Nested
    @DisplayName("Temperature")
    class TemperatureTests {

        @Test
        @DisplayName("Temperature stores value correctly")
        void temperature_storesValue() {
            Temperature t = new Temperature(72.5);
            assertEquals(72.5, t.temperature());
        }

        @Test
        @DisplayName("Default constructor initializes to 80°F")
        void temperature_defaultConstructor_is80() {
            Temperature t = new Temperature();
            assertEquals(80.0, t.temperature());
        }

        @Test
        @DisplayName("Temperature is immutable — new instance required for changes")
        void temperature_isImmutable() {
            Temperature t1 = new Temperature(65.0);
            Temperature t2 = new Temperature(70.0);
            assertEquals(65.0, t1.temperature());
            assertEquals(70.0, t2.temperature());
        }
    }
}
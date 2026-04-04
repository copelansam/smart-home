package com.example.smarthome.domain.smartdevices.statemachine.states;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StateRegistry {

    private static final Map<String, Supplier<IState>> registry = new HashMap<>();

    public static void register(String name, Supplier<IState> supplier){
        registry.put(name, supplier);
    }

    public static IState create(String name){
        Supplier<IState> supplier = registry.get(name);
        if (supplier == null){
            throw new IllegalArgumentException("Unknown State");
        }
        return supplier.get();
    }
}

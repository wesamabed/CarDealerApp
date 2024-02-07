package com.example.project;

import java.util.ArrayList;

public class CarDataManager {
    private static CarDataManager instance = null;
    private ArrayList<CarType> carTypes;

    private CarDataManager() {
        carTypes = new ArrayList<>();
    }

    public static CarDataManager getInstance() {
        if (instance == null) {
            instance = new CarDataManager();
        }
        return instance;
    }

    public ArrayList<CarType> getCarTypes() {
        return carTypes;
    }

    public void setCarTypes(ArrayList<CarType> carTypes) {
        this.carTypes = carTypes;
    }
}

package com.wcsmobile.ble;

import java.util.HashMap;

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
   // public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
   // public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
   /* public static String DEVICE_WRITE_DATA_SERVER ="0000ffe5-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_WRITE_DATA_DEVICE ="0000ffe9-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_READ_DATA_SERVER ="0000ffe0-0000-1000-8000-00805f9b34fb";
   public static String DEVICE_READ_DATA_DEVICE ="0000ffe4-0000-1000-8000-00805f9b34fb";
*/
    public static String DEVICE_WRITE_DATA_SERVER ="0000ffd0-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_WRITE_DATA_DEVICE ="0000ffd1-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_READ_DATA_SERVER ="0000fff0-0000-1000-8000-00805f9b34fb";
   public static String DEVICE_READ_DATA_DEVICE ="0000fff1-0000-1000-8000-00805f9b34fb";

    public static String DEVICE_READ_DATA_ATT ="0000ff50-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_READ_DATA_CHA ="0000ff51-0000-1000-8000-00805f9b34fb";

    static {
        //Services.
    //    attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
     //   attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
    	attributes.put(DEVICE_WRITE_DATA_SERVER, "Uart Write Service");
    	attributes.put(DEVICE_READ_DATA_SERVER, "Uart Read Service");
        //Characteristics.
     //   attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
     //   attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
       	attributes.put(DEVICE_WRITE_DATA_DEVICE, "Uart Write Device");
       	attributes.put(DEVICE_READ_DATA_DEVICE, "Uart Read Device");

    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
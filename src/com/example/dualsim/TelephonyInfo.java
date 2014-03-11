/*
 * Queries GsmCellLocation NeighboringCellInfo on dualsim phones which use Gemini function names 
 * See log for all functions supported by TelephonyManager on your phone
 * 
 */
package com.example.dualsim;

import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public final class TelephonyInfo {

    private static TelephonyInfo telephonyInfo;
    private String imeiSIM1;
    private String imeiSIM2;
    private boolean isSIM1Ready;
    private boolean isSIM2Ready;
    private CellLocation cell1; 
    private CellLocation cell2; 
    

    public CellLocation getCellLocation1(){ 
    	return cell1; 
    }
    public CellLocation getCellLocation2(){ 
    	return cell2; 
    }
    
    public String getImeiSIM1() {
        return imeiSIM1;
    }

    /*public static void setImeiSIM1(String imeiSIM1) {
        TelephonyInfo.imeiSIM1 = imeiSIM1;
    }*/

    public String getImeiSIM2() {
        return imeiSIM2;
    }

    /*public static void setImeiSIM2(String imeiSIM2) {
        TelephonyInfo.imeiSIM2 = imeiSIM2;
    }*/

    public boolean isSIM1Ready() {
        return isSIM1Ready;
    }

    /*public static void setSIM1Ready(boolean isSIM1Ready) {
        TelephonyInfo.isSIM1Ready = isSIM1Ready;
    }*/

    public boolean isSIM2Ready() {
        return isSIM2Ready;
    }

    /*public static void setSIM2Ready(boolean isSIM2Ready) {
        TelephonyInfo.isSIM2Ready = isSIM2Ready;
    }*/

    public boolean isDualSIM() {
        return imeiSIM2 != null;
    }

    private TelephonyInfo() {
    }

    public static TelephonyInfo getInstance(Context context){

        if(telephonyInfo == null) {

            telephonyInfo = new TelephonyInfo();

            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            printTelephonyManagerMethodNamesForThisDevice(context); 

            telephonyInfo.imeiSIM1 = telephonyManager.getDeviceId();;
            telephonyInfo.imeiSIM2 = null;

            try {
                telephonyInfo.imeiSIM1 = getStringOfInt(context, "getDeviceIdGemini", 0) + "\n";
                telephonyInfo.imeiSIM2 = getStringOfInt(context, "getDeviceIdGemini", 1) + "\n";
                
                telephonyInfo.imeiSIM1 += "operator 1: " + getStringOfInt(context, "getNetworkOperatorGemini", 0) + "\n";
                telephonyInfo.imeiSIM2 += "operator 2: " + getStringOfInt(context, "getNetworkOperatorGemini", 1) + "\n";

                telephonyInfo.imeiSIM1 += getStringOfInt(context, "getNetworkTypeNameGemini", 0) + "\n";
                telephonyInfo.imeiSIM2 += getStringOfInt(context, "getNetworkTypeNameGemini", 1) + "\n";

                List<NeighboringCellInfo> l1 = getListOfInt(context, "getNeighboringCellInfoGemini", 0); 
                List<NeighboringCellInfo> l2 = getListOfInt(context, "getNeighboringCellInfoGemini", 1);
                telephonyInfo.imeiSIM1 += "NeighborList 1:\n";
                for(int i = 0; i < l1.size(); i++){
                	telephonyInfo.imeiSIM1 += "CID:" + (l1.get(i).getCid() + " LAC:" + l1.get(i).getLac() + " RSSI:" + (-113+2*l1.get(i).getRssi()) + "\n"); 
                } 
                telephonyInfo.imeiSIM2 += "NeighborList 2:\n";
                for(int i = 0; i < l2.size(); i++){
                	telephonyInfo.imeiSIM2 += "CID:" + (l2.get(i).getCid() + " LAC:" + l2.get(i).getLac() + " RSSI:" + (-113+2*l2.get(i).getRssi()) + "\n");
                } 
                

            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.imeiSIM1 = getStringOfInt(context, "getDeviceId", 0);
                    telephonyInfo.imeiSIM2 = getStringOfInt(context, "getDeviceId", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
            telephonyInfo.isSIM2Ready = false;

            try {
                telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
                telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
                Log.println (Log.DEBUG, "Dual", "getSimStateGemini found");
            } catch (GeminiMethodNotFoundException e) {

                e.printStackTrace();

                try {
                    telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
                    telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }
                        
            try {
                telephonyInfo.cell1 = getCellLocationBySlot(context, "getCellLocationGemini", 0);
                telephonyInfo.cell2 = getCellLocationBySlot(context, "getCellLocationGemini", 1);
            } catch (GeminiMethodNotFoundException e1) {
                //Call here for next manufacturer's predicted method name if you wish
            	Log.println (Log.DEBUG, "Dual", "getCellLocationGemini not found");
                e1.printStackTrace();
            }
           
        }

        return telephonyInfo;
    }

    private static CellLocation getCellLocationBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

    	CellLocation loc = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getLoc = telephonyClass.getMethod(predictedMethodName, parameter);
            Log.println (Log.DEBUG, "Dual", "getloc method " + getLoc.toString());
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getLoc.invoke(telephony, obParameter);
 
            if(ob_phone != null){
                loc = (CellLocation)ob_phone;

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }
        Log.println (Log.DEBUG, "Dual", "slot "+ slotID + " loc==null:" + (loc==null));

        return loc;
    }

    
    private static String getStringOfInt(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String result = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                result = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return result;
    }


    private static List getListOfInt(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        List result = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                result = (List)ob_phone;

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return result;
    }

    
    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if(ob_phone != null){
                int simState = Integer.parseInt(ob_phone.toString());
                if(simState == TelephonyManager.SIM_STATE_READY){
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }


    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
    
    public static void printTelephonyManagerMethodNamesForThisDevice(Context context) {

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            Method[] methods = telephonyClass.getMethods();
            for (int idx = 0; idx < methods.length; idx++) {
            	 Log.println (Log.DEBUG, "Dual", methods[idx] + " declared by " + methods[idx].getDeclaringClass() + "\n");	
               // System.out.println("\n" + methods[idx] + " declared by " + methods[idx].getDeclaringClass());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    } 
    
}



package com.zxxkj.util;

public class SipStatusCodeUtil {

    public static boolean returnStatus(String cause){
         switch (cause){
             case "DESTINATION_OUT_OF_ORDER":
                 return true;
             case  "FACILITY_REJECTED":
                 return true;
             case  "NORMAL_CIRCUIT_CONGESTION":
                 return true;
             case  "NETWORK_OUT_OF_ORDER":
                 return true;
             case  "NORMAL_TEMPORARY_FAILURE":
                 return true;
             case  "SWITCH_CONGESTION":
                 return true;
             case  "REQUESTED_CHAN_UNAVAIL":
                 return true;
             case  "BEARERCAPABILITY_NOTAVAIL":
                 return true;
             case  "FACILITY_NOT_IMPLEMENTED":
                 return true;
             case  "SERVICE_NOT_IMPLEMENTED":
                 return true;
             case  "RECOVERY_ON_TIMER_EXPIRE":
                 return true;
             default:
                 return false;
         }
    }
}

package com.example.dualsim;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.gsm.GsmCellLocation;

import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

        String imeiSIM1 = telephonyInfo.getImeiSIM1();
        String imeiSIM2 = telephonyInfo.getImeiSIM2();
 
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
        
        GsmCellLocation cell1 =  (android.telephony.gsm.GsmCellLocation)telephonyInfo.getCellLocation1(); 
        GsmCellLocation cell2 =  (android.telephony.gsm.GsmCellLocation)telephonyInfo.getCellLocation2();

        
        boolean isDualSIM = telephonyInfo.isDualSIM();

        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setText("\n" + 
                " IS DUAL SIM : " + isDualSIM + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n\n" + 
                " CID1: " + cell1.getCid() + "\n" +
                " LAC1: " + cell1.getLac() + "\n" +
        		" IMEI1 : " + imeiSIM1 + "\n\n" + 
                
                " CID2: " + cell2.getCid() + "\n" + 
                " LAC2: " + cell2.getLac() + "\n" +
                " IMEI2 : " + imeiSIM2 + "\n" +
                "\n"
        		);
    }
     
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
package com.gamicarts.awaytext.sms;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.gamicarts.awaytext.MainActivity;

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent)
    {
        //Check if awayTextButton is on
        if(MainActivity.readInternalFile(context,"awayTextOn"))
        {
            //Checks if the contacts button is on
            if (MainActivity.readInternalFile(context, "contactOn"))
            {
                Bundle intentExtras = intent.getExtras();
                if (intentExtras != null) {
                    Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                    String smsMessageStr = "";
                    for (int i = 0; i < sms.length; ++i)
                    {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                        String smsBody = smsMessage.getMessageBody().toString();
                        String address = smsMessage.getOriginatingAddress();

                        smsMessageStr += "SMS From: " + address + "\n";
                        smsMessageStr += smsBody + "\n";

                        SmsManager sender = SmsManager.getDefault();
                        if (contactExists(context, address))
                        {
                            sender.sendTextMessage(address, null, "Hi im away be back later =)", null, null);
                        }
                    }
                    Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

                }
            } else {
                Bundle intentExtras = intent.getExtras();
                if (intentExtras != null)
                {
                    Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                    String smsMessageStr = "";
                    for (int i = 0; i < sms.length; ++i)
                    {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                        String smsBody = smsMessage.getMessageBody().toString();
                        String address = smsMessage.getOriginatingAddress();

                        smsMessageStr += "SMS From: " + address + "\n";
                        smsMessageStr += smsBody + "\n";

                        SmsManager sender = SmsManager.getDefault();
                        sender.sendTextMessage(address, null, "Hi im away be back later =)", null, null);
                    }
                    Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

                }
            }
        }

    }

    public boolean contactExists(Context context, String number)
    {
        // number is the phone number
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try
        {
            if (cur.moveToFirst())
            {
                return true;
            }
        }
        finally
        {
            if (cur != null)
                cur.close();
        }
        return false;
    }
}
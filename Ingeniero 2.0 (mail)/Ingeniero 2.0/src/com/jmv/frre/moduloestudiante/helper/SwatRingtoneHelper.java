package com.jmv.frre.moduloestudiante.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class SwatRingtoneHelper {
	public static String defaultRingtone = "content://settings/system/notification_sound";
    private static String swat_ringtone;


    public static String getRingtone(Context context){
        try {
            if (context!=null){
                //Open the InputStream from the Assets
                InputStream fis = context.getAssets().open("ringtones/beeper_loop.ogg");
                if (fis == null)
                    return null;

                //Open a File to save the ringtone in the SD (/sdcard/Android/data/com.your.package/)
                File path = new
                        File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Android/data/com.jmv.frre.moduloestudiante/");
                if(!path.exists())
                    path.mkdirs();

                //Create the proper file
                File f = new File(path, "Beeper" + ".ogg");

                //Dump the InputStream in the File
                OutputStream fos = new FileOutputStream(f);
                byte[] buf =new byte[1024];
                int len;
                while((len=fis.read(buf))>0){
                    fos.write(buf,0,len);
                }
                fos.close();
                fis.close();

                //Here are the metadata of the ringtone
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
                values.put(MediaStore.MediaColumns.TITLE, "Beeper");
                values.put(MediaStore.MediaColumns.SIZE, f.length());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
                values.put(MediaStore.Audio.Media.ARTIST, "Beeper");
                values.put(MediaStore.Audio.Media.DURATION, 10000);
                values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                values.put(MediaStore.Audio.Media.IS_ALARM, false);
                values.put(MediaStore.Audio.Media.IS_MUSIC, false);


                //We put in the DDBB of MediaStore
                Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
                context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + f.getAbsolutePath() + "\"", null);
                Uri newUri = context.getContentResolver().insert(uri, values);

                //Set as default
//                RingtoneManager.setActualDefaultRingtoneUri(
//                        context,
//                        RingtoneManager.TYPE_RINGTONE,
//                        newUri);

                swat_ringtone = newUri.toString();
                return swat_ringtone;
            } else {
                return swat_ringtone!=null?swat_ringtone:defaultRingtone;
            }
                

        } catch (FileNotFoundException e) {
        
        } catch (IOException e) {
        
        }
        
        return defaultRingtone;
    }

}

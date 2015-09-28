package com.example.send;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by root on 15-9-14.
 */
public class HandleContact {
    private List<Contacter> contactList = null;
    private static int PHONES_NAME_INDEX = 0;
    private static int PHONES_NUMBER_INDEX = 1;
    private static int PHONES_SORT_KEY = 3;

    private Map<String, Integer> alphaIndex;

    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID }; private HandleContact(){
    }
    private static HandleContact instance = new HandleContact();
    public static HandleContact getInstance(){
        return instance;
    }

    public int getSize(){
        return contactList.size();
    }

    public Contacter getContact(int index){
        return contactList.get(index);
    }

    public void getContactList(Context context){
        if (contactList == null){
            contactList = new ArrayList<>();

        }
        ContentResolver resolver = context.getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null){
            while(phoneCursor.moveToNext()){
                Contacter contacter = new Contacter();
                contacter.setPhoneNumber(phoneCursor.getString(PHONES_NUMBER_INDEX));
                Log.d("shanlihou", contacter.getPhoneNumber());

                contacter.setUserName(phoneCursor.getString(PHONES_NAME_INDEX));
                Log.d("shanlihou", contacter.getUserName());

                contacter.setSortKey(getSortKey(contacter.getUserName()));
                Log.d("shanlihou", contacter.getSortKey());

                contactList.add(contacter);

            }
        }
        sortContacts();

        alphaIndex = new HashMap<>();
        int length = contactList.size();
        for (int i = 0; i < length; i++){
            String currentAlpha = getAlpha(contactList.get(i).getSortKey());
            String previewAlpha = i > 0 ? getAlpha(contactList.get(i - 1).getSortKey()) : " ";
            if (!previewAlpha.equals(currentAlpha)){
                alphaIndex.put(currentAlpha, i);
            }
        }
    }

    public int getAlphaIndex(String key){
        if (alphaIndex.containsKey(key)){
            return alphaIndex.get(key);
        }
        return -1;
    }

    public void sortContacts(){
        Collections.sort(contactList, new CompareValues());
    }

    public String getSortKey(String word){
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String retStr = "";

        int length = word.length();
        for(int i = 0; i < length; i++){
            char c = word.charAt(i);
            try{
                String[] values = PinyinHelper.toHanyuPinyinStringArray(c, format);
                Log.d("shanlihou", values.length + "");
                Log.d("shanlihou", Arrays.toString(values));
                retStr += values[0];
            }catch (Exception ex){
                ex.printStackTrace();
                retStr += c;
            }
        }
        Log.d("shanlihou", retStr);

        return retStr;
    }

    public String getAlpha(String str){
        if (str == null){
            return "#";
        }
        if (str.trim().length() == 0){
            return "#";
        }
        char c = str.trim().charAt(0);

        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()){
            return (c + "").toUpperCase();
        }
        else{
            return "#";
        }
    }
}

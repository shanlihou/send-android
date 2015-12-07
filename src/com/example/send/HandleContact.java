package com.example.send;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by root on 15-9-14.
 */
public class HandleContact {
    private static final int PHONES_NAME_INDEX = 0;
    private static final int PHONES_NUMBER_INDEX = 1;
    private static final int PHONES_CONTACT_ID = 3;
    private static final int PHONES_SORT_KEY = 4;
    private static final int PHONES_LOOKUP_KEY = 5;
    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
    };

    private static final int RAW_INDEX_VERSION = 0;
    private static final int RAW_INDEX_CONTACT_ID = 1;
    private static final String[] RAW_CONTACTS_PROJECTION = new String[]{
            ContactsContract.RawContacts.VERSION,
            ContactsContract.RawContacts.CONTACT_ID
    };

    private Map<String, Integer> mAlphaIndex = null;
    private Map<String, Contacter> mContactsLookUpMap = null;
    private Map<String, Contacter> mContactsIDMap = null;
    private List<Contacter> mContactList = null;
    private List<Contacter> mSearchList = null;
    private static HandleContact mInstance = new HandleContact();
    private HandleDB mContactDB = null;
    private Context mContext = null;
    private boolean mIsSearch = false;

    private HandleContact(){
    }
    public static HandleContact getInstance(){
        return mInstance;
    }

    public void init(Context context){
        mContactDB = new HandleDB(context);
        mContext = context.getApplicationContext();
    }
    public int getSize(){
        if (mSearchList == null){
            return 0;
        }else{
            return mSearchList.size();
        }
    }

    public Contacter getContact(int index){
        if (mSearchList == null){
            return null;
        }

        if (index >= 0 && index < mSearchList.size()){
            return mSearchList.get(index);
        }else{
            return null;
        }
    }

    public void getContactListsFromOrigin(Context context){
        if (mContactList == null){
            mContactList = new ArrayList<>();

        }
        else{
            mContactList.clear();
        }

        if (mContactsIDMap == null){
            mContactsIDMap = new HashMap<>();
        }
        else{
            mContactsIDMap.clear();
        }
        context = context.getApplicationContext();
        getContactsListOrigin(mContactList, mContactsIDMap, context);
        getSearchList(null);
        getAlphaMap();
    }

    public void getSearchList(String searchText){
        if (mSearchList == null){
            mSearchList = new ArrayList<>();
        }else{
            mSearchList.clear();
        }
        Iterator<Contacter> iter = mContactList.iterator();
        Contacter contacter;
        if (searchText == null){
            while(iter.hasNext()){
                contacter = iter.next();
                contacter.setSpanName(Html.fromHtml(contacter.getDisplayName()));
                contacter.setSpanNum(Html.fromHtml(contacter.getPhoneNumber(0)));
                mSearchList.add(contacter);
            }
            return ;
        }
        int length = searchText.length();
        while(iter.hasNext()){
            contacter = iter.next();
            Iterator<String> iterNum = contacter.getPhoneNumberList().iterator();
            boolean isAdd = false;
            String temp = contacter.getDisplayName();
            int index = temp.indexOf(searchText);
            if (index != -1){
                contacter.setSpanName(getSpanned(temp, index, index + length));
                isAdd = true;
            }else{
                index = contacter.getSortKey().indexOf(searchText);
                if (index != -1){
                    contacter.setSpanName(Html.fromHtml(contacter.getDisplayName()));
                    isAdd = true;
                }
            }
            while(iterNum.hasNext()){
                temp = iterNum.next();
                index = temp.indexOf(searchText);
                if (index != -1){
                    contacter.setSpanNum(getSpanned(temp, index, index + length));
                    isAdd = true;
                    break;
                }
            }
            if (isAdd){
                mSearchList.add(contacter);
            }
        }
    }

    private Spanned getSpanned(String text, int start, int end){
        Spanned temp = Html.fromHtml(text.substring(0, start)
                + "<u><font color=#FF0000>"
                + text.substring(start, end) + "</font></u>"
                + text.substring(end, text.length()));
        return temp;
    }

    public void getContactsListOrigin(List<Contacter> contactList, Map<String, Contacter> contactMap, Context context){
        ContentResolver resolver = context.getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null){
            while(phoneCursor.moveToNext()){
                Contacter contacter = new Contacter();
                contacter.setContactId(phoneCursor.getString(PHONES_CONTACT_ID));
                if (contactMap.containsKey(contacter.getContactId())){
                    contactMap.get(contacter.getContactId()).addPhoneNumber(phoneCursor.getString(PHONES_NUMBER_INDEX));
                    continue;
                }

                contacter.setLookupKey(phoneCursor.getString(PHONES_LOOKUP_KEY));
                contacter.addPhoneNumber(phoneCursor.getString(PHONES_NUMBER_INDEX));
                contacter.setDisplayName(phoneCursor.getString(PHONES_NAME_INDEX));
                contacter.setSortKey(getSortKey(contacter.getDisplayName()));
                contacter.setIsDeleted(0);
                contactMap.put(contacter.getContactId(), contacter);
                contactList.add(contacter);
            }
        }
        String temp;
        phoneCursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI, RAW_CONTACTS_PROJECTION, null, null, null);
        if (phoneCursor != null){
            while(phoneCursor.moveToNext()){
                temp = phoneCursor.getString(RAW_INDEX_CONTACT_ID);
                if (contactMap.containsKey(temp)){
                    contactMap.get(temp).setVersion(phoneCursor.getInt(RAW_INDEX_VERSION));
                }
            }
        }
        sortContacts();
    }

    public void setIsSearch(boolean isSearch) {
        this.mIsSearch = isSearch;
    }


    public void mergeContacts(){
        if(mContactList == null){
            mContactList = new ArrayList<>();
            mContactsLookUpMap = new HashMap<>();
            getContactsListFromDB();
        }else if(mContactsLookUpMap == null){
            getLookUpMap();
        }

        Iterator<Contacter> iter = mContactList.iterator();
        while(iter.hasNext()){
            Contacter contacter = iter.next();
            contacter.setIsDeleted(1);
        }
        List<Contacter> contactList = new ArrayList<>();
        Map<String, Contacter> contactMap = new HashMap<>();
        getContactsListOrigin(contactList, contactMap, mContext);
        iter = contactList.iterator();
        while(iter.hasNext()){
            Contacter contacter = iter.next();
            String lookup = contacter.getLookupKey();
            if (mContactsLookUpMap.containsKey(lookup)){
                if (mContactsLookUpMap.get(lookup).getVersion() < contacter.getVersion()){
                    mContactsLookUpMap.get(lookup).setAll(contacter);
                }
                mContactsLookUpMap.get(lookup).setIsDeleted(0);
            }else{
                mContactsLookUpMap.put(lookup, contacter);
                mContactList.add(contacter);
            }
        }
        sortContacts();
        saveContactsList();
        Log.d("shanlihou", "merge finished");
    }

    public void getLookUpMap(){
        if (mContactsLookUpMap == null){
            mContactsLookUpMap = new HashMap<>();
        }else{
            mContactsLookUpMap.clear();
        }
        Iterator<Contacter> iter = mContactList.iterator();
        while(iter.hasNext()){
            Contacter contacter = iter.next();
            mContactsLookUpMap.put(contacter.getLookupKey(), contacter);
        }
    }

    public void getAlphaMap(){
        if (mAlphaIndex == null)
            mAlphaIndex = new HashMap<>();
        else
            mAlphaIndex.clear();
        int length = mContactList.size();
        for (int i = 0; i < length; i++){
            String currentAlpha = getAlpha(mContactList.get(i).getSortKey());
            String previewAlpha = i > 0 ? getAlpha(mContactList.get(i - 1).getSortKey()) : " ";
            if (!previewAlpha.equals(currentAlpha)){
                mAlphaIndex.put(currentAlpha, i);
            }
        }
    }

    public void getContactsListFromDB(){
        if (mContactList == null)
            mContactList = new ArrayList<>();
        else
            mContactList.clear();

        if (mContactsLookUpMap == null)
            mContactsLookUpMap = new HashMap<>();
        else
            mContactsLookUpMap.clear();

        mContactDB.getContactList(mContactList, mContactsLookUpMap);
        if (mContactList == null){
            Log.d("shanlihou", "still null");
        }else{
            Log.d("shanlihou", "still not null");
            sortContacts();
            getSearchList(null);
            getAlphaMap();
        }
    }

    public int getAlphaIndex(String key){
        if (mAlphaIndex.containsKey(key)){
            return mAlphaIndex.get(key);
        }
        return -1;
    }

    public void sortContacts(){
        Collections.sort(mContactList, new CompareValues());
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
                if (values == null){
                    retStr += c;
                    continue;
                }
                retStr += values[0];
            }catch (Exception ex){
                ex.printStackTrace();
                retStr += c;
            }
        }
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
    public void saveContactsList(){
        mContactDB.saveContactsList(mContactList);
    }
}

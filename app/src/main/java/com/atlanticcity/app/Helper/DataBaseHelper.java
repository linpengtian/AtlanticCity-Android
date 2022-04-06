package com.atlanticcity.app.Helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.atlanticcity.app.Models.ContactModel;

import java.util.ArrayList;
import java.util.List;





public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "contacts.db";
    private static final String DEVICE_CONTACTS = "device_contacts";
    public SQLiteDatabase Database;
    private static Context mContext;

    public DataBaseHelper(Context context) {
        super(context, DBNAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createContactsTable = "CREATE TABLE \"device_contacts\" (\n" +
                "\t\"id\"\tINTEGER,\n" +
                "\t\"name\"\tTEXT,\n" +
                "\t\"mobile_number\"\tTEXT,\n" +
                "\t\"photo\"\tTEXT,\n" +
                "\t\"is_sent\"\tINTEGER,\n" +
                "\tPRIMARY KEY(\"id\")\n" +
                ");";

        sqLiteDatabase.execSQL(createContactsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP if TABLE EXISTS" + DEVICE_CONTACTS);
    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if(Database != null && Database.isOpen()) {
            return;
        }
        Database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {
        if(Database!=null) {
            Database.close();
        }
    }
    public static String ExecuteQuery(Context context, String Qry ) {
        String status = "0";
        DataBaseHelper db = new DataBaseHelper(context);

        try {
            db.openDatabase();
            if(Qry.length()>0) {
                db.Database.execSQL(Qry);
            }

            status = "1";
        } catch (Exception ex) {
            status = "-1";
        } finally {
            db.closeDatabase();
        }
        return status;
    }

    public static boolean insertDeviceContacts(Context context, List<ContactModel> contactModels){
        if(contactModels.size()>0){

            for( int i = 0; i < contactModels.size() ; i++){
                String query = "INSERT INTO device_contacts (id,name,mobile_number,photo,is_sent)  VALUES";
                query += "("  + contactModels.get(i).id + ",'" + contactModels.get(i).name +  "','" + contactModels.get(i).mobileNumber  + "','" +  contactModels.get(i).getPhoto()  + "'," +contactModels.get(i).is_sent +");";
                DataBaseHelper.ExecuteQuery(context,query);
            }
            return true;
        }else {

            return false;
        }
    }

    public static void DeleteContactsTable(Context context){

        String query = "DELETE FROM device_contacts";
        DataBaseHelper.ExecuteQuery(context,query);

    }

    public static List<ContactModel> GetDeviceContacts(Context context) {

        Cursor cursor=null;
        ContactModel rec = null;
        List<ContactModel> contactModelArrayList = new ArrayList<ContactModel>();
        DataBaseHelper db = new DataBaseHelper(context);
        db.openDatabase();
        String sqlquery = "SELECT * FROM device_contacts ORDER BY name ASC";
        cursor = db.Database.rawQuery(sqlquery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            rec = new ContactModel(cursor);

            rec.getId();
            rec.getName();
            rec.getMobileNumber();
            rec.getPhoto();
            rec.getIs_sent();

            contactModelArrayList.add(rec);
            cursor.moveToNext();
        }
        cursor.close();
        db.closeDatabase();

        return contactModelArrayList;
    }

}

package com.mahta.rastin.broadcastapplication.helper;

import android.transition.Slide;

import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.model.Group;
import com.mahta.rastin.broadcastapplication.model.Media;
import com.mahta.rastin.broadcastapplication.model.MyNotification;
import com.mahta.rastin.broadcastapplication.model.Post;
import com.mahta.rastin.broadcastapplication.model.Program;
import com.mahta.rastin.broadcastapplication.model.Slider;
import com.mahta.rastin.broadcastapplication.model.Staff;
import com.mahta.rastin.broadcastapplication.model.StaffUpdated;
import com.mahta.rastin.broadcastapplication.model.Student;
import com.mahta.rastin.broadcastapplication.model.Workbook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JSONParser {
    public static int getResultCodeFromJson(String content){
        int resultCode;
        try {
            JSONObject obj = new JSONObject(content);
            resultCode = obj.getInt(Keys.KEY_RESULT_CODE);
        } catch (JSONException e) {
            G.e("1: " + e.getMessage());
            return 0;
        }
        return resultCode;
    }

    public static List<Post> parsePosts(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);
            if (data.length() > 0){
                List<Post> postList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){
                        JSONObject jpost = data.getJSONObject(i);
                        Post post = new Post();

                        post.setId(jpost.getInt(Keys.KEY_ID));
                        post.setTitle(jpost.getString(Keys.KEY_TITLE));
                        post.setContent(jpost.getString(Keys.KEY_CONTENT));
                        post.setPreview(jpost.getString(Keys.KEY_PREVIEW));
                        post.setDate(jpost.getString(Keys.KEY_DATE_UPDATED));
                        postList.add(post);
                    }
                }
                return postList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("2: " + e.getMessage());
            return null;
        }
    }

    public static List<Program> parsePrograms(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);
            if (data.length() > 0){
                List<Program> programList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){
                        JSONObject jprogram = data.getJSONObject(i);
                        Program program = new Program();

                        program.setId(jprogram.getInt(Keys.KEY_ID));
                        program.setTitle(jprogram.getString(Keys.KEY_TITLE));
                        program.setContent(jprogram.getString(Keys.KEY_CONTENT));
                        program.setPreview(jprogram.getString(Keys.KEY_PREVIEW));
                        program.setDate(jprogram.getString(Keys.KEY_DATE_UPDATED));
                        program.setGroup_id(jprogram.getInt(Keys.KEY_GROUP_ID));
                        programList.add(program);
                    }
                }
                return programList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("2: " + e.getMessage());
            return null;
        }
    }

    public static List<Media> parseMedia(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);
            if (data.length() > 0){
                List<Media> mediaList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){
                        JSONObject jmedia = data.getJSONObject(i);
                        Media media = new Media();

                        media.setId(jmedia.getInt(Keys.KEY_ID));
                        media.setTitle(jmedia.getString(Keys.KEY_TITLE));
                        media.setDescription(jmedia.getString(Keys.KEY_DESCRIPTION));
                        media.setPath(jmedia.getString(Keys.KEY_MEDIA));
                        media.setDate(jmedia.getString(Keys.KEY_DATE_UPDATED));
                        mediaList.add(media);
                    }
                }
                return mediaList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("3: " + e.getMessage());
            return null;
        }
    }

    public static String parseToken(String content){
        String token = "";
        try {
            JSONObject obj = new JSONObject(content);
            token = obj.getString(Keys.KEY_DATA);
        } catch (JSONException e) {
            G.e("4: " + e.getMessage());
        }
        return token;
    }

    public static Slider parseSlider(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONObject sobj = obj.getJSONArray(Keys.KEY_DATA).getJSONObject(0);

            Slider slider = new Slider();
            slider.setId(sobj.getInt(Keys.KEY_ID));
            slider.setImage_1(sobj.getString(Keys.KEY_IMAGE_1));
            slider.setImage_2(sobj.getString(Keys.KEY_IMAGE_2));
            slider.setImage_3(sobj.getString(Keys.KEY_IMAGE_3));
            slider.setImage_4(sobj.getString(Keys.KEY_IMAGE_4));
            slider.setUpdated_at(sobj.getString(Keys.KEY_DATE_UPDATED));

            return slider;
        } catch (JSONException e) {
            G.e("4: " + e.getMessage());
            return null;
        }
    }

    public static Date parseSliderUpdated(String content){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            JSONObject obj = new JSONObject(content);
            Date date = format.parse(obj.getString(Keys.KEY_DATA));

            return date;
        } catch (JSONException e) {
            G.e("4: " + e.getMessage());
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Staff> parseStaff(String content){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);
            if (data.length() > 0){
                List<Staff> staffList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){
                        JSONObject sobj = data.getJSONObject(i);

                        Staff staff = new Staff();
                        staff.setId(sobj.getInt(Keys.KEY_ID));
                        staff.setFirst_name(sobj.getString(Keys.KEY_FIRST_NAME));
                        staff.setLast_name(sobj.getString(Keys.KEY_LAST_NAME));
                        staff.setEmail(sobj.getString(Keys.KEY_EMAIL));
                        staff.setDescription(sobj.getString(Keys.KEY_DESCRIPTION));
                        staff.setImage(sobj.getString(Keys.KEY_PHOTO));

                        Date date = format.parse(sobj.getString(Keys.KEY_DATE_UPDATED));
                        staff.setUpdated_at(date);
                        staffList.add(staff);
                    }
                }
                return staffList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("6: " + e.getMessage());
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;

        }
    }


    public static List<StaffUpdated> parseStaffUpdated(String content){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);
            if (data.length() > 0){
                List<StaffUpdated> list = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){
                        JSONObject sobj = data.getJSONObject(i);

                        StaffUpdated item = new StaffUpdated();
                        item.setId(sobj.getInt(Keys.KEY_ID));
                        Date date = format.parse(sobj.getString(Keys.KEY_DATE_UPDATED));
                        item.setUpdated_at(date);
                        list.add(item);
                    }
                }
                return list;
            }else
                return null;
        } catch (JSONException e) {
            G.e("4: " + e.getMessage());
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Student parseStudent(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONObject sobj = obj.getJSONObject(Keys.KEY_DATA);

            Student student = new Student();
            student.setFirst_name(sobj.getString(Keys.KEY_FIRST_NAME));
            student.setLast_name(sobj.getString(Keys.KEY_LAST_NAME));
            student.setNational_code(sobj.getString(Keys.KEY_NATIONAL_CODE));
            student.setGroup_id(sobj.getInt(Keys.KEY_GROUP_ID));
            student.setStudent(sobj.getBoolean(Keys.KEY_IS_STUDENT));

            return student;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("5: " + e.getMessage());
            return null;
        }
    }

    public static List<Workbook> parseWorkbooks(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);
            if (data.length() > 0){
                List<Workbook> workbookList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){
                        JSONObject wobj = data.getJSONObject(i);

                        Workbook workbook = new Workbook();
                        workbook.setYear(wobj.getString(Keys.KEY_YEAR));
                        workbook.setMonth(wobj.getString(Keys.KEY_MONTH));
                        workbook.setGrades(wobj.getString(Keys.KEY_GRADES));
                        workbook.setLessons(wobj.getString(Keys.KEY_LESSONS));
                        workbook.setScale(wobj.getInt(Keys.KEY_SCALE));
                        workbook.setCreated_at(wobj.getString(Keys.KEY_CREATED_AT));
                        workbook.setUpdated_at(wobj.getString(Keys.KEY_DATE_UPDATED));
                        workbookList.add(workbook);
                    }
                }
                return workbookList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("6: " + e.getMessage());
            return null;
        }
    }

    public static List<MyNotification> parseNotifications(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);
            if (data.length() > 0){
                List<MyNotification> myNotificationList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){
                        JSONObject jnotif = data.getJSONObject(i);
                        MyNotification myNotification = new MyNotification();

                        myNotification.setId(jnotif.getInt(Keys.KEY_ID));
                        myNotification.setCategory_id(jnotif.getInt(Keys.KEY_CATEGORY_ID));
                        myNotification.setContent(jnotif.getString(Keys.KEY_CONTENT));
                        myNotificationList.add(myNotification);
                    }
                }
                return myNotificationList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("8: " + e.getMessage());
            return null;
        }
    }

    public static List<Group> parseGroups(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);
            if (data.length() > 0){
                List<Group> groupList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){
                        JSONObject jgroup = data.getJSONObject(i);
                        Group group = new Group();

                        group.setId(jgroup.getInt(Keys.KEY_ID));
                        group.setTitle(jgroup.getString(Keys.KEY_TITLE));
                        groupList.add(group);
                    }
                }
                return groupList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("9: " + e.getMessage());
            return null;
        }
    }

}

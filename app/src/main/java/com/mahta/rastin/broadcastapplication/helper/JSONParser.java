package com.mahta.rastin.broadcastapplication.helper;

import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.model.Group;
import com.mahta.rastin.broadcastapplication.model.Media;
import com.mahta.rastin.broadcastapplication.model.MyNotification;
import com.mahta.rastin.broadcastapplication.model.Post;
import com.mahta.rastin.broadcastapplication.model.Program;
import com.mahta.rastin.broadcastapplication.model.Student;
import com.mahta.rastin.broadcastapplication.model.Workbook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public static Student parseStudent(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONObject sobj = obj.getJSONObject(Keys.KEY_DATA);

            Student student = new Student();
            student.setFirst_name(sobj.getString(Keys.KEY_FIRST_NAME));
            student.setLast_name(sobj.getString(Keys.KEY_LAST_NAME));

            if (sobj.has(Keys.KEY_PHONE_NUMBER))
                student.setPhone_number(sobj.getString(Keys.KEY_PHONE_NUMBER));

            if (sobj.has(Keys.KEY_NATIONAL_CODE))
                student.setNational_code(sobj.getString(Keys.KEY_NATIONAL_CODE));

            if (sobj.has(Keys.KEY_GRADE))
                student.setGrade(sobj.getString(Keys.KEY_GRADE));

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

    public static Post parsePost(String content){
        try {
            JSONObject obj = new JSONObject(content);
            JSONObject pobj = obj.getJSONArray(Keys.KEY_DATA).getJSONObject(0);

            Post post = new Post();
            post.setId(pobj.getInt(Keys.KEY_ID));
            post.setTitle(pobj.getString(Keys.KEY_TITLE));
            post.setContent(pobj.getString(Keys.KEY_CONTENT));
            post.setPreview(pobj.getString(Keys.KEY_PREVIEW));
            post.setDate(pobj.getString(Keys.KEY_DATE_UPDATED));
            return post;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("7: " + e.getMessage());
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

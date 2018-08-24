package com.mahta.rastin.broadcastapplication.helper;


import android.content.Context;

import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.model.FavoriteMedia;
import com.mahta.rastin.broadcastapplication.model.FavoritePost;
import com.mahta.rastin.broadcastapplication.model.FavoriteProgram;
import com.mahta.rastin.broadcastapplication.model.Group;
import com.mahta.rastin.broadcastapplication.model.Media;
import com.mahta.rastin.broadcastapplication.model.Post;
import com.mahta.rastin.broadcastapplication.model.Program;
import com.mahta.rastin.broadcastapplication.model.Slider;
import com.mahta.rastin.broadcastapplication.model.Staff;
import com.mahta.rastin.broadcastapplication.model.Student;
import com.mahta.rastin.broadcastapplication.model.UserToken;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmController {
    private Realm realm;

    public RealmController(Context context){
        realm = Realm.getInstance(
                new RealmConfiguration.Builder()
                        .name(context.getPackageName() + ".realm")
                        .build());
    }

    public Realm getRealm() {
        return realm;
    }

    public void refresh() {
        realm.refresh();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on UserToken Model
     **********************************************************************************************/
    //find all objects in the UserToken.class
    public UserToken getUserToken() {
        return realm.where(UserToken.class).findFirst();
    }

    //check if UserToken.class is empty
    public boolean hasUserToken() {
        return !realm.where(UserToken.class).findAll().isEmpty();
    }

    //add a UserToken to Realm
    public void addUserToken(UserToken userToken, boolean isParent){
        //Because each user can only have one UserToken
        removeUserToken();

        userToken.setParent(isParent);
        userToken.setId((int) (Math.random()*100));
        realm.beginTransaction();
        realm.copyToRealm(userToken);
        realm.commitTransaction();
    }

    //remove UserToken from realm
    public void removeUserToken() {
        realm.beginTransaction();
        RealmResults<UserToken> result = realm.where(UserToken.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Student Model
     **********************************************************************************************/
    //find all objects in the UserToken.class
    public Student getStudent() {
        return realm.where(Student.class).findFirst();
    }

    //check if UserToken.class is empty
    public boolean hasStudent() {
        return !realm.where(Student.class).findAll().isEmpty();
    }

    //add a UserToken to Realm
    public void addStudent(Student student){
        //Because each user can only have one UserToken
        removeStudent();

        student.setId((int) (Math.random()*100));
        realm.beginTransaction();
        realm.copyToRealm(student);
        realm.commitTransaction();
    }

    //remove UserToken from realm
    public void removeStudent() {
        realm.beginTransaction();
        RealmResults<Student> result = realm.where(Student.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Group Model
     **********************************************************************************************/
    //find all objects in the Group.class
    public List<Group> getGroupList() {
        return realm.where(Group.class).findAll();
    }

    //check if Group.class is empty
    public boolean hasGroup() {
        return !realm.where(Group.class).findAll().isEmpty();
    }

    //add a Group to Realm
    public void addGroup(Group group){
        realm.beginTransaction();
        realm.copyToRealm(group);
        realm.commitTransaction();
    }

    public Group getGroup(int id){
        return realm.where(Group.class).equalTo("id", id).findFirst();
    }

    //remove UserToken from realm
    public void clearAllGroups() {
        realm.beginTransaction();
        RealmResults<Group> result = realm.where(Group.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Post Model
     **********************************************************************************************/
    //find all objects in the Post.class
    public RealmResults<Post> getAllPosts() {
        return realm.where(Post.class).findAll();
    }

    //check if Post.class is empty
    public boolean hasPosts() {
        return !realm.where(Post.class).findAll().isEmpty();
    }

    //clear all objects from Post.class
    public void clearAllPosts() {
        realm.beginTransaction();
        RealmResults<Post> result = realm.where(Post.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Post to Realm
    public void addPost(Post post){
        realm.beginTransaction();
        realm.copyToRealm(post);
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Media Model
     **********************************************************************************************/
    //find all objects in the Media.class
    public RealmResults<Media> getAllMedia() {
        return realm.where(Media.class).findAll();
    }

    //check if Media.class is empty
    public boolean hasMedia() {
        return !realm.where(Media.class).findAll().isEmpty();
    }

    //clear all objects from Post.class
    public void clearAllMedia() {
        realm.beginTransaction();
        RealmResults<Media> result = realm.where(Media.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Media to Realm
    public void addMedia(Media media){
        realm.beginTransaction();
        realm.copyToRealm(media);
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Program Model
     **********************************************************************************************/
    //find all objects in the Post.class
    public RealmResults<Program> getAllPrograms() {
        return realm.where(Program.class).findAll();
    }

    //check if Post.class is empty
    public boolean hasPrograms() {
        return !realm.where(Program.class).findAll().isEmpty();
    }

    //clear all objects from Post.class
    public void clearAllPrograms() {
        realm.beginTransaction();
        RealmResults<Program> result = realm.where(Program.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Post to Realm
    public void addProgram(Program program){
        realm.beginTransaction();
        realm.copyToRealm(program);
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Staff Model
     **********************************************************************************************/
    //find all objects in the Post.class
    public RealmResults<Staff> getAllStaff() {
        return realm.where(Staff.class).findAll().sort(Keys.KEY_ID, Sort.ASCENDING);
    }

    //check if Post.class is empty
    public boolean hasStaff() {
        return !realm.where(Staff.class).findAll().isEmpty();
    }

    //check if Post.class is empty
    public boolean hasStaff(int id) {
        return realm.where(Staff.class).equalTo("id", id).findFirst() != null;
    }

    //check if Post.class is empty
    public Staff getStaff(int id) {
        return realm.where(Staff.class).equalTo("id", id).findFirst();
    }

    //check if Post.class is empty
    public void removeStaff(int id) {
        realm.beginTransaction();
        RealmResults<Staff> result = realm.where(Staff.class)
                .equalTo(Keys.KEY_ID, id).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //clear all objects from Post.class
    public void clearAllStaffs() {
        realm.beginTransaction();
        RealmResults<Staff> result = realm.where(Staff.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Post to Realm
    public void addStaff(Staff staff){
        realm.beginTransaction();
        realm.copyToRealm(staff);
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on FavoritePost Model
     **********************************************************************************************/
    //find all objects in the Post.class
    public RealmResults<FavoritePost> getAllFavoritePosts() {
        return realm.where(FavoritePost.class).findAll().sort(Keys.KEY_ID, Sort.DESCENDING);
    }

    public RealmResults<FavoritePost> getFavoritePostWithTitle(String title) {
        return realm.where(FavoritePost.class).contains(
                "title",
                title,
                Case.INSENSITIVE).findAll().sort(Keys.KEY_ID, Sort.DESCENDING);

    }

    public boolean hasFavoritePost(int id){
        return !realm.where(FavoritePost.class).equalTo(Keys.KEY_ID, id).findAll().isEmpty();
    }

    //check if Post.class is empty
    public boolean hasFavoritePosts() {
        return !realm.where(FavoritePost.class).findAll().isEmpty();
    }

    //clear all objects from Post.class
    public void clearAllFvoritePosts() {
        realm.beginTransaction();
        RealmResults<FavoritePost> result = realm.where(FavoritePost.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Post to Realm
    public void addFavoritePost(FavoritePost post){
        realm.beginTransaction();
        realm.copyToRealm(post);
        realm.commitTransaction();
    }

    //remove a Post to Realm
    public void removeFavoritePost(int id){
        realm.beginTransaction();
        RealmResults<FavoritePost> result = realm.where(FavoritePost.class)
                .equalTo(Keys.KEY_ID, id).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
        G.i("Removing " + id);
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on FavoritePost Model
     **********************************************************************************************/
    //find all objects in the program.class
    public RealmResults<FavoriteProgram> getAllFavoritePrograms() {
        return realm.where(FavoriteProgram.class).findAll().sort(Keys.KEY_ID, Sort.DESCENDING);
    }

    public RealmResults<FavoriteProgram> getFavoriteProgramWithTitle(String title) {
        return realm.where(FavoriteProgram.class).contains(
                "title",
                title,
                Case.INSENSITIVE).findAll().sort(Keys.KEY_ID, Sort.DESCENDING);

    }

    public boolean hasFavoriteProgram(int id){
        return !realm.where(FavoriteProgram.class).equalTo(Keys.KEY_ID, id).findAll().isEmpty();
    }

    //check if Program.class is empty
    public boolean hasFavoritePrograms() {
        return !realm.where(FavoriteProgram.class).findAll().isEmpty();
    }

    //clear all objects from Program.class
    public void clearAllFvoritePrograms() {
        realm.beginTransaction();
        RealmResults<FavoriteProgram> result = realm.where(FavoriteProgram.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Program to Realm
    public void addFavoriteProgram(FavoriteProgram program){
        realm.beginTransaction();
        realm.copyToRealm(program);
        realm.commitTransaction();
    }

    //remove a Program to Realm
    public void removeFavoriteProgram(int id){
        realm.beginTransaction();
        RealmResults<FavoriteProgram> result = realm.where(FavoriteProgram.class)
                .equalTo(Keys.KEY_ID, id).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
        G.i("Removing " + id);
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on FavoriteMedia Model
     **********************************************************************************************/
    //find all objects in the Post.class
    public RealmResults<FavoriteMedia> getAllFavoriteMedia() {
        return realm.where(FavoriteMedia.class).findAll().sort(Keys.KEY_ID, Sort.DESCENDING);
    }

    public RealmResults<FavoriteMedia> getFavoriteMediaWithTitle(String title) {
        return realm.where(FavoriteMedia.class).contains(
                "title",
                title,
                Case.INSENSITIVE).findAll().sort(Keys.KEY_ID, Sort.DESCENDING);

    }

    public boolean hasFavoriteMedia(int id){
        return !realm.where(FavoriteMedia.class).equalTo(Keys.KEY_ID, id).findAll().isEmpty();
    }

    //check if Post.class is empty
    public boolean hasFavoriteMedia() {
        return !realm.where(FavoriteMedia.class).findAll().isEmpty();
    }

    //clear all objects from Post.class
    public void clearAllFvoriteMedia() {
        realm.beginTransaction();
        RealmResults<FavoriteMedia> result = realm.where(FavoriteMedia.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Post to Realm
    public void addFavoriteMedia(FavoriteMedia media){
        realm.beginTransaction();
        realm.copyToRealm(media);
        realm.commitTransaction();
    }

    //remove a Post to Realm
    public void removeFavoriteMedia(int id){
        realm.beginTransaction();
        RealmResults<FavoriteMedia> result = realm.where(FavoriteMedia.class)
                .equalTo(Keys.KEY_ID, id).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
        G.i("Removing " + id);
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Slider Model
     **********************************************************************************************/
    //find all objects in the UserToken.class
    public Slider getSlider() {
        return realm.where(Slider.class).findFirst();
    }

    //check if UserToken.class is empty
    public boolean hasSlider() {
        return !realm.where(Slider.class).findAll().isEmpty();
    }

    //add a UserToken to Realm
    public void addSlider(Slider slider){
        //Because each user can only have one slider
        removeUserSlider();
        realm.beginTransaction();
        realm.copyToRealm(slider);
        realm.commitTransaction();
    }

    //remove UserToken from realm
    public void removeUserSlider() {
        realm.beginTransaction();
        RealmResults<Slider> result = realm.where(Slider.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

}

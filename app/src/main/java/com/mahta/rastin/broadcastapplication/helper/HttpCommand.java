package com.mahta.rastin.broadcastapplication.helper;

import android.content.ContentValues;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;


public class HttpCommand {
    private OnResultListener onResultListener;
    private String currentCommand;
    private ContentValues currentParams;
    private String[] currentArgs;
    private HttpManager httpManager;

    //Commands
    public static final String COMMAND_REGISTER = "register";
    public static final String COMMAND_GET_VERIFICATION_CODE = "get_verification_code";
    public static final String COMMAND_CONFIRM = "confirm";
    public static final String COMMAND_LOGIN = "login";
    public static final String COMMAND_LOGIN_AS_PARENT = "login as parent";
    public static final String COMMAND_FORGET_PASSWORD= "forget password";

    public static final String COMMAND_CHECK_TOKEN = "check_token";
    public static final String COMMAND_UPDATE_FCM_TOKEN = "update_fcm_token";
    public static final String COMMAND_GET_WORKBOOK = "get workbook";
    public static final String COMMAND_GET_INFO = "get info";
    public static final String COMMAND_UPDATE_INFO = "update info";
    public static final String COMMAND_CHANGE_PASSWORD = "change password";

    public static final String COMMAND_GET_POSTS = "get posts";
    public static final String COMMAND_GET_MESSAGES = "get messages";
    public static final String COMMAND_GET_SLIDER = "get slider";
    public static final String COMMAND_GET_STAFF = "get staff";
    public static final String COMMAND_GET_SLIDER_UPDATED = "get slider updated";
    public static final String COMMAND_GET_STAFF_UPDATED = "get staff updated";
    public static final String COMMAND_SEND_TICKET = "send ticket";
    public static final String COMMAND_GET_LAST_NOTIFICATIONS = "get last notifications";
    public static final String COMMAND_GET_GROUP_LIST = "get group list";


    public HttpCommand(String command,ContentValues params,String ... args){
        this.currentCommand = command;
        this.currentParams = params;
        this.currentArgs = args;
        httpManager = new HttpManager();
    }

    public void execute(){
        if (httpManager!=null && onResultListener!=null){
            switch (currentCommand) {
                case COMMAND_REGISTER:
                    commandRegister();
                    break;
                case COMMAND_GET_VERIFICATION_CODE:
                    commandGetVerificationCode();
                    break;
                case COMMAND_CONFIRM:
                    commandConfirm();
                    break;
                case COMMAND_LOGIN:
                    commandLogin();
                    break;
                case COMMAND_LOGIN_AS_PARENT:
                    commandLoginAsParent();
                    break;
                case COMMAND_FORGET_PASSWORD:
                    commandForgetPassword();
                    break;

                case COMMAND_CHECK_TOKEN:
                    commandCheckToken();
                    break;
                case COMMAND_UPDATE_FCM_TOKEN:
                    commandUpdateFcmToken();
                    break;
                case COMMAND_GET_INFO:
                    commandGetInfo();
                    break;
                case COMMAND_UPDATE_INFO:
                    commandUpdateInfo();
                    break;
                case COMMAND_GET_WORKBOOK:
                    commandGetWorkbook();
                    break;
                case COMMAND_CHANGE_PASSWORD:
                    commandChangePassword();
                    break;

                case COMMAND_GET_POSTS:
                    commandGetPosts();
                    break;
                case COMMAND_GET_MESSAGES:
                    commandGetMessages();
                    break;
                case COMMAND_SEND_TICKET:
                    commandSendTicket();
                    break;
                case COMMAND_GET_LAST_NOTIFICATIONS:
                    commandGetLastNotifications();
                    break;
                case COMMAND_GET_GROUP_LIST:
                    commandGetGroupList();
                    break;
                case COMMAND_GET_SLIDER:
                    commandGetSlider();
                    break;
                case COMMAND_GET_SLIDER_UPDATED:
                    commandGetSliderUpdated();
                    break;
                case COMMAND_GET_STAFF:
                    commandGetStaff();
                    break;
                case COMMAND_GET_STAFF_UPDATED:
                    commandGetStaffUpdated();
                    break;
                default:
                    G.e("Invalid Command");
            }
        }
        else
            G.e("Inappropriate Command Structure");
    }

    public HttpCommand setOnResultListener(OnResultListener listener){
        onResultListener = listener;
        httpManager.setOnResultListener(new OnResultListener() {
            @Override
            public void onResult(String result) {
                if(onResultListener!=null){
                    onResultListener.onResult(result);
                }
            }
        });
        return this;
    }

    private void commandRegister(){ httpManager.post(G.BASE_URL+"ustudent/register",currentParams); }

    private void commandGetVerificationCode(){ httpManager.post(G.BASE_URL+"ustudent/verification_code",currentParams); }

    private void commandConfirm(){ httpManager.post(G.BASE_URL+"ustudent/confirm",currentParams); }

    private void commandLogin(){ httpManager.post(G.BASE_URL+"ustudent/login",currentParams); }

    private void commandLoginAsParent(){ httpManager.post(G.BASE_URL+"ustudent/login/parent",currentParams); }

    private void commandForgetPassword(){ httpManager.post(G.BASE_URL+"ustudent/forget_password",currentParams); }


    private void commandCheckToken() { httpManager.post(G.BASE_URL+"ustudent/check_token",currentParams); }

    private void commandUpdateFcmToken() { httpManager.post(G.BASE_URL+"ustudent/update_fcm_token",currentParams); }

    private void commandGetInfo() { httpManager.post(G.BASE_URL+"ustudent/info",currentParams); }

    private void commandUpdateInfo() { httpManager.post(G.BASE_URL+"ustudent/info/update",currentParams); }

    private void commandChangePassword() { httpManager.post(G.BASE_URL+"ustudent/change_password",currentParams); }

    private void commandGetWorkbook(){ httpManager.post(G.BASE_URL+"ustudent/workbook",currentParams); }


    private void commandGetPosts(){ httpManager.get(G.BASE_URL+"posts",currentArgs);}

    private void commandGetMessages(){ httpManager.post(G.BASE_URL+"messages",currentParams);}

    private void commandSendTicket() { httpManager.post(G.BASE_URL+"send_ticket",currentParams); }

    private void commandGetLastNotifications(){ httpManager.get(G.BASE_URL+"notification",currentArgs);}

    private void commandGetGroupList() { httpManager.get(G.BASE_URL+"groups",currentArgs);}

    private void commandGetSlider() { httpManager.get(G.BASE_URL+"slider",currentArgs);}

    private void commandGetSliderUpdated() { httpManager.get(G.BASE_URL+"slider/updated",currentArgs);}

    private void commandGetStaffUpdated() { httpManager.get(G.BASE_URL+"staff/updated",currentArgs); }

    private void commandGetStaff() { httpManager.post(G.BASE_URL+"staff",currentParams); }

}



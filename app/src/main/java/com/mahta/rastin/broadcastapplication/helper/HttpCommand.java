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
    public static final String COMMAND_LOGIN = "login";
    public static final String COMMAND_AUTHORIZE = "authorize";
    public static final String COMMAND_CONFIRM = "confirm";
    public static final String COMMAND_GET_POSTS = "get posts";
    public static final String COMMAND_GET_WORKBOOK = "get workbook";
    public static final String COMMAND_GET_INFO = "get info";
    public static final String COMMAND_GET_POST = "get post";
    public static final String COMMAND_SEND_TICKET = "send ticket";
    public static final String COMMAND_GET_LAST_NOTIFICATIONS = "get last notifications";
    public static final String COMMAND_GET_PASSWORD = "get password";
    public static final String COMMAND_CHANGE_PASSWORD = "change password";
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
                case COMMAND_LOGIN:
                    setCommandLogin();
                    break;
                case COMMAND_AUTHORIZE:
                    commandAuthorize();
                    break;
                case COMMAND_GET_POSTS:
                    commandGetPosts();
                    break;
                case COMMAND_CONFIRM:
                    commandConfirm();
                    break;
                case COMMAND_GET_WORKBOOK:
                    commandGetWorkbook();
                    break;
                case COMMAND_GET_INFO:
                    commandGetInfo();
                    break;
                case COMMAND_GET_POST:
                    commandGetPost();
                    break;
                case COMMAND_SEND_TICKET:
                    commandSendTicket();
                    break;
                case COMMAND_GET_LAST_NOTIFICATIONS:
                    commandGetLastNotifications();
                    break;
                case COMMAND_GET_PASSWORD:
                    commandGetPassword();
                case COMMAND_CHANGE_PASSWORD:
                    commandChangePassword();
                    break;
                case COMMAND_GET_GROUP_LIST:
                    commandGetGroupList();
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

    private void setCommandLogin(){ httpManager.post(G.BASE_URL+"student/login",currentParams); }

    private void commandAuthorize(){ httpManager.post(G.BASE_URL+"student/authorize",currentParams); }

    private void commandConfirm(){ httpManager.post(G.BASE_URL+"student/confirm",currentParams); }

    private void commandGetWorkbook(){ httpManager.post(G.BASE_URL+"student/workbook",currentParams); }

    private void commandGetPosts(){ httpManager.get(G.BASE_URL+"posts",currentArgs);}

    private void commandGetPost(){ httpManager.get(G.BASE_URL+"post",currentArgs);}

    private void commandGetInfo() { httpManager.post(G.BASE_URL+"student/info",currentParams); }

    private void commandSendTicket() { httpManager.post(G.BASE_URL+"send_ticket",currentParams); }

    private void commandGetLastNotifications(){ httpManager.get(G.BASE_URL+"notification",currentArgs);}

    private void commandGetPassword() { httpManager.post(G.BASE_URL+"student/get_password",currentParams); }

    private void commandChangePassword() { httpManager.post(G.BASE_URL+"student/change_password",currentParams); }

    private void commandGetGroupList() { httpManager.get(G.BASE_URL+"groups",currentArgs);}
}



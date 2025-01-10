package ir.tildaweb.tildachatmessaging.app;

import android.content.Context;
import android.util.Log;

//import com.aghajari.emojiview.AXEmojiManager;
//import com.aghajari.emojiview.appleprovider.AXAppleEmojiProvider;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import ir.tildaweb.tildachatmessaging.app.request.SocketRequestController;

public class TildaChatApp {

    private static final String TAG = "TildaChatApp";
    private static Socket socket;
    private static Integer mUserId;
    private static SocketRequestController socketRequestController;
    //    private static PublishSubject<String> publishSubject;
    public static String _FILE_URL;
    public static String _downloadFolder = "tilda";
    public static String _uploadRoute;
    public static boolean _isTime48 = false;

    public static void setUpEmojis(Context context) {
//        AXEmojiManager.install(context, new AXAppleEmojiProvider(context));
    }

    public static void setUp(String chatUrl, String query, Integer userId, boolean reset) {
        if (reset) {
            if (socket != null)
                socket.disconnect();
            socket = null;
        }
        if (socket == null) {
            try {
                mUserId = userId;
                IO.Options options = new IO.Options();
                options.forceNew = true;
                options.query = query;
                socket = IO.socket(chatUrl, options);
                socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                });
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public static void setConstantsUp(String FILE_URL, String downloadFolder, String uploadRoute) {
        _FILE_URL = FILE_URL;
        _downloadFolder = downloadFolder;
        _uploadRoute = uploadRoute;
    }

    public static void setConstantsUp(String FILE_URL, String downloadFolder, String uploadRoute, boolean isTime48) {
        _FILE_URL = FILE_URL;
        _downloadFolder = downloadFolder;
        _uploadRoute = uploadRoute;
        _isTime48 = isTime48;
    }


    public static Integer getUserId() {
        return mUserId;
    }

    public static Socket getSocket() {
        if (socket != null) {
            if (!socket.connected()) {
                socket.connect();
            }
            Log.d(TAG, "getSocket: " + socket.connected());
        }
        return socket;
    }

//    public static PublishSubject<String> getPublishSubject() {
//        if (publishSubject == null) {
//            publishSubject = PublishSubject.create();
//        }
//        return publishSubject;
//    }

    public static SocketRequestController getSocketRequestController() {
        if (socketRequestController == null) {
            socketRequestController = SocketRequestController.getInstance();
        }
        return socketRequestController;
    }
}
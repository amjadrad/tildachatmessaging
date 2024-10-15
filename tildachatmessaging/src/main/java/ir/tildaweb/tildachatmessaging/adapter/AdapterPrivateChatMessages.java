package ir.tildaweb.tildachatmessaging.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eightbitlab.com.blurview.RenderScriptBlur;
import ir.tildaweb.tildachatmessaging.R;
import ir.tildaweb.tildachatmessaging.app.TildaChatApp;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatFileReplyfalseMeGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatFileReplyfalseMePrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatFileReplyfalseOtherGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatFileReplyfalseOtherPrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatFileReplytrueMeGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatFileReplytrueMePrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatFileReplytrueOtherGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatFileReplytrueOtherPrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPictureReplyfalseMeGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPictureReplyfalseMePrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPictureReplyfalseOtherGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPictureReplyfalseOtherPrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPictureReplytrueMeGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPictureReplytrueMePrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPictureReplytrueOtherGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPictureReplytrueOtherPrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPurchasableSecurePictureReplyfalseMePrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatPurchasableSecurePictureReplyfalseOtherPrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatSecurePictureReplyfalseMePrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatSecurePictureReplyfalseOtherPrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatTextReplyfalseMeGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatTextReplyfalseMePrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatTextReplyfalseOtherGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatTextReplyfalseOtherPrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatTextReplytrueMeGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatTextReplytrueMePrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatTextReplytrueOtherGroupBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatTextReplytrueOtherPrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatVoiceReplyfalseMePrivateBinding;
import ir.tildaweb.tildachatmessaging.databinding.ListSocketChatVoiceReplyfalseOtherPrivateBinding;
import ir.tildaweb.tildachatmessaging.dialogs.DialogShowPicture;
import ir.tildaweb.tildachatmessaging.enums.ChatroomType;
import ir.tildaweb.tildachatmessaging.interfaces.IChatUtils;
import ir.tildaweb.tildachatmessaging.interfaces.LoadMoreData;
import ir.tildaweb.tildachatmessaging.models.base_models.Message;
import ir.tildaweb.tildachatmessaging.ui.values.MessageTypeUtil;
import ir.tildaweb.tildachatmessaging.utils.DateUtils;
import ir.tildaweb.tildachatmessaging.utils.FileDownloaderNew;
import ir.tildaweb.tildachatmessaging.utils.FontUtils;
import ir.tildaweb.tildachatmessaging.utils.OnSwipeTouchListener;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;


public class AdapterPrivateChatMessages extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FileDownloaderNew.OnFileDownloadListener {

    public enum SearchType {
        REPLY,
    }

    private final String TAG = getClass().getName();
    private final Context context;
    private final Activity activity;
    private final ArrayList<Message> chatMessages;
    private final RecyclerView recyclerView;
    private final int userId;
    private final DateUtils dateHelper;
    private ChatroomType roomType = ChatroomType.PRIVATE;
    private final String FILE_URL;
    private boolean isAdmin;

    private final int visibleThreshold = 5;
    private int firstVisibleItem;
    private boolean loading = true;
    private final LoadMoreData loadMoreData;
    private final IChatUtils iChatUtils;
    private final DownloadManager downloadManager;
    private boolean isDownloadingFile = false;
    private boolean isWorkWithFullname = false;
    private static FontUtils fontUtils;
    private Typeface typeface = null;
    private boolean isDarkMode = false;
    private boolean isUserReadPreviousMessage = false;


    public AdapterPrivateChatMessages(Context context, Activity activity, int userId, String FILE_URL, RecyclerView recyclerView, ArrayList<Message> chatMessages, LoadMoreData loadMoreData, IChatUtils iChatUtils, Typeface typeface) {
        this.chatMessages = chatMessages;
        this.context = context;
        this.activity = activity;
        this.userId = userId;
        this.iChatUtils = iChatUtils;
        this.recyclerView = recyclerView;
        this.loadMoreData = loadMoreData;
        this.dateHelper = new DateUtils();
        fontUtils = new FontUtils(typeface);
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (FILE_URL.endsWith("/")) {
            this.FILE_URL = FILE_URL;
        } else {
            this.FILE_URL = FILE_URL.concat("/");
        }
        setScrollListener();
    }

    public void setWorkWithFullname() {
        this.isWorkWithFullname = true;
    }

    private void setScrollListener() {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    firstVisibleItem = linearLayoutManager
                            .findFirstVisibleItemPosition();
                    if (!loading && (firstVisibleItem - visibleThreshold) < 0) {
                        if (loadMoreData != null) {
                            loading = true;
                            loadMoreData.onLoadMore();
                        }
                    }
                    if (loadMoreData != null) {
                        loadMoreData.onShowGoDownButton(linearLayoutManager.findLastCompletelyVisibleItemPosition() - getItemCount() < -10);
                    }
                }
            });
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    public void setFont(Typeface typeface) {
        this.typeface = typeface;
    }

    public void setDarkMode() {
        this.isDarkMode = true;
    }

    public void setUserReadPreviousMessage(boolean isUserReadPreviousMessage) {
        this.isUserReadPreviousMessage = isUserReadPreviousMessage;
    }

    //Text
    public static class ChatHolder_Text_ReplyFalse_Me_Private extends Holder {
        private final ListSocketChatTextReplyfalseMePrivateBinding binding;

        public ChatHolder_Text_ReplyFalse_Me_Private(@NonNull ListSocketChatTextReplyfalseMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Text_ReplyFalse_Me_Group extends Holder {

        private final ListSocketChatTextReplyfalseMeGroupBinding binding;

        public ChatHolder_Text_ReplyFalse_Me_Group(ListSocketChatTextReplyfalseMeGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Text_ReplyFalse_Other_Private extends Holder {
        private final ListSocketChatTextReplyfalseOtherPrivateBinding binding;

        public ChatHolder_Text_ReplyFalse_Other_Private(ListSocketChatTextReplyfalseOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Text_ReplyTrue_Me_Private extends Holder {
        private final ListSocketChatTextReplytrueMePrivateBinding binding;

        public ChatHolder_Text_ReplyTrue_Me_Private(ListSocketChatTextReplytrueMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Text_ReplyTrue_Me_Group extends Holder {
        private final ListSocketChatTextReplytrueMeGroupBinding binding;

        public ChatHolder_Text_ReplyTrue_Me_Group(ListSocketChatTextReplytrueMeGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Text_ReplyTrue_Other_Private extends Holder {
        private final ListSocketChatTextReplytrueOtherPrivateBinding binding;


        public ChatHolder_Text_ReplyTrue_Other_Private(ListSocketChatTextReplytrueOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Text_ReplyFalse_Other_Group extends Holder {
        private final ListSocketChatTextReplyfalseOtherGroupBinding binding;

        public ChatHolder_Text_ReplyFalse_Other_Group(ListSocketChatTextReplyfalseOtherGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Text_ReplyTrue_Other_Group extends Holder {
        private final ListSocketChatTextReplytrueOtherGroupBinding binding;

        public ChatHolder_Text_ReplyTrue_Other_Group(ListSocketChatTextReplytrueOtherGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }


    //Picture
    public static class ChatHolder_Picture_ReplyFalse_Me_Private extends Holder {
        private final ListSocketChatPictureReplyfalseMePrivateBinding binding;

        public ChatHolder_Picture_ReplyFalse_Me_Private(ListSocketChatPictureReplyfalseMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Picture_ReplyTrue_Me_Private extends Holder {
        private final ListSocketChatPictureReplytrueMePrivateBinding binding;

        public ChatHolder_Picture_ReplyTrue_Me_Private(ListSocketChatPictureReplytrueMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Picture_ReplyFalse_Me_Group extends Holder {
        private final ListSocketChatPictureReplyfalseMeGroupBinding binding;

        public ChatHolder_Picture_ReplyFalse_Me_Group(ListSocketChatPictureReplyfalseMeGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Picture_ReplyTrue_Me_Group extends Holder {
        private final ListSocketChatPictureReplytrueMeGroupBinding binding;

        public ChatHolder_Picture_ReplyTrue_Me_Group(ListSocketChatPictureReplytrueMeGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Picture_ReplyFalse_Other_Private extends Holder {
        private final ListSocketChatPictureReplyfalseOtherPrivateBinding binding;

        public ChatHolder_Picture_ReplyFalse_Other_Private(ListSocketChatPictureReplyfalseOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Picture_ReplyFalse_Other_Group extends Holder {
        private final ListSocketChatPictureReplyfalseOtherGroupBinding binding;


        public ChatHolder_Picture_ReplyFalse_Other_Group(ListSocketChatPictureReplyfalseOtherGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Picture_ReplyTrue_Other_Private extends Holder {
        private final ListSocketChatPictureReplytrueOtherPrivateBinding binding;

        public ChatHolder_Picture_ReplyTrue_Other_Private(ListSocketChatPictureReplytrueOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Picture_ReplyTrue_Other_Group extends Holder {
        private final ListSocketChatPictureReplytrueOtherGroupBinding binding;

        public ChatHolder_Picture_ReplyTrue_Other_Group(ListSocketChatPictureReplytrueOtherGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    //File
    public static class ChatHolder_File_ReplyFalse_Me_Private extends Holder {
        private final ListSocketChatFileReplyfalseMePrivateBinding binding;

        public ChatHolder_File_ReplyFalse_Me_Private(ListSocketChatFileReplyfalseMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_File_ReplyFalse_Me_Group extends Holder {
        private final ListSocketChatFileReplyfalseMeGroupBinding binding;

        public ChatHolder_File_ReplyFalse_Me_Group(ListSocketChatFileReplyfalseMeGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_File_ReplyFalse_Other_Private extends Holder {
        private final ListSocketChatFileReplyfalseOtherPrivateBinding binding;

        public ChatHolder_File_ReplyFalse_Other_Private(ListSocketChatFileReplyfalseOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_File_ReplyTrue_Me_Private extends Holder {
        private final ListSocketChatFileReplytrueMePrivateBinding binding;

        public ChatHolder_File_ReplyTrue_Me_Private(ListSocketChatFileReplytrueMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_File_ReplyTrue_Me_Group extends Holder {
        private final ListSocketChatFileReplytrueMeGroupBinding binding;

        public ChatHolder_File_ReplyTrue_Me_Group(ListSocketChatFileReplytrueMeGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_File_ReplyTrue_Other_Private extends Holder {
        private final ListSocketChatFileReplytrueOtherPrivateBinding binding;


        public ChatHolder_File_ReplyTrue_Other_Private(ListSocketChatFileReplytrueOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_File_ReplyFalse_Other_Group extends Holder {
        private final ListSocketChatFileReplyfalseOtherGroupBinding binding;

        public ChatHolder_File_ReplyFalse_Other_Group(ListSocketChatFileReplyfalseOtherGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_File_ReplyTrue_Other_Group extends Holder {
        private final ListSocketChatFileReplytrueOtherGroupBinding binding;


        public ChatHolder_File_ReplyTrue_Other_Group(ListSocketChatFileReplytrueOtherGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }


    //Video
    public static class ChatHolder_Video_ReplyFalse_Me_Private extends Holder {
        private final ListSocketChatFileReplyfalseMePrivateBinding binding;

        public ChatHolder_Video_ReplyFalse_Me_Private(ListSocketChatFileReplyfalseMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Video_ReplyFalse_Me_Group extends Holder {
        private final ListSocketChatFileReplyfalseMeGroupBinding binding;

        public ChatHolder_Video_ReplyFalse_Me_Group(ListSocketChatFileReplyfalseMeGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Video_ReplyFalse_Other_Private extends Holder {
        private final ListSocketChatFileReplyfalseOtherPrivateBinding binding;

        public ChatHolder_Video_ReplyFalse_Other_Private(ListSocketChatFileReplyfalseOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Video_ReplyTrue_Me_Private extends Holder {
        private final ListSocketChatFileReplytrueMePrivateBinding binding;

        public ChatHolder_Video_ReplyTrue_Me_Private(ListSocketChatFileReplytrueMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Video_ReplyTrue_Me_Group extends Holder {
        private final ListSocketChatFileReplytrueMeGroupBinding binding;

        public ChatHolder_Video_ReplyTrue_Me_Group(ListSocketChatFileReplytrueMeGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Video_ReplyTrue_Other_Private extends Holder {
        private final ListSocketChatFileReplytrueOtherPrivateBinding binding;


        public ChatHolder_Video_ReplyTrue_Other_Private(ListSocketChatFileReplytrueOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Video_ReplyFalse_Other_Group extends Holder {
        private final ListSocketChatFileReplyfalseOtherGroupBinding binding;

        public ChatHolder_Video_ReplyFalse_Other_Group(ListSocketChatFileReplyfalseOtherGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Video_ReplyTrue_Other_Group extends Holder {
        private final ListSocketChatFileReplytrueOtherGroupBinding binding;

        public ChatHolder_Video_ReplyTrue_Other_Group(ListSocketChatFileReplytrueOtherGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    //Voice
    //Secure picture
    public static class ChatHolder_Voice_ReplyFalse_Me_Private extends Holder {
        private final ListSocketChatVoiceReplyfalseMePrivateBinding binding;

        public ChatHolder_Voice_ReplyFalse_Me_Private(ListSocketChatVoiceReplyfalseMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_Voice_ReplyFalse_Other_Private extends Holder {
        private final ListSocketChatVoiceReplyfalseOtherPrivateBinding binding;

        public ChatHolder_Voice_ReplyFalse_Other_Private(ListSocketChatVoiceReplyfalseOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }


    //Secure picture
    public static class ChatHolder_SecurePicture_ReplyFalse_Me_Private extends Holder {
        private final ListSocketChatSecurePictureReplyfalseMePrivateBinding binding;

        public ChatHolder_SecurePicture_ReplyFalse_Me_Private(ListSocketChatSecurePictureReplyfalseMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_SecurePicture_ReplyFalse_Other_Private extends Holder {
        private final ListSocketChatSecurePictureReplyfalseOtherPrivateBinding binding;

        public ChatHolder_SecurePicture_ReplyFalse_Other_Private(ListSocketChatSecurePictureReplyfalseOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    //Purchasable Secure Picture
    public static class ChatHolder_PurchasableSecurePicture_ReplyFalse_Me_Private extends Holder {
        private final ListSocketChatPurchasableSecurePictureReplyfalseMePrivateBinding binding;

        public ChatHolder_PurchasableSecurePicture_ReplyFalse_Me_Private(ListSocketChatPurchasableSecurePictureReplyfalseMePrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }

    public static class ChatHolder_PurchasableSecurePicture_ReplyFalse_Other_Private extends Holder {
        private final ListSocketChatPurchasableSecurePictureReplyfalseOtherPrivateBinding binding;

        public ChatHolder_PurchasableSecurePicture_ReplyFalse_Other_Private(ListSocketChatPurchasableSecurePictureReplyfalseOtherPrivateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            fontUtils.replaceFonts(binding.getRoot());
        }
    }


    @Override
    public int getItemViewType(int position) {
        Message chatMessage = chatMessages.get(position);
        //        Log.d(TAG, "getItemViewType: " + type + " _ " + chatMessage.getMessage() + " _ " + chatMessage.getUserId());
        return MessageTypeUtil.getType(chatMessage, userId, roomType);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        Message chatMessage = chatMessages.get(holder.getAdapterPosition());
        Log.d(TAG, "onViewAttachedToWindow: " + chatMessage.getSeenCount());
        if (chatMessage.getUserId() != userId) {
            if (chatMessage.getSeenCount() == 0) {
                Log.d(TAG, "onViewAttachedToWindow: " + chatMessage.getSeenCount());
                iChatUtils.onMessageSeen(chatMessage.getId());
            } else {
                Log.d(TAG, "onViewAttachedToWindow: " + chatMessage.getSeenCount());
            }
        }
        Log.d(TAG, "onViewAttachedToWindow: -------------------------------");
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Message type (text:1, picture:2 , file:3 , voice:4 , secure_picture:5)
        //Reply (false:1, true:2)
        //User type (me:1 , other:2)
        //Chatroom type (private:1 , channel:2, group:3)

        //MRUC0(4,2,2,3,0)

        switch (viewType) {
            case 11111:
                return new ChatHolder_Text_ReplyFalse_Me_Private(ListSocketChatTextReplyfalseMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
//            case 11121:
//                return null;
            case 11131:
                return new ChatHolder_Text_ReplyFalse_Me_Group(ListSocketChatTextReplyfalseMeGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 11211:
                return new ChatHolder_Text_ReplyFalse_Other_Private(ListSocketChatTextReplyfalseOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 11221://Channel,todo
                return new ChatHolder_Text_ReplyFalse_Other_Private(ListSocketChatTextReplyfalseOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 11231:
                return new ChatHolder_Text_ReplyFalse_Other_Group(ListSocketChatTextReplyfalseOtherGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 12111:
                return new ChatHolder_Text_ReplyTrue_Me_Private(ListSocketChatTextReplytrueMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
//            case 12121://Channel
//                return new ChatHolder_Text_ReplyFalse_Me_Private(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_socket_chat_text_replyfalse_me_private, parent, false));
            case 12131:
                return new ChatHolder_Text_ReplyTrue_Me_Group(ListSocketChatTextReplytrueMeGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 12211:
                return new ChatHolder_Text_ReplyTrue_Other_Private(ListSocketChatTextReplytrueOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));
//            case 12221:
//                return new ChatHolder_Text_ReplyFalse_Me_Private(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_socket_chat_text_replyfalse_me_private, parent, false));
            case 12231:
                return new ChatHolder_Text_ReplyTrue_Other_Group(ListSocketChatTextReplytrueOtherGroupBinding.inflate(LayoutInflater.from(context), parent, false));

            case 21111:
                return new ChatHolder_Picture_ReplyFalse_Me_Private(ListSocketChatPictureReplyfalseMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
//            case 21121:
//                return new ChatHolder_Text_ReplyFalse_Me_Private(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_socket_chat_text_replyfalse_me_private, parent, false));
            case 21131:
                return new ChatHolder_Picture_ReplyFalse_Me_Group(ListSocketChatPictureReplyfalseMeGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 21211:
                return new ChatHolder_Picture_ReplyFalse_Other_Private(ListSocketChatPictureReplyfalseOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 21221://channel todo
                return new ChatHolder_Picture_ReplyFalse_Other_Private(ListSocketChatPictureReplyfalseOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 21231:
                return new ChatHolder_Picture_ReplyFalse_Other_Group(ListSocketChatPictureReplyfalseOtherGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 22111:
                return new ChatHolder_Picture_ReplyTrue_Me_Private(ListSocketChatPictureReplytrueMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
////            case 22121:
////                return new ChatHolder_Text_ReplyFalse_Me_Private(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_socket_chat_text_replyfalse_me_private, parent, false));
            case 22131:
                return new ChatHolder_Picture_ReplyTrue_Me_Group(ListSocketChatPictureReplytrueMeGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 22211:
                return new ChatHolder_Picture_ReplyTrue_Other_Private(ListSocketChatPictureReplytrueOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));
////            case 22221:
////                return new ChatHolder_Text_ReplyFalse_Me_Private(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_socket_chat_text_replyfalse_me_private, parent, false));
            case 22231:
                return new ChatHolder_Picture_ReplyTrue_Other_Group(ListSocketChatPictureReplytrueOtherGroupBinding.inflate(LayoutInflater.from(context), parent, false));

            //File
            case 31111:
                return new ChatHolder_File_ReplyFalse_Me_Private(ListSocketChatFileReplyfalseMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 31131:
                return new ChatHolder_File_ReplyFalse_Me_Group(ListSocketChatFileReplyfalseMeGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 31211:
                return new ChatHolder_File_ReplyFalse_Other_Private(ListSocketChatFileReplyfalseOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 31231:
                return new ChatHolder_File_ReplyFalse_Other_Group(ListSocketChatFileReplyfalseOtherGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 32111:
                return new ChatHolder_File_ReplyTrue_Me_Private(ListSocketChatFileReplytrueMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 32131:
                return new ChatHolder_File_ReplyTrue_Me_Group(ListSocketChatFileReplytrueMeGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 32211:
                return new ChatHolder_File_ReplyTrue_Other_Private(ListSocketChatFileReplytrueOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 32231:
                return new ChatHolder_File_ReplyTrue_Other_Group(ListSocketChatFileReplytrueOtherGroupBinding.inflate(LayoutInflater.from(context), parent, false));
            case 31221://channel todo
                return new ChatHolder_File_ReplyFalse_Other_Private(ListSocketChatFileReplyfalseOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));

            //Voice
            case 41111:
                return new ChatHolder_Voice_ReplyFalse_Me_Private(ListSocketChatVoiceReplyfalseMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 41211:
                return new ChatHolder_Voice_ReplyFalse_Other_Private(ListSocketChatVoiceReplyfalseOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));

            //Secure picture
            case 51111:
                return new ChatHolder_SecurePicture_ReplyFalse_Me_Private(ListSocketChatSecurePictureReplyfalseMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 51211:
                return new ChatHolder_SecurePicture_ReplyFalse_Other_Private(ListSocketChatSecurePictureReplyfalseOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));

            //Purchasable Secure picture
            case 61111:
                return new ChatHolder_PurchasableSecurePicture_ReplyFalse_Me_Private(ListSocketChatPurchasableSecurePictureReplyfalseMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
            case 61211:
                return new ChatHolder_PurchasableSecurePicture_ReplyFalse_Other_Private(ListSocketChatPurchasableSecurePictureReplyfalseOtherPrivateBinding.inflate(LayoutInflater.from(context), parent, false));


        }

        return new ChatHolder_Text_ReplyFalse_Me_Private(ListSocketChatTextReplyfalseMePrivateBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    private void showPopupMenu(View view, Message chatMessage, boolean isFromMe, boolean isText, boolean isChannel, boolean isChannelAdmin) {
        // copy , reply , delete
        PopupMenu popup = new PopupMenu(activity, (view));
        MenuInflater inflater = popup.getMenuInflater();
        if (isFromMe && isText) {
            inflater.inflate(R.menu.popup_menu_chat_click_message_me_text, popup.getMenu());
        } else if (isFromMe && !isText) {
            inflater.inflate(R.menu.popup_menu_chat_click_message_me, popup.getMenu());
        } else if (isText) {
            inflater.inflate(R.menu.popup_menu_chat_click_message_other_text, popup.getMenu());
        } else if (!isText) {
            inflater.inflate(R.menu.popup_menu_chat_click_message_other, popup.getMenu());
        } else if (isChannel && isChannelAdmin) {
            inflater.inflate(R.menu.popup_menu_chat_click_message_me_text, popup.getMenu());
        } else {
            inflater.inflate(R.menu.popup_menu_chat_click_message_channel_other_text, popup.getMenu());
        }
        popup.show();
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.copy) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("متن", chatMessage.getMessage());
                clipboard.setPrimaryClip(clip);
                iChatUtils.onCopy();
            } else if (itemId == R.id.reply) {
                iChatUtils.onReply(chatMessage);
            } else if (itemId == R.id.edit) {
                iChatUtils.onEdit(chatMessage);
            } else if (itemId == R.id.delete) {
                iChatUtils.onDelete(chatMessage);
            }
            return false;
        });
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        final Message chatMessage = chatMessages.get(position);

        //Message type (text , picture , file , voice)
        //Reply (false, true)
        //User type (me , other)
        //Chatroom type (private , channel, group)

        //MRUC0(4,2,2,3,0)

        viewHolder.itemView.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeTop() {
//                Log.d(TAG, "onSwipeTop: ");
            }

            public void onSwipeRight() {
//                Log.d(TAG, "onSwipeRight: ");
            }

            public void onSwipeLeft() {
//                Log.d(TAG, "onSwipeLeft: ");
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(50);
                }
                iChatUtils.onReply(chatMessage);
            }

            public void onSwipeBottom() {
//                Log.d(TAG, "onSwipeBottom: ");
            }

        });

        switch (viewHolder.getItemViewType()) {
            case 11111: {
                ChatHolder_Text_ReplyFalse_Me_Private holder = (ChatHolder_Text_ReplyFalse_Me_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });

                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }
                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, true, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 11131: {
                ChatHolder_Text_ReplyFalse_Me_Group holder = (ChatHolder_Text_ReplyFalse_Me_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }
                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }

                holder.binding.tvMessage.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, true, false, false));
                holder.binding.linearChatMessage.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, true, false, false));
                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, true, false, false));
                break;
            }
            case 11211: {
                ChatHolder_Text_ReplyFalse_Other_Private holder = (ChatHolder_Text_ReplyFalse_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, true, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            //Channel text
            case 11221: {
                ChatHolder_Text_ReplyFalse_Other_Private holder = (ChatHolder_Text_ReplyFalse_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, true, true, isAdmin));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 11231: {
                ChatHolder_Text_ReplyFalse_Other_Group holder = (ChatHolder_Text_ReplyFalse_Other_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }

                String name = "";
                if (isWorkWithFullname) {
                    name = chatMessage.getUser().getFullname();
                } else {
                    if (chatMessage.getUser().getFirstName() != null) {
                        name = chatMessage.getUser().getFirstName();
                    }
                    if (chatMessage.getUser().getLastName() != null) {
                        name += " " + chatMessage.getUser().getLastName();
                    }
                }
                holder.binding.tvUserName.setText(String.format("%s", name));
                Glide.with(context).load(FILE_URL + chatMessage.getUser().getPicture()).placeholder(ContextCompat.getDrawable(context, R.drawable.ic_user_circle)).into(holder.binding.imageViewProfile);
                holder.binding.linearLayoutUserInfo.setOnClickListener(view -> iChatUtils.onMessageItemUserInfoClick(chatMessage));

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, true, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 12111: {
                ChatHolder_Text_ReplyTrue_Me_Private holder = (ChatHolder_Text_ReplyTrue_Me_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }
                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }

                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, true, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 12131: {
                ChatHolder_Text_ReplyTrue_Me_Group holder = (ChatHolder_Text_ReplyTrue_Me_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }
                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }

                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, true, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 12211: {
                ChatHolder_Text_ReplyTrue_Other_Private holder = (ChatHolder_Text_ReplyTrue_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }

                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, true, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;

            }
            case 12231: {
                ChatHolder_Text_ReplyTrue_Other_Group holder = (ChatHolder_Text_ReplyTrue_Other_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }

                String name = "";
                if (isWorkWithFullname) {
                    name = chatMessage.getUser().getFullname();
                } else {
                    if (chatMessage.getUser().getFirstName() != null) {
                        name = chatMessage.getUser().getFirstName();
                    }
                    if (chatMessage.getUser().getLastName() != null) {
                        name += " " + chatMessage.getUser().getLastName();
                    }
                }
                holder.binding.tvUserName.setText(String.format("%s", name));

                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));
                Glide.with(context).load(FILE_URL + chatMessage.getUser().getPicture()).placeholder(ContextCompat.getDrawable(context, R.drawable.ic_user_circle)).into(holder.binding.imageViewProfile);

                holder.binding.linearLayoutUserInfo.setOnClickListener(view -> iChatUtils.onMessageItemUserInfoClick(chatMessage));

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, true, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }

            case 21111: {

                final ChatHolder_Picture_ReplyFalse_Me_Private holder = (ChatHolder_Picture_ReplyFalse_Me_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));

                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                break;
            }
            case 21131: {
                final ChatHolder_Picture_ReplyFalse_Me_Group holder = (ChatHolder_Picture_ReplyFalse_Me_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));

                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }

                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                break;
            }
            case 21211: {
                final ChatHolder_Picture_ReplyFalse_Other_Private holder = (ChatHolder_Picture_ReplyFalse_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                break;
            }
            //channel picture
            case 21221: {
                final ChatHolder_Picture_ReplyFalse_Other_Private holder = (ChatHolder_Picture_ReplyFalse_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));
                holder.itemView.setOnClickListener(view -> {
                    if (isAdmin) {
                        showPopupMenu(holder.binding.tvTime, chatMessage, true, false, true, isAdmin);
                    }
                });
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());
                break;
            }
            case 21231: {
                final ChatHolder_Picture_ReplyFalse_Other_Group holder = (ChatHolder_Picture_ReplyFalse_Other_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));

                String name = "";
                if (isWorkWithFullname) {
                    name = chatMessage.getUser().getFullname();
                } else {
                    if (chatMessage.getUser().getFirstName() != null) {
                        name = chatMessage.getUser().getFirstName();
                    }
                    if (chatMessage.getUser().getLastName() != null) {
                        name += " " + chatMessage.getUser().getLastName();
                    }
                }
                holder.binding.tvUserName.setText(String.format("%s", name));
                Glide.with(context).load(FILE_URL + chatMessage.getUser().getPicture()).placeholder(ContextCompat.getDrawable(context, R.drawable.ic_user_circle)).into(holder.binding.imageViewProfile);
                holder.binding.linearLayoutUserInfo.setOnClickListener(view -> iChatUtils.onMessageItemUserInfoClick(chatMessage));
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                break;
            }
            case 22111: {
                final ChatHolder_Picture_ReplyTrue_Me_Private holder = (ChatHolder_Picture_ReplyTrue_Me_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));

                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }

                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                break;
            }
            case 22131: {
                final ChatHolder_Picture_ReplyTrue_Me_Group holder = (ChatHolder_Picture_ReplyTrue_Me_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));

                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }

                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                break;
            }
            case 22211: {
                final ChatHolder_Picture_ReplyTrue_Other_Private holder = (ChatHolder_Picture_ReplyTrue_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));

                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                break;
            }

            case 22231: {
                final ChatHolder_Picture_ReplyTrue_Other_Group holder = (ChatHolder_Picture_ReplyTrue_Other_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));
                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));
                String name = "";
                if (isWorkWithFullname) {
                    name = chatMessage.getUser().getFullname();
                } else {
                    if (chatMessage.getUser().getFirstName() != null) {
                        name = chatMessage.getUser().getFirstName();
                    }
                    if (chatMessage.getUser().getLastName() != null) {
                        name += " " + chatMessage.getUser().getLastName();
                    }
                }
                holder.binding.tvUserName.setText(String.format("%s", name));
                Glide.with(context).load(FILE_URL + chatMessage.getUser().getPicture()).placeholder(ContextCompat.getDrawable(context, R.drawable.ic_user_circle)).into(holder.binding.imageViewProfile);
                holder.binding.linearLayoutUserInfo.setOnClickListener(view -> iChatUtils.onMessageItemUserInfoClick(chatMessage));
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                break;
            }
            //File
            case 31111: {
                ChatHolder_File_ReplyFalse_Me_Private holder = (ChatHolder_File_ReplyFalse_Me_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage()));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                if (chatMessage.getProgress() != null) {
                    if (chatMessage.getProgress() == -1 || chatMessage.getProgress() == 100) {
                        holder.binding.tvProgress.setVisibility(View.GONE);
                    } else {
                        holder.binding.tvProgress.setVisibility(View.VISIBLE);
                        holder.binding.tvProgress.setText(String.format("در حال دانلود... %s%s کامل شده", chatMessage.getProgress(), "%"));
                    }
                } else {
                    holder.binding.tvProgress.setVisibility(View.GONE);
                }

                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }
                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }

                if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.GONE);
                } else {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.VISIBLE);
                }
                holder.binding.coordinatorDownloadFile.setOnClickListener(v -> {
                    if (checkReadExternalPermission(activity)) {
                        if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                            FileDownloaderNew.openFile(context, chatMessage.getMessage());
                        } else {
                            //Download file
                            downloadFile(chatMessage);
                        }
                    }
                });

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 31131: {
                ChatHolder_File_ReplyFalse_Me_Group holder = (ChatHolder_File_ReplyFalse_Me_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage().substring(chatMessage.getMessage().indexOf("_nznv_") + 6)));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });

                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }
                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }

                if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.GONE);
                } else {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.VISIBLE);
                }
                holder.binding.coordinatorDownloadFile.setOnClickListener(v -> {
                    if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                        FileDownloaderNew.openFile(context, chatMessage.getMessage());
                    } else {
                        downloadFile(chatMessage);
                    }
                });
                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 31211: {
                ChatHolder_File_ReplyFalse_Other_Private holder = (ChatHolder_File_ReplyFalse_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage().substring(chatMessage.getMessage().indexOf("_nznv_") + 6)));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, holder.binding.tvMessage)
                        .setOnLinkClickListener((textView, url) -> {
                            iChatUtils.onMessageTextLinkClick(url);
                            return true;
                        });
                if (chatMessage.getProgress() != null) {
                    if (chatMessage.getProgress() == -1 || chatMessage.getProgress() == 100) {
                        holder.binding.tvProgress.setVisibility(View.GONE);
                    } else {
                        holder.binding.tvProgress.setVisibility(View.VISIBLE);
                        holder.binding.tvProgress.setText(String.format("در حال دانلود... %s%s کامل شده", chatMessage.getProgress(), "%"));
                    }
                } else {
                    holder.binding.tvProgress.setVisibility(View.GONE);
                }

                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }

                if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.GONE);
                } else {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.VISIBLE);
                }
                holder.binding.coordinatorDownloadFile.setOnClickListener(v -> {

                    if (checkReadExternalPermission(activity)) {
                        if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                            FileDownloaderNew.openFile(context, chatMessage.getMessage());
                        } else {
                            downloadFile(chatMessage);
                        }
                    }
                });
                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            //channel file
            case 31221: {
                ChatHolder_File_ReplyFalse_Other_Private holder = (ChatHolder_File_ReplyFalse_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage().substring(chatMessage.getMessage().indexOf("_nznv_") + 6)));
                if (chatMessage.getProgress() != null) {
                    if (chatMessage.getProgress() == -1 || chatMessage.getProgress() == 100) {
                        holder.binding.tvProgress.setVisibility(View.GONE);
                    } else {
                        holder.binding.tvProgress.setVisibility(View.VISIBLE);
                        holder.binding.tvProgress.setText(String.format("در حال دانلود... %s%s کامل شده", chatMessage.getProgress(), "%"));
                    }
                } else {
                    holder.binding.tvProgress.setVisibility(View.GONE);
                }

                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }
                if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.GONE);
                } else {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.VISIBLE);
                }
                holder.binding.coordinatorDownloadFile.setOnClickListener(v -> {
                    if (checkReadExternalPermission(activity)) {
                        if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                            FileDownloaderNew.openFile(context, chatMessage.getMessage());
                        } else {
                            downloadFile(chatMessage);
                        }
                    }
                });
                holder.itemView.setOnClickListener(view -> {
                    if (isAdmin) {
                        showPopupMenu(holder.binding.tvTime, chatMessage, true, false, true, true);
                    } else {
                        if (checkReadExternalPermission(activity)) {
                            if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                                FileDownloaderNew.openFile(context, chatMessage.getMessage());
                            } else {
                                downloadFile(chatMessage);
                            }
                        }
                    }
                });
                break;
            }
            case 31231: {
                ChatHolder_File_ReplyFalse_Other_Group holder = (ChatHolder_File_ReplyFalse_Other_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage().substring(chatMessage.getMessage().indexOf("_nznv_") + 6)));
                if (chatMessage.getProgress() != null) {
                    if (chatMessage.getProgress() == -1 || chatMessage.getProgress() == 100) {
                        holder.binding.tvProgress.setVisibility(View.GONE);
                    } else {
                        holder.binding.tvProgress.setVisibility(View.VISIBLE);
                        holder.binding.tvProgress.setText(String.format("در حال دانلود... %s%s کامل شده", chatMessage.getProgress(), "%"));
                    }
                } else {
                    holder.binding.tvProgress.setVisibility(View.GONE);
                }

                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }

                String name = "";
                if (isWorkWithFullname) {
                    name = chatMessage.getUser().getFullname();
                } else {
                    if (chatMessage.getUser().getFirstName() != null) {
                        name = chatMessage.getUser().getFirstName();
                    }
                    if (chatMessage.getUser().getLastName() != null) {
                        name += " " + chatMessage.getUser().getLastName();
                    }
                }
                holder.binding.tvUserName.setText(String.format("%s", name));

                Glide.with(context).load(FILE_URL + chatMessage.getUser().getPicture()).placeholder(ContextCompat.getDrawable(context, R.drawable.ic_user_circle)).into(holder.binding.imageViewProfile);

                if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.GONE);
                } else {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.VISIBLE);
                }
                holder.binding.coordinatorDownloadFile.setOnClickListener(v -> {
                    if (checkReadExternalPermission(activity)) {
                        if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                            FileDownloaderNew.openFile(context, chatMessage.getMessage());
                        } else {
                            downloadFile(chatMessage);
                        }
                    }
                });
                holder.binding.linearLayoutUserInfo.setOnClickListener(view -> iChatUtils.onMessageItemUserInfoClick(chatMessage));
                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 32111: {
                ChatHolder_File_ReplyTrue_Me_Private holder = (ChatHolder_File_ReplyTrue_Me_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage().substring(chatMessage.getMessage().indexOf("_nznv_") + 6)));
                if (chatMessage.getProgress() != null) {
                    if (chatMessage.getProgress() == -1 || chatMessage.getProgress() == 100) {
                        holder.binding.tvProgress.setVisibility(View.GONE);
                    } else {
                        holder.binding.tvProgress.setVisibility(View.VISIBLE);
                        holder.binding.tvProgress.setText(String.format("در حال دانلود... %s%s کامل شده", chatMessage.getProgress(), "%"));
                    }
                } else {
                    holder.binding.tvProgress.setVisibility(View.GONE);
                }

                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }
                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }
                holder.itemView.setOnClickListener(view -> {

                    PopupMenu popup = new PopupMenu(activity, (holder.binding.tvTime));
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup_menu_chat_click_message_me, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(item -> {
                        int itemId = item.getItemId();
                        if (itemId == R.id.reply) {
                            iChatUtils.onReply(chatMessage);
                        } else if (itemId == R.id.delete) {
                            iChatUtils.onDelete(chatMessage);
                        }
                        return false;
                    });
                });

                if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.GONE);
                } else {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.VISIBLE);
                }
                holder.binding.coordinatorDownloadFile.setOnClickListener(v -> {
                    if (checkReadExternalPermission(activity)) {
                        if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                            FileDownloaderNew.openFile(context, chatMessage.getMessage());
                        } else {
                            downloadFile(chatMessage);
                        }
                    }
                });
                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 32131: {
                ChatHolder_File_ReplyTrue_Me_Group holder = (ChatHolder_File_ReplyTrue_Me_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage().substring(chatMessage.getMessage().indexOf("_nznv_") + 6)));
                if (chatMessage.getProgress() != null) {
                    if (chatMessage.getProgress() == -1 || chatMessage.getProgress() == 100) {
                        holder.binding.tvProgress.setVisibility(View.GONE);
                    } else {
                        holder.binding.tvProgress.setVisibility(View.VISIBLE);
                        holder.binding.tvProgress.setText(String.format("در حال دانلود... %s%s کامل شده", chatMessage.getProgress(), "%"));
                    }
                } else {
                    holder.binding.tvProgress.setVisibility(View.GONE);
                }

                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }
                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }
                holder.itemView.setOnClickListener(view -> {

                    PopupMenu popup = new PopupMenu(activity, (holder.binding.tvTime));
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup_menu_chat_click_message_me, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(item -> {
                        int itemId = item.getItemId();
                        if (itemId == R.id.reply) {
                            iChatUtils.onReply(chatMessage);
                        } else if (itemId == R.id.delete) {
                            iChatUtils.onDelete(chatMessage);
                        }
                        return false;
                    });
                });

                if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.GONE);
                } else {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.VISIBLE);
                }
                holder.binding.coordinatorDownloadFile.setOnClickListener(v -> {
                    if (checkReadExternalPermission(activity)) {
                        if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                            FileDownloaderNew.openFile(context, chatMessage.getMessage());
                        } else {
                            downloadFile(chatMessage);
                        }
                    }
                });

                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));
                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }
            case 32211: {
                ChatHolder_File_ReplyTrue_Other_Private holder = (ChatHolder_File_ReplyTrue_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage().substring(chatMessage.getMessage().indexOf("_nznv_") + 6)));
                if (chatMessage.getProgress() != null) {
                    if (chatMessage.getProgress() == -1 || chatMessage.getProgress() == 100) {
                        holder.binding.tvProgress.setVisibility(View.GONE);
                    } else {
                        holder.binding.tvProgress.setVisibility(View.VISIBLE);
                        holder.binding.tvProgress.setText(String.format("در حال دانلود... %s%s کامل شده", chatMessage.getProgress(), "%"));
                    }
                } else {
                    holder.binding.tvProgress.setVisibility(View.GONE);
                }

                if (chatMessage.getReply().getMessageType().equals("text")) {
                    holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                    holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                } else if (chatMessage.getReply().getMessageType().equals("picture")) {
                    holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                    holder.binding.tvReplyMessage.setVisibility(View.GONE);
                    Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                } else {
                    holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                    holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }

                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));

                if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.GONE);
                } else {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.VISIBLE);
                }
                holder.binding.coordinatorDownloadFile.setOnClickListener(v -> {
                    if (checkReadExternalPermission(activity)) {
                        if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                            FileDownloaderNew.openFile(context, chatMessage.getMessage());
                        } else {
                            downloadFile(chatMessage);
                        }
                    }
                });

                holder.itemView.setOnClickListener(view -> {
                    PopupMenu popup = new PopupMenu(activity, (holder.binding.tvTime));
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup_menu_chat_click_message_other, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.reply) {
                            iChatUtils.onReply(chatMessage);
                        }
                        return false;
                    });
                });
                break;
            }
            case 32231: {
                ChatHolder_File_ReplyTrue_Other_Group holder = (ChatHolder_File_ReplyTrue_Other_Group) viewHolder;
                if (typeface != null) {
                    holder.binding.tvMessage.setTypeface(typeface);
                    holder.binding.tvTime.setTypeface(typeface);
                    holder.binding.tvReplyMessage.setTypeface(typeface);
                }
                holder.binding.tvMessage.setText(String.format("%s", chatMessage.getMessage().substring(chatMessage.getMessage().indexOf("_nznv_") + 6)));
                if (chatMessage.getProgress() != null) {
                    if (chatMessage.getProgress() == -1 || chatMessage.getProgress() == 100) {
                        holder.binding.tvProgress.setVisibility(View.GONE);
                    } else {
                        holder.binding.tvProgress.setVisibility(View.VISIBLE);
                        holder.binding.tvProgress.setText(String.format("در حال دانلود... %s%s کامل شده", chatMessage.getProgress(), "%"));
                    }
                } else {
                    holder.binding.tvProgress.setVisibility(View.GONE);
                }
                switch (chatMessage.getReply().getMessageType()) {
                    case "text":
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        break;
                    case "picture":
                        holder.binding.cardViewReplyPicture.setVisibility(View.VISIBLE);
                        holder.binding.tvReplyMessage.setVisibility(View.GONE);
                        Glide.with(context).load(FILE_URL + chatMessage.getReply().getMessage()).into(holder.binding.imageViewReplyMessage);
                        break;
                    case "file":
                        holder.binding.cardViewReplyPicture.setVisibility(View.GONE);
                        holder.binding.tvReplyMessage.setVisibility(View.VISIBLE);
                        break;
                }
                if (chatMessage.getReply().getMessageType().equals("file")) {
                    if (chatMessage.getReply().getMessage().contains("_nznv_")) {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage().substring(chatMessage.getReply().getMessage().indexOf("_nznv_") + 6)));
                    } else {
                        holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                    }
                } else {
                    holder.binding.tvReplyMessage.setText(String.format("%s", chatMessage.getReply().getMessage()));
                }
                if (chatMessage.getUpdatedAt() != null) {
                    String normalizedDate = chatMessage.getUpdatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                } else {
                    String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                    DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                    holder.binding.tvTime.setText(getTime(dateObject));
                }

                holder.binding.linearLayoutReply.setOnClickListener(view -> getMessagePosition(chatMessage.getReplyMessageId(), SearchType.REPLY));

                if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.GONE);
                } else {
                    holder.binding.coordinatorDownloadedFile.setVisibility(View.VISIBLE);
                }
                holder.binding.coordinatorDownloadFile.setOnClickListener(v -> {
                    if (checkReadExternalPermission(activity)) {
                        if (FileDownloaderNew.isFileExists(context, chatMessage.getMessage())) {
                            FileDownloaderNew.openFile(context, chatMessage.getMessage());
                        } else {
                            downloadFile(chatMessage);
                        }
                    }
                });
                holder.binding.linearLayoutUserInfo.setOnClickListener(view -> iChatUtils.onMessageItemUserInfoClick(chatMessage));
                String name = "";
                if (isWorkWithFullname) {
                    name = chatMessage.getUser().getFullname();
                } else {
                    if (chatMessage.getUser().getFirstName() != null) {
                        name = chatMessage.getUser().getFirstName();
                    }
                    if (chatMessage.getUser().getLastName() != null) {
                        name += " " + chatMessage.getUser().getLastName();
                    }
                }
                holder.binding.tvUserName.setText(String.format("%s", name));
                Glide.with(context).load(FILE_URL + chatMessage.getUser().getPicture()).placeholder(ContextCompat.getDrawable(context, R.drawable.ic_user_circle)).into(holder.binding.imageViewProfile);

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                holder.binding.tvMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                holder.binding.linearChatMessage.setOnClickListener(view -> holder.itemView.callOnClick());
                break;
            }

            case 41111: {
                final ChatHolder_Voice_ReplyFalse_Me_Private holder = (ChatHolder_Voice_ReplyFalse_Me_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));
                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }
                holder.binding.coordinatorPlayVoice.setOnClickListener(view -> iChatUtils.onPlayVoice(chatMessage));
                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                break;
            }
            case 41211: {
                final ChatHolder_Voice_ReplyFalse_Other_Private holder = (ChatHolder_Voice_ReplyFalse_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));
                holder.binding.coordinatorPlayVoice.setOnClickListener(view -> iChatUtils.onPlayVoice(chatMessage));
                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                break;
            }

            case 51111: {
                final ChatHolder_SecurePicture_ReplyFalse_Me_Private holder = (ChatHolder_SecurePicture_ReplyFalse_Me_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));

                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }
                holder.binding.blurView.setupWith(holder.binding.getRoot(), new RenderScriptBlur(context))
                        .setBlurRadius(15f);
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> {
//                    iChatUtils.onDelete(chatMessage);//it's me, no need to delete my own message
                    new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show();
                });

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                break;
            }
            case 51211: {
                final ChatHolder_SecurePicture_ReplyFalse_Other_Private holder = (ChatHolder_SecurePicture_ReplyFalse_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));
                holder.binding.blurView.setupWith(holder.binding.getRoot(), new RenderScriptBlur(context))
                        .setBlurRadius(15f);


                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> {
                    iChatUtils.onDelete(chatMessage);
                    new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show();
                });
                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                break;
            }

            case 61111: {
                final ChatHolder_PurchasableSecurePicture_ReplyFalse_Me_Private holder = (ChatHolder_PurchasableSecurePicture_ReplyFalse_Me_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));

                if (chatMessage.getSeenCount() != 0) {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_double_check));
                } else {
                    holder.binding.imageViewSeen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat_single_check));
                }
                holder.binding.blurView.setupWith(holder.binding.getRoot(), new RenderScriptBlur(context))
                        .setBlurRadius(15f);
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);

//                new CountDownTimer(2000, 200) {
//
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        if (holder.binding.blurView.getVisibility() == View.VISIBLE) {
//                            holder.binding.blurView.setupWith(holder.binding.getRoot(), new RenderScriptBlur(context))
//                                    .setBlurRadius(5f);
//                        } else {
//                            holder.binding.blurView.setVisibility(View.VISIBLE);
//                            holder.binding.blurView.setupWith(holder.binding.getRoot(), new RenderScriptBlur(context))
//                                    .setBlurRadius(15f);
//                        }
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        holder.binding.blurView.setVisibility(View.VISIBLE);
//                    }
//                }.start();

                holder.binding.imageView.setOnClickListener(view -> new DialogShowPicture(activity, FILE_URL, chatMessage.getMessage()).show());

                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, true, false, false, false));
                break;
            }
            case 61211: {
                final ChatHolder_PurchasableSecurePicture_ReplyFalse_Other_Private holder = (ChatHolder_PurchasableSecurePicture_ReplyFalse_Other_Private) viewHolder;
                if (typeface != null) {
                    holder.binding.tvTime.setTypeface(typeface);
                }
                String normalizedDate = chatMessage.getCreatedAt().replace(".000Z", "").replace("T", " ");
                DateUtils.DateObject dateObject = dateHelper.getParsedDate(normalizedDate);
                holder.binding.tvTime.setText(getTime(dateObject));
                holder.binding.blurView.setupWith(holder.binding.getRoot(), new RenderScriptBlur(context))
                        .setBlurRadius(15f);
                Glide.with(context).load(FILE_URL + chatMessage.getMessage()).into(holder.binding.imageView);
                holder.binding.imageView.setOnClickListener(view -> iChatUtils.onShowPurchasableSecurePicture(chatMessage));
                holder.itemView.setOnClickListener(view -> showPopupMenu(holder.binding.tvTime, chatMessage, false, false, false, false));
                break;
            }
        }
    }

    public int getItemCount() {
        return this.chatMessages.size();
    }

    public void addItem(Message message) {
        this.chatMessages.add(message);
        notifyItemInserted(chatMessages.size() - 1);
        if (!isUserReadPreviousMessage) {
            this.recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }

    public void deleteItem(Integer messageId) {
        int i = 0;
        for (Message message : chatMessages) {
            if (message.getId().equals(messageId)) {
                chatMessages.remove(i);
                notifyItemRemoved(i);
                break;
            }
            i++;
        }
    }

    public void updateItem(Message message) {
        int i = 0;
        for (Message item : chatMessages) {
            if (item.getId().equals(message.getId())) {
                item.setMessage(message.getMessage());
                notifyItemChanged(i);
                break;
            }
            i++;
        }
    }

    public void updateFileItemProgress(int id, int progress) {
        int i = 0;
        for (Message item : chatMessages) {
            if (item.getId().equals(id)) {
                item.setProgress(progress);
                notifyItemChanged(i);
                break;
            }
            i++;
        }
    }


    public void seenItem(Integer messageId) {
        int i = 0;
        for (Message message : chatMessages) {
            if (message.getId().equals(messageId)) {
                message.setSeenCount(message.getSeenCount() + 1);
                notifyItemChanged(i);
                break;
            }
            i++;
        }
    }

    public String getTime(DateUtils.DateObject dateObject) {
        if (TildaChatApp._isTime48) {
            return DateUtils.getTime48WithZero(dateObject.hour, dateObject.minute);
        } else {
            return DateUtils.getTimeWithZero(dateObject.hour, dateObject.minute);
        }
    }

    public void addItems(int page, List<Message> input) {
        for (Message chatMessage : input) {
            this.chatMessages.add(0, chatMessage);
            notifyItemInserted(0);
        }
        if (page == 1) {
            this.recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }

    public void setRoomType(ChatroomType roomType) {
        this.roomType = roomType;
    }

    public void setRoomAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setLoaded() {
        this.loading = false;
    }

    //Search
    public void getMessagePosition(int messageId, SearchType searchType) {
        for (int i = 0; i < chatMessages.size(); i++) {
            if (chatMessages.get(i).getId() == messageId) {
                iChatUtils.onLoadMoreForSearchFinish();
                if (searchType == SearchType.REPLY) {
                    showReplySearch(i);
                }
                return;
            }
        }
        iChatUtils.onLoadMoreForSearch(messageId, searchType);
    }

    private void showReplySearch(int position) {
        if (position >= 0) {
            recyclerView.scrollToPosition(position);
//            chatMessages.get(position).setReplyWave(true);
            notifyItemChanged(position);
        }
    }

    protected boolean checkReadExternalPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            } else {
                return true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            } else {
                return true;
            }
        }
    }

    public void clearAll() {
        this.chatMessages.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onFileDownloaded(String path) {

    }

    @Override
    public void onFileDownloaded(Long downloadId) {

    }

    private void downloadFile(Message chatMessage) {
        if (isDownloadingFile) {

        } else {
            isDownloadingFile = true;
            android.os.Message message = android.os.Message.obtain();
            message.what = UPDATE_DOWNLOAD_PROGRESS;
            message.arg1 = 0;
            message.arg2 = chatMessage.getId();
            mainHandler.sendMessage(message);
            FileDownloaderNew fileDownloaderNew = new FileDownloaderNew(context, TildaChatApp._downloadFolder);
            fileDownloaderNew.setOnFileDownloadListener(new FileDownloaderNew.OnFileDownloadListener() {
                @Override
                public void onFileDownloaded(String path) {

                }

                @Override
                public void onFileDownloaded(Long downloadId) {

                    // Run a task in a background thread to check download progress
                    if (executor == null || executor.isShutdown()) {
                        executor = Executors.newFixedThreadPool(1);
                        executor.execute(() -> {
                            int progress = 0;
                            boolean isDownloadFinished = false;
                            while (!isDownloadFinished) {
                                try {
                                    Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
                                    if (cursor != null && cursor.moveToFirst()) {
                                        @SuppressLint("Range") int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                        switch (downloadStatus) {
                                            case DownloadManager.STATUS_RUNNING:
                                                @SuppressLint("Range") long totalBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                                if (totalBytes > 0) {
                                                    @SuppressLint("Range") long downloadedBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                                    progress = (int) (downloadedBytes * 100 / totalBytes);
                                                }

                                                break;
                                            case DownloadManager.STATUS_SUCCESSFUL:
                                                progress = 100;
                                                isDownloadFinished = true;
                                                isDownloadingFile = false;
                                                break;
                                            case DownloadManager.STATUS_PAUSED:
                                            case DownloadManager.STATUS_PENDING:
                                                break;
                                            case DownloadManager.STATUS_FAILED:
                                                progress = 0;
                                                isDownloadFinished = true;
                                                isDownloadingFile = false;
                                                break;
                                        }
                                        cursor.close();
                                        android.os.Message message = android.os.Message.obtain();
                                        message.what = UPDATE_DOWNLOAD_PROGRESS;
                                        message.arg1 = progress;
                                        message.arg2 = chatMessage.getId();
                                        mainHandler.sendMessage(message);
                                    } else {
                                        isDownloadingFile = false;
                                        isDownloadFinished = true;
                                        progress = 0;
                                        android.os.Message message = android.os.Message.obtain();
                                        message.what = UPDATE_DOWNLOAD_PROGRESS;
                                        message.arg1 = progress;
                                        message.arg2 = chatMessage.getId();
                                        mainHandler.sendMessage(message);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
            fileDownloaderNew.execute(FILE_URL + chatMessage.getMessage());
        }
    }

    // Indicate that we would like to update download progress
    private static final int UPDATE_DOWNLOAD_PROGRESS = 1;
    // Use a background thread to check the progress of downloading
    private ExecutorService executor;
    // Use a handler to update progress bar on the main thread
    private final Handler mainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull android.os.Message msg) {
            if (msg.what == UPDATE_DOWNLOAD_PROGRESS) {
                int downloadProgress = msg.arg1;
                if (downloadProgress % 4 == 0) {
                    updateFileItemProgress(msg.arg2, downloadProgress);
                }
                if (downloadProgress == 100 || downloadProgress == -1) {
                    updateFileItemProgress(msg.arg2, 100);
                    executor.shutdown();
                    mainHandler.removeCallbacksAndMessages(null);
                }
                // Update your progress bar here.

//                progressBar.setProgress(downloadProgress);
            }
            return true;
        }
    });
}

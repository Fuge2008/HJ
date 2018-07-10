package io.rong.imkit;//package io.rong.imkit;

import io.rong.common.RLog;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

import static android.content.ContentValues.TAG;

/**
 * Created by fzwoo on 2017/5/21.
 */

public class EventMore {
    public  EventMore() {
    }
    public static class MessageLeftEvent {
        public int left;

        public MessageLeftEvent(int left) {
            this.left = left;
        }
    }

    public static class OnReceiveMessageEvent {
        Message message;
        int left;

        public OnReceiveMessageEvent(Message message, int left) {
            this.message = message;
            this.left = left;
        }

        public Message getMessage() {
            return this.message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public int getLeft() {
            return this.left;
        }

        public void setLeft(int left) {
            this.left = left;
        }
    }
    /** @deprecated */
    @Deprecated
    public static void setOnReceiveUnreadCountChangedListener(final RongIM.OnReceiveUnreadCountChangedListener listener, Conversation.ConversationType... conversationTypes) {
        if(listener != null && conversationTypes != null && conversationTypes.length != 0) {
            UnReadMessageManager.getInstance().addObserver(conversationTypes, new IUnReadMessageObserver() {
                public void onCountChanged(int count) {
                    listener.onMessageIncreased(count);
                }
            });
        } else {
            RLog.w(TAG, "setOnReceiveUnreadCountChangedListener Illegal argument");
        }
    }

    public static  void addUnReadMessageCountChangedObserver(IUnReadMessageObserver observer, Conversation.ConversationType... conversationTypes) {
        if(observer != null && conversationTypes != null && conversationTypes.length != 0) {
            UnReadMessageManager.getInstance().addObserver(conversationTypes, observer);
        } else {
            RLog.w(TAG, "addOnReceiveUnreadCountChangedListener Illegal argument");
        }
    }

    public static  void removeUnReadMessageCountChangedObserver(IUnReadMessageObserver observer) {
        if(observer == null) {
            RLog.w(TAG, "removeOnReceiveUnreadCountChangedListener Illegal argument");
        } else {
            UnReadMessageManager.getInstance().removeObserver(observer);
        }
    }


}

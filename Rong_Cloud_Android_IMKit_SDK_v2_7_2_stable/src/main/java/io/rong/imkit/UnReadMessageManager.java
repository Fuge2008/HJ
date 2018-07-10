package io.rong.imkit;//package io.rong.imkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.EventMore.MessageLeftEvent;
import io.rong.imkit.model.Event.ConnectEvent;
import io.rong.imkit.model.Event.ConversationRemoveEvent;
import io.rong.imkit.model.Event.ConversationUnreadEvent;
import io.rong.imkit.model.Event.OnReceiveMessageEvent;
import io.rong.imkit.model.Event.SyncReadStatusEvent;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;

public class UnReadMessageManager {
    private static final String TAG = "UnReadMessageManager";
    private List<UnReadMessageManager.MultiConversationUnreadMsgInfo> mMultiConversationUnreadInfos;
    private int left;

    private UnReadMessageManager(Object o) {
        this.mMultiConversationUnreadInfos = new ArrayList();
        EventBus.getDefault().register(this);
    }

    public static UnReadMessageManager getInstance() {
        return UnReadMessageManager.SingletonHolder.sInstance;
    }

    public void onEventMainThread(OnReceiveMessageEvent event) {
        RLog.d("UnReadMessageManager", "OnReceiveMessageEvent " + event.getLeft());
        this.left = event.getLeft();
        this.syncUnreadCount(event.getMessage(), event.getLeft());
    }

    public void onEventMainThread(MessageLeftEvent event) {
        RLog.d("UnReadMessageManager", "MessageLeftEvent " + event.left);
        this.left = event.left;
        this.syncUnreadCount((Message)null, event.left);
    }

    public void onEventMainThread(ConnectEvent event) {
        this.syncUnreadCount((Message)null, 0);
    }

    private void syncUnreadCount(Message message, int left) {
        Iterator i$ = this.mMultiConversationUnreadInfos.iterator();

        while(true) {
            while(i$.hasNext()) {
                final UnReadMessageManager.MultiConversationUnreadMsgInfo msgInfo = (UnReadMessageManager.MultiConversationUnreadMsgInfo)i$.next();
                if(left == 0) {
                    RongIMClient.getInstance().getUnreadCount(msgInfo.conversationTypes, new ResultCallback() {
                        public void onSuccess(Integer integer) {

                        }

                        @Override
                        public void onSuccess(Object o) {
                            RLog.d("UnReadMessageManager", "get result: " + o);

                            msgInfo.count = (Integer)o;
                            msgInfo.observer.onCountChanged((Integer)o);

                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                } else {
                    boolean needUpdate = false;
                    if(message == null) {
                        needUpdate = true;
                    } else {
                        ConversationType[] arr$ = msgInfo.conversationTypes;
                        int len$ = arr$.length;

                        for(int i$1 = 0; i$1 < len$; ++i$1) {
                            ConversationType ct = arr$[i$1];
                            if(ct.equals(message.getConversationType())) {
                                needUpdate = true;
                                break;
                            }
                        }
                    }

                    if(needUpdate) {
                        ++msgInfo.count;
                        msgInfo.observer.onCountChanged(msgInfo.count);
                    }
                }
            }

            return;
        }
    }

    public void onEventMainThread(ConversationRemoveEvent removeEvent) {
        ConversationType conversationType = removeEvent.getType();
        Iterator i$ = this.mMultiConversationUnreadInfos.iterator();

        while(true) {
            while(i$.hasNext()) {
                final UnReadMessageManager.MultiConversationUnreadMsgInfo msgInfo = (UnReadMessageManager.MultiConversationUnreadMsgInfo)i$.next();
                ConversationType[] arr$ = msgInfo.conversationTypes;
                int len$ = arr$.length;

                for(int i$1 = 0; i$1 < len$; ++i$1) {
                    ConversationType ct = arr$[i$1];
                    if(ct.equals(conversationType)) {
                        RongIMClient.getInstance().getUnreadCount(msgInfo.conversationTypes, new ResultCallback() {
                            public void onSuccess(Integer integer) {
                                msgInfo.count = integer.intValue();
                                msgInfo.observer.onCountChanged(integer.intValue());
                            }

                            @Override
                            public void onSuccess(Object o) {
                                msgInfo.count = (Integer) o;
                                msgInfo.observer.onCountChanged((Integer) o);
                            }

                            public void onError(ErrorCode e) {
                            }
                        });
                        break;
                    }
                }
            }

            return;
        }
    }

    public void onEventMainThread(ConversationUnreadEvent unreadEvent) {
        ConversationType conversationType = unreadEvent.getType();
        Iterator i$ = this.mMultiConversationUnreadInfos.iterator();

        while(true) {
            while(i$.hasNext()) {
                final UnReadMessageManager.MultiConversationUnreadMsgInfo msgInfo = (UnReadMessageManager.MultiConversationUnreadMsgInfo)i$.next();
                ConversationType[] arr$ = msgInfo.conversationTypes;
                int len$ = arr$.length;

                for(int i$1 = 0; i$1 < len$; ++i$1) {
                    ConversationType ct = arr$[i$1];
                    if(ct.equals(conversationType)) {
                        RongIMClient.getInstance().getUnreadCount(msgInfo.conversationTypes, new ResultCallback() {
                            public void onSuccess(Integer integer) {
                                msgInfo.count = integer.intValue();
                                msgInfo.observer.onCountChanged(integer.intValue());
                            }

                            @Override
                            public void onSuccess(Object o) {
                                msgInfo.count = (Integer) o;
                                msgInfo.observer.onCountChanged((Integer) o);

                            }

                            public void onError(ErrorCode e) {
                            }
                        });
                        break;
                    }
                }
            }

            return;
        }
    }

    public void addObserver(ConversationType[] conversationTypes, IUnReadMessageObserver observer) {
        final UnReadMessageManager.MultiConversationUnreadMsgInfo msgInfo = new UnReadMessageManager.MultiConversationUnreadMsgInfo(null);
        msgInfo.conversationTypes = conversationTypes;
        msgInfo.observer = observer;
        this.mMultiConversationUnreadInfos.add(msgInfo);
        RongIMClient.getInstance().getUnreadCount(conversationTypes, new ResultCallback() {
            public void onSuccess(Integer integer) {
                msgInfo.count = integer.intValue();
                msgInfo.observer.onCountChanged(integer.intValue());
            }

            @Override
            public void onSuccess(Object o) {
                msgInfo.count = (Integer) o;
                msgInfo.observer.onCountChanged((Integer) o);
            }

            public void onError(ErrorCode e) {
            }
        });
    }

    public void removeObserver(IUnReadMessageObserver observer) {
        UnReadMessageManager.MultiConversationUnreadMsgInfo result = null;
        Iterator i$ = this.mMultiConversationUnreadInfos.iterator();

        while(i$.hasNext()) {
            UnReadMessageManager.MultiConversationUnreadMsgInfo msgInfo = (UnReadMessageManager.MultiConversationUnreadMsgInfo)i$.next();
            if(msgInfo.observer == observer) {
                result = msgInfo;
                break;
            }
        }

        if(result != null) {
            this.mMultiConversationUnreadInfos.remove(result);
        }

    }

    public void clearObserver() {
        this.mMultiConversationUnreadInfos.clear();
    }

    public void onMessageReceivedStatusChanged() {
        this.syncUnreadCount((Message)null, 0);
    }

    public void onEventMainThread(SyncReadStatusEvent event) {
        RLog.d("UnReadMessageManager", "SyncReadStatusEvent " + this.left);
        if(this.left == 0) {
            ConversationType conversationType = event.getConversationType();
            Iterator i$ = this.mMultiConversationUnreadInfos.iterator();

            while(true) {
                while(i$.hasNext()) {
                    final UnReadMessageManager.MultiConversationUnreadMsgInfo msgInfo = (UnReadMessageManager.MultiConversationUnreadMsgInfo)i$.next();
                    ConversationType[] arr$ = msgInfo.conversationTypes;
                    int len$ = arr$.length;

                    for(int i$1 = 0; i$1 < len$; ++i$1) {
                        ConversationType ct = arr$[i$1];
                        if(ct.equals(conversationType)) {
                            RongIMClient.getInstance().getUnreadCount(msgInfo.conversationTypes, new ResultCallback() {
                                public void onSuccess(Integer integer) {
                                    msgInfo.count = integer.intValue();
                                    msgInfo.observer.onCountChanged(integer.intValue());
                                }

                                @Override
                                public void onSuccess(Object o) {
                                    msgInfo.count = (Integer) o;
                                    msgInfo.observer.onCountChanged((Integer) o);
                                }

                                public void onError(ErrorCode e) {
                                }
                            });
                            break;
                        }
                    }
                }

                return;
            }
        }
    }

    private class MultiConversationUnreadMsgInfo {
        ConversationType[] conversationTypes;
        int count;
        IUnReadMessageObserver observer;

        private MultiConversationUnreadMsgInfo(Object o) {
        }
    }

    private static class SingletonHolder {
        static UnReadMessageManager sInstance = new UnReadMessageManager(null);

        private SingletonHolder() {
        }
    }
}
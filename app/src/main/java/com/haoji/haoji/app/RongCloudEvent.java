package com.haoji.haoji.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.util.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.AlterDialogFragment;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.TextInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.location.RealTimeLocationConstant;
import io.rong.imlib.location.message.RealTimeLocationStartMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.DiscussionNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.PublicServiceMultiRichContentMessage;
import io.rong.message.PublicServiceRichContentMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;


/**
 * 融云SDK事件监听处理。 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有： 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。 5、群组信息提供者：GetGroupInfoProvider。
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。 8、地理位置提供者：LocationProvider。
 * 9、自定义 push 通知： OnReceivePushMessageListener。
 * 10、会话列表界面操作的监听器：ConversationListBehaviorListener。
 */
public final class RongCloudEvent implements RongIMClient.OnReceiveMessageListener, RongIM.OnSendMessageListener,
		RongIM.UserInfoProvider, RongIM.GroupInfoProvider, RongIM.ConversationBehaviorListener,
		RongIMClient.ConnectionStatusListener, RongIM.LocationProvider, 
		RongIM.ConversationListBehaviorListener, Handler.Callback, RongIM.GroupUserInfoProvider {

	private static final String TAG = RongCloudEvent.class.getSimpleName();

	private static RongCloudEvent mRongCloudInstance;

	private Context mContext;
	private Handler mHandler;
	private SharedPreferencesUtil util;

	/**
	 * 初始化 RongCloud.
	 *
	 * @param context
	 *            上下文。
	 */
	public static void init(Context context) {

		if (mRongCloudInstance == null) {

			synchronized (RongCloudEvent.class) {

				if (mRongCloudInstance == null) {
					mRongCloudInstance = new RongCloudEvent(context);
				}
			}
		}
	}

	/**
	 * 构造方法。
	 *
	 * @param context
	 *            上下文。
	 */
	private RongCloudEvent(Context context) {
		mContext = context;
		initDefaultListener();
		mHandler = new Handler(this);
		util = new SharedPreferencesUtil(context);
	}

	/**
	 * 获取RongCloud 实例。
	 *
	 * @return RongCloud。
	 */
	public static RongCloudEvent getInstance() {
		return mRongCloudInstance;
	}

	/**
	 * RongIM.init(this) 后直接可注册的Listener。
	 */
	private void initDefaultListener() {

		RongIM.setUserInfoProvider(this, true);// 设置用户信息提供者。
		RongIM.setGroupInfoProvider(this, true);// 设置群组信息提供者。
		RongIM.setConversationBehaviorListener(this);// 设置会话界面操作的监听器。
		RongIM.setLocationProvider(this);// 设置地理位置提供者,不用位置的同学可以注掉此行代码
		RongIM.setConversationListBehaviorListener(this);// 会话列表界面操作的监听器
		RongIM.getInstance().setSendMessageListener(this);// 设置发出消息接收监听器.
		RongIM.setGroupUserInfoProvider(this, true);
		RongIM.setOnReceiveMessageListener(this);// 自定义 push 通知。
		// 消息体内是否有 userinfo 这个属性
		RongIM.getInstance().setMessageAttachedUserInfo(true);
	}

	/**
	 * 连接成功注册。
	 * <p/>
	 * 在RongIM-connect-onSuccess后调用。
	 */
	@SuppressWarnings("static-access")
	public void setOtherListener() {
		RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(this);// 设置消息接收监听器。
		RongIM.getInstance().getRongIMClient().setConnectionStatusListener(this);// 设置连接状态监听器。

		TextInputProvider textInputProvider = new TextInputProvider(RongContext.getInstance());
		RongIM.setPrimaryInputProvider(textInputProvider);

		// 扩展功能自定义
		InputProvider.ExtendProvider[] provider = { new ImageInputProvider(RongContext.getInstance()), // 图片
				new CameraInputProvider(RongContext.getInstance()), // 相机
				//new RealTimeLocationInputProvider(RongContext.getInstance()), // 地理位置
				//new VoIPInputProvider(RongContext.getInstance()),// 语音通话
		};

		InputProvider.ExtendProvider[] provider1 = { new ImageInputProvider(RongContext.getInstance()), // 图片
				new CameraInputProvider(RongContext.getInstance()), // 相机
				//new RealTimeLocationInputProvider(RongContext.getInstance()), // 地理位置
				//new ContactsProvider(RongContext.getInstance()),// 通讯录
		};

		RongIM.getInstance().resetInputExtensionProvider(ConversationType.PRIVATE, provider);
		RongIM.getInstance().resetInputExtensionProvider(ConversationType.DISCUSSION, provider1);
		RongIM.getInstance().resetInputExtensionProvider(ConversationType.GROUP, provider1);
		RongIM.getInstance().resetInputExtensionProvider(ConversationType.CUSTOMER_SERVICE, provider1);
		RongIM.getInstance().resetInputExtensionProvider(ConversationType.CHATROOM, provider1);
	}


	private Bitmap getAppIcon() {
		BitmapDrawable bitmapDrawable;
		Bitmap appIcon;
		bitmapDrawable = (BitmapDrawable) RongContext.getInstance().getApplicationInfo()
				.loadIcon(RongContext.getInstance().getPackageManager());
		appIcon = bitmapDrawable.getBitmap();
		return appIcon;
	}


	/**
	 * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
	 *
	 * @param message
	 *            接收到的消息的实体信息。
	 * @param left
	 *            剩余未拉取消息数目。
	 */
	@Override
	public boolean onReceived(Message message, int left) {

		Log.d(TAG, "onReceived-TextMessage:" + left);
		MessageContent messageContent = message.getContent();
		if (messageContent instanceof TextMessage) {// 文本消息
			TextMessage textMessage = (TextMessage) messageContent;
			textMessage.getExtra();
			Log.d(TAG, "onReceived-TextMessage:" + textMessage.getContent());
		} else if (messageContent instanceof ImageMessage) {// 图片消息
			ImageMessage imageMessage = (ImageMessage) messageContent;
			Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
		} else if (messageContent instanceof VoiceMessage) {// 语音消息
			VoiceMessage voiceMessage = (VoiceMessage) messageContent;
			Log.d(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
		} else if (messageContent instanceof RichContentMessage) {// 图文消息
			RichContentMessage richContentMessage = (RichContentMessage) messageContent;
			Log.d(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
		} else if (messageContent instanceof InformationNotificationMessage) {// 小灰条消息
			InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
			Log.e(TAG, "onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
		} else if (messageContent instanceof ContactNotificationMessage) {// 好友添加消息
			ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
			Log.d(TAG, "onReceived-ContactNotificationMessage:getExtra;" + contactContentMessage.getExtra());
			Log.d(TAG, "onReceived-ContactNotificationMessage:+getmessage:"
					+ contactContentMessage.getMessage().toString());

		} else if (messageContent instanceof DiscussionNotificationMessage) {// 讨论组通知消息
			DiscussionNotificationMessage discussionNotificationMessage = (DiscussionNotificationMessage) messageContent;
			Log.d(TAG,
					"onReceived-discussionNotificationMessage:getExtra;" + discussionNotificationMessage.getOperator());
			setDiscussionName(message.getTargetId());
		} else {
			Log.d(TAG, "onReceived-其他消息，自己来判断处理");
		}
		return false;

	}

	/**
	 * 讨论组名称修改后刷新本地缓存
	 *
	 * @param targetId
	 *            讨论组 id
	 */
	@SuppressWarnings("deprecation")
	private void setDiscussionName(String targetId) {

		if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
			RongIM.getInstance().getRongIMClient().getDiscussion(targetId,
					new RongIMClient.ResultCallback<Discussion>() {
						@Override
						public void onSuccess(Discussion discussion) {

							RongIM.getInstance().refreshDiscussionCache(discussion);
							Log.i(TAG, "------discussion.getName---" + discussion.getName());
						}

						@Override
						public void onError(RongIMClient.ErrorCode e) {

						}
					});
		}
	}

	/**
	 * 消息发送前监听器处理接口（是否发送成功可以从SentStatus属性获取）。
	 *
	 * @param message
	 *            发送的消息实例。
	 * @return 处理后的消息实例。
	 */
	@Override
	public Message onSend(Message message) {

		MessageContent messageContent = message.getContent();

		if (messageContent instanceof TextMessage) {// 文本消息
			TextMessage textMessage = (TextMessage) messageContent;
			Log.e("qinxiao", "--onSend:" + textMessage.getContent() + ", extra=" + message.getExtra());
		}

		return message;
	}

	/**
	 * 消息在UI展示后执行/自己的消息发出后执行,无论成功或失败。
	 *
	 * @param message
	 *            消息。
	 */
	@Override
	public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
		Log.e("qinxiao", "onSent:" + message.getObjectName() + ", extra=" + message.getExtra());

		if (message.getSentStatus() == Message.SentStatus.FAILED) {

			if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_CHATROOM) {// 不在聊天室

			} else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_DISCUSSION) {// 不在讨论组

			} else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_GROUP) {// 不在群组

			} else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.REJECTED_BY_BLACKLIST) {// 你在他的黑名单中
				// WinToast.toast(mContext, "你在对方的黑名单中");
			}
		}

		MessageContent messageContent = message.getContent();

		if (messageContent instanceof TextMessage) {// 文本消息
			TextMessage textMessage = (TextMessage) messageContent;
			Log.e(TAG, "onSent-TextMessage:" + textMessage.getContent());
		} else if (messageContent instanceof ImageMessage) {// 图片消息
			ImageMessage imageMessage = (ImageMessage) messageContent;
			Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
		} else if (messageContent instanceof VoiceMessage) {// 语音消息
			VoiceMessage voiceMessage = (VoiceMessage) messageContent;
			Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
		} else if (messageContent instanceof RichContentMessage) {// 图文消息
			RichContentMessage richContentMessage = (RichContentMessage) messageContent;
			Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
		} else {
			Log.d(TAG, "onSent-其他消息，自己来判断处理");
		}
		return false;
	}

	/**
	 * 用户信息的提供者：GetUserInfoProvider 的回调方法，获取用户信息。
	 *
	 * @param userId
	 *            用户 Id。
	 * @return 用户信息，（注：由开发者提供用户信息）。
	 */
	@Override
	public UserInfo getUserInfo(String userId) {
		/**
		 * demo 代码 开发者需替换成自己的代码。
		 */
		String name = "";
		Uri uri = null;
		NetIntent netIntent=new NetIntent();
		JSONObject json;
		try {
			String rep=netIntent.client_getUserById(userId);
			System.out.println(rep+"重复1------》");
			JSONObject jsonObject=new JSONObject(rep);
			if (jsonObject.has("user")){
				json=jsonObject.getJSONObject("user");
				name=json.getString("nickname");
				if (name.equals("")|name.equals("null")){
					name=json.getString("phone");
				}
				uri=Uri.parse(json.getString("pic"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		UserInfo userInfo = new UserInfo(userId, name, uri);
		return userInfo;
	}
	
	public void RefrshUserInfo(String userId){
		RongIM.getInstance().refreshUserInfoCache(getUserInfo(userId));
	}
	
	/**
	 * 刷新所有用户缓存
	 */
	public void RefrshUserInfoAll(){
		/*ArrayList<GlxxTUser> users=userDB.getUser();
		for (GlxxTUser glxxTUser : users) {
			RongIM.getInstance().refreshUserInfoCache(getUserInfo(glxxTUser.getUserid()));
		}*/
	}
	/**
	 * 刷新用户缓存数据。
	 *
	 *  需要更新的用户缓存数据。
	 */
	public void RefreshUserInfo(String userid,String nickname,String url){
		UserInfo info=new UserInfo(userid,nickname,Uri.parse(url));
		//UserInfo info=new UserInfo()


	RongIM.getInstance().refreshUserInfoCache(new UserInfo("userId", userid, Uri.parse(url)));
	}
	/**
	 * 群组信息的提供者：GetGroupInfoProvider 的回调方法， 获取群组信息。
	 *
	 * @param groupId 群组 Id.
	 * @return 群组信息，（注：由开发者提供群组信息）。
	 */
	@Override
	public Group getGroupInfo(String groupId) {
		// /**
		// * demo 代码 开发者需替换成自己的代码。
		// */
		// if (DemoContext.getInstance().getGroupMap() == null)
		// return null;
		//
		// return DemoContext.getInstance().getGroupMap().get(groupId);
		return null;
	}

	/**
	 * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击用户头像后执行。
	 *
	 * @param context
	 *            应用当前上下文。
	 * @param conversationType
	 *            会话类型。
	 * @param user
	 *            被点击的用户的信息。
	 * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
	 */
	@Override
	public boolean onUserPortraitClick(Context context, ConversationType conversationType, UserInfo user) {

		/**
		 * demo 代码 开发者需替换成自己的代码。
		 */
		if (user != null) {

			if (conversationType.equals(ConversationType.PUBLIC_SERVICE)
					|| conversationType.equals(ConversationType.APP_PUBLIC_SERVICE)) {
				RongIM.getInstance().startPublicServiceProfile(mContext, conversationType, user.getUserId());
			} else {

			}
		}

		return false;
	}

	@Override
	public boolean onUserPortraitLongClick(Context context, ConversationType conversationType,
			UserInfo userInfo) {
		Log.e(TAG, "----onUserPortraitLongClick");

		return true;
	}

	/**
	 * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
	 *
	 * @param context
	 *            应用当前上下文。
	 * @param message
	 *            被点击的消息的实体信息。
	 * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
	 */
	@Override
	public boolean onMessageClick(final Context context, final View view, final Message message) {
		Log.e(TAG, "----onMessageClick");

		// real-time location message begin
		if (message.getContent() instanceof RealTimeLocationStartMessage) {
			RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance()
					.getRealTimeLocationCurrentState(message.getConversationType(), message.getTargetId());

			if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE) {
				startRealTimeLocation(context, message.getConversationType(), message.getTargetId());
			} else if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING) {

				final AlterDialogFragment alterDialogFragment = AlterDialogFragment.newInstance("", "加入位置共享", "取消",
						"加入");
				alterDialogFragment.setOnAlterDialogBtnListener(new AlterDialogFragment.AlterDialogBtnListener() {

					@Override
					public void onDialogPositiveClick(AlterDialogFragment dialog) {
						RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance()
								.getRealTimeLocationCurrentState(message.getConversationType(), message.getTargetId());

						if (status == null
								|| status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE) {
							startRealTimeLocation(context, message.getConversationType(), message.getTargetId());
						} else {
							joinRealTimeLocation(context, message.getConversationType(), message.getTargetId());
						}

					}

					@Override
					public void onDialogNegativeClick(AlterDialogFragment dialog) {
						alterDialogFragment.dismiss();
					}
				});

				alterDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager());
			} else {

				if (status != null
						&& (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_OUTGOING
								|| status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_CONNECTED)) {

					// Intent intent = new Intent(((FragmentActivity) context),
					// RealTimeLocationActivity.class);
					// intent.putExtra("conversationType",
					// message.getConversationType().getValue());
					// intent.putExtra("targetId", message.getTargetId());
					// context.startActivity(intent);
				}
			}
			return true;
		}

		// real-time location message end
		/**
		 * demo 代码 开发者需替换成自己的代码。
		 */
		if (message.getContent() instanceof LocationMessage) {

		} else if (message.getContent() instanceof RichContentMessage) {
			RichContentMessage mRichContentMessage = (RichContentMessage) message.getContent();
			Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());
			Log.e(TAG, "----RichContentMessage-------");

		} else if (message.getContent() instanceof ImageMessage) {
//			ImageMessage imageMessage = (ImageMessage) message.getContent();
//			Intent intent = new Intent(context, ShowPhotoFragmentActivity.class);
//
//			intent.putExtra("photo",
//					imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri());
//			if (imageMessage.getThumUri() != null)
//				intent.putExtra("thumbnail", imageMessage.getThumUri());
//
//			context.startActivity(intent);
		} else if (message.getContent() instanceof PublicServiceMultiRichContentMessage) {
			Log.e(TAG, "----PublicServiceMultiRichContentMessage-------");

		} else if (message.getContent() instanceof PublicServiceRichContentMessage) {
			Log.e(TAG, "----PublicServiceRichContentMessage-------");

		}

		Log.d("Begavior", message.getObjectName() + ":" + message.getMessageId());

		return false;
	}

	private void startRealTimeLocation(Context context, ConversationType conversationType,
			String targetId) {
		// RongIMClient.getInstance().startRealTimeLocation(conversationType,
		// targetId);
		//
		// Intent intent = new Intent(((FragmentActivity) context),
		// RealTimeLocationActivity.class);
		// intent.putExtra("conversationType", conversationType.getValue());
		// intent.putExtra("targetId", targetId);
		// context.startActivity(intent);
	}

	private void joinRealTimeLocation(Context context, ConversationType conversationType,
			String targetId) {
		// RongIMClient.getInstance().joinRealTimeLocation(conversationType,
		// targetId);
		//
		// Intent intent = new Intent(((FragmentActivity) context),
		// RealTimeLocationActivity.class);
		// intent.putExtra("conversationType", conversationType.getValue());
		// intent.putExtra("targetId", targetId);
		// context.startActivity(intent);
	}

	/**
	 * 当点击链接消息时执行。
	 *
	 * @param context
	 *            上下文。
	 * @param link
	 *            被点击的链接。
	 * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
	 */
	@Override
	public boolean onMessageLinkClick(Context context, String link) {
		return false;
	}

	@Override
	public boolean onMessageLongClick(Context context, View view, Message message) {

		Log.e(TAG, "----onMessageLongClick");
		return false;
	}

	/**
	 * 连接状态监听器，以获取连接相关状态:ConnectionStatusListener 的回调方法，网络状态变化时执行。
	 *
	 * @param status
	 *            网络状态。
	 */
	@Override
	public void onChanged(ConnectionStatus status) {
		Log.d(TAG, "onChanged:" + status);
		if (status.getMessage().equals(ConnectionStatus.DISCONNECTED.getMessage())) {
		}
	}

	/**
	 * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
	 *
	 * @param context
	 *            上下文
	 * @param callback
	 *            回调
	 */
	@Override
	public void onStartLocation(Context context, LocationCallback callback) {
		/**
		 * demo 代码 开发者需替换成自己的代码。
		 */
		// DemoContext.getInstance().setLastLocationCallback(callback);
		//
		// Intent intent = new Intent(context, SOSOLocationActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(intent);
	}

	/**
	 * 点击会话列表 item 后执行。
	 *
	 * @param context
	 *            上下文。
	 * @param view
	 *            触发点击的 View。
	 * @param conversation
	 *            会话条目。
	 * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
	 */
	@Override
	public boolean onConversationClick(Context context, View view, UIConversation conversation) {
		MessageContent messageContent = conversation.getMessageContent();

		Log.e(TAG, "--------onConversationClick-------");
		if (messageContent instanceof TextMessage) {// 文本消息

			TextMessage textMessage = (TextMessage) messageContent;

			textMessage.getExtra();
		} else if (messageContent instanceof ContactNotificationMessage) {
			Log.e(TAG, "---onConversationClick--ContactNotificationMessage-");

			// context.startActivity(new Intent(context,
			// NewFriendListActivity.class));
			return true;
		}

		return false;
	}

	/**
	 * 长按会话列表 item 后执行。
	 *
	 * @param context
	 *            上下文。
	 * @param view
	 *            触发点击的 View。
	 * @param conversation
	 *            长按会话条目。
	 * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
	 */
	@Override
	public boolean onConversationLongClick(Context context, View view, UIConversation conversation) {
		return false;
	}


	@Override
	public boolean handleMessage(android.os.Message message) {
		return false;
	}

	@Override
	public GroupUserInfo getGroupUserInfo(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onConversationPortraitClick(Context arg0, ConversationType arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onConversationPortraitLongClick(Context arg0, ConversationType arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * 根据会话类型列表清空所有会话及会话消息，回调方式通知是否清空成功。
	 *
	 * @param callback          是否清空成功的回调。
	 * @param conversationTypes 会话类型。
	 */
	public void clearConversations(final RongIMClient.ResultCallback callback, final Conversation.ConversationType... conversationTypes){

	}

	/**
	 * 从会话列表中移除某一会话，但是不删除会话内的消息。
	 * <p/>
	 * 如果此会话中有新的消息，该会话将重新在会话列表中显示，并显示最近的历史消息。
	 *
	 * @param conversationType 会话类型。
	 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
	 * @param callback         移除会话是否成功的回调。
	 */
	public void removeConversation(final Conversation.ConversationType conversationType, final String targetId, final RongIMClient.ResultCallback<Boolean> callback){}


}

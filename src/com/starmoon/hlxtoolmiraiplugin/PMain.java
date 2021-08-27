package com.starmoon.hlxtoolmiraiplugin;

import com.starmoon.org.json.JSONArray;
import com.starmoon.org.json.JSONObject;
import com.starmoon.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import kotlin.reflect.KClass;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.MiraiConsole;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.ResourceContainer;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MessageTooLargeException;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageContent;
import net.mamoe.mirai.message.data.MessageUtils;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.RichMessage;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.MiraiLogger;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import java.util.TimerTask;
import com.starmoon.util.TimerUtils;
import java.util.Timer;
import java.util.HashMap;

// 基于 MiraiConsole 的葫芦侠3楼机器人插件
public class PMain extends JavaPlugin {
	public static final PMain INSTANCE = new PMain();
	public static Bot sBot;
	public static MiraiLogger mLogger;

	private static final long host=2868255248L; // 主人QQ
	private int count=200;
	private static List<Copy> copys=new ArrayList<>();
	private static List<Access> mCacheAccesses=new ArrayList<>();
	private static HashSet<Long> admins=new ArrayList<>();
	private static List<PostComments> sCachePostComments=new ArrayList<>();
	private static List<MessageReceipt> mReceipts = new ArrayList<>();
	private static HashSet<Long> adminGroupIDs=new ArrayList<>();
	static PrintWriter pw;
	static StringWriter sw=new StringWriter();
	private static f f;
	static {
		admins.add(host);
	}

	// 插件创建
	public PMain() {
		super(new JvmPluginDescriptionBuilder("com.starmoon.hlxtoolmiraiplugin", "1.0").author("wuyin").version("1.0").build());
		mLogger = MiraiConsole.INSTANCE.getMainLogger();

	}
	// 插件启用时执行
	public void onEnable() {
		try {
			try {
				GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, new GetBot());
				GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, new a());
				GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, new k());
				mLogger.info("插件初始化成功，数据储存目录：" + getDataFolder());
			} catch (NoSuchMethodError e) {
				Class<?> cls;
				try {
					cls = Class.forName("j$.util.function.Consumer");
				} catch (ClassNotFoundException er) {
					return;
				}
				Object l=Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, new i());
				Method m = GlobalEventChannel.INSTANCE.getClass().getMethod("subscribeAlways", Class.class, cls);
				m.invoke(GlobalEventChannel.INSTANCE, GroupMessageEvent.class, l);
				m.invoke(GlobalEventChannel.INSTANCE, FriendMessageEvent.class, l);
				mLogger.info("插件初始化成功，数据储存目录：" + getDataFolder());
			}
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException) {
				e = ((InvocationTargetException)e).getTargetException();
			}
			throw new RuntimeException("Unable to initialization group events: " + e.toString(), e);
		}
	}
	// Bot 登录初始化完成时执行
	class GetBot implements Consumer<BotOnlineEvent> {

		@Override
		public void accept(BotOnlineEvent p1) {
			if (sBot == null) {
				sBot = p1.getBot();
				restore(sBot);
			}
		}

	}
	// 接收到群消息时执行
	class a implements Consumer<GroupMessageEvent> {

		@Override
		public void accept(GroupMessageEvent p1) {
			try {
				processGroupMsg(p1);
			} catch (Throwable e) {
				if (e instanceof InvocationTargetException) {
					e = ((InvocationTargetException)e).getTargetException();
				}
				throw new RuntimeException("Unable to process group message: " + e.toString(), e);
			}
		}


	}
	// 接收到好友消息时执行
	class k implements Consumer<FriendMessageEvent> {

		@Override
		public void accept(FriendMessageEvent p1) {
			try {
				processFriendMsg(p1);
			} catch (Throwable e) {
				if (e instanceof InvocationTargetException) {
					e = ((InvocationTargetException)e).getTargetException();
				}
				throw new RuntimeException("Unable to process friend message: " + e.toString(), e);
			}
		}

	}
	// 系统找不到方法时就会以此创建接口对象
	class i implements InvocationHandler {

		@Override
		public Object invoke(Object p1, Method p2, Object[] p3) throws Throwable {
			if (p2.getName().equals("accept")) {
				if (p3 != null && p3.length == 1 && p3[0] instanceof GroupMessageEvent) {
					try {
						processGroupMsg((GroupMessageEvent)p3[0]);
						return null;
					} catch (Throwable e) {
						if (e instanceof InvocationTargetException) {
							e = ((InvocationTargetException)e).getTargetException();
						}
						throw new RuntimeException("Unable to process group message: " + e.toString(), e);
					}
				}
				if (p3 != null && p3.length == 1 && p3[0] instanceof FriendMessageEvent) {
					try {
						processFriendMsg((FriendMessageEvent)p3[0]);
						return null;
					} catch (Throwable e) {
						if (e instanceof InvocationTargetException) {
							e = ((InvocationTargetException)e).getTargetException();
						}
						throw new RuntimeException("Unable to process friend message: " + e.toString(), e);
					}
				}
			}
			if (p2.getName().equals("andThen")) {
				if (p3 != null && p3.length == 1 && Class.forName("j$.util.function.Consumer").isInstance(p3[0])) {
					return p3[0];
				}
			}
			throw new IllegalStateException();
		}

	}
	// 插件被禁用时执行
	public void onDisable() {
	}
	// 插件加载时执行
	public void onLoad(@NotNull PluginComponentStorage g) {
	}
	
	// 仅支持MiraiConsole插件导入
	public static void main(String[] args)
	{
		throw new UnsupportedOperationException();
	}

	// 异常信息处理
	public static void sendExceptionMessage(Contact con, Throwable e) {
		if (e instanceof SocketTimeoutException) {
			con.sendMessage("机器人服务器连接超时");
		} else if (e instanceof SocketException) {
			con.sendMessage("机器人服务器连接出错");
		}
		// 更多异常待补充
	}

	// 处理好友消息
	public void processFriendMsg(FriendMessageEvent event) throws Throwable {
		Friend friend=event.getFriend();
		MessageChain chain=event.getMessage();
		for (SingleMessage s:chain) {
			if (s instanceof PlainText) {
			    String msg=s.contentToString();
				if (msg.equals("撤回") && event.getSender().getId() == host){
					try {
						if (mReceipts.size() >= 1) {
							int index=mReceipts.size() - 1;
							mReceipts.get(index).recall();
							mReceipts.remove(index);
							friend.sendMessage("撤回成功");
						} else {
							friend.sendMessage("没有可以撤回的消息");
						}
					} catch (IllegalStateException e) {
						try {
							if (e.toString().contains("recalled")) {
								int index=mReceipts.size() - 1;
								mReceipts.remove(index);
								friend.sendMessage("上一条消息已经撤回");
							}
						} catch (Throwable err) {
							friend.sendMessage("撤回失败，错误：" + e.toString());
						}
						return;
					} catch (Throwable e) {
						friend.sendMessage("撤回失败，错误：" + e.toString());
					}
                    return;
				}
			}
		}
	}

	// 处理群消息
	public void processGroupMsg(GroupMessageEvent event) throws Throwable {
		Group group=event.getGroup();
		MessageChain chain=event.getMessage();
		mLogger.info("接收到消息：" + chain.contentToString());
		mLogger.info("消息元素数量：" + chain.size());
		StringBuilder sb=new StringBuilder();
		for (SingleMessage msg:chain) {
			sb.append(msg.getClass().getSimpleName());
			sb.append(", ");
		}
		mLogger.info("包含属性：" + sb.toString());
		boolean isAtSelf=false;
		for (SingleMessage msg:chain) {
			if (msg instanceof At) {
				At at=(At)msg;
				if (at.getTarget() == event.getBot().getId()) {
					isAtSelf = true;
					continue;
				}
			}
			if (msg instanceof PlainText) {
				PlainText text=(PlainText)msg;
				String content=text.contentToString();
				if (isAtSelf && !content.startsWith(" ") && content.length() <= 1) {
					return;
				}
				String msgStr=content;
				if (isAtSelf && msgStr.startsWith(" ")) {
					msgStr = msgStr.substring(1, msgStr.length());
				}
				mLogger.info("接收文字：" + msgStr);
				if (isAtSelf && msgStr.equals("使用帮助")) {
					mLogger.info("发送帮助消息");
					// 测试
//					MessageReceipt mr = send(group,"12");
//					mLogger.info("是否发送：" + mr.isToGroup());
//					mr.recall();
//					mLogger.info("撤回");
//					if (true) return;
					// 正文
					send(group, "指令&说明\n"
						 + "\n"
						 + "●查0回复帖子\n"
						 + "可以获取版块中最近发布的200个帖子存在多少个0回复帖子。\n"
						 + "●查0回复帖子+[数字]\n"
						 + "可以获取版块中最近发布的指定帖子数以内存在多少个0回复帖子。\n"
						 + "●查板块数据||查版块数据\n"
						 + "可以获取版块现在的详细话题数、热度等（精确到个位）。\n"
						 + "●查关注ID+[用户ID]\n"
						 + "可以查看该用户关注列表前10人的数字ID，仅授权QQ群可使用。\n"
						 + "●查关注前+[数字]+ID+[用户ID]\n"
						 + "可以查看该用户关注列表前几人的数字ID，仅授权QQ群可使用。\n"
						 + "●查关注第+[数字]+ID+[用户ID]\n"
						 + "可以查看该用户关注列表第几人的数字ID，仅授权QQ群可使用。\n"
						 + "●查关注第+[数字]+到第+[数字]+ID+[用户ID]\n"
						 + "可以查看该用户关注列表第几人到第几人的数字ID，仅授权QQ群可使用。\n"
						 + "●开启0回复帖子监控\n"
						 + "开启实时监控版块0回复帖子，超过设定值将提醒回复，仅授权人员可开启。\n"
						 + "●关闭0回复帖子监控\n"
						 + "关闭实时监控版块0回复帖子，仅授权人员可关闭。\n"
						 + "●设置0回复帖子监控数+[数字]\n"
						 + "设置实时监控版块0回复帖子的数量，仅授权人员可设置，范围：2位数-4位数数字。\n"
						 + "●查询0回复帖子监控数\n"
						 + "查询目前设置的0回复帖子监控数量。\n"
						 + "●查帖子ID\n"
						 + "在30秒内分享帖子到群可获得一个该帖子的数字ID。\n"
						 + "●监控帖子+[帖子ID]\n"
						 + "对该帖子ID对应的帖子实行监控，刷新频率2分钟/次，如有最新回复将在刷新时会发送该回复信息到QQ群，仅授权人员可使用。\n"
						 + "●查询授权人员\n"
						 + "查询可操作机器人设置的管理人员。\n"
						 + "●开启复读机\n"
						 + "娱乐指令，你们说一句我重复说一句。\n"
						 + "●关闭复读机\n"
						 + "我不再重复说一句。"
						 );
					return;
				}
				long groupID=group.getId();
				if (isEquals(isAtSelf, msgStr, "查0回复帖子")) {
					count = 200;
					new Thread(new b(group)).start();
					return;
				}
				if (isMatches(isAtSelf, msgStr, "查0回复帖子[1-9]\\d{1,3}$")) {
					count = Integer.parseInt(msgStr.substring(msgStr.indexOf("子") + 1, msgStr.length()));
					new Thread(new b(group)).start();
					return;
				} else if (isMatches(isAtSelf, msgStr, "查0回复帖子[1-9]\\d{0,10}$")) {
					send(group, "查询数值不符合规范：2位数-4位数");
					return;
				}
				if (isEquals(isAtSelf, msgStr, "生成葫芦侠链接")) {
					if (comment0s.isEmpty()) {
						send(group, "当前无可生成链接的帖子，请先查询0回复帖子之后重试");
						return;
					}
					for (int i=0;i < comment0s.size();i++) {
						StringBuilder sp=new StringBuilder();
						if (i >= 10) {
							send(group, "超过显示条数：10");
							break;
						}
						String url = postid2url(comment0s.get(i).getInt("postID"), "tool");
						sp.append(i + 1).append(". ").append(comment0s.get(i).getString("title")).append("\n").append(url);
						if (i < 10) {
							send(group, sp.toString());
						}
					}
					comment0s.clear();
					return;
				}
				if (isEquals(isAtSelf, msgStr, "生成葫芦侠3楼链接") || isEquals(isAtSelf, msgStr, "生成葫芦侠三楼链接")) {
					if (comment0s.isEmpty()) {
						send(group, "当前无可生成链接的帖子，请先查询0回复帖子之后重试");
						return;
					}
					for (int i=0;i < comment0s.size();i++) {
						StringBuilder sp=new StringBuilder();
						if (i >= 10) {
							send(group, "超过显示条数：10");
							break;
						}
						String url = postid2url(comment0s.get(i).getInt("postID"), "floor");
						sp.append(i + 1).append(". ").append(comment0s.get(i).getString("title")).append("\n").append(url);
						if (i < 10) {
							send(group, sp.toString());
						}
					}
					comment0s.clear();
					return;
				}
				if (isEquals(isAtSelf, msgStr, "查版块数据") || isEquals(isAtSelf, msgStr, "查板块数据")) {
					new Thread(new c(group)).start();
					return;
				}
				if (isMatches(isAtSelf, msgStr, "查关注ID[1-9]\\d{1,8}$")) {
					for (long l:adminGroupIDs) {
						if (l == event.getGroup().getId()) {
							long userID=Long.parseLong(msgStr.substring(msgStr.indexOf("D") + 1, msgStr.length()));
							new Thread(new e(group, userID)).start();
						}
					}
					return;
				}
				if (isMatches(isAtSelf, msgStr, "查关注前[0-9]*ID[1-9]\\d{1,8}$") || isMatches(isAtSelf, msgStr, "查关注前[0-9]*ID [1-9]\\d{1,8}$")
					|| isMatches(isAtSelf, msgStr, "查关注第[0-9]*ID[1-9]\\d{1,8}$") || isMatches(isAtSelf, msgStr, "查关注第[0-9]*ID [1-9]\\d{1,8}$")) {
					for (long l:adminGroupIDs) {
						if (l == event.getGroup().getId()) {
							boolean isOne = msgStr.charAt(3) == '第';
							int count;
							if (!isOne) {
								try {
									count = Integer.parseInt(msgStr.substring(msgStr.indexOf("前") + 1, msgStr.indexOf("I")));
								} catch (NumberFormatException e) {
									send(group, "查关注参数不正确");
									return;
								}
								if (count > 300) {
									send(group, "查询数值过大了，若要查询，可以分批次查询");
									return;
								}
							} else {
								try {
									count = Integer.parseInt(msgStr.substring(msgStr.indexOf("第") + 1, msgStr.indexOf("I")));
								} catch (NumberFormatException e) {
									send(group, "查关注参数不正确");
									return;
								}
							}
							int blank=msgStr.indexOf("D") + 1;
							if (msgStr.charAt(blank) == ' ') {
								blank = blank + 1;
							}
							long userID=Long.parseLong(msgStr.substring(blank, msgStr.length()));
							new Thread(new e(count, group, userID, isOne)).start();
						}
					}
					return;
				}
				if (isMatches(isAtSelf, msgStr, "查关注第[0-9]*到[0-9]*ID[1-9]\\d{1,8}$") || isMatches(isAtSelf, msgStr, "查关注第[0-9]*到[0-9]*ID [1-9]\\d{1,8}$")) {
					for (long l:adminGroupIDs) {
						if (l == event.getGroup().getId()) {
							int start;
							int end;
							try {
								start = Integer.parseInt(msgStr.substring(msgStr.indexOf("第") + 1, msgStr.indexOf("到")));
								end = Integer.parseInt(msgStr.substring(msgStr.indexOf("到") + 1, msgStr.indexOf("I")));
							} catch (Exception e) {
								send(group, "查关注参数不正确");
								return;
							}
							int count=end - start;
							if (count > 300) {
								send(group, "查询数值过大了，若要查询，可以分批次查询");
								return;
							}
							if (count < 1) {
								send(group, "查询数量错误，若为1个请使用该指令来查询：查关注第xID+ID");
								return;
							}
							int blank=msgStr.indexOf("D") + 1;
							if (msgStr.charAt(blank) == ' ') {
								blank = blank + 1;
							}
							long userID=Long.parseLong(msgStr.substring(blank, msgStr.length()));
							new Thread(new j(group, start, end, userID)).start();
						}
					}
					return;
				}
                if (isMatches(isAtSelf, msgStr, "查收藏帖子ID[1-9]\\d{1,8}$")) {
                    for (long l:adminGroupIDs) {
                        if (l == event.getGroup().getId()) {
                            long userID=Long.parseLong(msgStr.substring(msgStr.indexOf("D") + 1, msgStr.length()));
                            new Thread(new l(group, userID)).start();
                        }
                    }
                    return;
                }
                if (isMatches(isAtSelf, msgStr, "查收藏前[0-9]*帖子ID[1-9]\\d{1,8}$") || isMatches(isAtSelf, msgStr, "查收藏前[0-9]*帖子ID [1-9]\\d{1,8}$")
                    || isMatches(isAtSelf, msgStr, "查收藏第[0-9]*帖子ID[1-9]\\d{1,8}$") || isMatches(isAtSelf, msgStr, "查收藏第[0-9]*帖子ID [1-9]\\d{1,8}$")) {
                    for (long l:adminGroupIDs) {
                        if (l == event.getGroup().getId()) {
                            boolean isOne = msgStr.charAt(3) == '第';
                            int count;
                            if (!isOne) {
                                String str=msgStr.substring(msgStr.indexOf("前") + 1, msgStr.indexOf("帖"));
                                try {
                                    count = Integer.parseInt(str);
                                } catch (NumberFormatException e) {
                                    send(group, "查收藏参数不正确，识别为："+str);
                                    return;
                                }
                                if (count > 300) {
                                    send(group, "查询数值过大了，若要查询，可以分批次查询");
                                    return;
                                }
                            } else {
                                String str=msgStr.substring(msgStr.indexOf("第") + 1, msgStr.indexOf("帖"));
                                try {
                                    count = Integer.parseInt(str);
                                } catch (NumberFormatException e) {
                                    send(group, "查收藏参数不正确，识别为："+str);
                                    return;
                                }
                            }
                            int blank=msgStr.indexOf("D") + 1;
                            if (msgStr.charAt(blank) == ' ') {
                                blank = blank + 1;
                            }
                            long userID=Long.parseLong(msgStr.substring(blank, msgStr.length()));
                            new Thread(new l(count, group, userID, isOne)).start();
                        }
                    }
                    return;
                }
                if (isMatches(isAtSelf, msgStr, "查收藏第[0-9]*到[0-9]*帖子ID[1-9]\\d{1,8}$") || isMatches(isAtSelf, msgStr, "查收藏第[0-9]*到[0-9]*帖子ID [1-9]\\d{1,8}$")) {
                    for (long l:adminGroupIDs) {
                        if (l == event.getGroup().getId()) {
                            int start;
                            int end;
                            try {
                                start = Integer.parseInt(msgStr.substring(msgStr.indexOf("第") + 1, msgStr.indexOf("到")));
                                end = Integer.parseInt(msgStr.substring(msgStr.indexOf("到") + 1, msgStr.indexOf("帖")));
                            } catch (Exception e) {
                                send(group, "查收藏参数不正确");
                                return;
                            }
                            int count=end - start;
                            if (count > 300) {
                                send(group, "查询数值过大了，若要查询，可以分批次查询");
                                return;
                            }
                            if (count < 1) {
                                send(group, "查询数量错误，若为1个请使用该指令来查询：查收藏第x帖子ID+ID");
                                return;
                            }
                            int blank=msgStr.indexOf("D") + 1;
                            if (msgStr.charAt(blank) == ' ') {
                                blank = blank + 1;
                            }
                            long userID=Long.parseLong(msgStr.substring(blank, msgStr.length()));
                            new Thread(new m(group, start, end, userID)).start();
                        }
                    }
                    return;
                }
                
				if (isEquals(isAtSelf, msgStr, "查帖子ID")) {
					if (f == null) f = new f(event.getGroup());
					send(group, "请在30秒内分享要查询的帖子或者帖子链接到本群");
					f.isPostSending = true;
					f.groupID = groupID;
					new Thread(f).start();
					return;
				}
				if (isMatches(isAtSelf, msgStr, "监控帖子[1-9]\\d{2,9}$") && isAdmin(event.getSender().getId())) {
					long postid=Long.parseLong(msgStr.substring(msgStr.indexOf("子") + 1, msgStr.length()));
					PostComments pc=getPostComments(groupID, postid);
					if (pc.isPostAccessEnabled) {
						send(group, "该帖子正在监控");
						return;
					}
					pc.isPostAccessEnabled = true;
					pc.isFirstSend = true;
					pc.postid = postid;
					new Thread(new h(group.getBot(), pc)).start();
					return;
				}
				if (isMatches(isAtSelf, msgStr, "取消监控[1-9]\\d{2,9}$") && isAdmin(event.getSender().getId())) {
					long postid=Long.parseLong(msgStr.substring(msgStr.indexOf("控") + 1, msgStr.length()));
					PostComments pc=getPostComments(groupID, postid);
					if (!pc.isPostAccessEnabled) {
						send(group, "该帖子不在监控状态");
						return;
					}
					pc.isPostAccessEnabled = false;
					pc.isFirstSend = true;
					pc.sendedComments = new JSONArray();
					send(group, "取消监控成功");
					return;
				}
				if (isEquals(isAtSelf, msgStr, "开启0回复帖子监控")) {
					Access a=getAccess(groupID);
					if (a.accessZeroComment) {
						send(group, "0回复帖子监控已经开启，无需重复开启");
						return;
					}
					if (!isAdmin(event.getSender().getId())) {
						send(group, "你没有权限开启0回复帖子监控哦。");
						return;
					}
					a.accessZeroComment = true;
					save();
					send(group, "开启成功，当最近发布的" + a.accessCount + "个帖子中出现大量0回复帖子时将会提醒回复");
					new Thread(new d(group.getBot(), a)).start();
					return;
				}
				if (isEquals(isAtSelf, msgStr, "关闭0回复帖子监控")) {
					Access a = getAccess(groupID);
					if (!a.accessZeroComment) {
						send(group, "当前0回复帖子监控是关闭状态");
						return;
					}
					if (!isAdmin(event.getSender().getId())) {
						send(group, "你没有权限关闭0回复帖子监控哦。");
						return;
					}
					a.accessZeroComment = false;
					save();
					send(group, "关闭成功");
					return;
				}
                if(isMatches(isAtSelf, msgStr, "开启版块数据统计[1-9]\\d{1,3}$")){
                    if(!isAdmin(event.getSender().getId()))
                    {
                        send(group, "你没有权限开启版块数据统计功能哦。");
                        return;
                    }
                    int cateId=Integer.parseInt(msgStr.substring(msgStr.indexOf("计") + 1, msgStr.length()));
                    CategoryData data=getCategoryData(groupID, cateId);
                    if(data.isTimerRun)
                    {
                        send(group, "此版块的数据统计功能目前是开启状态，无需重复开启");
                        return;
                    }
                    p p=new p(group, cateId);
                    Timer timer = TimerUtils.startTimer(p, 0, 0, 0);
                    data.isTimerRun=true;
                    data.mTimer=timer;
                    save();
                    send(group, "已开启此版块的数据统计功能，每天0点统计一次，并会将结果发送至本群");
                    return;
                }
                if(isMatches(isAtSelf, msgStr, "关闭版块数据统计[1-9]\\d{1,3}$")){
                    if(!isAdmin(event.getSender().getId()))
                    {
                        send(group, "你没有权限关闭版块数据统计功能哦。");
                        return;
                    }
                    int cateId=Integer.parseInt(msgStr.substring(msgStr.indexOf("计") + 1, msgStr.length()));
                    CategoryData data=getCategoryData(groupID, cateId);
                    if(!data.isTimerRun)
                    {
                        send(group, "此版块的数据统计功能目前是关闭状态");
                        return;
                    }
                    data.isTimerRun=false;
                    if(data.mTimer!=null){
                        data.mTimer.cancel();
                    }
                    save();
                    send(group, "已关闭此版块的数据统计功能");
                    return;
                }
                if (isMatches(isAtSelf, msgStr, "设置0回复帖子监控数[1-9]\\d{1,3}$")) {
					Access a = getAccess(groupID);
					if (!isAdmin(event.getSender().getId())) {
						send(group, "你没有权限设置0回复帖子监控的数量哦。");
						return;
					}
					a.accessCount = Integer.parseInt(msgStr.substring(msgStr.indexOf("数") + 1, msgStr.length()));
					save();
					send(group, "设置成功");
					return;
				} else if (isMatches(isAtSelf, msgStr, "设置0回复帖子监控数[1-9]\\d{0,10}$")) {
					if (!isAdmin(event.getSender().getId())) {
						send(group, "你没有权限设置0回复帖子监控的数量哦。");
						return;
					}
					send(group, "设定数值不符合规范：2位数-4位数");
					return;
				}
				if (isEquals(isAtSelf, msgStr, "设置0回复帖子监控数")) {
					if (!isAdmin(event.getSender().getId())) {
						send(group, "你没有权限设置0回复帖子监控的数量哦。");
						return;
					}
					send(group, "请指定一个数值，范围：2位数-4位数");
					return;
				}
                if (isMatches(isAtSelf, msgStr, "添加负责人员[1-9]\\d{4,10} 部门：[\u4e00-\u9fa5]*")) {
                    if(msgStr.indexOf("：")==(msgStr.length()-1))
                    {
                        send(group, "请填入对应部门");
                        return;
                    }
                    if (!isAdmin(event.getSender().getId())) {
                        send(group, "你没有权限添加负责人员。");
                        return;
                    }
                    long l=Long.parseLong(msgStr.substring(msgStr.indexOf("员") + 1, msgStr.indexOf(" ")));
                    String formated=msgStr.substring(msgStr.indexOf("：")+1, msgStr.length());
                    for(AdminMember admin:mAdminMembers)
                    {
                        if(admin.qq==l&&admin.formated.equals(formated))
                        {
                            send(group, "已存在该负责人员："+admin.qq+"\n部门："+admin.formated);
                            return;
                        }
                    }
                    AdminMember am=new AdminMember();
                    am.qq=l;
                    am.formated=formated;
                    mAdminMembers.add(am);
                    save();
                    send(group, "已添加负责人员：" + l+"\n部门："+formated);
                    return;
                }
                if (isMatches(isAtSelf, msgStr, "移除负责人员[1-9]\\d{4,10} 部门：[\u4e00-\u9fa5]*")) {
                    if(msgStr.indexOf("：")==(msgStr.length()-1))
                    {
                        send(group, "请填入对应部门");
                        return;
                    }
                    if (!isAdmin(event.getSender().getId())) {
                        send(group, "你没有权限移除负责人员。");
                        return;
                    }
                    long l=Long.parseLong(msgStr.substring(msgStr.indexOf("员") + 1, msgStr.indexOf(" ")));
                    String formated=msgStr.substring(msgStr.indexOf("：")+1, msgStr.length());
                    try {
                        for(AdminMember admin:mAdminMembers)
                        {
                            if(admin.qq==l&&admin.formated.equals(formated)){
                                mAdminMembers.remove(admin);
                                save();
                                send(group, "已移除负责人员：" + l + "\n部门："+formated);
                                return;
                            }
                        }
                        send(group, "部门不存在此负责人员：" + l + "\n目前负责人员：\n" + mAdminMembers);
                    } catch (Throwable e) {
                        send(group, "部门可能不存在此负责人员：" + l + "\n目前负责人员：\n" + mAdminMembers+"\n异常信息："+e);
                    }
                    return;
                }
				if (isMatches(isAtSelf, msgStr, "添加授权人员[1-9]\\d{4,10}")) {
					if (event.getSender().getId() != host) {
						send(group, "你没有权限添加授权人员。");
						return;
					}
					long l=Long.parseLong(msgStr.substring(msgStr.indexOf("员") + 1, msgStr.length()));
                    for(long qq:admins)
                    {
                        if(qq==l){
                            send(group, "已有该授权人员，不能重复添加。");
                            return;
                        }
                    }
					admins.add(l);
					save();
					send(group, "已添加授权人员：" + l);
					return;
				}
                if (isMatches(isAtSelf, msgStr, "添加授权群[1-9]\\d{4,10}")) {
                    if (event.getSender().getId() != host) {
                        send(group, "你没有权限添加授权群。");
                        return;
                    }
                    long l=Long.parseLong(msgStr.substring(msgStr.indexOf("群") + 1, msgStr.length()));
                    adminGroupIDs.add(l);
                    save();
                    send(group, "已添加授权群：" + l);
                    return;
                }
				if (isEquals(isAtSelf, msgStr, "查询授权人员")) {
					send(group, "目前授权人员：" + admins);
					return;
				}
                if (isEquals(isAtSelf, msgStr, "查询负责人员")) {
                    send(group, "目前负责人员：" + mAdminMembers);
                    return;
                }
                if (isEquals(isAtSelf, msgStr, "查询授权群")) {
                    send(group, "目前授权群：" + adminGroupIDs);
                    return;
                }
				if (isEquals(isAtSelf, msgStr, "查询0回复帖子监控数")) {
					Access a=getAccess(groupID);
					if (a.accessZeroComment) {
						send(group, "正在监控0回复的帖子数量为" + a.accessCount);
					} else {
						send(group, "已设置监控0回复的帖子数量为" + a.accessCount);
					}
					return;
				}
				if (isMatches(isAtSelf, msgStr, "移除授权人员[1-9]\\d{4,10}")) {
					if (event.getSender().getId() != host) {
						send(group, "你没有权限移除授权人员。");
						return;
					}
					long l=Long.parseLong(msgStr.substring(msgStr.indexOf("员") + 1, msgStr.length()));
					if (l == host) {
						send(group, "不可移除自己哦。");
						return;
					}
					try {
						admins.remove(l);
						save();
						send(group, "已移除授权人员：" + l);
					} catch (Exception e) {
						send(group, "不存在此授权人员：" + l + "\n目前授权人员：\n" + admins);
					}
					return;
				}
                if(msgStr.startsWith("http://bbs.huluxia.com/wap/thread/")&&f.isPostSending)
                {
                    f.isPostSending=false;
                    try {
                        send(group, "查询到该帖子ID为"+url2postid(msgStr));
                    }catch(Throwable e){
                        send(group, "查询格式错误或内部异常："+e.toString());
                    }
                }
                if (isMatches(isAtSelf, msgStr, "移除授权群[1-9]\\d{4,10}")) {
                    if (event.getSender().getId() != host) {
                        send(group, "你没有权限移除授权群。");
                        return;
                    }
                    long l=Long.parseLong(msgStr.substring(msgStr.indexOf("群") + 1, msgStr.length()));
                    try {
                        adminGroupIDs.remove(l);
                        save();
                        send(group, "已移除授权群：" + l);
                    } catch (Exception e) {
                        send(group, "不存在此授权群：" + l + "\n目前授权群：\n" + admins);
                    }
                    return;
                }
				if (isEquals(isAtSelf, msgStr, "开启复读机")) {
					Copy c=getCopy(groupID);
					if (c.enabled) {
						PlainText t=new PlainText("我正模仿人说话呢，别闹");
						Face f=new Face(Face.XIE_YAN_XIAO);
						Face f2=new Face(Face.BAO_JIN);
						List<Message> l=new ArrayList<>();
						l.add(t);
						l.add(f);
						l.add(f2);
						MessageChain msgChain=MessageUtils.newChain(l);
						send(group, msgChain);
						return;
					}
					c.enabled = true;
					save();
					PlainText t=new PlainText("复读机开启，你们说一句我说一句，哈哈");
					Face f=new Face(Face.XIE_YAN_XIAO);
					List<Message> l=new ArrayList<>();
					l.add(t);
					l.add(f);
					MessageChain msgChain=MessageUtils.newChain(l);
					send(group, msgChain);
					return;
				}
				if (isEquals(isAtSelf, msgStr, "关闭复读机")) {
					Copy c=getCopy(groupID);
					if (!c.enabled) {
						PlainText t=new PlainText("我没有模仿人说话好不好");
						Face f=new Face(Face.XIE_YAN_XIAO);
						Face f2=new Face(Face.BAO_JIN);
						List<Message> l=new ArrayList<>();
						l.add(t);
						l.add(f);
						l.add(f2);
						MessageChain msgChain=MessageUtils.newChain(l);
						send(group, msgChain);
						return;
					}
					c.enabled = false;
					save();
					PlainText t=new PlainText("我不说了啊，别打我");
					Face f=new Face(Face.XIE_YAN_XIAO);
					Face f2=new Face(Face.BAO_JIN);
					List<Message> l=new ArrayList<>();
					l.add(t);
					l.add(f);
					l.add(f2);
					MessageChain msgChain=MessageUtils.newChain(l);
					send(group, msgChain);
					return;
				}
				Copy c=getCopy(groupID);
				if (c.enabled) {
					List<Message> l=new ArrayList<>();
					for (SingleMessage m2:chain) {
						if (m2 instanceof MessageContent) {
							l.add(m2);
						}
					}
					send(group, MessageUtils.newChain(l));
				}
				break;
			}
			// 链接消息
			if (msg instanceof RichMessage) {
				RichMessage m=(RichMessage)msg;
				String xml=m.getContent();
				if (((xml.startsWith("{\"app\":\"com.tencent.structmsg\"") || ((xml.contains("<?xml") && xml.contains("url=")) || xml.startsWith("http://bbs.huluxia.com/wap/thread/"))) && f != null && f.isPostSending && f.groupID == event.getGroup().getId())) {
					new Thread(new g(group, xml)).start();
					return;
				}
			}
		}
	}
	private boolean isAdmin(long qq) {
		for (long q:admins) {
			if (q == qq) {
				return true;
			}
		}
		return false;
	}
	private boolean isEquals(boolean isAtSelf, String msg,  String s) {
		// 代码策略更改.
		return msg.equals(s);
	}
	private boolean isMatches(boolean isAtSelf, String msg, String s) {
		// 代码策略更改.
		return msg.matches(s);
	}


	static class Copy {
		long groupID;
		boolean enabled=false;
	}
	static class Access {
		long groupID;
		boolean accessZeroComment=false;
		int accessCount=200;
	}
	private static Access getAccess(long groupID) {
		for (Access a:mCacheAccesses) {
			if (a.groupID == groupID) {
				return a;
			}
		}
		Access a=new Access();
		a.groupID = groupID;
		mCacheAccesses.add(a);
		return a;
	}
	private static Copy getCopy(long groupID) {
		for (Copy a:copys) {
			if (a.groupID == groupID) {
				return a;
			}
		}
		Copy a=new Copy();
		a.groupID = groupID;
		copys.add(a);
		return a;
	}
	static class PostComments {
		JSONArray sendedComments;
		boolean isFirstSend=true;
		boolean isPostAccessEnabled=false;
		long groupID;
		long postid;
	}
	private static PostComments getPostComments(long groupID, long postid) {
		for (PostComments pc:sCachePostComments) {
			if (pc.groupID == groupID && pc.postid == postid) {
				return pc;
			}
		}
		PostComments pc=new PostComments();
		pc.groupID = groupID;
		pc.postid = postid;
		sCachePostComments.add(pc);
		return pc;
	}

	// 保存数据
	public void save() {
		JSONObject obj=new JSONObject();
		JSONArray a=new JSONArray();
		for (Copy c:copys) {
			JSONObject j=new JSONObject();
			j.put("groupID", c.groupID);
			j.put("enabled", c.enabled);
			a.put(j);
		}
		obj.put("copys", a);
		JSONArray b = new JSONArray();
		for (Access acc:mCacheAccesses) {
			JSONObject j=new JSONObject();
			j.put("groupID", acc.groupID);
			j.put("accessZeroComment", acc.accessZeroComment);
			j.put("accessCount", acc.accessCount);
			b.put(j);
		}
		obj.put("accesses", b);
		JSONArray c=new JSONArray();
		for (long l:admins) {
			c.put(l);
		}
		obj.put("admins", c);
        JSONArray de=new JSONArray();
        for (long l:adminGroupIDs) {
            de.put(l);
        }
        obj.put("adminGroupIDs", de);
		JSONArray d=new JSONArray();
		for (PostComments pc:sCachePostComments) {
			JSONObject j=new JSONObject();
			j.put("groupID", pc.groupID);
			j.put("sendedComments", pc.sendedComments);
			j.put("isFirstSend", pc.isFirstSend);
			j.put("isPostAccessEnabled", pc.isPostAccessEnabled);
			j.put("postid", pc.postid);
			d.put(j);
		}
		obj.put("PostComments", d);
        JSONArray e=new JSONArray();
        for (AdminMember admin:mAdminMembers) {
            JSONObject j=new JSONObject();
            j.put("qq", admin.qq);
            j.put("formated", admin.formated);
            e.put(j);
        }
        obj.put("AdminMembers", e);
        JSONArray f=new JSONArray();
        for (CategoryData data:sCategoryData) {
            JSONObject j=new JSONObject();
            j.put("cateId", data.cateId);
            j.put("groupID", data.groupID);
            j.put("isTimerRun", data.isTimerRun);
            j.put("lastPostCount", data.lastPostCount);
            j.put("lastViewCount",data.lastViewCount);
            f.put(j);
        }
        obj.put("CategoryData", f);
        
		try {
			FileOutputStream out=new FileOutputStream(new File(getDataFolder(), "data.json"));
			out.write(obj.toString().getBytes());
			out.close();
		} catch (Exception err) {
			mLogger.error("数据本地储存失败：" + err.toString(), err);
		}
	}

	// 恢复数据
	public void restore(Bot bot) {
		try {
			JSONObject o=new JSONObject(IOUtils.toString(new FileInputStream(new File(getDataFolder(), "data.json"))));
			if (o.has("copys")) {
				JSONArray a = o.getJSONArray("copys");
				for (int n=0;n < a.length();n++) {
					JSONObject ob=a.getJSONObject(n);
					Copy c=new Copy();
					c.groupID = ob.getLong("groupID");
					c.enabled = ob.getBoolean("enabled");
					copys.add(c);
				}
			}
			if (o.has("accesses")) {
				JSONArray b=o.getJSONArray("accesses");
				for (int n=0;n < b.length();n++) {
					JSONObject ob=b.getJSONObject(n);
					Access c=new Access();
					c.groupID = ob.getLong("groupID");
					c.accessZeroComment = ob.getBoolean("accessZeroComment");
					c.accessCount = ob.getInt("accessCount");
					mCacheAccesses.add(c);
					if (c.accessZeroComment) {
						new Thread(new o(bot, c)).start();
					}
				}
			}
			if (o.has("admins")) {
				JSONArray c=o.getJSONArray("admins");
				for (int n=0;n < c.length();n++) {
					if (c.getLong(n) == host) continue;
					admins.add(c.getLong(n));
				}
			}
            if (o.has("adminGroupIDs")) {
                JSONArray c=o.getJSONArray("adminGroupIDs");
                for (int n=0;n < c.length();n++) {
                    if (c.getLong(n) == host) continue;
                    adminGroupIDs.add(c.getLong(n));
                }
            }
			if (o.has("PostComments")) {
				JSONArray d=o.getJSONArray("PostComments");
				for (int n=0;n < d.length();n++) {
					JSONObject ob=d.getJSONObject(n);
					PostComments p=new PostComments();
					p.groupID = ob.getLong("groupID");
					p.isFirstSend = ob.getBoolean("isFirstSend");
					p.isPostAccessEnabled = ob.getBoolean("isPostAccessEnabled");
					p.sendedComments = ob.getJSONArray("sendedComments");
					p.postid = ob.getLong("postid");
					sCachePostComments.add(p);
                    if (p.isPostAccessEnabled) {
                        new Thread(new n(bot, p)).start();
                    }
				}
			}
            if (o.has("AdminMembers")) {
                JSONArray b=o.getJSONArray("AdminMembers");
                for (int n=0;n < b.length();n++) {
                    JSONObject ob=b.getJSONObject(n);
                    AdminMember am=new AdminMember();
                    am.qq=ob.getLong("qq");
                    am.formated=ob.getString("formated");
                    mAdminMembers.add(am);
                }
            }
            if (o.has("CategoryData")) {
                JSONArray d=o.getJSONArray("CategoryData");
                for (int n=0;n < d.length();n++) {
                    JSONObject ob=d.getJSONObject(n);
                    CategoryData data=new CategoryData();
                    data.groupID = ob.getLong("groupID");
                    data.isTimerRun=ob.getBoolean("isTimerRun");
                    data.cateId=ob.getInt("cateId");
                    data.lastPostCount=ob.getLong("lastPostCount");
                    data.lastViewCount=ob.getLong("lastViewCount");
                    sCategoryData.add(data);
                    if (data.isTimerRun) {
                        p p=new p(getGroup(bot, data.groupID), data.cateId);
                        Timer timer=TimerUtils.startTimer(p, 0, 0, 0);
                        data.mTimer=timer;
                    }
                }
            }
            
		} catch (Throwable e) {
			mLogger.error("恢复运行失败", e);
		}
	}
    class n implements Runnable
    {
        private final Bot mBot;
        private final PostComments mPostComments;
        n(Bot bot, PostComments pc){
            mBot=bot;
            mPostComments=pc;
        }

        @Override
        public void run() {
            Group g=null;
            while(g==null){
                try {
                    g=getGroup(mBot, mPostComments.groupID);
                }catch(Throwable e){
                }
            }
            new h(mBot, mPostComments, true).run();
        }

    }
    class o implements Runnable
    {
        private final Bot mBot;
        private final Access mAccess;
        o(Bot bot, Access pc){
            mBot=bot;
            mAccess=pc;
        }

        @Override
        public void run() {
            Group g=null;
            while(g==null){
                try {
                    g=getGroup(mBot, mAccess.groupID);
                }catch(Throwable e){
                }
            }
            new d(mBot, mAccess).run();
        }

    }
    
	public static MessageReceipt send(Contact con, Message msg) {
//		// Mirai-Core版本问题，超过两个字符发不出去，只能私聊发消息
//		if(event instanceof GroupMessageEvent && isShowGroupName){
//			try
//			{
//				msg = new PlainText("[发送自【"+((GroupMessageEvent)event).getGroup().getName()+"】群]\n").plus(msg);
//			}catch(Exception e){}
//		}
//		return event.getSender().sendMessage(msg);
		mLogger.info("发送消息：" + msg.contentToString());
		if (msg instanceof MessageChain) {
			MessageChain chain=(MessageChain)msg;
			StringBuilder sb=new StringBuilder();
			for (Message ms:chain) {
				sb.append(ms.getClass().getSimpleName());
				sb.append(", ");
			}
			mLogger.info("发送消息包含属性：" + sb.toString());
		} else {
			mLogger.info("该消息类型不属于MessageChain，它是：" + msg.getClass());
		}
		MessageReceipt rec = con.sendMessage(msg);
		mReceipts.add(rec);
		if (mReceipts.size() >= 100) {
			mReceipts.remove(0);
		}
		return rec;
	}

	public static MessageReceipt send(Contact con, String msg) {
		mLogger.info("发送消息：" + msg);
		MessageReceipt rec = con.sendMessage(msg);
		mReceipts.add(rec);
		if (mReceipts.size() >= 100) {
			mReceipts.remove(0);
		}
		return rec;
	}
	public static PMain getInstance() {
		return INSTANCE;
	}

	// 接口已有对应方法，AIDE误报，运行时不会触发
	public static ResourceContainer create(ClassLoader p1) {
		throw new UnsupportedOperationException();
	}

	public static ResourceContainer create(KClass<?> p1) {
		throw new UnsupportedOperationException();
	}

	public static ResourceContainer create(Class<?> p1) {
		throw new UnsupportedOperationException();
	}

	public File resolveDataFile(Path relativePath) {
		throw new UnsupportedOperationException();
	}

	public File resolveDataFile(String relativePath) {
		throw new UnsupportedOperationException();
	}

	public Path resolveDataPath(String relativePath) {
		throw new UnsupportedOperationException();
	}

	public Path resolveConfigPath(Path relativePath) {
		throw new UnsupportedOperationException();
	}

	public Path resolveDataPath(Path relativePath) {
		throw new UnsupportedOperationException();
	}

	public File resolveConfigFile(String relativePath) {
		throw new UnsupportedOperationException();
	}

	public File resolveConfigFile(Path relativePath) {
		throw new UnsupportedOperationException();
	}

	public Path resolveConfigPath(String relativePath) {
		throw new UnsupportedOperationException();
	}

	public String getResource(String path) {
		throw new UnsupportedOperationException();
	}

	public String getResource(String path, Charset charset) {
		throw new UnsupportedOperationException();
	}
	private static List<JSONObject> comment0s=new ArrayList<>();

	class b implements Runnable {
		private final Group group;
		b(Group e) {
			group = e;
		}

		@Override
		public void run() {
			try {
				String str = accessCatePosts(63, count);
				StringBuilder b=new StringBuilder();
				JSONObject json=new JSONObject(str);
				JSONArray ja = json.getJSONArray("posts");
				int c=0;
				comment0s.clear();
				for (int i=0;i < ja.length();i++) {
					JSONObject obj = ja.getJSONObject(i);
					if (obj.getInt("commentCount") == 0) {
						comment0s.add(obj);
						c++;
					}
				}
				if (comment0s.size() == 0) {
					send(group, "最近发布的" + count + "个帖子不存在0回复帖子");
					return;
				}
				b.append("最近发布的" + count + "个帖子存在" + c + "个0回复帖子，分别是：");
				b.append("\n");
				int n=0;
				for (JSONObject j:comment0s) {
					if (n >= 20) {
						b.append("\n超过显示条数：20");
						break;
					}
					n++;
					b.append("\n" + n + ". " + j.getString("title"));
					long l = j.getLong("createTime");
					b.append("\n发布时间：" + getTime(l));
				}
				send(group, b.toString());
			} catch (Throwable e) {
				send(group, "查询失败，错误：" + e.toString());
				mLogger.error("查询失败，错误：" + e.toString(), e);
			}
		}
	}
	// 返回时间
	public static String getTime(long l) {
		Date d=new Date(l);
		StringBuilder s=new StringBuilder();
//		int y=1900+d.getYear();
//		s.append(y+"年");
		int m=1 + d.getMonth();
		s.append(m + "月");
		s.append(d.getDate() + "日 ");
		StringBuilder sb=new StringBuilder();
		int min=d.getMinutes();
		if (min < 10) {
			sb.append("0").append(min);
		} else {
			sb.append(min);
		}
		s.append(d.getHours() + ":" + sb);
		return s.toString();
	}
	// 新线程获取帖子评论
	public static void accessPostComment(final JSONArray ars, final long postid) throws Exception {
		Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String str = accessPost(1, postid);
						JSONObject json=new JSONObject(str);
						int count = json.getJSONObject("post").getInt("commentCount");
						int pageSize=(int)Math.floor(count / 20);
						JSONArray array=json.getJSONArray("comments");
						for (int i=0;i < array.length();i++) {
							JSONObject obj = array.getJSONObject(i);
							JSONObject newObj=new JSONObject();
							newObj.put("text", obj.getString("text"));
							newObj.put("nick", obj.getJSONObject("user").getString("nick"));
							newObj.put("identityTitle", obj.getJSONObject("user").getString("identityTitle"));
							newObj.put("seq", obj.getInt("seq"));
							newObj.put("commentID", obj.getJSONObject("commentID"));
							newObj.put("title", json.getJSONObject("post").getString("title"));
							newObj.put("images", obj.getJSONArray("images"));
							ars.put(newObj);
						}
						if (pageSize == 1) return;
						for (int i=2;i <= pageSize;i++) {
							accessPostComment0(i, ars, postid);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			});
		t.start();
		t.join();
	}
	// 解析获取帖子评论
	private static void accessPostComment0(String str, JSONArray ars) throws Exception {
		JSONObject json=new JSONObject(str);
		JSONArray array=json.getJSONArray("comments");
		for (int i=0;i < array.length();i++) {
			JSONObject obj = array.getJSONObject(i);
			JSONObject newObj=new JSONObject();
			newObj.put("text", obj.getString("text"));
			newObj.put("nick", obj.getJSONObject("user").getString("nick"));
			newObj.put("identityTitle", obj.getJSONObject("user").getString("identityTitle"));
			newObj.put("seq", obj.getInt("seq"));
			newObj.put("commentID", obj.getLong("commentID"));
			newObj.put("images", obj.getJSONArray("images"));
			ars.put(newObj);
		}
	}
	// 获取帖子评论
	public static void accessPostComment0(int page, JSONArray ars, long postid) throws Exception {
		String str=accessPost(page, postid);
		accessPostComment0(str, ars);
	}
	public static String accessPost(int page, long postid) throws Exception {
		String postUrl = "http://floor.huluxia.com/post/detail/ANDROID/2.3?platform=2&gkey=000000&app_version=4.1.0.4.1&versioncode=20141451&market_id=floor_huluxia&_key=&device_code=%5Bd%5D43ab35a5-e0c9-49e7-8f04-c8f70852e050&post_id=" + postid + "&page_no=" + page + "&page_size=20&doc=1";
		return requestHttpString(postUrl);
	}
	// 请求数据
	public static String requestHttpString(String url) throws Exception {
		OkHttpClient okHttpClient = new OkHttpClient();
		final Request request = new Request.Builder()
			.url(url)
			.build();
		final Call call = okHttpClient.newCall(request);
		Response response = call.execute();
		return response.body().string();
	}
	// 获取帖子页数
	public static int getPostPageSize(long postid) throws Exception {
		String str=accessPost(1, postid);
		return getPostPageSize(str);
	}
	public static int getPostPageSize(String str) throws Exception {
		JSONObject json=new JSONObject(str);
		double commentCount = (double)json.getJSONObject("post").getInt("commentCount");
		return (int)Math.ceil(commentCount / 20D);
	}
	// 获取版块帖子列表
	public static String accessCatePosts(int cateId, int count) throws Exception {
		String cateUrl = "http://floor.huluxia.com/post/list/ANDROID/2.1?platform=2&gkey=000000&app_version=4.1.0.4.1&versioncode=20141451&market_id=floor_huluxia&_key=&device_code=%5Bd%5D43ab35a5-e0c9-49e7-8f04-c8f70852e050&start=0&count=" + count + "&cat_id=" + cateId + "&tag_id=0&sort_by=1";
		return requestHttpString(cateUrl);
	}
	// 获取版块信息
    public static String accessCateDetails(int cateId) throws Exception
    {
        return requestHttpString("http://floor.huluxia.com/category/detail/ANDROID/2.0?cat_id="+cateId+"&platform=2&gkey=000000&app_version=4.1.0.4.1&versioncode=20141451&market_id=floor_huluxia&_key=&device_code=%5Bd%5D43ab35a5-e0c9-49e7-8f04-c8f70852e050&phone_brand_type=MI");
    }

	// 获取版块信息并发送
	class c implements Runnable {
		private final Group group;
		c(Group e) {group = e;}

		@Override
		public void run() {
			try {
				String str=accessCateDetails(63); // 版块ID=63为我的世界版块
				JSONObject json=new JSONObject(str);
				long postCount=json.getLong("postCount");
				long viewCount=json.getLong("viewCount");
				String description=json.getString("description");
				StringBuilder b=new StringBuilder();
				b.append(json.getString("title")).append("\n")
					.append("描述：")
					.append(description)
					.append("\n话题数：")
					.append(postCount)
					.append("\n热度：")
					.append(viewCount);
				send(group, b.toString());
			} catch (Throwable e) {
				send(group, "查询失败，错误：" + e.toString());
				mLogger.error("查询失败，错误：" + e.toString(), e);
			}
		}

	}
	// 版块数据储存
    static final List<CategoryData> sCategoryData=new ArrayList<>();
    static CategoryData getCategoryData(long groupID, int cateId) {
        for (CategoryData pc:sCategoryData) {
            if (pc.groupID == groupID && pc.cateId == cateId) {
                return pc;
            }
        }
        CategoryData pc=new CategoryData();
        pc.groupID = groupID;
        pc.cateId = cateId;
        sCategoryData.add(pc);
        return pc;
    }
    public static class CategoryData
    {
        public boolean isTimerRun;
        public long groupID;
        public int cateId;
        public long lastPostCount;
        public long lastViewCount;
        public Timer mTimer;
    }
    // 每天定时任务
    class p extends TimerTask
    {
        private final Group group;
        private final int cateId;
        p(Group g, int id){
            group=g;
            cateId=id;
        }

        @Override
        public void run() {
            CategoryData data=getCategoryData(group.getId(), cateId);
            if(data.isTimerRun){
                try {
                    String str=accessCateDetails(cateId);
                    JSONObject json=new JSONObject(str);
                    long postCount=json.getLong("postCount");
                    long viewCount=json.getLong("viewCount");
                    StringBuilder b=new StringBuilder();
                    long lastPostCount=data.lastPostCount;
                    long lastViewCount=data.lastViewCount;
                    b.append(json.getString("title"))
                        .append("\n话题数：")
                        .append(postCount)
                        .append("\n热度：")
                        .append(viewCount);
                        if(lastPostCount!=0&&lastViewCount!=0){
                            b.append("\n相较于前一天帖子增长：")
                            .append(postCount-lastPostCount)
                            .append("\n相较于前一天热度增长：")
                            .append(viewCount-lastViewCount);
                        }else{
                            b.append("\n该版块数据已记录，以便下次整点查询时可以获取一天的版块数据");
                        }
                    data.lastPostCount=postCount;
                    data.lastViewCount=viewCount;
                    save();
                    send(group, b.toString());
                } catch (Throwable e) {
                    send(group, "查询失败，错误：" + e.toString());
                    mLogger.error("查询失败，错误：" + e.toString(), e);
                }
            }
            
        }

        
    }
    // 检测版块0回复帖子
	class d implements Runnable {
		private final Bot mBot;
		private final List<Sender> list=new ArrayList<>();
		private final Access access;
		private int c=0;
		d(Bot bot, Access a) {
			mBot = bot;
			list.add(new Sender(200));
			list.add(new Sender(100));
			list.add(new Sender(50));
			list.add(new Sender(30));
			list.add(new Sender(20));
			access = a;
		}

		@Override
		public void run() {
			while (access.accessZeroComment) {
				try {
					String str = accessCatePosts(63, access.accessCount); // 版块ID=63为我的世界版块
					JSONObject json=new JSONObject(str);
					JSONArray ja = json.getJSONArray("posts");
					int n=0;
					for (int i=0;i < ja.length();i++) {
						JSONObject obj = ja.getJSONObject(i);
						if (obj.getInt("commentCount") == 0) {
							n++;
						}
					}
					c = n;
					// 循环遍历每个检测线
					for (int i=0;i < list.size();i++) {
						// 如果目前0回复帖子大于检测线，并且之前未发送过消息，则该条件判断后执行。
						if (c > list.get(i).count && !list.get(i).isSended) {
							send(getGroup(mBot, access.groupID), "版块最近发布的" + access.accessCount + "个帖子包含的0回复帖子已经超过" + list.get(i).count + "条，请注意回复！");
							list.get(i).isSended = true;
							// 为防止低检测线的再次触发，将低检测线都置为已发送
							for (int j=i;j < list.size();j++) {
								list.get(j).isSended = true;
							}
						}
						if (c <= list.get(i).count && list.get(i).isSended) {
							list.get(i).isSended = false;
						}
					}
					Thread.sleep(120000L);
				} catch (Throwable e) {
				}
			}
		}

	}
	static class Sender {
		final int count;
		boolean isSended;
		Sender(int c) {
			count = c;
		}
	}
	// 访问关注
	public static String accessAttention(long userID, int count) throws Exception {
		String url="http://floor.huluxia.com/friendship/following/list/ANDROID/2.0?platform=2&gkey=000000&app_version=4.1.0.4.1&versioncode=20141451&market_id=floor_huluxia&_key=&device_code=%5Bd%5D43ab35a5-e0c9-49e7-8f04-c8f70852e050&start=0&count=" + count + "&user_id=" + userID;
		return requestHttpString(url);
	}
	// 访问收藏
    public static String accessFavorite(long userID, int count) throws Exception {
        String url="http://floor.huluxia.com/post/favorite/list/ANDROID/2.0?platform=2&gkey=000000&app_version=4.1.0.5&versioncode=20141452&market_id=floor_huluxia&_key=&device_code=%5Bd%5D43ab35a5-e0c9-49e7-8f04-c8f70852e050&phone_brand_type=MI&start=0&count="+count+"&user_id="+userID;
        return requestHttpString(url);
    }
    // 获取关注userid
	class e implements Runnable {
		private final Group group;
		private final int count;
		private final long userID;
		private final boolean isOne;
		e(Group e, long u) {
			this(10, e, u, false);
		}
		e(int c, Group e, long u, boolean is) {
			count = c;
			group = e;
			userID = u;
			isOne = is;
		}

		@Override
		public void run() {
			try {
				String str=accessAttention(userID, count);
				JSONObject obj=new JSONObject(str);
				JSONArray ar=obj.getJSONArray("friendships");
				int n=count;
				int length=ar.length();
				if (length <= 0) {
					send(group, "该用户没有关注任何人。");
					return;
				}
				StringBuilder b=new StringBuilder();
				if (length < n) {
					if (isOne) {
						send(group, "你要查询的数值超出了该用户的关注总数，该用户仅关注了" + length + "人。");
						return;
					}
					b.append("你要查询的数值超出了该用户的关注总数，仅展示" + length + "人的数字ID。\n");
					n = length;
				}
				if (isOne) {
					appendAttentionTo(b, ar.length() - 1, ar);
				} else {
					for (int i=0;i < n;i++) {
						appendAttentionTo(b, i, ar);
					}
				}
				try {
					send(group, b.toString());
				} catch (MessageTooLargeException e) {
					send(group, "查询数量过大，若要查询，可以分批次查询");
				}
			} catch (Throwable e) {
				send(group, "查询失败");
				sendErrorToHost(group.getBot(), e);
				mLogger.error("查询失败，错误：" + e.toString(), e);
			}

		}
	}
	// 获取收藏postid
    class l implements Runnable {
        private final Group group;
        private final int count;
        private final long userID;
        private final boolean isOne;
        l(Group e, long u) {
            this(5, e, u, false);
        }
        l(int c, Group e, long u, boolean is) {
            count = c;
            group = e;
            userID = u;
            isOne = is;
        }

        @Override
        public void run() {
            try {
                String str=accessFavorite(userID, count);
                JSONObject obj=new JSONObject(str);
                JSONArray ar=obj.getJSONArray("posts");
                int n=count;
                int length=ar.length();
                if (length <= 0) {
                    send(group, "该用户没有收藏任何帖子。");
                    return;
                }
                StringBuilder b=new StringBuilder();
                if (length < n) {
                    if (isOne) {
                        send(group, "你要查询的数值超出了该用户的收藏总数，该用户仅收藏了" + length + "个帖子。");
                        return;
                    }
                    b.append("你要查询的数值超出了该用户的收藏总数，仅展示" + length + "个帖子的数字ID。\n");
                    n = length;
                }
                if (isOne) {
                    appendFavoriteTo(b, ar.length() - 1, ar);
                } else {
                    for (int i=0;i < n;i++) {
                        appendFavoriteTo(b, i, ar);
                    }
                }
                try {
                    send(group, b.toString());
                } catch (MessageTooLargeException e) {
                    send(group, "查询数量过大，若要查询，可以分批次查询");
                }
            } catch (Throwable e) {
                send(group, "查询失败");
                sendErrorToHost(group.getBot(), e);
                mLogger.error("查询失败，错误：" + e.toString(), e);
            }

        }
    }
    // 向主人发送错误信息
	public static void sendErrorToHost(Bot bot, Throwable e) {
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		for (Friend f:bot.getFriends()) {
			if (f.getId() == host) {
				f.sendMessage("机器人内部错误或者发送者格式错误：\n" + sw.toString());
			}
		}
	}
	// 获取关注userid
	class j implements Runnable {
		private final Group group;
		private final int start;
		private final int end;
		private final long userID;
		j(Group e, int s, int en, long u) {
			group = e;
			start = s;
			end = en;
			userID = u;
		}

		@Override
		public void run() {
			try {
				String str = accessAttention(userID, end);
				JSONObject obj=new JSONObject(str);
				JSONArray ar=obj.getJSONArray("friendships");
				int n=end;
				int length=ar.length();
				if (length <= 0) {
					send(group, "该用户没有关注任何人。");
					return;
				}
				if (start > length) {
					send(group, "你要查询的数值超出了该用户的关注总数，该用户仅关注了" + length + "人。");
					return;
				}
				StringBuilder b=new StringBuilder();
				if (end > length) {
					if (start == length) {
						b.append("你要查询的数值超出了该用户的关注总数，仅展示关注第").append(start).append("的ID。\n");
					} else {
						b.append("你要查询的数值超出了该用户的关注总数，仅展示关注第").append(start).append("到").append(length).append("的ID。\n");
					}
				}
				int starts=start - 1;
				for (int i=starts;i < n;i++) {
					if (i >= length) {
						break;
					}
					appendAttentionTo(b, i, ar);
				}
				try {
					send(group, b.toString());
				} catch (MessageTooLargeException e) {
					send(group, "查询数量过大，若要查询，可以分批次查询");
				}
			} catch (Throwable e) {
				send(group, "查询失败");
				sendErrorToHost(group.getBot(), e);
				mLogger.error("查询失败，错误：" + e.toString(), e);
			}

		}

	}
	// 获取收藏postid
    class m implements Runnable {
        private final Group group;
        private final int start;
        private final int end;
        private final long userID;
        m(Group e, int s, int en, long u) {
            group = e;
            start = s;
            end = en;
            userID = u;
        }

        @Override
        public void run() {
            try {
                String str = accessFavorite(userID, end);
                JSONObject obj=new JSONObject(str);
                JSONArray ar=obj.getJSONArray("posts");
                int n=end;
                int length=ar.length();
                if (length <= 0) {
                    send(group, "该用户没有收藏任何帖子。");
                    return;
                }
                if (start > length) {
                    send(group, "你要查询的数值超出了该用户的收藏总数，该用户仅收藏了" + length + "个帖子。");
                    return;
                }
                StringBuilder b=new StringBuilder();
                if (end > length) {
                    if (start == length) {
                        b.append("你要查询的数值超出了该用户的收藏总数，仅展示收藏第").append(start).append("的帖子ID。\n");
                    } else {
                        b.append("你要查询的数值超出了该用户的收藏总数，仅展示收藏第").append(start).append("到").append(length).append("的帖子ID。\n");
                    }
                }
                int starts=start - 1;
                for (int i=starts;i < n;i++) {
                    if (i >= length) {
                        break;
                    }
                    appendFavoriteTo(b, i, ar);
                }
                try {
                    send(group, b.toString());
                } catch (MessageTooLargeException e) {
                    send(group, "查询数量过大，若要查询，可以分批次查询");
                }
            } catch (Throwable e) {
                send(group, "查询失败");
                sendErrorToHost(group.getBot(), e);
                mLogger.error("查询失败，错误：" + e.toString(), e);
            }

        }

    }
    // 添加关注user到消息
	void appendAttentionTo(StringBuilder b, int index, JSONArray ar) {
		JSONObject o=ar.getJSONObject(index).getJSONObject("user");
		if (index != 0 && !b.toString().isEmpty()) {
			b.append("\n");
		}
		b.append("☞名字：").append(o.getString("nick"))
			.append("\n").append("ID:").append(o.getLong("userID"));
	}
	// 添加收藏帖子到消息
    void appendFavoriteTo(StringBuilder b, int index, JSONArray ar) {
        JSONObject o=ar.getJSONObject(index);
        if (index != 0 && !b.toString().isEmpty()) {
            b.append("\n");
        }
        b.append("☞标题：").append(o.getString("title"))
            .append("\n").append("ID:").append(o.getLong("postID"));
    }
    
    // 帖子检测
	class f implements Runnable {
		boolean isPostSending=false;
		long groupID;
		private final Group group;
		f(Group g) {
			group = g;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {}
			if (isPostSending) {
				isPostSending = false;
				send(group, "30秒已过，没有检测到分享帖子");
			}
		}

	}
	// 根据链接查询帖子ID
	class g implements Runnable {
		private final Group group;
		private final String msg;
		g(Group e, String m) {
			group = e;
			msg = m;
		}

		@Override
		public void run() {
			try {
				String url;
				if (msg.contains("<?xml") && msg.contains("url=")) {
					url = msg.substring(msg.indexOf("http://"), msg.indexOf("\" serviceID"));
				} else if (msg.startsWith("{\"app\":\"com.tencent.structmsg\"")) {
					JSONObject json=new JSONObject(msg);
					url = json.getJSONObject("meta").getJSONObject("news").getString("jumpUrl");
				} else {
					url = msg;
				}
				send(group, "查询到该帖子ID为" + url2postid(url));
				f.isPostSending = false;
			} catch (Throwable e) {
				send(group, "查询格式错误或内部异常：" + e.toString());
				mLogger.error("查询格式错误或内部异常：" + e.toString(), e);
				f.isPostSending = false;
			}
		}

	}
	// 获取群对象
	public static Group getGroup(Bot bot, long groupID) {
		for (Group g:bot.getGroups()) {
			if (g.getId() == groupID) {
				return g;
			}
		}
		throw new IllegalArgumentException("Group id " + groupID + " is not found.");
	}
	// 获取好友对象
	public static Friend getFriend(Bot bot, long friendID) {
		for (Friend f:bot.getFriends()) {
			if (f.getId() == friendID) {
				return f;
			}
		}
		throw new IllegalArgumentException("Friend id " + friendID + " is not found.");
	}
	// 监控帖子评论
	class h implements Runnable {
		private final Bot mBot;
		private final PostComments pc;
		private final boolean isRestoreData;
		h(Bot bot, PostComments pcs) {
			this(bot, pcs, false);
		}
		h(Bot bot, PostComments pcs, boolean res) {
			mBot = bot;
			pc = pcs;
			isRestoreData = res;
		}

		@Override
		public void run() {
			while (pc.isPostAccessEnabled) {
				try {
					Group g=getGroup(mBot, pc.groupID);
					JSONArray ars=new JSONArray();
					String str=accessPost(1, pc.postid);
					String title=new JSONObject(str).getJSONObject("post").getString("title");
					int pageSize=getPostPageSize(str);
					if (pageSize == 1) {
						accessPostComment0(str, ars);
					} else if (pageSize > 1) {
						accessPostComment0(pageSize, ars, pc.postid);
						if (ars.length() == 0) {
							accessPostComment0(pageSize - 1, ars, pc.postid);
						}
					}

					// 以下方法弃用
//					@Deprecated
//					if (pageSize > 1) {
//						accessPostComment0(pageSize - 1, ars, pc.postid);
//					}
//					accessPostComment0(pageSize, ars, pc.postid);
					if (pc.isFirstSend) {
						pc.isFirstSend = false;
						pc.sendedComments = ars;
						if (!isRestoreData) {
							send(g, "正在监控：" + title);
						}
						save();
						Thread.sleep(60000);
						continue;
					}
					con:for (int n=0;n < ars.length();n++) {
						JSONObject jo=ars.getJSONObject(n);
						long commentID = jo.getLong("commentID");
						for (int i=0;i < pc.sendedComments.length();i++) {
							if (pc.sendedComments.getJSONObject(i).getLong("commentID") == commentID)
								continue con;
						}
						String s=jo.getString("identityTitle");
						String text=jo.getString("text");
						JSONArray imageUrls=jo.getJSONArray("images");
						List<Image> imgMsgs=new ArrayList<>();
						for (int i=0;i < imageUrls.length();i++) {
							InputStream in = new OkHttpClient().newCall(new Request.Builder().url(imageUrls.getString(i)).build()).execute().body().byteStream();
							imgMsgs.add(Contact.Companion.uploadImage(g, in, (String)null));
							IOUtils.closeQuietly(in);
						}
						Message msg;
						if (!text.contains("\n想要加入组别：")) {
							StringBuilder sb=new StringBuilder()
								.append("帖子【").append(title).append("】有新回复\n")
								.append(processName(jo.getString("nick"))).append("  ")
								.append(s.isEmpty() ?"无称号": s).append("  ")
								.append(jo.getInt("seq")).append("楼\n");
							sb.append("内容：\n").append(text);
							msg = new PlainText(sb.toString());
						} else {
							String st3=text.substring(text.indexOf("\n想要加入组别：") + 8, text.length());
							StringBuilder sb2=new StringBuilder()
								.append("\n帖子【").append(title).append("】有人报名，快去接人啦！\n")
								.append(processName(jo.getString("nick"))).append("  ")
								.append(s.isEmpty() ?"无称号": s).append("  ")
								.append(jo.getInt("seq")).append("楼\n");
							msg = new PlainText(sb2.toString());
							Message at=getAtAsMemberOrNull(st3, g);
							if (at==null) {
								StringBuilder sb=new StringBuilder()
									.append("帖子【").append(title).append("】有人报名\n")
									.append(processName(jo.getString("nick"))).append("  ")
									.append(s.isEmpty() ?"无称号": s).append("  ")
									.append(jo.getInt("seq")).append("楼\n");
								sb.append("内容：\n").append(text);
								msg = new PlainText(sb.toString());
							} else {
								msg = at.plus(msg).plus(new PlainText(new StringBuilder().append("内容：\n").append(text)));
							}
						}
						for (Image image:imgMsgs) {
							msg = msg.plus(image);
						}
						send(g, msg);
						pc.sendedComments.put(jo);
						save();
//						FileOutputStream f = new FileOutputStream(getDataFolder() + "/comment_" + commentID + ".txt");
//						f.write(msg.contentToString().getBytes());
//						f.close();
					}
					Thread.sleep(120000);
				} catch (Throwable e) {
					mLogger.error("监控错误：", e);
				}
			}
		}

	}
	// 负责人员
    public static class AdminMember
    {
        public long qq;
        public String formated;

        @Override
        public String toString() {
            return new StringBuilder("QQ：").append(qq).append(" 负责：").append(formated).toString();
        }
    }
    private static final List<AdminMember> mAdminMembers=new ArrayList<>();
    
    // 获取群成员对象
    public static NormalMember getMemberOrNull(Group group, long qq)
    {
        if(group==null) return null;
        for(NormalMember mem:group.getMembers())
        {
            if(mem.getId()==qq)
            {
                return mem;
            }
        }
        return null;
    }
    // 获取可At的群成员对象
    public static Message getAtAsMemberOrNull(@NotNull String str, @NotNull Group group)
    {
        Message at=null;
        List<String> splitFormated=new ArrayList<>();
        for(AdminMember admin:mAdminMembers)
        {
            if(str.contains(admin.formated))
            {
                splitFormated.add(admin.formated);
            }
        }
        if(splitFormated.size()>1)
        {
            for(int n=0;n<splitFormated.size();n++)
            {
                int start=str.indexOf(splitFormated.get(n));
                if(start!=-1)
                {
                    str=str.substring(start, str.length());
                }
            }
        }
        Set<Long> ats=new HashSet<>();
        for(AdminMember admin:mAdminMembers)
        {
            if(str.contains(admin.formated))
            {
                NormalMember mem=getMemberOrNull(group, admin.qq);
                if(mem == null)
                {
                   continue;
                }
                ats.add(admin.qq);
            }
        }
        for(long l:ats)
        {
            at=(at==null?new At(l):at.plus(new At(l)).plus(" "));
        }
        return at;
    }
    // 帖子链接转帖子ID
	public static int url2postid(String url) {
		int index=url.indexOf("&product=");
		if (index < 0) {
			index = url.indexOf("&amp;product=");
		}
		if (index < 0) {
			index = url.length();
		}
		String para2=url.substring(url.indexOf("para=") + 5, index);
		String para3=URLDecoder.decode(para2);
		byte[] b=Base64.decode(para3, 0);
		String st;
		try {
			st = new String(b, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Error occured: " + e.toString(), e);
		}
		char[] array=st.toCharArray();
		for (int i = 0; i < array.length; i++) {
			array[i] = (char) (array[i] ^ 1984);
		}
		String dec = new String(array);
		return Integer.parseInt(dec.substring(0, dec.indexOf("_"))) ^ 193186672;

	}
	// 帖子ID转帖子链接
	public static String postid2url(int postid, String product) {
		int randomFactor = new Random().nextInt(1000000);
		String str=(postid ^ 193186672) + "_" + randomFactor;
		char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            array[i] = (char) (array[i] ^ 1984);
        }
        byte[] b;
        String para;
        try {
            b = new String(array).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Error occured: " + e.toString(), e);
        }
		try {
			para = Base64.encodeToString(b, 0);
		} catch (Exception e) {
			throw new RuntimeException("Error occured: " + e.toString(), e);
		}
		String url="http://bbs.huluxia.com/wap/thread/%d.html?para=%s";
		String result=String.format(Locale.getDefault(), url, randomFactor, URLEncoder.encode(para));
		if (product != null) {
			result = new StringBuilder(result).append("&product=").append(product).toString();
		}
		return result;
	}

	// 名字处理（无效果）
	public static String processName(String str) {
		if (str == null) return str;
		if (!str.contains("‮")) {
			return str;
		}
		char[] chs=str.toCharArray();
		for (int n=0;n < chs.length;n++) {
			if (chs[n] == '‮') {
				chs = ArrayUtils.remove(chs, n);
			}
		}
		return new String(chs);
	}


}

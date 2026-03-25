package de.instinct.eqfleet.menu.module.social;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.instinct.api.social.dto.Friend;
import de.instinct.api.social.dto.FriendRequestSendResponse;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.profile.model.UsernameTextField;
import de.instinct.eqfleet.menu.module.social.message.CreateGroupMessage;
import de.instinct.eqfleet.menu.module.social.message.FriendRequestMessage;
import de.instinct.eqfleet.menu.module.social.message.InviteToGroupMessage;
import de.instinct.eqfleet.menu.module.social.message.LeaveGroupMessage;
import de.instinct.eqfleet.menu.module.social.message.RemoveFriendMessage;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.module.list.ActionList;
import de.instinct.eqlibgdxutils.rendering.ui.module.list.ActionListElement;
import de.instinct.eqlibgdxutils.rendering.ui.module.list.ListActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class SocialRenderer extends BaseModuleRenderer {
	
	private ActionList friendList;
	private ActionList groupList;
	private Label friendsLabel;
	private Label groupLabel;
	
	private Popup profilePopup;
	private ElementList popupContent;
	private ColorButton groupInteractButton;
	
	private Label addFriendLabel;
	private UsernameTextField addFriendField;
	
	private final float FRIEND_ADD_MESSAGE_DURATION = 3f;
	private float friendAddRemainingTime;
	private Label friendAddMessageLabel;
	
	public SocialRenderer() {
		friendList = new ActionList();
		friendList.setDecorated(true);
		friendList.setElementClickHandler(new ListActionHandler() {
			
			@Override
			public void triggered(ActionListElement element) {
				createProfilePopup(element.getValue(), true);
			}
			
		});
		groupList = new ActionList();
		groupList.setDecorated(true);
		groupList.setElementClickHandler(new ListActionHandler() {
			
			@Override
			public void triggered(ActionListElement element) {
				createProfilePopup(element.getValue(), false);
			}
		
		});
		friendsLabel = new Label("Friends");
		friendsLabel.setColor(new Color(SkinManager.skinColor));
		friendsLabel.setType(FontType.LARGE);
		groupLabel = new Label("Groups");
		groupLabel.setColor(new Color(SkinManager.skinColor));
		groupLabel.setType(FontType.LARGE);
		
		groupInteractButton = new ColorButton("Create Group");
		groupInteractButton.setAction(new Action() {
			
			@Override
			public void execute() {
				if (SocialModel.groupData == null) {
					SocialModel.messageQueue.add(CreateGroupMessage.builder().build());
				} else {
					SocialModel.messageQueue.add(LeaveGroupMessage.builder().build());
				}
			}
			
		});
		
		addFriendLabel = new Label("Add Friend");
		addFriendLabel.setType(FontType.SMALL);
		
		addFriendField = new UsernameTextField(new TextfieldActionHandler() {
			
			@Override
			public void confirmed() {
				SocialModel.messageQueue.add(FriendRequestMessage.builder().toName(addFriendField.getContent()).build());
				addFriendField.setContent("");
			}
			
		});
		addFriendField.getTextField().getContentLabel().setType(FontType.SMALL);
		addFriendField.getTextField().getLimitLabel().setType(FontType.SMALL);
		
		friendAddMessageLabel = new Label("");
		friendAddMessageLabel.setColor(new Color());
		friendAddMessageLabel.setType(FontType.SMALL);
	}
	
	private void createProfilePopup(String friendName, boolean isFriend) {
		popupContent = new ElementList();
		popupContent.setMargin(10f);
		popupContent.setFixedWidth(150f);
		ColorButton inviteToGroupButton = new ColorButton("Invite Group");
		inviteToGroupButton.setFixedWidth(150f);
		inviteToGroupButton.setFixedHeight(30f);
		inviteToGroupButton.setLayer(1);
		inviteToGroupButton.setAction(new Action() {
			
			@Override
			public void execute() {
				SocialModel.messageQueue.add(InviteToGroupMessage.builder().friendName(friendName).build());
				PopupRenderer.close();
			}
			
		});
		if (isFriend && SocialModel.groupData != null) popupContent.getElements().add(inviteToGroupButton);
		
		ColorButton deleteButton = new ColorButton("Delete Friend");
		deleteButton.setFixedWidth(150f);
		deleteButton.setFixedHeight(30f);
		deleteButton.setLayer(1);
		deleteButton.setAction(new Action() {
			
			@Override
			public void execute() {
				SocialModel.messageQueue.add(RemoveFriendMessage.builder().friendName(friendName).build());
				PopupRenderer.close();
			}
			
		});
		if (isFriend) popupContent.getElements().add(deleteButton);
		
		profilePopup = Popup.builder()
				.title(friendName)
				.contentContainer(popupContent)
				.closeOnClickOutside(true)
				.windowColor(new Color(SkinManager.skinColor))
				.titleColor(new Color(SkinManager.skinColor))
				.onCloseAction(new Action() {
					
					@Override
					public void execute() {
						friendList.setActiveElement("");
						groupList.setActiveElement("");
					}
					
				})
				.build();
		PopupRenderer.create(profilePopup);
	}

	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		if (SocialModel.playerData != null) {
			addFriendField.setBounds(MenuModel.moduleBounds.x + 20, MenuModel.moduleBounds.y + 30, (MenuModel.moduleBounds.width / 2) - 40, 25);
			friendAddMessageLabel.setBounds(addFriendField.getBounds().x, addFriendField.getBounds().y - 20, addFriendField.getBounds().width, 15);
			if (friendAddRemainingTime > 0) {
				friendAddRemainingTime -= Gdx.graphics.getDeltaTime();
				if (friendAddRemainingTime <= 0) {
					friendAddMessageLabel.setText("");
					friendAddRemainingTime = 0;
				}
			}
			if (SocialModel.friendRequestSendResponse != null) {
				if (SocialModel.friendRequestSendResponse == FriendRequestSendResponse.SUCCESS) {
					friendAddMessageLabel.getColor().set(Color.GREEN);
				} else {
					friendAddMessageLabel.getColor().set(Color.GRAY);
				}
				friendAddMessageLabel.setText(SocialModel.friendRequestSendResponse.toString());
				friendAddRemainingTime = FRIEND_ADD_MESSAGE_DURATION;
				SocialModel.friendRequestSendResponse = null;
			}
			addFriendLabel.setBounds(addFriendField.getBounds().x, addFriendField.getBounds().y + addFriendField.getBounds().height, addFriendField.getBounds().width, 20);
			
			friendList.setBounds(MenuModel.moduleBounds.x + 20, MenuModel.moduleBounds.y + 20 + 100, (MenuModel.moduleBounds.width / 2) - 40, MenuModel.moduleBounds.height - 40 - 40 - 100);
			friendList.getElements().clear();
			if (SocialModel.playerData.getFriends() != null) {
				for (Friend friend : SocialModel.playerData.getFriends()) {
					friendList.getElements().add(ActionListElement.builder()
							.value(friend.getName())
							.label(friend.getName())
							.build());
				}
			}
			friendsLabel.setBounds(friendList.getBounds().x, friendList.getBounds().y + friendList.getBounds().height, friendList.getBounds().width, 40);
			
			groupList.setBounds(MenuModel.moduleBounds.x + (MenuModel.moduleBounds.width / 2) + 20, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 150, (MenuModel.moduleBounds.width / 2) - 40, 90);
			groupList.getElements().clear();
			groupLabel.setBounds(groupList.getBounds().x, groupList.getBounds().y + groupList.getBounds().height, groupList.getBounds().width, 40);
			groupInteractButton.setBounds(MenuModel.moduleBounds.x + (MenuModel.moduleBounds.width / 2) + 20, groupList.getBounds().y - 40, (MenuModel.moduleBounds.width / 2) - 40, 30);
			if (SocialModel.groupData != null && SocialModel.groupData.getMembers() != null) {
				for (String groupMember : SocialModel.groupData.getMembers()) {
					groupList.getElements().add(ActionListElement.builder()
							.value(groupMember)
							.label(groupMember)
							.build());
				}
				groupInteractButton.getLabel().setText("Leave Group");
			} else {
				groupInteractButton.getLabel().setText("Create Group");
			}
		}
	}

	@Override
	public void render() {
		friendList.render();
		addFriendField.render();
		addFriendLabel.render();
		friendAddMessageLabel.render();
		friendsLabel.render();
		groupList.render();
		groupLabel.render();
		groupInteractButton.render();
	}

	@Override
	public void dispose() {
		friendList.dispose();
		addFriendField.dispose();
		friendsLabel.dispose();
		groupList.dispose();
		groupLabel.dispose();
		groupInteractButton.dispose();
	}

}

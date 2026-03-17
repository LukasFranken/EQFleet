package de.instinct.eqfleet.menu.main.header.components.social;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqfleet.menu.module.social.SocialModel;
import de.instinct.eqfleet.menu.module.social.message.RespondToInviteMessage;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class GroupSocialHeaderComponent extends Component {

	private List<Label> groupNameLabels;

	private MiniWindow groupInviteWindow;

	public GroupSocialHeaderComponent() {
		groupNameLabels = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Border groupBorder = new Border();
			groupBorder.setSize(1f);
			groupBorder.setColor(new Color(SkinManager.skinColor));
			Label groupNameLabel = new Label("");
			groupNameLabel.setColor(new Color(SkinManager.skinColor));
			groupNameLabel.setType(FontType.TINY);
			groupNameLabel.setBorder(groupBorder);
			groupNameLabels.add(groupNameLabel);
		}

		groupInviteWindow = new MiniWindow(MiniWindowConfiguration.builder()
				.title("GROUP INVITE")
				.content("")
				.acceptButtonText("JOIN")
				.acceptAction(new Action() {

					@Override
					public void execute() {
						SocialModel.messageQueue.add(RespondToInviteMessage.builder()
								.inviteId(0)
								.accept(true)
								.build());
					}

				})
				.declineButtonText("X")
				.declineAction(new Action() {
					
					@Override
					public void execute() {
						SocialModel.messageQueue.add(RespondToInviteMessage.builder()
								.inviteId(0)
								.accept(false)
								.build());
					}
					
				})
				.build());
	}

	@Override
	protected void updateComponent() {
		groupInviteWindow.setBounds(getBounds());
		
		int i = 0;
		for (Label groupNameLabel : groupNameLabels) {
			groupNameLabel.setBounds(getBounds().x, getBounds().y + getBounds().height - ((i + 1) * (getBounds().height / 3)), getBounds().width, getBounds().height / 3);
			i++;
		}

		if (SocialModel.groupData == null || SocialModel.groupData.getMembers() == null) {
			if (SocialModel.playerData.getGroupInvites().size() > 0) {
				groupInviteWindow.getContentLabel().setText(SocialModel.playerData.getGroupInvites().get(0).getFromName());
			}
		} else {
			for (Label groupNameLabel : groupNameLabels) {
				groupNameLabel.setText("");
			}
			int x = 0;
			for (String groupMember : SocialModel.groupData.getMembers()) {
				groupNameLabels.get(x).setText(groupMember);
				x++;
			}
		}
	}

	@Override
	protected void renderComponent() {
		if (SocialModel.groupData == null) {
			if (SocialModel.playerData.getGroupInvites().size() != 0) {
				groupInviteWindow.render();
			}
		} else {
			for (Label groupNameLabel : groupNameLabels) {
				if (!groupNameLabel.getText().contentEquals(""))
					groupNameLabel.render();
			}
		}
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		groupInviteWindow.dispose();
	}

}

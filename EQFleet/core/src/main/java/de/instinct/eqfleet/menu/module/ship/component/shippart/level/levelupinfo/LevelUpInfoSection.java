package de.instinct.eqfleet.menu.module.ship.component.shippart.level.levelupinfo;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqfleet.menu.common.components.label.DefaultLabelFactory;
import de.instinct.eqfleet.menu.common.components.label.LabelStackConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LevelUpInfoSection extends Component {
	
	private float rowHeight = 15f;
	private float tagWidth = 120f;
	
	private List<LevelUpInfoRow> infoRows;
	
	private boolean maxLevel;
	
	public LevelUpInfoSection(LevelUpInfoSectionConfig config) {
		super();
		infoRows = new ArrayList<>();
		maxLevel = config.getNextLevel() == -1;
		LevelUpInfoRow headerRow = createRow(LevelUpInfo.builder()
				.tagValue("")
				.currentValue("Lv " + config.getCurrentLevel())
				.changeValue(maxLevel ? "MAX" : "->")
				.nextValue(maxLevel ? "" : "Lv " + config.getNextLevel())
				.build(), config.getColor());
		headerRow.setHeader(true);
		infoRows.add(headerRow);
		
		for (LevelUpInfo info : config.getLevelUpInfos()) {
			infoRows.add(createRow(info, config.getColor()));
		}
	}
	
	private LevelUpInfoRow createRow(LevelUpInfo info, Color color) {
		Label tagLabel = new Label(info.getTagValue());
		tagLabel.setType(FontType.SMALL);
		tagLabel.setColor(color);
		tagLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		ElementStack currentValueLabel = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
				.tag(info.getCurrentValue())
				.value(info.getNextValue() != null ? info.getNextValue() : null)
				.colorTag(color)
				.colorValue(color)
				.type(FontType.SMALL)
				.build());
		
		Label changeLabel = new Label(info.getChangeValue());
		changeLabel.setType(FontType.SMALL);
		changeLabel.setColor(Color.GREEN);
		
		return LevelUpInfoRow.builder()
				.tagLabel(tagLabel)
				.currentAndNextValueLabelStack(currentValueLabel)
				.changeValueLabel(changeLabel)
				.build();
	}

	@Override
	protected void updateComponent() {
		int i = 1;
		float height = calculateHeight();
		for (LevelUpInfoRow row : infoRows) {
			float rowY = (getBounds().y + height) - (i * rowHeight);
			row.getTagLabel().setPosition(getBounds().x, rowY);
			row.getTagLabel().setFixedHeight(row.isHeader() ? rowHeight + 5f : rowHeight);
			row.getTagLabel().setFixedWidth(tagWidth);
			
			row.getCurrentAndNextValueLabelStack().setPosition(getBounds().x + tagWidth, rowY);
			row.getCurrentAndNextValueLabelStack().setFixedHeight(row.isHeader() ? rowHeight + 5f : rowHeight);
			row.getCurrentAndNextValueLabelStack().setFixedWidth(calculateWidth() - tagWidth);
			
			row.getChangeValueLabel().setPosition(getBounds().x + tagWidth, rowY);
			row.getChangeValueLabel().setFixedHeight(row.isHeader() ? rowHeight + 5f : rowHeight);
			row.getChangeValueLabel().setFixedWidth(calculateWidth() - tagWidth);
			i++;
		}
	}
	
	@Override
	protected void renderComponent() {
		for (LevelUpInfoRow row : infoRows) {
			row.getTagLabel().render();
			row.getCurrentAndNextValueLabelStack().render();
			row.getChangeValueLabel().render();
		}
	}

	@Override
	public float calculateHeight() {
		return infoRows.size() * rowHeight + (infoRows.get(0).isHeader() ? 5f : 0f);
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		for (LevelUpInfoRow row : infoRows) {
			row.getCurrentAndNextValueLabelStack().dispose();
			row.getChangeValueLabel().dispose();
		}
	}

}

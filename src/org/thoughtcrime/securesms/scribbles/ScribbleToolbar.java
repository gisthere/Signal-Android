/**
 * Copyright (C) 2016 Open Whisper Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.thoughtcrime.securesms.scribbles;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.thoughtcrime.securesms.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ScribbleToolbar extends Toolbar implements View.OnClickListener {

  private enum Selected {
    NONE,
    STICKER,
    TEXT,
  }

  private int foregroundSelectedTint;
  private int foregroundUnselectedTint;

  private LinearLayout toolsView;

  private ImageView saveView;
  private ImageView textView;
  private ImageView stickerView;

  private ImageView separatorView;

  private ImageView undoView;
  private ImageView deleteView;

  private Drawable background;

  @Nullable
  private ScribbleToolbarListener listener;

  private int      toolColor = Color.RED;
  private Selected selected  = Selected.NONE;

  public ScribbleToolbar(Context context) {
    super(context);
    init(context);
  }

  public ScribbleToolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public ScribbleToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    inflate(context, R.layout.scribble_toolbar, this);

    this.toolsView     = (LinearLayout) findViewById(R.id.tools_view);
    this.textView      = (ImageView) findViewById(R.id.text_button);
    this.stickerView   = (ImageView) findViewById(R.id.sticker_button);
    this.separatorView = (ImageView) findViewById(R.id.separator);
    this.saveView      = (ImageView) findViewById(R.id.save);

    this.undoView   = (ImageView) findViewById(R.id.undo);
    this.deleteView = (ImageView) findViewById(R.id.delete);

    this.background               = getResources().getDrawable(R.drawable.circle_tintable);
    this.foregroundSelectedTint   = getResources().getColor(R.color.white);
    this.foregroundUnselectedTint = getResources().getColor(R.color.grey_800);

    this.undoView.setOnClickListener(this);
    this.textView.setOnClickListener(this);
    this.stickerView.setOnClickListener(this);
    this.separatorView.setOnClickListener(this);
    this.deleteView.setOnClickListener(this);
    this.saveView.setOnClickListener(this);
  }

  public void setListener(@Nullable ScribbleToolbarListener listener) {
    this.listener = listener;
  }

  public void setToolColor(int toolColor) {
    this.toolColor = toolColor;
    this.background.setColorFilter(new PorterDuffColorFilter(toolColor, PorterDuff.Mode.MULTIPLY));
  }

  public int getToolColor() {
    return this.toolColor;
  }

  @Override
  public void onClick(View v) {
    this.toolsView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

    if (v == this.stickerView) {
      setNoneSelected();
      if (listener != null) listener.onStickerSelected(true);
    } else if (v == this.textView) {
      boolean enabled = selected != Selected.TEXT;
      setTextSelected(enabled);
      if (listener != null) listener.onTextSelected(enabled);
    } else if (v == this.deleteView) {
      setNoneSelected();
      if (listener != null) listener.onDeleteSelected();
    } else if (v == this.undoView) {
      if (listener != null) listener.onPaintUndo();
    } else if (v == this.saveView) {
      if (listener != null) listener.onSave();
    }
  }

  public void setTextSelected(boolean enabled) {
    if (enabled) {
      this.textView.setBackground(background);
      this.textView.setColorFilter(new PorterDuffColorFilter(foregroundSelectedTint, PorterDuff.Mode.MULTIPLY));

      this.stickerView.setBackground(null);
      this.stickerView.setColorFilter(new PorterDuffColorFilter(foregroundUnselectedTint, PorterDuff.Mode.MULTIPLY));

      this.separatorView.setVisibility(View.VISIBLE);
      this.undoView.setVisibility(View.GONE);
      this.deleteView.setVisibility(View.VISIBLE);

      this.selected = Selected.TEXT;
    } else {
      this.textView.setBackground(null);
      this.textView.setColorFilter(new PorterDuffColorFilter(foregroundUnselectedTint, PorterDuff.Mode.MULTIPLY));

      this.separatorView.setVisibility(View.GONE);
      this.deleteView.setVisibility(View.GONE);

      this.selected = Selected.NONE;
    }
  }

  public void setStickerSelected(boolean enabled) {
    if (enabled) {
      this.textView.setBackground(null);
      this.textView.setColorFilter(new PorterDuffColorFilter(foregroundUnselectedTint, PorterDuff.Mode.MULTIPLY));

      this.separatorView.setVisibility(View.VISIBLE);
      this.undoView.setVisibility(View.GONE);
      this.deleteView.setVisibility(View.VISIBLE);

      this.selected = Selected.STICKER;
    } else {
      this.separatorView.setVisibility(View.GONE);
      this.deleteView.setVisibility(View.GONE);

      this.selected = Selected.NONE;
    }
  }

  public void setNoneSelected() {
    setStickerSelected(false);
    setTextSelected(false);

    this.selected = Selected.NONE;
  }

  public interface ScribbleToolbarListener {
    public void onPaintUndo();
    public void onTextSelected(boolean enabled);
    public void onStickerSelected(boolean enabled);
    public void onDeleteSelected();
    public void onSave();
  }
}

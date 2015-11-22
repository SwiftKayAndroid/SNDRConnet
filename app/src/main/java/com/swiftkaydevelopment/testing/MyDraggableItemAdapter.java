/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    Edited by Kevin Haines
 */

package com.swiftkaydevelopment.testing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.me.kaede.tagview.Tag;
import com.me.kaede.tagview.TagView;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractDataProvider;
import com.h6ah4i.android.example.advrecyclerview.common.utils.DrawableUtils;
import com.h6ah4i.android.example.advrecyclerview.common.utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.richeditor.RichEditor;

public class MyDraggableItemAdapter
        extends RecyclerView.Adapter<MyDraggableItemAdapter.MyViewHolder>
        implements DraggableItemAdapter<MyDraggableItemAdapter.MyViewHolder> {

    SharedPreferences prefs;

    class TitleValues{
        String title;
        String value;
        int imgloc;
    }

    static Context context;
    ImageLoader imageLoader;
    private static final String TAG = "MyDraggableItemAdapter";

    private AbstractDataProvider mProvider;

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView tvname;
        public TextView tvphone;
        public TextView tvemail;
        public ImageView ivpicture;

        public MyViewHolder(View v) {
            super(v);

            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            tvname = (TextView) v.findViewById(R.id.tvlist_item_name);
            tvphone = (TextView) v.findViewById(R.id.tvlist_item_phone);
            tvemail = (TextView) v.findViewById(R.id.tvlist_item_email);
            ivpicture = (ImageView) v.findViewById(R.id.ivlist_item);
            v.setTag(this);
        }
    }

    public MyDraggableItemAdapter(Context context,AbstractDataProvider dataProvider) {
        this.context = context;
        Log.d("test","MyDraggableItemAdapter constructor: ");
        mProvider = dataProvider;
        imageLoader = new ImageLoader(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate((viewType == 0) ? R.layout.list_item_draggable : R.layout.list_item2_draggable, parent, false);
        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        final boolean isActive = item.getIsActive();
        if (!isActive) {

            holder.tvname.setTextColor(context.getResources().getColor(R.color.inactive_text));
            holder.tvemail.setTextColor(context.getResources().getColor(R.color.inactive_text));
            holder.tvphone.setTextColor(context.getResources().getColor(R.color.inactive_text));
            holder.ivpicture.setAlpha(.50f);

        } else {
            holder.tvname.setTextColor(context.getResources().getColor(R.color.normal_blue));
            holder.tvemail.setTextColor(context.getResources().getColor(R.color.normal_blue));
            holder.tvphone.setTextColor(context.getResources().getColor(R.color.normal_blue));
            holder.ivpicture.setAlpha(1.0f);
        }

        // set text
        holder.tvname.setText(item.getFirstname() + " " + item.getLastname());
        holder.tvemail.setText(item.getEmail());
        holder.tvphone.setText(item.getPhone());
        imageLoader.DisplayImage(item.getPicture(),holder.ivpicture,false);



        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View bottomView = inflater.inflate(R.layout.full_contact_info,null,false);
                ImageView ivphoto = (ImageView) bottomView.findViewById(R.id.ivcontactcard);
                ImageView ivdismiss = (ImageView) bottomView.findViewById(R.id.ivcontact_card_dismiss);
                TextView tvcontactname = (TextView) bottomView.findViewById(R.id.tvcontact_card_name);
                ListView lv = (ListView) bottomView.findViewById(R.id.lvcontact_card);
                tvcontactname.setText(item.getFirstname() + " " + item.getLastname());
                imageLoader.DisplayImage(item.getPicture(), ivphoto, true);

                List<TitleValues> mlist = new ArrayList<TitleValues>();
                TitleValues val = new TitleValues();
                val.title = "ID";
                val.value = item.getDataId();
                val.imgloc = R.drawable.id;
                mlist.add(val);

                val = new TitleValues();
                val.title = "Index";
                val.value = Integer.toString(item.getIndex());
                val.imgloc = R.drawable.index;
                mlist.add(val);

                val = new TitleValues();
                val.title = "GUID";
                val.value = item.getGUID();
                val.imgloc = R.drawable.tag;
                mlist.add(val);

                val = new TitleValues();
                val.title = "Is Active";
                val.value = String.valueOf(item.getIsActive());
                val.imgloc = R.drawable.success;
                mlist.add(val);

                val = new TitleValues();
                val.title = "Email";
                val.value = item.getEmail();
                val.imgloc = R.drawable.laptop;
                mlist.add(val);

                val = new TitleValues();
                val.title = "Phone";
                val.value = item.getPhone();
                val.imgloc = R.drawable.phone;
                mlist.add(val);

                val = new TitleValues();
                val.title = "Address";
                val.value = item.getAddress();
                val.imgloc = R.drawable.location;
                mlist.add(val);

                val = new TitleValues();
                val.title = "Notification";
                val.value = item.getNotifications();
                val.imgloc = R.drawable.notification;
                mlist.add(val);

                lv.setAdapter(new ContactCardAdapter(context, mlist));

                View header = inflater.inflate(R.layout.contact_card_header,null);
                TagView tagview = (TagView) header.findViewById(R.id.tagview);

                String[] tags = item.getTags();
                for (int i = 0; i < tags.length;i++) {
                    Tag tag = new Tag(tags[i]);
                    tag.tagTextColor = Color.parseColor("#FFFFFF");
                    tag.layoutColor = context.getResources().getColor(R.color.normal_blue);
                    tag.layoutColorPress = Color.parseColor("#555555");
                    tag.radius = 20f;
                    tag.tagTextSize = 14f;
                    tag.layoutBorderSize = 1f;
                    tag.layoutBorderColor = Color.parseColor("#FFFFFF");
                    //tag.isDeletable = true;
                    tagview.addTag(tag);
                }

                lv.addHeaderView(header);

                View footer = inflater.inflate(R.layout.richtext_footer,null);
                final RichTextDisplayView displayer = (RichTextDisplayView) footer.findViewById(R.id.displayeditor);
                displayer.setHTML(item.getHTML());
                displayer.setMinimumHeight(200);

                displayer.setOnTouchListener(new View.OnTouchListener() {

                    public final static int FINGER_RELEASED = 0;
                    public final static int FINGER_TOUCHED = 1;
                    public final static int FINGER_DRAGGING = 2;
                    public final static int FINGER_UNDEFINED = 3;
                    private int fingerState = FINGER_RELEASED;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        switch (motionEvent.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                                else fingerState = FINGER_UNDEFINED;
                                break;

                            case MotionEvent.ACTION_UP:
                                if(fingerState != FINGER_DRAGGING) {
                                    fingerState = FINGER_RELEASED;

                                    RecyclerListViewFragment.bottomSheet.dismissSheet();
                                    final View pop = inflater.inflate(R.layout.richtext_holder, null, false);
                                    RecyclerListViewFragment.bottomSheet.showWithSheetView(pop);


                                    final RichEditor mEditor = (RichEditor) pop.findViewById(R.id.editor);
                                    final Button btnsave = (Button) pop.findViewById(R.id.btnsaverichtext);

                                    mEditor.setEditorFontSize(22);
                                    mEditor.setEditorFontColor(Color.BLACK);
                                    mEditor.setBackgroundColor(Color.WHITE);
                                    mEditor.setPadding(10, 10, 10, 10);
                                    mEditor.setPlaceholder("Insert text here...");

                                    mEditor.setHtml(item.getHTML());


                                    btnsave.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            item.setHTML(mEditor.getHtml());
                                            RecyclerListViewFragment.bottomSheet.showWithSheetView(bottomView);
                                            displayer.setHTML(item.getHTML());

                                        }
                                    });

                                    pop.findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.undo();
                                        }
                                    });

                                    pop.findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.redo();
                                        }
                                    });

                                    pop.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setBold();
                                        }
                                    });

                                    pop.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setItalic();
                                        }
                                    });

                                    pop.findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setSubscript();
                                        }
                                    });

                                    pop.findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setSuperscript();
                                        }
                                    });

                                    pop.findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setStrikeThrough();
                                        }
                                    });

                                    pop.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setUnderline();
                                        }
                                    });

                                    pop.findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setHeading(1);
                                        }
                                    });

                                    pop.findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setHeading(2);
                                        }
                                    });

                                    pop.findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setHeading(3);
                                        }
                                    });

                                    pop.findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setHeading(4);
                                        }
                                    });

                                    pop.findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setHeading(5);
                                        }
                                    });

                                    pop.findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setHeading(6);
                                        }
                                    });

                                    pop.findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
                                        boolean isChanged;

                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                                            isChanged = !isChanged;
                                        }
                                    });

                                    pop.findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
                                        boolean isChanged;

                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                                            isChanged = !isChanged;
                                        }
                                    });

                                    pop.findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setIndent();
                                        }
                                    });

                                    pop.findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setOutdent();
                                        }
                                    });

                                    pop.findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setAlignLeft();
                                        }
                                    });

                                    pop.findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setAlignCenter();
                                        }
                                    });

                                    pop.findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setAlignRight();
                                        }
                                    });

                                    pop.findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mEditor.setBlockquote();
                                        }
                                    });

                                }
                                else if (fingerState == FINGER_DRAGGING) fingerState = FINGER_RELEASED;
                                else fingerState = FINGER_UNDEFINED;
                                break;

                            case MotionEvent.ACTION_MOVE:
                                if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) fingerState = FINGER_DRAGGING;
                                else fingerState = FINGER_UNDEFINED;
                                break;

                            default:
                                fingerState = FINGER_UNDEFINED;
                        }
                        return false;
                    }
                });

                lv.addFooterView(footer);

                ivdismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecyclerListViewFragment.bottomSheet.dismissSheet();
                    }
                });

                RecyclerListViewFragment.bottomSheet.showWithSheetView(bottomView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        mProvider.moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }
}

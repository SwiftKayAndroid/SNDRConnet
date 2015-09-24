package com.h6ah4i.android.example.advrecyclerview.common.data;

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
 *
 *    Edited by Kevin Haines
 */


import java.util.List;

public abstract class AbstractDataProvider {

    public static abstract class Data {
        public abstract long getId();

        public abstract boolean isSectionHeader();

        public abstract int getViewType();

        public abstract int getSwipeReactionType();

        public abstract String getDataId();
        public abstract int getIndex();
        public abstract boolean getIsActive();
        public abstract String getGUID();
        public abstract String getPicture();
        public abstract String getFirstname();
        public abstract String getLastname();
        public abstract String getEmail();
        public abstract String getPhone();
        public abstract String getAddress();
        public abstract String[] getTags();
        public abstract String getHTML();
        public abstract void setHTML(String html);
        public abstract List<DataProvider.Friends> getFriends();
        public abstract String getNotifications();



        public abstract void setPinnedToSwipeLeft(boolean pinned);

        public abstract boolean isPinnedToSwipeLeft();
    }

    public abstract int getCount();

    public abstract Data getItem(int index);

    public abstract void removeItem(int position);

    public abstract void moveItem(int fromPosition, int toPosition);

    public abstract int undoLastRemoval();
}

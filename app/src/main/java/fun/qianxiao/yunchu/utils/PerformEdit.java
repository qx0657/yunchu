package fun.qianxiao.yunchu.utils;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.Stack;

import fun.qianxiao.yunchu.R;

/**
 * 撤销和恢复撤销
 * compile 'ren.qinc.edit:lib:0.0.3'
 * Created by 沈钦赐 on 16/6/23.
 */
public class PerformEdit {
    //操作序号(一次编辑可能对应多个操作，如替换文字，就是删除+插入)
    int index;
    Stack<PerformEdit.Action> history = new Stack<>();
    Stack<PerformEdit.Action> historyBack = new Stack<>();
    private ImageView menu_undo, menu_redo;

    private Editable editable;
    //撤销和重做操作标志，防止撤销时候，重复操作
    private boolean flag = false;

    public PerformEdit(@NonNull EditText editText, ImageView menu_undo, ImageView menu_redo) {
        this.menu_undo = menu_undo;
        this.menu_redo = menu_redo;
        this.menu_undo.setEnabled(false);
        this.menu_redo.setEnabled(false);
        CheckNull(editText, "EditText不能为空");
        editable = editText.getText();
        editText.addTextChangedListener(new PerformEdit.Watcher());
    }

    protected void onEditableChanged(Editable s) {

    }

    protected void onTextChanged(Editable s) {

    }

    public boolean canUndo(){
        return !history.empty();
    }

    public boolean canRedo(){
        return !historyBack.empty();
    }

    /**
     * 首次设置文本
     * Set default text.
     */
    public final void setDefaultText(CharSequence text){
        clearHistory();
        flag = true;
        editable.replace(0,editable.length(),text);
        flag = false;
    }

    /**
     * 清理记录
     * Clear history.
     */
    public final void clearHistory() {
        history.clear();
        historyBack.clear();
        menu_redo.setEnabled(false);
        menu_undo.setEnabled(false);
    }


    /**
     * 撤销
     * Undo.
     */
    public final void undo() {
        if (history.empty()) return;
        //锁定操作
        flag = true;
        PerformEdit.Action action = history.pop();
        historyBack.push(action);
        menu_redo.setEnabled(true);
        if (action.isAdd) {
            //撤销添加
            editable.delete(action.cursor, action.cursor + action.actionTarget.length());
        } else {
            //插销删除
            editable.insert(action.cursor, action.actionTarget);
        }
        //释放操作
        flag = false;
        //判断是否是下一个动作是否和本动作是同一个操作，直到不同为止
        if (!history.empty() && history.peek().index == action.index){
            undo();
        }
        if(history.empty()){
            menu_undo.setEnabled(false);
        }
    }

    /**
     * 恢复
     * Redo.
     */
    public final void redo() {
        if (historyBack.empty()) return;
        flag = true;
        PerformEdit.Action action = historyBack.pop();
        history.push(action);
        menu_undo.setEnabled(true);

        if (action.isAdd)//恢复添加
            editable.insert(action.cursor, action.actionTarget);
        else//恢复删除
            editable.delete(action.cursor, action.cursor + action.actionTarget.length());
        flag = false;
        //判断是否是下一个动作是否和本动作是同一个操作
        if (!historyBack.empty() && historyBack.peek().index == action.index)
            redo();
        if(historyBack.empty()){
            menu_redo.setEnabled(false);
        }
    }

    private class Watcher implements TextWatcher {

        /**
         * Before text changed.
         *
         * @param s     the s
         * @param start the start 起始光标
         * @param count the count 选择数量
         * @param after the after 替换增加的文字数
         */
        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            int end = start + count;
            if (end > start && end <= s.length()) {
                CharSequence charSequence = s.subSequence(start, end);
                //发生文字变化
                if (charSequence.length() > 0) {
                    PerformEdit.Action action = new PerformEdit.Action(charSequence, start, false);
                    history.push(action);
                    menu_undo.setEnabled(true);
                    historyBack.clear();
                    action.setIndex(++index);

                }
            }
        }

        /**
         * On text changed.
         *
         * @param s      the s
         * @param start  the start 起始光标
         * @param before the before 选择数量
         * @param count  the count 添加的数量
         */
        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
            if (flag) return;
            int end = start + count;
            if (end > start) {
                CharSequence charSequence = s.subSequence(start, end);
                //发生文字变化
                if (charSequence.length() > 0) {
                    PerformEdit.Action action = new PerformEdit.Action(charSequence, start, true);
                    history.push(action);
                    menu_undo.setEnabled(true);
                    historyBack.clear();
                    if (before > 0) {
                        //文字替换（先删除再增加），删除和增加是同一个操作，所以不需要增加序号
                        action.setIndex(index);
                    } else {
                        action.setIndex(++index);
                    }
                }
            }
        }

        @Override
        public final void afterTextChanged(Editable s) {
            if (flag) return;
            if (s != editable) {
                editable = s;
                onEditableChanged(s);
            }
            PerformEdit.this.onTextChanged(s);
        }

    }

    private class Action {
        /** 改变字符. */
        CharSequence actionTarget;
        /** 光标位置. */
        int cursor;
        /** 标志增加操作. */
        boolean isAdd;
        /** 操作序号. */
        int index;

        public Action(CharSequence actionTag, int cursor, boolean add) {
            this.actionTarget = actionTag;
            this.cursor = cursor;
            this.isAdd = add;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }


    private static void CheckNull(Object o,String message) {
        if(o == null)throw new IllegalStateException(message);
    }
}

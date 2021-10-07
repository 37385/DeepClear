package nettal.deepclear;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;

public class SearchableDialog extends android.app.AlertDialog.Builder {
    public SearchableDialog(Context context, ArrayList<? extends View> viewList) {
        super(context);
        ListViewAdapter<? extends View> adapter = new ListViewAdapter<>(viewList);
        /*
         EditText
         */
        EditText editText = new EditText(context);
        editText.setHint("搜索");
        editText.setAllCaps(false);
        editText.setMaxLines(1);
        editText.addTextChangedListener(adapter);
        editText.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);
        editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);//单行输入的前提
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        /*
         ListView
         */
        ListView listView = new ListView(context);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(adapter);
        listView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        /*
         LinearLayout
         */
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);//垂直方向
        linearLayout.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(editText);
        linearLayout.addView(listView);
        /*
         Dialog
         */
        setView(linearLayout);
    }
}

class ListViewAdapter<T extends View> extends android.widget.BaseAdapter
        implements android.text.TextWatcher, AdapterView.OnItemClickListener {
    private final ArrayList<T> viewArrayList;
    private final ArrayList<T> viewList;

    ListViewAdapter(ArrayList<T> viewList) {
        this.viewList = viewList;
        this.viewArrayList = new ArrayList<>(viewList);
    }

    @Override
    public int getCount() {
        return viewArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return viewArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return viewArrayList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        return viewArrayList.get(position);
    }

    /* Text Watcher*/
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(android.text.Editable editable) {
        viewArrayList.clear();
        viewArrayList.addAll(viewList);
        if (editable != null && editable.length() != 0) {//此时输入框不为空
            String lowCase = editable.toString().toLowerCase();
            ArrayList<LinkedList<T>> linkedLists = new ArrayList<>();
            a:
            for (int k = viewArrayList.size() - 1; k >= 0; k--) {//根据index把item塞入linkedLists.要倒着来。
                //	a:for(int k = 0 ; k<viewArrayList.size();k++){//根据index把item塞入linkedLists.
                T t = viewArrayList.get(k);
                int index = t.toString().toLowerCase().indexOf(lowCase);
                if (index == -1) continue;
                while (index >= linkedLists.size()) {
                    linkedLists.add(new LinkedList<>());
                }
                java.util.ListIterator<T> iterator = linkedLists.get(index).listIterator();
                while (iterator.hasNext()) {//从小到大排序
                    if (t.toString().length() <= iterator.next().toString().length()) {
                        iterator.previous();//向前一次
                        iterator.add(t);
                        continue a;
                    }
                }
                linkedLists.get(index).addLast(t);//注意continue a;
            }
            viewArrayList.clear();
            for (int b = 0; b < linkedLists.size(); b++) {//塞入排序过的
                viewArrayList.addAll(linkedLists.get(b));
            }
        }
        notifyDataSetChanged();
    }

    /* OnItemClickListener*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setEnabled(!view.isEnabled());
    }
}


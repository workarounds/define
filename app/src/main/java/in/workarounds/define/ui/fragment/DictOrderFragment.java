package in.workarounds.define.ui.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import in.workarounds.define.R;
import in.workarounds.define.constants.DictionaryConstants;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by madki on 27/10/15.
 */
public class DictOrderFragment extends Fragment {
    private RecyclerView dictionaries;
    private DictionaryAdapter adapter;
    private ItemTouchHelper helper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dict_order, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dictionaries = (RecyclerView) view.findViewById(R.id.rv_dictionaries);
        adapter = new DictionaryAdapter(getContext());
        adapter.setDragListener(new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                helper.startDrag(viewHolder);
            }
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(dictionaries);

        dictionaries.setLayoutManager(new LinearLayoutManager(getContext()));
        dictionaries.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(adapter != null) {
            adapter.updatePrefs();
        }
    }

    private static class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.ViewHolder> implements OnItemTouchHelperAdapter {
        private static final int DICTIONARY_ENABLED = 1;
        private static final int DICTIONARY_DISABLED = 2;
        private OnStartDragListener dragListener;
        private HashMap<Integer, String> dictionaryMap;
        private ArrayList<Integer> order;
        private ArrayList<Integer> disabled;
        private Context context;

        public DictionaryAdapter(Context context) {
            this.context = context;

            dictionaryMap = new HashMap<>();
            for(int i : DictionaryConstants.allIds) {
                dictionaryMap.put(i, DictionaryConstants.dictNames[i] + " Dictionary");
            }

            int[] prefOrder = PrefUtils.getDictionaryOrder(context);
            order = arrayToArrayList(prefOrder);
            disabled = new ArrayList<>();

            for(int i: dictionaryMap.keySet()) {
                if(!isIntInArray(prefOrder, i)) {
                    disabled.add(i);
                }
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case DICTIONARY_ENABLED:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dict_order, parent, false);
                    break;
                default:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dict_order_disabled, parent, false);
                    break;
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (position >= 0 && position < order.size()) {
                holder.dictionaryName.setText(dictionaryMap.get(order.get(position)));
                holder.dragHandle.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (MotionEventCompat.getActionMasked(event) ==
                                MotionEvent.ACTION_DOWN && dragListener != null) {
                            dragListener.onStartDrag(holder);
                        }
                        return false;
                    }
                });
                holder.actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        disabled.add(order.get(position));
                        order.remove(position);
                        notifyDataSetChanged();
                        updatePrefs();
                    }
                });
            } else {
                final int correctedPosition = position - order.size();
                holder.dictionaryName.setText(
                        dictionaryMap.get(disabled.get(correctedPosition))
                );
                holder.actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.add(disabled.get(correctedPosition));
                        disabled.remove(correctedPosition);
                        notifyDataSetChanged();
                        updatePrefs();
                    }
                });
                holder.dictionaryName.setPaintFlags(holder.dictionaryName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        public void setDragListener(OnStartDragListener listener) {
            this.dragListener = listener;
        }

        @Override
        public int getItemCount() {
            return dictionaryMap.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= 0 && position < order.size()) {
                return DICTIONARY_ENABLED;
            } else {
                return DICTIONARY_DISABLED;
            }
        }

        public void updatePrefs() {
            PrefUtils.setDictionaryOrder(context, arrayListToArray(order));
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if(fromPosition < order.size() && toPosition < order.size()) {
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(order, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(order, i, i - 1);
                    }
                }
                notifyItemMoved(fromPosition, toPosition);
                updatePrefs();
                return true;
            }
            return false;
        }

        @Override
        public void onItemDismiss(int position) {

        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView dictionaryName;
            View actionButton;
            View dragHandle;

            public ViewHolder(View itemView) {
                super(itemView);

                dictionaryName = (TextView) itemView.findViewById(R.id.tv_dictionary_name);
                actionButton = itemView.findViewById(R.id.iv_action_button);
                dragHandle = itemView.findViewById(R.id.iv_drag_handle);
            }

        }

        private static boolean isIntInArray(int[] array, int integer) {
            if(array == null) return false;
            for (int i: array) {
                if(i==integer) return true;
            }
            return false;
        }

        private static int[] arrayListToArray(ArrayList<Integer> integers) {
            int[] array = new int[integers.size()];
            for(int i=0; i<integers.size(); i++) {
                array[i] = integers.get(i);
            }
            return array;
        }

        private static ArrayList<Integer> arrayToArrayList(int[] array) {
            ArrayList<Integer> integers = new ArrayList<>();
            for (int i: array) {
                integers.add(i);
            }
            return integers;
        }
    }

    private static class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private final OnItemTouchHelperAdapter adapter;

        public ItemTouchHelperCallback(OnItemTouchHelperAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    public interface OnItemTouchHelperAdapter {
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }

    public interface OnStartDragListener {

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }
}

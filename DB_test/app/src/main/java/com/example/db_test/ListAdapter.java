package com.example.db_test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements ListAdapterClickListener {
    ArrayList<info> items = new ArrayList<info>();
    ListAdapterClickListener listener;
    Context context;    // 다이얼로그를 위해 받아오기
    Handler handler = new Handler();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.list, viewGroup, false);

        this.context = viewGroup.getContext();  // 다이얼로그를 위해 받아오기

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        info item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(info item) {
        items.add(item);
    }

    public void setItems(ArrayList<info> items) {
        this.items = items;
    }

    public info getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, info item) {
        items.set(position, item);
    }

    public void setOnItemClickListener(ListAdapterClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView number, content;   // list.xml 에 있는 텍스트 뷰 2개 선언

        public ViewHolder(View itemView, final ListAdapterClickListener listener) {
            super(itemView);

            number = itemView.findViewById(R.id.list_text_number);
            content = itemView.findViewById(R.id.list_text_content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    delete(number.getText().toString(), getAdapterPosition());  // 삭제 함수 실행
                    return false;
                }
            });
        }

        public void setItem(info item) {    // 가져온 값 설정
            number.setText(item.getNumber());
            content.setText(item.getContent());
        }
    }

    private void delete(String number, int position) {  // 다이얼로그 호출과 삭제하는 코드
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("삭제할까요?");
        builder.setCancelable(false);   // 다이얼로그 화면 밖 터치 방지

        builder.setPositiveButton("삭제", new androidx.appcompat.app.AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() { // 스레드 생성
                    @Override
                    public void run() { // 스레드 런
                        String text = Web.delete(number);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (text.equals("삭제")) {    // db에서 삭제가 됐으면 리스트에서도 지우기
                                    items.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, items.size());
                                }
                                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();   // 토스트 메시지
                            }
                        });
                    }
                }).start();
            }
        });
        builder.setNegativeButton("취소", new androidx.appcompat.app.AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
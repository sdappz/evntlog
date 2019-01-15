package com.swagger.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.swagger.app.PartnerListActivity;
import com.swagger.app.R;
import com.swagger.app.model.ServiceCategoryModel;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.RecyclerViewHolder> {
    Context ctx;
    List<ServiceCategoryModel> values;

    public ServiceAdapter(Context context, List<ServiceCategoryModel> values) {
        this.ctx = context;
        this.values = values;

    }

    @Override
    public ServiceAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.servicegrid_adapter, parent, false);
        return new ServiceAdapter.RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServiceAdapter.RecyclerViewHolder holder, int position) {
        final ServiceCategoryModel sModel = values.get(position);
        holder.txtServiceName.setText(sModel.getCategory());
        if (!sModel.getImagePath().equals(""))
            Picasso.get().load(sModel.getImagePath()).placeholder(R.mipmap.logo).into(holder.imgService);

        holder.containerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, PartnerListActivity.class);
                i.putExtra("serviceId", sModel.getId());
                ctx.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView txtServiceName;
        ImageView imgService;
        LinearLayout containerlayout;


        public RecyclerViewHolder(View itemView) {
            super(itemView);

            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            imgService = itemView.findViewById(R.id.serviceImg);
            containerlayout = itemView.findViewById(R.id.containerLayout);
            itemView.setClickable(true);

        }
    }
}

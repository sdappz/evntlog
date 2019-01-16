package com.swagger.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.swagger.app.R;
import com.swagger.app.model.PartnerModel;
import com.swagger.app.model.ServiceCategoryModel;

import java.util.List;

public class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.RecyclerViewHolder> {
    Context ctx;
    List<PartnerModel> values;

    public PartnerAdapter(Context context, List<PartnerModel> values) {
        this.ctx = context;
        this.values = values;

    }

    @Override
    public PartnerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.partner_adapter, parent, false);
        return new PartnerAdapter.RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PartnerAdapter.RecyclerViewHolder holder, int position) {
        final PartnerModel pModel = values.get(position);
        holder.partnerName.setText(pModel.getPartnerName());
        if (!pModel.getImagePath().equals(""))
            Picasso.get().load(pModel.getImagePath()).placeholder(R.mipmap.logo).into(holder.imgPartner);

        holder.serviceName.setText(pModel.getServiceName());

        holder.containerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "Work in progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView partnerName;
        ImageView imgPartner;
        TextView serviceName;
        LinearLayout containerlayout;


        public RecyclerViewHolder(View itemView) {
            super(itemView);

            partnerName = itemView.findViewById(R.id.partnerNameTxt);
            imgPartner = itemView.findViewById(R.id.partnerImage);
            containerlayout = itemView.findViewById(R.id.containerLayout);
            serviceName = itemView.findViewById(R.id.serviceTxt);
            itemView.setClickable(true);

        }
    }
}

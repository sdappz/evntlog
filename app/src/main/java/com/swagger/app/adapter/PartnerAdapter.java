package com.swagger.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.swagger.app.PartnerDetailsActivity;
import com.swagger.app.R;
import com.swagger.app.model.PartnerModel;
import com.swagger.app.model.ServiceCategoryModel;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import java.util.List;

public class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.RecyclerViewHolder> {
    Context ctx;
    List<PartnerModel> values;
    SharedPreferenceClass sharedPreferenceClass;

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
                Intent i = new Intent(ctx, PartnerDetailsActivity.class);
                i.putExtra("partnerName", pModel.getPartnerName());
                i.putExtra("partnerService", pModel.getServiceName());
                i.putExtra("id",pModel.getId());
                ctx.startActivity(i);
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
        RelativeLayout containerlayout;


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

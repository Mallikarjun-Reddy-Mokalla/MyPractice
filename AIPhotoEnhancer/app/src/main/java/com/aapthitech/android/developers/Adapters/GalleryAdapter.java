package com.aapthitech.android.developers.Adapters;

import static com.aapthitech.android.developers.Creations.creationsInstance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
 import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.DeleteConfirmDialogBinding;
 import com.aapthitech.android.developers.databinding.GalleryItemBinding;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public List<String> mImageslist = new ArrayList<>();
    private Context mContext;
    Dialog deleteDialog;

    public GalleryAdapter(List<String> mImageslist, Context mContext) {
        this.mImageslist = mImageslist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GalleryItemBinding binding = GalleryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mImageslist.get(position), position);

    }

    @Override
    public int getItemCount() {
        return mImageslist!=null ? mImageslist.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private GalleryItemBinding binding;

        public ViewHolder(@NonNull GalleryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String imageUrl, int pos) {
            Glide.with(binding.imageItem.getContext()).load(imageUrl).into(binding.imageItem);
            binding.deleteGalleryImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteConfirmDialog dialog = new DeleteConfirmDialog(mContext, pos);
                }
            });

            binding.shareGalleryImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareGalleryImage(imageUrl);
                }
            });
        }
    }

    private void shareGalleryImage(String img1) {
        Uri uriImage = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", new File(img1));
        File file = new File(img1);
        System.out.println(file);
        System.out.println(uriImage);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.app_name));
        String shareMessage = "I'm using " + mContext.getResources().getString(R.string.app_name) + " ! Get the app free at ";
        shareMessage = shareMessage + " : https://play.google.com/store/apps/details?id=" + mContext.getPackageName();
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uriImage);
        mContext.startActivity(sharingIntent);
    }

    public class DeleteConfirmDialog extends Dialog {

        private DeleteConfirmDialogBinding binding;

        public DeleteConfirmDialog(Context context, int position) {
            super(context);
            deleteDialog = new Dialog(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            binding = DeleteConfirmDialogBinding.inflate(LayoutInflater.from(context));
            deleteDialog.setContentView(binding.getRoot());

//              Apply margin to the CardView using layout parameters
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) binding.parentCard.getLayoutParams();
            int marginInPixels = (int) mContext.getResources().getDimension(R.dimen.dialog_margin); //Replace with your actual dimension resource
            layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
            binding.parentCard.setLayoutParams(layoutParams);
            deleteDialog.setCancelable(false);
            deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            deleteDialog.getWindow().setGravity(Gravity.CENTER);
            if (deleteDialog != null) {
                deleteDialog.show();
            }
            binding.dialogOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImageslist.size() >= position) {
                        File fdelete = new File(mImageslist.get(position));
                        if (fdelete.exists()) {
                            if (fdelete.delete()) {
                                System.out.println("file Deleted :" + mImageslist.get(position));
                            } else {
                                System.out.println("file not Deleted :" + mImageslist.get(position));
                            }
                        }

                        mImageslist.remove(position);
                        notifyDataSetChanged();
                        if (deleteDialog != null) {
                            if (deleteDialog.isShowing() && !((Activity) context).isFinishing()) {
                                deleteDialog.dismiss();
                            }
                        }
                        if (mImageslist.size() == 0 && context != null) {
                            creationsInstance.onImagesListEmpty();
                        }
                    }
                }
            });
            binding.dialogCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteDialog != null && deleteDialog.isShowing()) {
                        deleteDialog.dismiss();
                    }
                }
            });

        }
    }


}

// Generated by view binder compiler. Do not edit!
package xyz.tqydn.tipall.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.textfield.TextInputEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.tqydn.tipall.R;

public final class ActivityBuatTransaksiBinding implements ViewBinding {
  @NonNull
  private final NestedScrollView rootView;

  @NonNull
  public final TextView HargaBarang;

  @NonNull
  public final TextView HargaJualBarang;

  @NonNull
  public final ConstraintLayout LayoutMap;

  @NonNull
  public final TextView alamat;

  @NonNull
  public final ImageView barang;

  @NonNull
  public final CardView cardBarang;

  @NonNull
  public final ConstraintLayout cl;

  @NonNull
  public final LinearLayout counter;

  @NonNull
  public final TextView descBarang;

  @NonNull
  public final ImageView imagePenjual;

  @NonNull
  public final TextView jarak;

  @NonNull
  public final TextView judul;

  @NonNull
  public final TextInputEditText jumlah;

  @NonNull
  public final Button kirimTawaran;

  @NonNull
  public final ImageButton kurang;

  @NonNull
  public final ConstraintLayout layoutBarang;

  @NonNull
  public final ConstraintLayout layoutBintang;

  @NonNull
  public final LinearLayout linearLayout;

  @NonNull
  public final TextView namaBarang;

  @NonNull
  public final TextView namaPemilik;

  @NonNull
  public final TextView namaUsaha;

  @NonNull
  public final TextView ratingPenjual;

  @NonNull
  public final ImageButton tambah;

  @NonNull
  public final TextView tersedia;

  @NonNull
  public final TextView total;

  @NonNull
  public final TextView transaksi;

  @NonNull
  public final TextView tv;

  @NonNull
  public final TextView tv1;

  @NonNull
  public final View view1;

  @NonNull
  public final View view2;

  @NonNull
  public final View view3;

  @NonNull
  public final View view4;

  private ActivityBuatTransaksiBinding(@NonNull NestedScrollView rootView,
      @NonNull TextView HargaBarang, @NonNull TextView HargaJualBarang,
      @NonNull ConstraintLayout LayoutMap, @NonNull TextView alamat, @NonNull ImageView barang,
      @NonNull CardView cardBarang, @NonNull ConstraintLayout cl, @NonNull LinearLayout counter,
      @NonNull TextView descBarang, @NonNull ImageView imagePenjual, @NonNull TextView jarak,
      @NonNull TextView judul, @NonNull TextInputEditText jumlah, @NonNull Button kirimTawaran,
      @NonNull ImageButton kurang, @NonNull ConstraintLayout layoutBarang,
      @NonNull ConstraintLayout layoutBintang, @NonNull LinearLayout linearLayout,
      @NonNull TextView namaBarang, @NonNull TextView namaPemilik, @NonNull TextView namaUsaha,
      @NonNull TextView ratingPenjual, @NonNull ImageButton tambah, @NonNull TextView tersedia,
      @NonNull TextView total, @NonNull TextView transaksi, @NonNull TextView tv,
      @NonNull TextView tv1, @NonNull View view1, @NonNull View view2, @NonNull View view3,
      @NonNull View view4) {
    this.rootView = rootView;
    this.HargaBarang = HargaBarang;
    this.HargaJualBarang = HargaJualBarang;
    this.LayoutMap = LayoutMap;
    this.alamat = alamat;
    this.barang = barang;
    this.cardBarang = cardBarang;
    this.cl = cl;
    this.counter = counter;
    this.descBarang = descBarang;
    this.imagePenjual = imagePenjual;
    this.jarak = jarak;
    this.judul = judul;
    this.jumlah = jumlah;
    this.kirimTawaran = kirimTawaran;
    this.kurang = kurang;
    this.layoutBarang = layoutBarang;
    this.layoutBintang = layoutBintang;
    this.linearLayout = linearLayout;
    this.namaBarang = namaBarang;
    this.namaPemilik = namaPemilik;
    this.namaUsaha = namaUsaha;
    this.ratingPenjual = ratingPenjual;
    this.tambah = tambah;
    this.tersedia = tersedia;
    this.total = total;
    this.transaksi = transaksi;
    this.tv = tv;
    this.tv1 = tv1;
    this.view1 = view1;
    this.view2 = view2;
    this.view3 = view3;
    this.view4 = view4;
  }

  @Override
  @NonNull
  public NestedScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityBuatTransaksiBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityBuatTransaksiBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_buat_transaksi, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityBuatTransaksiBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.HargaBarang;
      TextView HargaBarang = rootView.findViewById(id);
      if (HargaBarang == null) {
        break missingId;
      }

      id = R.id.HargaJualBarang;
      TextView HargaJualBarang = rootView.findViewById(id);
      if (HargaJualBarang == null) {
        break missingId;
      }

      id = R.id.LayoutMap;
      ConstraintLayout LayoutMap = rootView.findViewById(id);
      if (LayoutMap == null) {
        break missingId;
      }

      id = R.id.alamat;
      TextView alamat = rootView.findViewById(id);
      if (alamat == null) {
        break missingId;
      }

      id = R.id.barang;
      ImageView barang = rootView.findViewById(id);
      if (barang == null) {
        break missingId;
      }

      id = R.id.cardBarang;
      CardView cardBarang = rootView.findViewById(id);
      if (cardBarang == null) {
        break missingId;
      }

      id = R.id.cl;
      ConstraintLayout cl = rootView.findViewById(id);
      if (cl == null) {
        break missingId;
      }

      id = R.id.counter;
      LinearLayout counter = rootView.findViewById(id);
      if (counter == null) {
        break missingId;
      }

      id = R.id.descBarang;
      TextView descBarang = rootView.findViewById(id);
      if (descBarang == null) {
        break missingId;
      }

      id = R.id.imagePenjual;
      ImageView imagePenjual = rootView.findViewById(id);
      if (imagePenjual == null) {
        break missingId;
      }

      id = R.id.jarak;
      TextView jarak = rootView.findViewById(id);
      if (jarak == null) {
        break missingId;
      }

      id = R.id.judul;
      TextView judul = rootView.findViewById(id);
      if (judul == null) {
        break missingId;
      }

      id = R.id.jumlah;
      TextInputEditText jumlah = rootView.findViewById(id);
      if (jumlah == null) {
        break missingId;
      }

      id = R.id.kirimTawaran;
      Button kirimTawaran = rootView.findViewById(id);
      if (kirimTawaran == null) {
        break missingId;
      }

      id = R.id.kurang;
      ImageButton kurang = rootView.findViewById(id);
      if (kurang == null) {
        break missingId;
      }

      id = R.id.layoutBarang;
      ConstraintLayout layoutBarang = rootView.findViewById(id);
      if (layoutBarang == null) {
        break missingId;
      }

      id = R.id.layoutBintang;
      ConstraintLayout layoutBintang = rootView.findViewById(id);
      if (layoutBintang == null) {
        break missingId;
      }

      id = R.id.linearLayout;
      LinearLayout linearLayout = rootView.findViewById(id);
      if (linearLayout == null) {
        break missingId;
      }

      id = R.id.namaBarang;
      TextView namaBarang = rootView.findViewById(id);
      if (namaBarang == null) {
        break missingId;
      }

      id = R.id.namaPemilik;
      TextView namaPemilik = rootView.findViewById(id);
      if (namaPemilik == null) {
        break missingId;
      }

      id = R.id.namaUsaha;
      TextView namaUsaha = rootView.findViewById(id);
      if (namaUsaha == null) {
        break missingId;
      }

      id = R.id.ratingPenjual;
      TextView ratingPenjual = rootView.findViewById(id);
      if (ratingPenjual == null) {
        break missingId;
      }

      id = R.id.tambah;
      ImageButton tambah = rootView.findViewById(id);
      if (tambah == null) {
        break missingId;
      }

      id = R.id.tersedia;
      TextView tersedia = rootView.findViewById(id);
      if (tersedia == null) {
        break missingId;
      }

      id = R.id.total;
      TextView total = rootView.findViewById(id);
      if (total == null) {
        break missingId;
      }

      id = R.id.transaksi;
      TextView transaksi = rootView.findViewById(id);
      if (transaksi == null) {
        break missingId;
      }

      id = R.id.tv;
      TextView tv = rootView.findViewById(id);
      if (tv == null) {
        break missingId;
      }

      id = R.id.tv1;
      TextView tv1 = rootView.findViewById(id);
      if (tv1 == null) {
        break missingId;
      }

      id = R.id.view1;
      View view1 = rootView.findViewById(id);
      if (view1 == null) {
        break missingId;
      }

      id = R.id.view2;
      View view2 = rootView.findViewById(id);
      if (view2 == null) {
        break missingId;
      }

      id = R.id.view3;
      View view3 = rootView.findViewById(id);
      if (view3 == null) {
        break missingId;
      }

      id = R.id.view4;
      View view4 = rootView.findViewById(id);
      if (view4 == null) {
        break missingId;
      }

      return new ActivityBuatTransaksiBinding((NestedScrollView) rootView, HargaBarang,
          HargaJualBarang, LayoutMap, alamat, barang, cardBarang, cl, counter, descBarang,
          imagePenjual, jarak, judul, jumlah, kirimTawaran, kurang, layoutBarang, layoutBintang,
          linearLayout, namaBarang, namaPemilik, namaUsaha, ratingPenjual, tambah, tersedia, total,
          transaksi, tv, tv1, view1, view2, view3, view4);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

// Generated by view binder compiler. Do not edit!
package xyz.tqydn.tipall.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.tqydn.tipall.R;

public final class RowTawaranBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final CardView cardTawaran;

  @NonNull
  public final CardView carddesc;

  @NonNull
  public final ImageView imageBarang;

  @NonNull
  public final ImageView imagePenjual;

  @NonNull
  public final TextView jarak;

  @NonNull
  public final TextView jumlahStok;

  @NonNull
  public final LinearLayout linearLayout;

  @NonNull
  public final TextView namaBarang;

  @NonNull
  public final TextView namaUsaha;

  @NonNull
  public final Button terima;

  private RowTawaranBinding(@NonNull CardView rootView, @NonNull CardView cardTawaran,
      @NonNull CardView carddesc, @NonNull ImageView imageBarang, @NonNull ImageView imagePenjual,
      @NonNull TextView jarak, @NonNull TextView jumlahStok, @NonNull LinearLayout linearLayout,
      @NonNull TextView namaBarang, @NonNull TextView namaUsaha, @NonNull Button terima) {
    this.rootView = rootView;
    this.cardTawaran = cardTawaran;
    this.carddesc = carddesc;
    this.imageBarang = imageBarang;
    this.imagePenjual = imagePenjual;
    this.jarak = jarak;
    this.jumlahStok = jumlahStok;
    this.linearLayout = linearLayout;
    this.namaBarang = namaBarang;
    this.namaUsaha = namaUsaha;
    this.terima = terima;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static RowTawaranBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowTawaranBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_tawaran, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowTawaranBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      CardView cardTawaran = (CardView) rootView;

      id = R.id.carddesc;
      CardView carddesc = rootView.findViewById(id);
      if (carddesc == null) {
        break missingId;
      }

      id = R.id.imageBarang;
      ImageView imageBarang = rootView.findViewById(id);
      if (imageBarang == null) {
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

      id = R.id.jumlahStok;
      TextView jumlahStok = rootView.findViewById(id);
      if (jumlahStok == null) {
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

      id = R.id.namaUsaha;
      TextView namaUsaha = rootView.findViewById(id);
      if (namaUsaha == null) {
        break missingId;
      }

      id = R.id.terima;
      Button terima = rootView.findViewById(id);
      if (terima == null) {
        break missingId;
      }

      return new RowTawaranBinding((CardView) rootView, cardTawaran, carddesc, imageBarang,
          imagePenjual, jarak, jumlahStok, linearLayout, namaBarang, namaUsaha, terima);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

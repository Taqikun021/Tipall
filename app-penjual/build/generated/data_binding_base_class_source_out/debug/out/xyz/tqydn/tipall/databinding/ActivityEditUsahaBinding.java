// Generated by view binder compiler. Do not edit!
package xyz.tqydn.tipall.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.tqydn.tipall.R;

public final class ActivityEditUsahaBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextInputLayout LayoutLokasi;

  @NonNull
  public final TextInputLayout LayoutNama;

  @NonNull
  public final TextView alamat;

  @NonNull
  public final Button buttonSimpan;

  @NonNull
  public final ConstraintLayout cardView2;

  @NonNull
  public final ConstraintLayout constraintLayout2;

  @NonNull
  public final TextInputEditText etLokasi;

  @NonNull
  public final TextInputEditText etNamaUsaha;

  @NonNull
  public final ImageButton getLokasi;

  @NonNull
  public final ImageButton imageProfil;

  @NonNull
  public final ImageView imageUsaha;

  @NonNull
  public final ImageView imageUsahaAwal;

  @NonNull
  public final TextView textView8;

  private ActivityEditUsahaBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextInputLayout LayoutLokasi, @NonNull TextInputLayout LayoutNama,
      @NonNull TextView alamat, @NonNull Button buttonSimpan, @NonNull ConstraintLayout cardView2,
      @NonNull ConstraintLayout constraintLayout2, @NonNull TextInputEditText etLokasi,
      @NonNull TextInputEditText etNamaUsaha, @NonNull ImageButton getLokasi,
      @NonNull ImageButton imageProfil, @NonNull ImageView imageUsaha,
      @NonNull ImageView imageUsahaAwal, @NonNull TextView textView8) {
    this.rootView = rootView;
    this.LayoutLokasi = LayoutLokasi;
    this.LayoutNama = LayoutNama;
    this.alamat = alamat;
    this.buttonSimpan = buttonSimpan;
    this.cardView2 = cardView2;
    this.constraintLayout2 = constraintLayout2;
    this.etLokasi = etLokasi;
    this.etNamaUsaha = etNamaUsaha;
    this.getLokasi = getLokasi;
    this.imageProfil = imageProfil;
    this.imageUsaha = imageUsaha;
    this.imageUsahaAwal = imageUsahaAwal;
    this.textView8 = textView8;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityEditUsahaBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityEditUsahaBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_edit_usaha, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityEditUsahaBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.LayoutLokasi;
      TextInputLayout LayoutLokasi = rootView.findViewById(id);
      if (LayoutLokasi == null) {
        break missingId;
      }

      id = R.id.LayoutNama;
      TextInputLayout LayoutNama = rootView.findViewById(id);
      if (LayoutNama == null) {
        break missingId;
      }

      id = R.id.alamat;
      TextView alamat = rootView.findViewById(id);
      if (alamat == null) {
        break missingId;
      }

      id = R.id.buttonSimpan;
      Button buttonSimpan = rootView.findViewById(id);
      if (buttonSimpan == null) {
        break missingId;
      }

      id = R.id.cardView2;
      ConstraintLayout cardView2 = rootView.findViewById(id);
      if (cardView2 == null) {
        break missingId;
      }

      id = R.id.constraintLayout2;
      ConstraintLayout constraintLayout2 = rootView.findViewById(id);
      if (constraintLayout2 == null) {
        break missingId;
      }

      id = R.id.etLokasi;
      TextInputEditText etLokasi = rootView.findViewById(id);
      if (etLokasi == null) {
        break missingId;
      }

      id = R.id.etNamaUsaha;
      TextInputEditText etNamaUsaha = rootView.findViewById(id);
      if (etNamaUsaha == null) {
        break missingId;
      }

      id = R.id.getLokasi;
      ImageButton getLokasi = rootView.findViewById(id);
      if (getLokasi == null) {
        break missingId;
      }

      id = R.id.imageProfil;
      ImageButton imageProfil = rootView.findViewById(id);
      if (imageProfil == null) {
        break missingId;
      }

      id = R.id.imageUsaha;
      ImageView imageUsaha = rootView.findViewById(id);
      if (imageUsaha == null) {
        break missingId;
      }

      id = R.id.imageUsahaAwal;
      ImageView imageUsahaAwal = rootView.findViewById(id);
      if (imageUsahaAwal == null) {
        break missingId;
      }

      id = R.id.textView8;
      TextView textView8 = rootView.findViewById(id);
      if (textView8 == null) {
        break missingId;
      }

      return new ActivityEditUsahaBinding((ConstraintLayout) rootView, LayoutLokasi, LayoutNama,
          alamat, buttonSimpan, cardView2, constraintLayout2, etLokasi, etNamaUsaha, getLokasi,
          imageProfil, imageUsaha, imageUsahaAwal, textView8);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
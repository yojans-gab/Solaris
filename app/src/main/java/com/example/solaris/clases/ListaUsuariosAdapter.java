package com.example.solaris.clases;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solaris.R;
import com.example.solaris.VerActivity;
import com.example.solaris.entidades.Usuarios;

import java.util.ArrayList;

public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.UsuarioViewHolder> {

    ArrayList<Usuarios> listausuarios;

    public ListaUsuariosAdapter(ArrayList<Usuarios> listausuarios){
        this.listausuarios = listausuarios;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_usuario,null,false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        holder.tvUsuario.setText(listausuarios.get(position).getUsuario());
        holder.tvCuenta.setText(String.valueOf(listausuarios.get(position).getCuenta()));
        holder.tvPassword.setText(listausuarios.get(position).getPassword());
        holder.tvTipoCuenta.setText(listausuarios.get(position).getTipoCuenta());
        holder.tvSaldo.setText(String.valueOf(listausuarios.get(position).getSaldo()));
    }

    @Override
    public int getItemCount() {
        return listausuarios.size();
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsuario, tvCuenta, tvPassword, tvTipoCuenta, tvSaldo;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvCuenta= itemView.findViewById(R.id.tvCuenta);
            tvPassword = itemView.findViewById(R.id.tvPassword);
            tvTipoCuenta = itemView.findViewById(R.id.tvTipoCuenta);
            tvSaldo = itemView.findViewById(R.id.tvSaldo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, VerActivity.class);
                    intent.putExtra("numerocuenta", listausuarios.get(getAdapterPosition()).getCuenta());
                    context.startActivity(intent);
                }
            });
        }
    }
}

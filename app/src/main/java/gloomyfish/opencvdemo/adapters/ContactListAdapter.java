package gloomyfish.opencvdemo.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gloomyfish.opencvdemo.R;
import gloomyfish.opencvdemo.entities.Contact;

public class ContactListAdapter extends ArrayAdapter<Contact> {

    private Context context;
    private List<Contact> contacts;


    public ContactListAdapter(Context context, List<Contact> contacts) {
        super(context, R.layout.contact_list_layout, contacts);
        this.context = context;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.contact_list_layout, null);
            viewHolder.textViewId = view.findViewById(R.id.textViewId);
            viewHolder.textViewName = view.findViewById(R.id.textViewName);
            viewHolder.textViewPhone = view.findViewById(R.id.textViewPhone);
            viewHolder.textViewEmail = view.findViewById(R.id.textViewEmail);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Contact contact = contacts.get(position);
        viewHolder.textViewId.setText(String.valueOf(contact.getId()));
        viewHolder.textViewName.setText(contact.getName());
        viewHolder.textViewPhone.setText(contact.getPhone());
        viewHolder.textViewEmail.setText(contact.getEmail());
        return view;
    }

    private static class ViewHolder {
        public static TextView textViewId;
        public static TextView textViewName;
        public static TextView textViewPhone;
        public static TextView textViewEmail;
    }
}

package com.surepay.library.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.surepay.library.data.Financial;
import com.surepay.library.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class TransactionAdapter
        extends BaseAdapter
{
    private Context context;
    private ArrayList<Financial> Items;

    public TransactionAdapter(Context context, ArrayList<Financial> Items) {
        this.context = context;
        this.Items = Items;
    }

    public int getCount() {
        return this.Items.size();
    } private class ViewHolder {
    TextView pan; TextView amount; TextView amountAr; TextView schemaNameAr; TextView schemaNameEn; TextView auth; TextView auth2; TextView transTypeEn; TextView transTypeAr; TextView merchantEn; TextView merchantAr; TextView merAddressAr; TextView merAddressEn; TextView bankId; TextView mid; TextView stan; TextView appVersion; TextView merchantPhone; TextView aquId; TextView tid; TextView rrn; TextView transDescrEn; TextView transDescrAr; TextView transDate; TextView transTime; TextView operationDate; TextView operationTime; TextView leapTagInfo1; TextView leapTagInfo2; TextView leapTagInfo3; TextView expDate; TextView transResultTxtAr; TextView transResultTxtEn; TextView merchCopyEn; TextView merchCopyAr; TextView clientCopyEn; TextView clientCopyAr;
    private ViewHolder() {} }
    public Object getItem(int position) {
        return this.Items.get(position);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder = new ViewHolder();

        convertView = mInflater.inflate(R.layout.view_financial_receipt, null);

        holder.pan = (TextView)convertView.findViewById(R.id.pan);
        holder.amount = (TextView)convertView.findViewById(R.id.amount);
        holder.amountAr = (TextView)convertView.findViewById(R.id.amountAr);
        holder.schemaNameAr = (TextView)convertView.findViewById(R.id.schemaAr);
        holder.schemaNameEn = (TextView)convertView.findViewById(R.id.schemaEn);
        holder.auth = (TextView)convertView.findViewById(R.id.auth_value);
        holder.auth2 = (TextView)convertView.findViewById(R.id.auth_value2);
        holder.transTypeEn = (TextView)convertView.findViewById(R.id.transTypeEn);
        holder.transTypeAr = (TextView)convertView.findViewById(R.id.transTypeAr);
        holder.merchantEn = (TextView)convertView.findViewById(R.id.retailer_name_en_textview);
        holder.merchantAr = (TextView)convertView.findViewById(R.id.retailer_name_ar_textview);
        holder.merAddressAr = (TextView)convertView.findViewById(R.id.addressArLine1);
        holder.merAddressEn = (TextView)convertView.findViewById(R.id.addressEnLine1);
        holder.bankId = (TextView)convertView.findViewById(R.id.bankId);
        holder.mid = (TextView)convertView.findViewById(R.id.mid);
        holder.stan = (TextView)convertView.findViewById(R.id.stan);
        holder.appVersion = (TextView)convertView.findViewById(R.id.appversion);
        holder.merchantPhone = (TextView)convertView.findViewById(R.id.merchantPhone);
        holder.aquId = (TextView)convertView.findViewById(R.id.aquId);
        holder.tid = (TextView)convertView.findViewById(R.id.tid);
        holder.rrn = (TextView)convertView.findViewById(R.id.rrn);
        holder.transDescrEn = (TextView)convertView.findViewById(R.id.transDescEn);
        holder.transDescrAr = (TextView)convertView.findViewById(R.id.transDescAr);
        holder.transDate = (TextView)convertView.findViewById(R.id.start_date_ar_textview);
        holder.transTime = (TextView)convertView.findViewById(R.id.start_time_ar_textview);
        holder.operationDate = (TextView)convertView.findViewById(R.id.operation_date_textview);
        holder.operationTime = (TextView)convertView.findViewById(R.id.operation_time_textview);
        holder.leapTagInfo1 = (TextView)convertView.findViewById(R.id.leapTagInfoLine1);
        holder.leapTagInfo2 = (TextView)convertView.findViewById(R.id.leapTagInfoLine2);
        holder.leapTagInfo3 = (TextView)convertView.findViewById(R.id.leapTagInfoLine3);
        holder.expDate = (TextView)convertView.findViewById(R.id.panFormatted);
        holder.transResultTxtAr = (TextView)convertView.findViewById(R.id.transactionResultTextAr);
        holder.transResultTxtEn = (TextView)convertView.findViewById(R.id.transactionResultTextEn);
        holder.merchCopyAr = (TextView)convertView.findViewById(R.id.merch_copy_ar);
        holder.merchCopyEn = (TextView)convertView.findViewById(R.id.merch_copy_en);
        holder.clientCopyAr = (TextView)convertView.findViewById(R.id.client_copy_ar);
        holder.clientCopyEn = (TextView)convertView.findViewById(R.id.client_copy_en);

        Financial item = (Financial)getItem(position);

        holder.pan.setText(item.getPAN());
        holder.amount.setText(item.getAMOUNT() + " " + this.context.getString(R.string.amount_ar));
        holder.amountAr.setText(this.context.getString(R.string.amount_en) + " " + item.getAMOUNT());
        holder.schemaNameAr.setText(item.getSCHEME_N_A());
        holder.schemaNameEn.setText(item.getSCHEME_N_E());
        holder.auth.setText(item.getAUTH());
        holder.auth2.setText(item.getAUTH());
        holder.transTypeAr.setText(item.getTRANS_TYPE());
        holder.merchantAr.setText(item.getMER_NAME_A());
        holder.merchantEn.setText(item.getMER_NAME_E());
        holder.merAddressAr.setText(item.getMER_ADDRS_A());
        holder.merAddressEn.setText(item.getMER_ADDRS_E());
        holder.bankId.setText(item.getBANK_ID());
        holder.mid.setText(item.getMID());
        holder.stan.setText(item.getSTAN());
        holder.appVersion.setText(item.getAPP_VER());
        holder.merchantPhone.setText(item.getMER_PHONE());
        holder.aquId.setText(item.getAQU_ID());
        holder.tid.setText(item.getTID());
        holder.rrn.setText(item.getRRN());
        holder.expDate.setText(item.getEXP_DATE().substring(0, 2) + "/" + item.getEXP_DATE().substring(2, 4));


        String transTypeStr = item.getTRANS_TYPE();
        if (transTypeStr.equals(String.valueOf(0))) {
            holder.transTypeEn.setText(this.context.getString(R.string.purchase_en));
            holder.transTypeAr.setText(this.context.getString(R.string.purchase_ar));
            holder.transDescrAr.setText(this.context.getString(R.string.purchase_amount_desc_ar));
            holder.transDescrEn.setText(this.context.getString(R.string.purchase_amount_desc_en));
        }

        String startDateTime = parseDateAndTime(item.getTRANS_DT_ST());
        String[] startDateTimeSeperated = startDateTime.split(" ");
        holder.transDate.setText(startDateTimeSeperated[0]);
        holder.transTime.setText(startDateTimeSeperated[1]);

        String endDateTime = parseDateAndTime(item.getTRANS_DT_END());
        String[] endDateTimeSeperated = endDateTime.split(" ");
        holder.operationDate.setText(endDateTimeSeperated[0]);
        holder.operationTime.setText(endDateTimeSeperated[1]);

        if (item.getLEAPICCTagsInfo() != null && item.getLEAPICCTagsInfo().length() > 0) {
            if (item.getLEAPICCTagsInfo().indexOf("|") > 0) {
                StringTokenizer tokens = new StringTokenizer(item.getLEAPICCTagsInfo(), "|");
                if (tokens.countTokens() > 0) {
                    if (tokens.countTokens() == 1) {
                        holder.leapTagInfo1.setText(tokens.nextToken());
                    } else if (tokens.countTokens() == 2) {
                        holder.leapTagInfo1.setText(tokens.nextToken());
                        holder.leapTagInfo2.setText(tokens.nextToken());
                    } else if (tokens.countTokens() == 3) {
                        holder.leapTagInfo1.setText(tokens.nextToken());
                        holder.leapTagInfo2.setText(tokens.nextToken());
                        holder.leapTagInfo3.setText(tokens.nextToken());
                    }
                }
            } else {
                holder.leapTagInfo1.setText(item.getLEAPICCTagsInfo());
            }
        }
        try {
            int transResultCode = Integer.parseInt(item.getTX_RSLT());
            int offlineCode = Integer.parseInt(item.getOFFLINE_TRX());
            switch (transResultCode) {
                case 0:
                    if (offlineCode == 3) {
                        holder.transResultTxtAr.setText(this.context.getString(R.string.received_ar));
                        holder.transResultTxtEn.setText(this.context.getString(R.string.received_en)); break;
                    }
                    if (offlineCode == 1) {
                        holder.transResultTxtAr.setText(this.context.getString(R.string.offline_approved_ar));
                        holder.transResultTxtEn.setText(this.context.getString(R.string.offline_approved_en)); break;
                    }
                    holder.transResultTxtAr.setText(this.context.getString(R.string.approved_ar));
                    holder.transResultTxtEn.setText(this.context.getString(R.string.approved_en));
                    break;


                case 1:
                    if (offlineCode == 1) {
                        holder.transResultTxtAr.setText(this.context.getString(R.string.offline_declined_ar));
                        holder.transResultTxtEn.setText(this.context.getString(R.string.offline_declined_en)); break;
                    }
                    holder.transResultTxtAr.setText(this.context.getString(R.string.declined_ar));
                    holder.transResultTxtEn.setText(this.context.getString(R.string.declined_en));
                    break;

                case 2:
                    holder.transResultTxtAr.setText(this.context.getString(R.string.void_ar));
                    holder.transResultTxtEn.setText(this.context.getString(R.string.void_en));
                    break;
                case 3:
                    holder.transResultTxtAr.setText(this.context.getString(R.string.cancelled_ar));
                    holder.transResultTxtEn.setText(this.context.getString(R.string.cancelled_en));
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (item.isMerchant()) {
            holder.merchCopyEn.setVisibility(0);
            holder.merchCopyAr.setVisibility(0);
            holder.clientCopyEn.setVisibility(8);
            holder.clientCopyAr.setVisibility(8);
        } else {
            holder.merchCopyEn.setVisibility(8);
            holder.merchCopyAr.setVisibility(8);
            holder.clientCopyEn.setVisibility(0);
            holder.clientCopyAr.setVisibility(0);
        }

        return convertView;
    }

    private String parseDateAndTime(String time) {
        String inputPattern = "yyyyMMddHHmmss";
        String outputPattern = "dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
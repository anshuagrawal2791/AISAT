package com.cognitio.aisat.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anshu on 28/10/16.
 */

public class Question implements Parcelable{
    private String question,option_a,option_b,option_c,option_d,answer,response,id,category;

    public Question(String question, String option_a, String option_b, String option_c, String option_d, String answer,String id,String category) {
        this.question = question;
        this.option_a = option_a;
        this.option_b = option_b;
        this.option_c = option_c;
        this.option_d = option_d;
        this.answer = answer;
        this.id = id;
        this.category = category;
        response=" ";
    }

    public Question(Parcel in) {
        question = in.readString();
        option_a = in.readString();
        option_b = in.readString();
        option_c = in.readString();
        option_d = in.readString();
        answer = in.readString();
        response = in.readString();
        id = in.readString();
        category = in.readString();

    }

    public String getCategory() {
        return category;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public String getQuestion() {
        return question;
    }

    public String getOption_a() {
        return option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public String getOption_d() {
        return option_d;
    }

    public String getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(option_a);
        dest.writeString(option_b);
        dest.writeString(option_c);
        dest.writeString(option_d);
        dest.writeString(answer);
        dest.writeString(response);
        dest.writeString(id);
        dest.writeString(category);
        

    }
    public static final Parcelable.Creator<Question> CREATOR
            = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
